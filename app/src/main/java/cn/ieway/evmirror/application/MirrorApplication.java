package cn.ieway.evmirror.application;

import android.app.Application;
import android.content.Context;

import com.hjq.toast.ToastUtils;
import com.tamsiree.rxkit.RxTool;
import com.umeng.commonsdk.UMConfigure;

import cn.ieway.evmirror.util.AppFrontBackHelper;
import cn.ieway.evmirror.webrtcclient.WebRtcClient;

/**
 * FileName: MirrorApplication
 * Author: Admin
 * Date: 2020/12/8 15:22
 * Description:
 */
public class MirrorApplication extends Application {

    private boolean isBackGround; //界面是否后台运行
    private Long backGroundTiem;//界面进入后台的时间
    public boolean isWlanOpen = true;


    public static WebRtcClient webRtcClient;
    public static MirrorApplication sMe;

    @Override
    public void onCreate() {
        super.onCreate();

        sMe = MirrorApplication.this;

        initTools(this);
        initFrontBackHelper();
    }


    /**
     * 工具类初始化
     *
     * @param application
     */
    private void initTools(Context application) {
        RxTool.init(application);
        ToastUtils.init(this);
        BaseConfig.init(application);

        // 友盟SDK预初始化函数
        // preInit预初始化函数耗时极少，不会影响App首次冷启动用户体验
        UMConfigure.preInit(application, null, null);

    }

    /**
     * 应用后台运行监听
     */
    private void initFrontBackHelper() {
        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(MirrorApplication.this, new AppFrontBackHelper.OnAppStatusListener() {
            @Override
            public void onFront() {
                isBackGround = false;
            }

            @Override
            public void onBack() {
                isBackGround = true;
                backGroundTiem = System.currentTimeMillis();
            }
        });
    }


    public boolean isBackGround() {
        return isBackGround;
    }

    public Long getBackGroundTiem() {
        return backGroundTiem;
    }

}
