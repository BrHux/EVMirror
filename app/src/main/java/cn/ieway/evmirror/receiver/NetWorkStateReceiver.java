package cn.ieway.evmirror.receiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import cn.ieway.evmirror.application.MirrorApplication;
import cn.ieway.evmirror.entity.eventbus.NetWorkMessageEvent;

/**
 * Created by Carson_Ho on 16/10/31.
 */
public class NetWorkStateReceiver extends BroadcastReceiver {
    private static final String TAG = "wifiReceiver";
    private NetworkInfo.State state ;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
            Log.i(TAG, "wifi信号强度变化");
        }
        //wifi连接上与否
        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                if (state != NetworkInfo.State.DISCONNECTED){
                    state = info.getState();
                    EventBus.getDefault().postSticky(new NetWorkMessageEvent(NetWorkMessageEvent.State.DISCONNECTED));
                }
                Log.i(TAG, "wifi断开");
            } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                if (state != NetworkInfo.State.CONNECTED){
                    state = info.getState();
                    EventBus.getDefault().postSticky(new NetWorkMessageEvent(NetWorkMessageEvent.State.CONNECTED));
                }
                //获取当前wifi名称
                Log.i(TAG, "连接到网络 " + wifiInfo.getSSID());
            }
        }
        //wifi打开与否
        if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if (wifistate == WifiManager.WIFI_STATE_DISABLED) {
                MirrorApplication.sMe.isWlanOpen = false;
                Log.i(TAG, "系统关闭wifi");
            } else if (wifistate == WifiManager.WIFI_STATE_ENABLED) {
                MirrorApplication.sMe.isWlanOpen = true;
                Log.i(TAG, "系统开启wifi");
            }
        }
    }


    /** wifi打开后状态判断
     * @param context
     * @param intent
     */
        private void wifiEnable(Context context, Intent intent){
            //wifi连接上与否
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    EventBus.getDefault().postSticky(new NetWorkMessageEvent(NetWorkMessageEvent.State.DISCONNECTED));
                    Log.i(TAG, "wifi断开");
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    //获取当前wifi名称
                    Log.i(TAG, "连接到网络 " + wifiInfo.getSSID());
                    EventBus.getDefault().postSticky(new NetWorkMessageEvent(NetWorkMessageEvent.State.CONNECTED));
                    signaltrength(context,intent);
                }
            }
        }

    /** wifi信号强度
     * @param context
     * @param intent
     */
        private void signaltrength(Context context, Intent intent){

        }
}

