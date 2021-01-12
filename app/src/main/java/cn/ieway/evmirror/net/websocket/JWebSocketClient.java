package cn.ieway.evmirror.net.websocket;

import android.util.Log;

import com.google.gson.Gson;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class JWebSocketClient extends WebSocketClient {
    String TAG = "JWebSocketClient";

    Gson gson;

    public JWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
        gson = new Gson();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "onOpen: ");
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "onMessage: "+message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "onClose: reason="+reason+" code="+code+"   remote="+remote);

    }
    @Override
    public void onError(Exception ex) {
        Log.d(TAG, "onError: "+ex.toString());
    }


}
