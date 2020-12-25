package cn.ieway.evmirror.modules.screenshare;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;

import cn.ieway.evmirror.webrtcclient.RtcListener;

/**
 * FileName: WebRtcListener
 * Author: Admin
 * Date: 2020/12/16 16:16
 * Description:
 */
public class WebRtcListener implements RtcListener {
    Handler jHandler;
    public WebRtcListener(Handler jHandler) {
        this.jHandler = jHandler;
    }

    @Override
    public void onCallReady(String callId) {

    }

    @Override
    public void onStatusChanged(String newStatus) {

    }

    @Override
    public void onLocalStream(MediaStream localStream, boolean show) {

    }

    @Override
    public void onAddRemoteStream(MediaStream remoteStream, int endPoint) {

    }

    @Override
    public void onRemoveRemoteStream(int endPoint) {
        Log.d("huangx", "onRemoveRemoteStream: ");
    }

    @Override
    public void onConnectError(int code, String reason, boolean remote) {
        Log.d("huangx", "onConnectError: "+reason);
        Message message = Message.obtain();
        message.what = -1;
        jHandler.sendMessage(message);
    }

    @Override
    public void onConnectCotrl(int code, String reason, boolean remote) {

    }

    @Override
    public void onMsgAskDialog(String type, String msg, long length) {

    }


}
