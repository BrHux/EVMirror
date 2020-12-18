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

    public State creentState;

    public NetWorkMessageEvent(State state) {
        creentState = state;
    }


}
