package cn.ieway.evmirror.application;

import cn.ieway.evmirror.base.BaseApplication;
import cn.ieway.evmirror.webrtcclient.WebRtcClient;

/**
 * FileName: MirrorApplication
 * Author: Admin
 * Date: 2020/12/8 15:22
 * Description:
 */
public class MirrorApplication extends BaseApplication {
    WebRtcClient webRtcClient;
    @Override
    public void onCreate() {
        super.onCreate();
    }
}
