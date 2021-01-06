package cn.ieway.evmirror.application;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.view.WindowManager;

import com.hjq.toast.ToastUtils;
import com.tamsiree.rxkit.RxTool;
import com.umeng.commonsdk.UMConfigure;

import java.util.List;

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
    private int video_fps = 30;
    private WindowManager windowManager;
    public int screenWidth = 1080;
    public int screenHeight = 1920;

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

    private void initScreenSize(MirrorApplication mirrorApplication) {
        Point mPoint = new Point();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealSize(mPoint);

        screenWidth = mPoint.x;
        screenHeight = mPoint.y;

//        if(mPoint.x>mPoint.y){
//            if(mPoint.y > 720){
//                screenHeight = 720;
//                screenWidth = mPoint.x*screenHeight/mPoint.y;
//                return;
//            }
//            screenWidth = 720;
//            screenHeight = mPoint.x*screenWidth/mPoint.y;
//        }else {
//            if(mPoint.x > 720){
//                screenWidth = 720;
//                screenHeight = mPoint.y*screenWidth/mPoint.x;
//                return;
//            }
//            screenHeight = 720;
//            screenWidth = mPoint.y*screenHeight/mPoint.x;
//        }
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
