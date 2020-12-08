package cn.ieway.evmirror.application;

import cn.ieway.evmirror.base.BaseApplication;

/**
 * FileName: MirrorApplication
 * Author: Admin
 * Date: 2020/12/8 15:22
 * Description:
 */
public class MirrorApplication extends BaseApplication {
    public static MirrorApplication sMe;

    @Override
    public void onCreate() {
        super.onCreate();
        sMe = MirrorApplication.this;
    }
}
