package cn.ieway.evmirror.webrtcclient;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjection.Callback;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.DataChannel;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.ieway.evmirror.webrtcclient.entity.DataChannelBean;
import cn.ieway.evmirror.webrtcclient.entity.MessageBean;
import cn.ieway.evmirror.webrtcclient.entity.MessageEntity;
import cn.ieway.evmirror.webrtcclient.entity.PeerConnectionParameters;
import cn.ieway.evmirror.webrtcclient.override.ScreenRecorder;

import static cn.ieway.evmirror.webrtcclient.DataChannelAdapter.BEGIN;
import static cn.ieway.evmirror.webrtcclient.DataChannelAdapter.RECEIVE_RESP;

/**
 *
 */
public class WebRtcClient {
    private final static String TAG = "WebRtcClient";
    public static int FRONT_FACING = 1;
    public static int BACK_FACING = 0;
    public static String DISCONNECTED = "DISCONNECTED";

    private Context ctx;
    private RtcListener rtcListener;
    private PeerConnectionParameters pcParams;
    private PeerConnectionFactory peerConnectionFactory;
    private EglBase.Context eglBaseContext;
    private LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
    //    private Socket socket;
    private JWebSocketClient webSocket;
    private final static int MAX_PEER = 10;
    private boolean[] endPoints = new boolean[MAX_PEER];
    private HashMap<String, Peer> peers = new HashMap<>();
    private MediaConstraints pcConstraints = new MediaConstraints();
    private MediaStream localMS;
    private VideoSource videoSource;
    VideoCapturer videoCapturer;
    VideoTrack videoTrack;
    ScreenRecorder screenCapturerAndroid;
    int mWidth, mHeight;
    private Intent mMediaProjectionPermissionResultData;
    private MediaProjection mMediaProjection;
    private int cameraID;
    private HashMap<String, Command> commandMap;
    Gson gson;
    private String localConnectID; //请求共享屏幕的id
    private String ownerID; //目标服务端ID
    public DataChannelAdapter dataChannelAdapter;
    public boolean isRequesting = false;
    private String mHost;
    private AudioSource audioSource;
    private AudioTrack audioTrack;

    public WebRtcClient(RtcListener listener, String host, PeerConnectionParameters params, EglBase.Context mEGLcontext, Context context) {
        this.rtcListener = listener;
        this.pcParams = params;
        this.mWidth = params.videoWidth;
        this.mHeight = params.videoHeight;
        this.ctx = context;
        this.eglBaseContext = mEGLcontext;
        gson = new GsonBuilder().create();
        this.commandMap = new HashMap<>();
        commandMap.put("id", new CreateInfoCommand());
        commandMap.put("allUser", new RequestHostIDCommand());
        commandMap.put("owner", new CreateOfferCommand());
        commandMap.put("offer", new CreateAnswerCommand());
        commandMap.put("answer", new SetRemoteSDPCommand());
        commandMap.put("candidate", new AddIceCandidateCommand());

        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "false"));
        pcConstraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("enable_rtp_data_channel", "false"));
        pcConstraints.optional.add(new MediaConstraints.KeyValuePair("enable_dtls_srtp", "true"));

        createPeerConnectionFactory();
        initSocket(host);
    }

    private void initSocket(String host) {
        mHost = host;
        try {
            webSocket = new JWebSocketClient(URI.create(host), rtcListener) {
                @Override
                public void onMessage(String message) {
                    super.onMessage(message);
                    try {
                        onSocketMessage(message);
                    } catch (JSONException e) {
                        Log.d(TAG, "[WebRTCClient] onMessage: JSONException " + e.toString());
                        e.printStackTrace();
                        return;
                    }
                }

                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    super.onOpen(handshakedata);
                    Log.d(TAG, "[WebRTCClient] onOpen: ");
                    try {
                        MessageEntity entity = new MessageEntity();
                        entity.setType("request");
                        entity.setData("");
                        webSocket.send(gson.toJson(entity));
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    super.onClose(code, reason, remote);
                    Log.d(TAG, "onClose: " + reason + " / " + remote);
                    rtcListener.onConnectError(code, reason, remote);
                }

                @Override
                public void onError(Exception ex) {
                    super.onError(ex);
                    rtcListener.onConnectError(-1, "连接主机失败 信息：" + ex.getMessage(), false);
                }
            };
            webSocket.setConnectionLostTimeout(10);
            webSocket.connectBlocking();
        } catch (InterruptedException e) {
            Log.d(TAG, "[WebRTCClient]  initSocket: Exception " + e.getMessage());
            e.printStackTrace();
            rtcListener.onConnectError(-1, "连接主机失败：" + e.getMessage(), false);
        }
    }

    /*1、create PeerConnectionFactory*/
    public void createPeerConnectionFactory() {
        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions
                .builder(ctx)
                .createInitializationOptions());
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();

        DefaultVideoEncoderFactory defaultVideoEncoderFactory =
                new DefaultVideoEncoderFactory(eglBaseContext, true, true);

        DefaultVideoDecoderFactory defaultVideoDecoderFactory =
                new DefaultVideoDecoderFactory(eglBaseContext);

        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory();
    }

    public void initLocalMs() {
        localMS = peerConnectionFactory.createLocalMediaStream("ARDAMS");
//        audioSource = peerConnectionFactory.createAudioSource(pcConstraints);
//        audioTrack = peerConnectionFactory.createAudioTrack("ARDAMSa0", audioSource);
        rtcListener.onLocalStream(localMS, false);
    }

    public boolean setAudioTrack(boolean audio) {
        List<AudioTrack> audioTracks = localMS.audioTracks;
        if (audioTracks.size() > 0) {
            for (AudioTrack track : audioTracks) {
                localMS.removeTrack(track);
            }
        }

        if (pcParams.audioCallEnabled && audio) {
            if (audioSource == null) {
                audioSource = peerConnectionFactory.createAudioSource(pcConstraints);
                audioTrack = peerConnectionFactory.createAudioTrack("ARDAMSa0", audioSource);
            }

            localMS.addTrack(audioTrack);
            return true;
        }
        return false;
    }

    public boolean isAudioTrack() {
        return localMS.audioTracks.size() > 0;
    }

    public void onStop() {
    }

    /**
     * Call this method in Activity.onDestroy()
     */
    public void onDestroy() {
        if (peerConnectionFactory != null) {
            peerConnectionFactory.stopAecDump();
        }
        try {
            for (Peer peer : peers.values()) {
                removePeer(peer.id);
            }
            peers.clear();
        } catch (Exception e) {
            Log.d(TAG, "[WebClient] onDestroy: Exception :" + e.toString());
        }
        if (videoCapturer != null) {
            videoCapturer.dispose();
        }
        if (screenCapturerAndroid != null) {
            screenCapturerAndroid.stopCapture();
            screenCapturerAndroid.dispose();
        }
        if (videoSource != null) {
            videoSource.dispose();
        }
        if (peerConnectionFactory != null) {
            peerConnectionFactory.dispose();
        }
        try {
            if (null != webSocket) {
                webSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webSocket = null;
        }
    }

    private void onSocketMessage(String message) throws JSONException {

        MessageEntity entity = gson.fromJson(message, MessageEntity.class);
        String from = mHost.substring(mHost.lastIndexOf("/") + 1);

        if (entity == null) return;
        if (entity.getType().equals("responce")) {
            setResponse(from);
            return;
        }
        if (entity.getType().equals("sdp")) {
            String jsonStr = entity.getData();
            JSONObject jsonData = new JSONObject(jsonStr);
            if (!peers.containsKey(from)) {
                int endPoint = findEndPoint();
                if (endPoint != MAX_PEER) {
                    Peer peer = addPeer(from, endPoint);

                    commandMap.get(jsonData.optString("type")).execute(from, jsonData);
                }
            } else {
                commandMap.get(jsonData.optString("type")).execute(from, jsonData);
            }
            return;
        }

        if (entity.getType().equals("ice_candidate")) {
            String jsonStr = entity.getData();
            JSONObject jsonData = new JSONObject(jsonStr);
            if (!peers.containsKey(from)) {
                int endPoint = findEndPoint();
                if (endPoint != MAX_PEER) {
                    Peer peer = addPeer(from, endPoint);

                    commandMap.get("candidate").execute(from, jsonData);
                }
            } else {
                commandMap.get("candidate").execute(from, jsonData);
            }
            return;
        }
    }

    private void setResponse(String from) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    if (!peers.containsKey(from)) {
                        int endPoint = findEndPoint();
                        if (endPoint != MAX_PEER) {
                            Peer peer = addPeer(from, endPoint);
                            commandMap.get("owner").execute(from, null);
                        }
                    } else {
                        commandMap.get("owner").execute(from, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        timer.schedule(task, 1000);
    }

    public void requestShare() {
        if (isRequesting) {
            return;
        }
        MessageBean messageBean = new MessageBean();
        MessageBean.DataBeanX dataBean = new MessageBean.DataBeanX();

        dataBean.setData_type("request");
        dataBean.setRequest_data("share");

        messageBean.setType("ctrl");
        messageBean.setErro_code(0);
        messageBean.setError("");
        messageBean.setData(dataBean);

        String message = gson.toJson(messageBean);
        webSocket.send(message);
        isRequesting = true;
    }


    private interface Command {
        void execute(String peerId, JSONObject payload) throws JSONException;
    }

    private class CreateInfoCommand implements Command {
        @Override
        public void execute(String id, JSONObject payload) throws JSONException {
            rtcListener.onCallReady(id);
        }
    }

    /**
     * 连接后,请求发起人的id，用于与发起人建立rtc通信，交换sdp，
     */
    private class RequestHostIDCommand implements Command {
        @Override
        public void execute(String id, JSONObject payload) throws JSONException {
            JSONObject bean = new JSONObject();
            bean.put("data_type", "request");
            bean.put("request_data", "owner");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", "ctrl");
            jsonObject.put("erro_code", 0);
            jsonObject.put("error", "");
            jsonObject.put("data", bean);

            String message = jsonObject.toString();
            webSocket.send(message);
        }
    }

    private class CreateOfferCommand implements Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Peer peer = peers.get(peerId);
                    peer.pc.createOffer(peer, pcConstraints);
                }
            }).start();
        }
    }

    private class CreateAnswerCommand implements Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Peer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.optString("type")),
                    payload.getString("sdp")
            );
            peer.pc.setRemoteDescription(peer, sdp);
            peer.pc.createAnswer(peer, pcConstraints);
        }
    }

    private class SetRemoteSDPCommand implements Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            Peer peer = peers.get(peerId);
            SessionDescription sdp = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(payload.optString("type")),
                    payload.getString("sdp")
            );
            peer.pc.setRemoteDescription(peer, sdp);
        }
    }

    private class AddIceCandidateCommand implements Command {
        public void execute(String peerId, JSONObject payload) throws JSONException {
            PeerConnection pc = peers.get(peerId).pc;
            if (pc.getRemoteDescription() != null) {
                IceCandidate candidate = new IceCandidate(
                        payload.optString("sdp_mid"),
                        payload.getInt("sdp_mline_index"),
                        payload.getString("candidate")
                );
                pc.addIceCandidate(candidate);
            }
        }
    }

    /**
     * Send a message through the signaling server
     *
     * @param to   id of recipient
     * @param type type of message
     * @param data payload of message
     * @throws JSONException
     */
    public void sendMessage(String to, String type, JSONObject data) throws JSONException {
        JSONObject message = new JSONObject();
        message.put("type", type);
        message.put("data", data);
        data.toString();
        webSocket.send(data.toString());
        //        socket.emit("message", message);
    }


    private Peer addPeer(String id, int endPoint) {
        Peer peer = new Peer(id, endPoint);
        peers.put(id, peer);
        endPoints[endPoint] = true;
        return peer;
    }

    private void removePeer(String id) {
        Peer peer = peers.get(id);
        if (peer != null) {
            peer.pc.close();
//            peers.remove(peer.id);
            endPoints[peer.endPoint] = false;
//            rtcListener.onRemoveRemoteStream(peer.endPoint);
        } else {
        }
    }

    class Peer extends PeerConnectionAdapter {
        private PeerConnection pc;
        private DataChannel dataChannel;
        private String id;
        private int endPoint;

        public Peer(String id, int endPoint) {
            super();
            if (localMS == null) {
                Log.d(TAG, "[WebRTCClient]  new Peer: localMS == null");
            } else {
                try {
                    PeerConnection.RTCConfiguration configuration = new PeerConnection.RTCConfiguration(iceServers);
                    configuration.enableCpuOveruseDetection = false;
                    this.pc = peerConnectionFactory.createPeerConnection(configuration, this);
                    this.id = id;
                    this.endPoint = endPoint;
//                    pc.setBitrate(8*1024*1024,10*1024*1024,12*1024*1024);
                    pc.addStream(localMS); //, new MediaConstraints()
                    rtcListener.onStatusChanged("CONNECTING");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onDataChannel(DataChannel dataChannel) {
            super.onDataChannel(dataChannel);
            dataChannel.registerObserver(dataChannelAdapter);
        }


        @Override
        public void onCreateSuccess(final SessionDescription sdp) {
            try {

                String description = Tools.getInstance().sortVideoCodec(sdp.description, Tools.VIDEO_CODEC_VP9, "");
                SessionDescription newSdp = new SessionDescription(sdp.type, description);
//                SessionDescription newSdp = sdp;
                JSONObject object = new JSONObject();
                object.put("type", newSdp.type.canonicalForm());
                object.put("sdp", newSdp.description);
                JSONObject payload = new JSONObject();
                payload.put("type", "sdp");
                payload.put("data", object.toString());

                pc.setLocalDescription(Peer.this, newSdp);
                sendMessage(id, newSdp.type.canonicalForm(), payload);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
            if (iceConnectionState == PeerConnection.IceConnectionState.DISCONNECTED ||
                    iceConnectionState == PeerConnection.IceConnectionState.CLOSED) {
                rtcListener.onStatusChanged(DISCONNECTED);
            }
        }

        @Override
        public void onIceCandidate(final IceCandidate candidate) {
            try {
                JSONObject data = new JSONObject();
                data.put("candidate", candidate.sdp);
                data.put("sdp_mline_index", candidate.sdpMLineIndex);
                data.put("sdp_mid", candidate.sdpMid);

                JSONObject payload = new JSONObject();

                payload.put("type", "ice_candidate");
                payload.put("data", data.toString());

                Log.d(TAG, "onIceCandidate: " + payload.toString());
                sendMessage(id, "ice_candidate", payload);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onAddStream(MediaStream mediaStream) {
            rtcListener.onAddRemoteStream(mediaStream, endPoint + 1);
        }

        @Override
        public void onRemoveStream(MediaStream mediaStream) {

        }

    }

    public int findEndPoint() {
        for (int i = 0; i < MAX_PEER; i++) if (!endPoints[i]) return i;
        return MAX_PEER;
    }

    public void setCamera(int id) {
        cameraID = id;
        if (localConnectID == null || localConnectID.isEmpty()) {
            requestShare();
        } else {
            shareScreenCamera();
        }
    }

    public void shareScreenCamera() {
        if (localMS == null) {
            initLocalMs();
        }
        if (videoTrack != null) {
            localMS.removeTrack(videoTrack);
            videoTrack.dispose();
            videoTrack = null;
        }
        if (pcParams.videoCallEnabled) {
            if (screenCapturerAndroid != null) {
                screenCapturerAndroid.stopCapture();
            }
            if (videoCapturer != null) {
                try {
                    videoCapturer.stopCapture();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d(TAG, "setCamera: InterruptedException " + e.toString());
                    videoCapturer = null;
                }
            }
            SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext);
            surfaceTextureHelper.setTextureSize(mWidth, mHeight);
            videoCapturer = createCameraCapturer(cameraID);
            videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
            videoCapturer.initialize(surfaceTextureHelper, ctx, videoSource.getCapturerObserver());
            videoCapturer.startCapture(mWidth, mHeight, pcParams.videoFps);
            videoTrack = peerConnectionFactory.createVideoTrack("ARDAMSv0", videoSource);
            localMS.addTrack(videoTrack);
        }

        if (pcParams.audioCallEnabled) {
            audioSource = peerConnectionFactory.createAudioSource(pcConstraints);
            AudioTrack audioTrack = peerConnectionFactory.createAudioTrack("ARDAMSa0", audioSource);
            localMS.addTrack(audioTrack);
        }

        rtcListener.onLocalStream(localMS, true);
    }

    /**
     * 屏幕共享
     *
     * @param data
     * @param mediaProjection
     */
    public void screenCapturer(Intent data, MediaProjection mediaProjection) {
        mMediaProjectionPermissionResultData = data;
        mMediaProjection = mediaProjection;
        shareScreenCapturer();
    }

    /**
     * 屏幕共享
     */
    public void shareScreenCapturer() {
        if (localMS == null) {
            initLocalMs();
        }
        if (videoTrack != null) {
            localMS.removeTrack(videoTrack);
            videoTrack.dispose();
            videoTrack = null;
        }
        if (pcParams.videoCallEnabled) {
            if (screenCapturerAndroid != null) {
                screenCapturerAndroid.stopCapture();
            }
            if (videoCapturer != null) {
                videoCapturer.dispose();
            }
            SurfaceTextureHelper surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBaseContext);
            screenCapturerAndroid = getScreen(mMediaProjectionPermissionResultData, mMediaProjection);
            videoSource = peerConnectionFactory.createVideoSource(screenCapturerAndroid.isScreencast());
            screenCapturerAndroid.initialize(surfaceTextureHelper, ctx, videoSource.getCapturerObserver());
            screenCapturerAndroid.startCapture(mWidth, mHeight, pcParams.videoFps);
            videoTrack = peerConnectionFactory.createVideoTrack("ARDAMSv0", videoSource);
            localMS.addTrack(videoTrack);
        }


        if (pcParams.audioCallEnabled) {
            audioSource = peerConnectionFactory.createAudioSource(pcConstraints);
//            localMS.addTrack(peerConnectionFactory.createAudioTrack("ARDAMSa0", audioSource));
        }
        rtcListener.onLocalStream(localMS, false);
    }


    @TargetApi(21)
    private ScreenRecorder getScreen(Intent data, MediaProjection mediaProjection) {
        return new ScreenRecorder(data, mediaProjection, new Callback() {
            @Override
            public void onStop() {
                super.onStop();
            }
        });

    }

    public VideoCapturer createCameraCapturer(int camID) {
        Camera1Enumerator enumerator = new Camera1Enumerator();
        final String[] deviceNames = enumerator.getDeviceNames();

        if (camID == BACK_FACING) { //获取后摄像头
            // First,try to find Back facing camera
            for (String deviceName : deviceNames) {
                if (enumerator.isBackFacing(deviceName)) {
                    VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                    if (videoCapturer != null) {
                        return videoCapturer;
                    }
                }
            }
            // Back facing camera not found, try something else
            for (String deviceName : deviceNames) {
                if (!enumerator.isBackFacing(deviceName)) {
                    VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                    if (videoCapturer != null) {
                        return videoCapturer;
                    }
                }
            }
        } else { //获取前摄像头
            //Sec, try to find front facing camera
            for (String deviceName : deviceNames) {
                if (enumerator.isFrontFacing(deviceName)) {
                    VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);
                    if (videoCapturer != null) {
                        return videoCapturer;
                    }
                }
            }
            // Front facing camera not found, try something else
            for (String deviceName : deviceNames) {
                if (!enumerator.isFrontFacing(deviceName)) {
                    VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                    if (videoCapturer != null) {
                        return videoCapturer;
                    }
                }
            }
        }
        return null;
    }


    public void changeCaptureFormat(int width, int height, int frame) {
        if (screenCapturerAndroid == null) return;
        screenCapturerAndroid.changeCaptureFormat(width, height, frame);
    }


}
