package cn.ieway.evmirror.screencapture;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.util.Log;
import android.view.Surface;

import com.hjq.toast.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import cn.ieway.evmirror.entity.eventbus.NetWorkMessageEvent;
import cn.ieway.evmirror.screencapture.media.VideoMediaCodec;
import cn.ieway.evmirror.util.DataTool;

import static cn.ieway.evmirror.application.MirrorApplication.sMe;


/**
 * 录制屏幕编码后发送数据包到指定服务端
 */

public class ScreenRecord extends Thread {

    private final static String TAG = "ScreenRecord";

    private Surface mSurface;
    private Context mContext;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjection mMediaProjection;

    private VideoMediaCodec mVideoMediaCodec;
    private Socket socket;
    private OutputStream outputStream;
    private String socketAddress;
    private String socketKey;
    private int socketPoint;

    public ScreenRecord(MediaProjection mp, String key) {
        this.mContext = sMe;
        this.mMediaProjection = mp;
        this.socketKey = key;
        byte[] bytes = sendStream(0, getElen(-1, -1), socketKey.getBytes());
    }

    //================================================================================

    public void setSocket(String address, int point){
        this.socketAddress = address;
        this.socketPoint = point;
    }

    public void initSocket() throws Exception {
        if (socketAddress == null || socketPoint == 0) return;
        socket = new Socket(socketAddress, socketPoint);
        socket.setKeepAlive(true);
        outputStream = socket.getOutputStream();
        //连接成功后发送key
        byte[] bytes = sendStream(0, getElen(-1, -1), socketKey.getBytes());
        outputStream.write(bytes);
    }

    @Override
    public void run() {
        if (mMediaProjection == null) {
            ToastUtils.show("参数异常，请重试。");
            return;
        }
        try {
            initSocket();
            startMediaDisplay();
        } catch (Exception e) {
            Log.d(TAG, "run: ======== "+e.getMessage());
            e.printStackTrace();
            EventBus.getDefault().postSticky(new NetWorkMessageEvent(NetWorkMessageEvent.State.UNKNOWN));
            release();
        }finally {
            Log.d(TAG, "run: --------------------------------------------------------");
        }
    }

    private CodecThread codecThread;
    private void startMediaDisplay() {
        codecThread = new CodecThread();
        codecThread.start();
    }

    public void surfaceChange() {
        if(codecThread != null){
            codecThread.interrupt();
            Log.d(TAG, "surfaceChange: isInterrupted: "+codecThread.isInterrupted());
            codecThread = null;
//            codecThread.change();
        }

        if (mVideoMediaCodec != null) {
            mVideoMediaCodec.release();
        }

        codecThread = new CodecThread();
        codecThread.start();

    }

    /**
     * @param head   包类型 0：[此数据包携带推流key] ; 1：[此数据包携带视频帧数据]
     * @param ex_content   拓展头byte数组[数据长度+数据内容]
     * @param stream 发送数据byte数组
     * @return
     * @throws Exception
     */
    private byte[] sendStream(int head, byte[] ex_content, byte[] stream) {
        //1 包类型 byte数组
        byte[] h_type = DataTool.int2Byte(head);
        //2、
        if (stream == null) {
            byte[] bytes = new byte[h_type.length + ex_content.length];
            System.arraycopy(h_type, 0, bytes, 0, h_type.length);
            System.arraycopy(ex_content, 0, bytes, h_type.length, ex_content.length);
            return bytes;
        }
        //3、

        byte[] d_len = DataTool.int2Byte(stream.length);
        //数据体[数据长度+数据]
        byte[] d_data = new byte[d_len.length + stream.length];
        System.arraycopy(d_len, 0, d_data, 0, d_len.length);
        System.arraycopy(stream, 0, d_data, d_len.length, stream.length);

        //4、
        byte[] bytes = new byte[h_type.length + ex_content.length + d_data.length];
        System.arraycopy(h_type, 0, bytes, 0, h_type.length);
        System.arraycopy(ex_content, 0, bytes, h_type.length, ex_content.length);
        System.arraycopy(d_data, 0, bytes, h_type.length + ex_content.length, d_data.length);
        return bytes;
    }

    /**
     * 拓展头
     * 拓展头长度  + 拓展头类型   +  拓展头数据
     * _ _ _ _  +  _ _ _ _    +   _ _ _ _
     * @param etype 拓展头类型 1：[拓展头携带H264数据包的类型]
     * @param edata 拓展头 0:[普通帧] ;1:[关键帧] ;2:[config帧]
     * @return
     */
    private byte[] getElen(int etype, int edata) {
//        Log.d(TAG, "getElen: ============ etype:"+etype+" / edata:"+edata);
        byte[] e_type = null;
        byte[] e_data = null;
        if (etype == -1 && edata == -1) {
            return DataTool.int2Byte(0);
        }

        int len = 0;
        if (etype != -1) { //拓展头类型
            e_type = DataTool.int2Byte(etype);
            len += e_type.length;
        }

        if (edata != -1) { //拓展头1
            e_data = DataTool.int2Byte(edata);
            len += e_data.length;
        }

        //拓展头长度
        byte[] aByte = DataTool.int2Byte(len);

        byte[] bytes = new byte[len + aByte.length];

        System.arraycopy(aByte, 0, bytes, 0, aByte.length);

        if (e_type != null) {
            System.arraycopy(e_type, 0, bytes, aByte.length, e_type.length);
        }

        if (e_data != null) {
            System.arraycopy(e_data, 0, bytes, aByte.length + e_type.length, e_data.length);
        }
        return bytes;
    }


    /**
     * 停止并释放资源
     **/
    public void release() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mVideoMediaCodec != null) {
                mVideoMediaCodec.release();
            }
            if(codecThread != null){
                codecThread.interrupt();
                Log.d(TAG, "surfaceChange: isInterrupted: "+codecThread.isInterrupted());
                codecThread = null;
            }
            outputStream = null;
            socket = null;
            mVideoMediaCodec = null;
        }
    }

    class CodecThread extends Thread{
        public CodecThread(){
            mVideoMediaCodec = new VideoMediaCodec();
        }
        @Override
        public void run() {
            super.run();
            if(isInterrupted()) {
                Log.d(TAG, "run: CodecThread isInterrupted ");
                if (mVideoMediaCodec != null) {
                    mVideoMediaCodec.release();
                    mVideoMediaCodec = null;
                }
                return;
            }
            mSurface = mVideoMediaCodec.getSurface();
            mVirtualDisplay = mMediaProjection.createVirtualDisplay(TAG + "-display", sMe.screenWidth, sMe.screenHeight, sMe.videoDpi, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                    mSurface, null, null);
            mVideoMediaCodec.isRun(true);
            mVideoMediaCodec.setMediaCodecCallBack(new VideoMediaCodec.MediaCodecCallBack() {
                @Override
                public void onVideoFrameMessage(byte[] bytes, int type, int frame) {
                    if (outputStream == null) return;
                    try {
                        byte[] bytes1 = sendStream(1, getElen(type, frame), bytes);
                        outputStream.write(bytes1);
                    }
                    catch (Exception e){
                        Log.d(TAG, "onVideoFrameMessage: "+e.getMessage());
                        EventBus.getDefault().postSticky(new NetWorkMessageEvent(NetWorkMessageEvent.State.UNKNOWN));
                        release();
                    }
                }
            });
            mVideoMediaCodec.getBuffer();
        }
    }


    //==============================================================================================


    public String getSocketKey() {
        return socketKey;
    }

    public void setSocketKey(String socketKey) {
        this.socketKey = socketKey;
    }

    public MediaProjection getmMediaProjection() {
        return mMediaProjection;
    }

    public void setmMediaProjection(MediaProjection mMediaProjection) {
        this.mMediaProjection = mMediaProjection;
    }

    public VideoMediaCodec getmVideoMediaCodec() {
        return mVideoMediaCodec;
    }

    public void setmVideoMediaCodec(VideoMediaCodec mVideoMediaCodec) {
        this.mVideoMediaCodec = mVideoMediaCodec;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(String socketAddress) {
        this.socketAddress = socketAddress;
    }

    public int getSocketPoint() {
        return socketPoint;
    }

    public void setSocketPoint(int socketPoint) {
        this.socketPoint = socketPoint;
    }

}
