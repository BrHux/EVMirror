package cn.ieway.evmirror.net.websocket;

import com.zhangke.websocket.SocketListener;
import com.zhangke.websocket.response.ErrorResponse;

import org.java_websocket.framing.Framedata;

import java.nio.ByteBuffer;

/**
 * FileName: ChatSocketListener
 * Author: Admin
 * Date: 2020/7/24 17:36
 * Description: WebSocket连接数据监听
 */
public class SimpleSocketListener implements SocketListener {

    private SocketCallBack callBack;

    public SimpleSocketListener() {

    }


    public void setCallBack(SocketCallBack callBack) {
        this.callBack = callBack;
    }

    private interface SocketCallBack {

    }

    @Override
    public void onConnected() {
    }

    @Override
    public void onConnectFailed(Throwable e) {
    }

    @Override
    public void onDisconnect() {
    }

    @Override
    public void onSendDataError(ErrorResponse errorResponse) {
    }

    @Override
    public <T> void onMessage(String message, T data) {
    }

    @Override
    public <T> void onMessage(ByteBuffer bytes, T data) {
    }

    @Override
    public void onPing(Framedata framedata) {
    }

    @Override
    public void onPong(Framedata framedata) {
    }
}
