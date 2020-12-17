package cn.ieway.evmirror.modules.screenshare;

import org.webrtc.MediaStream;

import cn.ieway.evmirror.webrtcclient.RtcListener;

/**
 * FileName: WebRtcListener
 * Author: Admin
 * Date: 2020/12/16 16:16
 * Description:
 */
public class WebRtcListener implements RtcListener {
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

    }

    @Override
    public void onConnectError(int code, String reason, boolean remote) {

    }

    @Override
    public void onConnectCotrl(int code, String reason, boolean remote) {

    }

    @Override
    public void onMsgAskDialog(String type, String msg, long length) {

    }


}
