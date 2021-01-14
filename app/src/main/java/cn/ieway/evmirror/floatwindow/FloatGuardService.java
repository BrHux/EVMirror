package cn.ieway.evmirror.floatwindow;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;


import cn.ieway.evmirror.R;

import static cn.ieway.evmirror.application.MirrorApplication.sMe;


public class FloatGuardService extends Service {

    public static final String EXTRA_CDM = "extra_cmd";
    public static final int POP_START = 1;
    public static final int POP_QUIT = 2;
    public static final int POP_UPDATE = 3;
    public static final int POP_CAM = 4;
    private static final String VIDEO_PATH = "path";
    public static boolean isRunning = false;

    private Context mContext;

    private int mScreenWidth;
    private int mScreenHeight;
    private static DisplayMetrics mDisplayMetrics = null;

    private ConstraintLayout mMenu;

    public FloatGuardService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 启动Service
     */
    public static boolean requestShow(Context context, String filePatch) {
        if (context == null){
            context = sMe;
        }

        if(isRunning) return  false;

        Intent intent = new Intent(context, FloatGuardService.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_CDM, POP_START);
        intent.putExtra(VIDEO_PATH, filePatch);
        context.startService(intent);
        return true;
    }

    /**
     * 退出Service
     */
    public static boolean requestQuit(Context context) {
        if (context == null){
            context = sMe;
        }


        Intent intent = new Intent(context,  FloatGuardService.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_CDM, POP_QUIT);
        context.startService(intent);
        return true;
    }

    /**
     * 退出Service
     */
    public static boolean requestCamera(Context context) {
        if (context == null){
            context = sMe;
        }
        Intent intent = new Intent(context,  FloatGuardService.class);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.putExtra(EXTRA_CDM, POP_CAM);
        context.startService(intent);
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics outMetrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getRealMetrics(outMetrics);
        mScreenWidth = 1;
        mScreenHeight = 1;

        initView();
        addView();
        isRunning = true;
    }

    /**
     * 初始化
     */
    private void initView() {
        mMenu = (ConstraintLayout) LayoutInflater.from(this).inflate(R.layout.float_capture_view, null);

//        mMenu.setPadding((int)(mScreenWidth*0.1),0,(int)(mScreenWidth*0.1),0);
    }


    private WindowManager mWindowManager = null;
    private WindowManager.LayoutParams mWindowManagerParams = null;

    private void addView() {
        mWindowManagerParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//8.0+
            mWindowManagerParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            mWindowManagerParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        //布局flags
        mWindowManagerParams.flags =  WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        mWindowManagerParams.gravity = Gravity.TOP|Gravity.LEFT; //set Suspension window to left and top
        mWindowManagerParams.width = mScreenWidth;
        mWindowManagerParams.height = mScreenHeight;
        mWindowManagerParams.format = PixelFormat.TRANSPARENT;
        mMenu.setBackgroundColor(ContextCompat.getColor(this,R.color.transparent));
        mWindowManager.addView(mMenu, mWindowManagerParams);
        mMenu.setVisibility(View.GONE);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_STICKY;
        int order = intent.getIntExtra(EXTRA_CDM, -1);

        switch (order){
            case POP_QUIT:{
                this.stopSelf();
                break;
            }
            case POP_START:{
                attach();
                break;
            }
        }

        return START_STICKY;
    }

    private void attach() {
        if (!XXPermissions.isGrantedPermission(mContext, Permission.SYSTEM_ALERT_WINDOW)) {
            ToastUtils.show("getString(R.string.content_save_video_result)");
            detach();
            return;
        }
        mMenu.setVisibility(View.VISIBLE);
    }

    private void detach() {
        Intent intent = new Intent(sMe,  FloatGuardService.class);
        stopService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        release();
        isRunning = false;
    }

    private void release() {
        mWindowManager.removeView(mMenu);
        mWindowManager = null;
    }

}
