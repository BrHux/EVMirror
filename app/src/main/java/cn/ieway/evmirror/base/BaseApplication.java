package cn.ieway.evmirror.base;

import android.app.Application;
import android.content.Context;

import com.hjq.toast.ToastUtils;
import com.tamsiree.rxkit.RxTool;

import cn.ieway.evmirror.application.BaseConfig;
import cn.ieway.evmirror.application.MirrorApplication;
import cn.ieway.evmirror.util.AppFrontBackHelper;

/**
 * FileName: BaseApplication
 * Author: Admin
 * Date: 2020/12/8 15:23
 * Description:
 */
public class BaseApplication extends Application {


    private boolean isBackGround; //界面是否后台运行
    private Long backGroundTiem;//界面进入后台的时间

    public static BaseApplication sMe;

    @Override
    public void onCreate() {
        super.onCreate();
        sMe = BaseApplication.this;

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
    }

    /**
     * 应用后台运行监听
     */
    private void initFrontBackHelper() {
        AppFrontBackHelper helper = new AppFrontBackHelper();
        helper.register(BaseApplication.this, new AppFrontBackHelper.OnAppStatusListener() {
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
