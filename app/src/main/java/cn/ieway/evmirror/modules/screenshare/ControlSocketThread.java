package cn.ieway.evmirror.modules.screenshare;

import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Arrays;
import cn.ieway.evmirror.entity.ControlMessageEntity;
import cn.ieway.evmirror.entity.eventbus.NetWorkMessageEvent;
import cn.ieway.evmirror.util.DataTool;

import static cn.ieway.evmirror.application.MirrorApplication.sMe;

public
/**
 * FileName: ControlSocketThread
 * Author: Admin
 * Date: 2021/1/11 9:43
 * Description: 
 */
class ControlSocketThread extends Thread {
    private String TAG = sMe.TAG+"control_thread";
    private Socket socket;
    private String socketUrl;
    private int socketPort;
    private OutputStream socketOutputStream;
    private ScreenShareActivityNew.ControlHandler handler ;

    public ControlSocketThread( String url, int port, ScreenShareActivityNew.ControlHandler controlHandler){
        this.socketUrl=url;
        this.socketPort = port;
        this.handler = controlHandler;
        socket = new Socket();
    }

    @Override
    public void run() {
        try {
            SocketAddress remoteAddr = new InetSocketAddress(socketUrl, socketPort);
            socket.connect(remoteAddr, 10000);  //等待建立连接的超时时间为1分钟
            socket.setKeepAlive(true);
            socketOutputStream = socket.getOutputStream();
            byte[] msg = sendSocketMsg("1.0", 0, null);
            //接受主机发送的指令
            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            byte[] b = new byte[4];
            dis.read(b, 0, 4);
            Log.d("huangx", "run: 收到的信息是 head ：" + Arrays.toString(b));
            int size = DataTool.byte2Int(b);
            byte[] bytes = new byte[size];
            dis.read(bytes, 0, size);
            onSocketMessage(new String(bytes));
        }
        catch (ConnectException e){
            Log.d(TAG, "run ConnectException:  "+e.getMessage());
            EventBus.getDefault().postSticky(new NetWorkMessageEvent(NetWorkMessageEvent.State.UNKNOWN));
        }
        catch (IOException e) {
            Log.d(TAG, "run IOException:  "+e.getMessage());
            EventBus.getDefault().postSticky(new NetWorkMessageEvent(NetWorkMessageEvent.State.UNKNOWN));
        }
    }


    /**
     * 接收客户端通信消息
     *
     * @param message
     * @throws JSONException
     */
    private void onSocketMessage(String message) {
        ControlMessageEntity entity = JSON.parseObject(message, ControlMessageEntity.class);
        if (entity == null) {
            return;
        }
        Log.d(TAG, "onSocketMessage: "+entity.toString());
        // 0: [ok] 1:[拒绝] 2:[错误]
        switch (entity.getType()) {
            case 0: {
                ControlMessageEntity.DataBean data = entity.getData();
                if (data.getKey().isEmpty()) {
                    //参数异常
                    break;
                }
                Message msg = Message.obtain();
                msg.what = ScreenShareActivityNew.HANDLER_START;
                msg.obj = data;
                handler.sendMessage(msg);
                Log.d(TAG, "onSocketMessage: ");
                break;
            }
        }
    }

    /**
     * 向客户端发送消息
     *
     * @param version
     * @param type
     * @param data
     */
    private byte[] sendSocketMsg(String version, int type, @Nullable ControlMessageEntity.DataBean data) throws IOException {
        ControlMessageEntity control;
        if (data == null) {
            control = new ControlMessageEntity(version, type);
        } else {
            control = new ControlMessageEntity(version, type, data);
        }
        byte[] message = control.getSendMsg(control);

        if(socketOutputStream != null){
            socketOutputStream.write(message);
            socketOutputStream.flush();
        }
        return message;
    }
}
