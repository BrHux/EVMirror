package cn.ieway.evmirror.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.hjq.toast.ToastUtils;
import com.tamsiree.rxkit.RxTool;
import com.umeng.commonsdk.UMConfigure;

import java.util.List;

import cn.ieway.evmirror.util.AppFrontBackHelper;

/**
 * FileName: MirrorApplication
 * Author: Admin
 * Date: 2020/12/8 15:22
 * Description:
 */
public class MirrorApplication extends Application {
    public String TAG = "ev_mirror_";
    private boolean isBackGround; //界面是否后台运行
    private Long backGroundTiem;//界面进入后台的时间
    public boolean isWlanOpen = true;

    public static MirrorApplication sMe;
    private int video_fps = 30;
    private WindowManager windowManager;
    private int W = 720;
    private int H = 1080;

    public int biteRate = 3*Const.VIDEO_BITRATE;

    public int screenWidth = W;
    public int screenHeight = H;
    public int videoDpi = 360;

    public int getVideo_fps() {
        return video_fps;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sMe = MirrorApplication.this;
        initTools(this);
        initFrontBackHelper();
        initScreenSize(this);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Point outSize = new Point();
        windowManager.getDefaultDisplay().getRealSize(outSize);
        setScreenSize(outSize);
    }

    private void initScreenSize(MirrorApplication mirrorApplication) {
        try {
            //屏幕尺寸
            Point mPoint = new Point();
            windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getRealSize(mPoint);
            //屏幕密度
            DisplayMetrics displayMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
            videoDpi = displayMetrics.densityDpi;
            if (mPoint.x > mPoint.y) {
                if (mPoint.y > screenWidth) {
                    screenHeight = W;
                    screenWidth = mPoint.x * H / mPoint.y;
                } else {
                    //                screenWidth = screenWidth;
                    screenHeight = mPoint.x * W / mPoint.y;
                }
            } else {
                if (mPoint.x > screenWidth) {
//                    screenWidth = screenWidth;
                    screenHeight = mPoint.y * W / mPoint.x;
                } else {
                    screenHeight = W;
                    screenWidth = mPoint.y * H / mPoint.x;
                }
            }

            //奇数判断
            if ((screenHeight & 1) != 0) {
                screenHeight = screenHeight + 1;
            }
            if ((screenWidth & 1) != 0) {
                screenWidth = screenWidth + 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /** 屏幕横竖屏切换
     * @param mPoint
     */
    public void setScreenSize(Point mPoint) {
        if ((mPoint.x > mPoint.y && screenWidth < screenHeight) ||
                (mPoint.y > mPoint.x && screenHeight < screenWidth)
        ) {
            int tem = screenWidth;
            screenWidth = screenHeight;
            screenHeight = tem;
        }
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

    public void actionInten(Class<?> cls) {
        actionInten(cls, 0);
    }

    public void actionInten(Class<?> cls, int type) {
        //获取ActivityManager
        ActivityManager mAm = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //获得当前运行的task
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
        for (ActivityManager.RunningTaskInfo rti : taskList) {
            //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
            if (rti.topActivity.getPackageName().equals(getPackageName())) {
                mAm.moveTaskToFront(rti.id, 0);
                Intent resultIntent = new Intent(this, cls);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                resultIntent.putExtra("type", type);
                startActivity(resultIntent);
                return;
            }
        }
        //若没有找到运行的task，用户结束了task或被系统释放，则重新启动mainactivity
        Intent resultIntent = new Intent(this, cls);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        resultIntent.putExtra("type", type);
        startActivity(resultIntent);
    }
}
