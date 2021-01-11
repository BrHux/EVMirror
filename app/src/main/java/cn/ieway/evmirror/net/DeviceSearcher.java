package cn.ieway.evmirror.net;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import cn.ieway.evmirror.entity.DeviceBean;
import cn.ieway.evmirror.util.LogUtil;
import cn.ieway.evmirror.util.NetWorkUtil;


/**
 * 设备搜索类
 * 搜索局域网下设备IP地址
 */
public abstract class DeviceSearcher extends Thread {
    private static final String TAG = DeviceSearcher.class.getSimpleName();

    private static final int DEVICE_FIND_PORT = /*5679*/5003; //需要扫描的端口
    private static final int RECEIVE_TIME_OUT = 2000; // 接收超时时间
    private static final int TIMER_TIME_OUT = 2000; // 计时器首次执行时间
    private static final int RESPONSE_DEVICE_MAX = 100; // 响应设备的最大个数，防止UDP广播攻击
    private static final int SEND_TIME_MAX = 2; // 广播发送次数
    private DatagramSocket hostSocket;
    private Set<DeviceBean> mDeviceSet;
    private String ipAddress = ""; //本机ip地址
    int[] pionts = new int[]{DEVICE_FIND_PORT, /*5680, 5681, 5682, 5683, 5684, 5685, 5686, 5687, 5688, 5689*/};//本地绑定端口
    Gson gson;
    private static final String SERVER_BROADCAST = "ev_screen_share_server_broadcast";

    private long lastAddTime = 0;
    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            long temp = System.currentTimeMillis()-lastAddTime;
            if (lastAddTime != 0 && temp > 1000){
                interrupt();
                timer.cancel();
            }
        }
    };

    protected DeviceSearcher() {
        mDeviceSet = new HashSet<>();
        gson = new GsonBuilder().create();
    }

    static class BroadCastBean {
        String msg_type = "ev_screen_share_client_broadcast";
        String id;
        String name;
        String url;
        /**
         * ip : 192.168.1.209
         * port : 10020
         */

        private String ip;
        private Integer port;


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMsg_type() {
            return msg_type;
        }

        public void setMsg_type(String msg_type) {
            this.msg_type = msg_type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }

    @Override
    public void run() {
        try {
            onSearchStart();
            timer.schedule(timerTask,TIMER_TIME_OUT,RECEIVE_TIME_OUT);

            BroadCastBean broadCastBean = new BroadCastBean();
            broadCastBean.setId(String.valueOf(System.currentTimeMillis()));
            byte[] sendData = gson.toJson(broadCastBean).getBytes();
            if (sendData == null) {
                return;
            }
            ipAddress = NetWorkUtil.getIpAddress();

            InetAddress broadIP = InetAddress.getByName("255.255.255.255");

            DatagramPacket sendPack = new DatagramPacket(sendData, sendData.length, broadIP, DEVICE_FIND_PORT);
            hostSocket = new DatagramSocket(null);
            // 设置接收超时时间
            hostSocket.setSoTimeout(RECEIVE_TIME_OUT);
            hostSocket.setReuseAddress(true);

            for (int p : pionts) {
                hostSocket.bind(new InetSocketAddress(p));
                if (hostSocket.isBound()) {
                    break;
                }
            }

            for (int i = 0; i < SEND_TIME_MAX; i++) {
                // 发送搜索广播
//                hostSocket.send(sendPack);
                // 监听来信
                byte[] receData = new byte[1024];
                DatagramPacket recePack = new DatagramPacket(receData, receData.length);
                try {
                    // 最多接收RESPONSE_DEVICE_MAX个，或超时跳出循环
                   int rspCount = RESPONSE_DEVICE_MAX;
                    while (rspCount-- > 0 && !isInterrupted()) {
                        hostSocket.receive(recePack);
                        if (parsePack(recePack)) {

                        }
                    }
                } catch (SocketTimeoutException e) {
                    LogUtil.i("[DeviceSearcher] run() SocketTimeoutException " + e.toString());
//                    e.printStackTrace();
                }
                catch (InterruptedIOException e){
                    LogUtil.i("[DeviceSearcher] run() InterruptedIOException " + e.toString());
                    break;
                }
            }
//            onSearchFinish(mDeviceSet);
        }catch (UnknownHostException e) {
            Log.d(TAG, "run: UnknownHostException: " + e.getMessage());
//            e.printStackTrace();
        } catch (SocketException e) {
            Log.d(TAG, "run: SocketException: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "run: IOException: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            Log.d(TAG, "run: Exception: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (hostSocket != null) {
                hostSocket.close();
            }
            if (timer != null){
                timer.cancel();
            }
            onSearchFinish(mDeviceSet);
        }
    }

    /**
     * 搜索开始时执行
     */
    public abstract void onSearchStart();

    /**
     * 搜索结束后执行
     *
     * @param deviceSet 搜索到的设备集合
     */
    public abstract void onSearchFinish(Set deviceSet);

    /**
     * 解析报文
     * 协议：$ + packType(1) + data(n)
     * data: 由n组数据，每组的组成结构type(1) + length(4) + data(length)
     * type类型中包含name、room类型，但name必须在最前面
     */
    private boolean parsePack(DatagramPacket pack) {
        if (pack == null || pack.getAddress() == null) {
            return false;
        }
        String ip = pack.getAddress().getHostAddress();
        int port = pack.getPort();
        if (ip.equals(ipAddress)) { //过滤本地广播
            return false;
        }

        for (DeviceBean d : mDeviceSet) { //过滤重复广播
            if (d.getIp().contains(ip)) {
                return false;
            }
        }

        int dataLen = pack.getLength();
        DeviceBean device = null;
        if (dataLen < 2) {
            return false;
        }

        byte[] data = new byte[dataLen];
        System.arraycopy(pack.getData(), pack.getOffset(), data, 0, dataLen);//获取有效data
        String dataStr = new String(data);

        BroadCastBean bean = gson.fromJson(dataStr, BroadCastBean.class);


        device = new DeviceBean();
        device.setName(bean.getName());
        device.setIp(bean.getIp());
        device.setPort(bean.getPort());

        Log.d(TAG, "parsePack: "+device.getName());
        if (device != null) {
            mDeviceSet.add(device);
            lastAddTime = System.currentTimeMillis();
            return true;
        }
        return false;
    }


}
