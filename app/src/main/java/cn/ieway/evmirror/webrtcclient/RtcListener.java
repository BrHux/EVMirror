package cn.ieway.evmirror.webrtcclient;

import org.webrtc.MediaStream;

/**
 * Implement this interface to be notified of events.
 */
public interface RtcListener {
    void onCallReady(String callId);

    void onStatusChanged(String newStatus);

    void onLocalStream(MediaStream localStream, boolean show);

    void onAddRemoteStream(MediaStream remoteStream, int endPoint);

    void onRemoveRemoteStream(int endPoint);
    void onConnectError(int code, String reason, boolean remote);
    void onConnectCotrl(int code, String reason, boolean remote);

    void onMsgAskDialog(String type, String msg, long length);

}


