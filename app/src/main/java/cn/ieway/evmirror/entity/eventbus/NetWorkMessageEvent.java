package cn.ieway.evmirror.entity.eventbus;

import android.net.NetworkInfo;

public
/**
 * FileName: NetWorkMessageEvent
 * Author: Admin
 * Date: 2020/12/18 13:31
 * Description: 
 */
class NetWorkMessageEvent {
    public enum State {
        CONNECTING, CONNECTED, SUSPENDED, DISCONNECTING, DISCONNECTED, UNKNOWN
    }

    public State currentState;

    private String reason = "";

    public NetWorkMessageEvent(State state) {
        currentState = state;
    }

    public NetWorkMessageEvent(State currentState, String reason) {
        this.currentState = currentState;
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
