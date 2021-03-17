package cn.ieway.evmirror.modules.screenshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.tamsiree.rxkit.view.RxToast;
import com.tamsiree.rxui.view.dialog.RxDialogSureCancel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ieway.evmirror.R;
import cn.ieway.evmirror.base.BaseActivity;
import cn.ieway.evmirror.entity.ControlMessageEntity;
import cn.ieway.evmirror.entity.eventbus.NetWorkMessageEvent;
import cn.ieway.evmirror.floatwindow.FloatGuardService;
import cn.ieway.evmirror.modules.about.AboutActivity;
import cn.ieway.evmirror.util.CommonUtils;

import static cn.ieway.evmirror.application.MirrorApplication.sMe;

public class ScreenShareActivityNew extends BaseActivity {

    private String TAG = sMe.TAG + "shareAct";

    private static final int CAPTURE_PERMISSION_REQUEST_CODE = 100;
    public static final int EXITE_ACTIVITY = 101;
    private static int DISCONNECT_DIALOG = 1001;

    private Intent serviceIntent;
    private Point displaySize;
    private String socketUrl = "192.168.1.128";
    private String socketName = "";

    @BindView(R.id.tv_tips)
    TextView mTips;
    @BindView(R.id.tv_device_name)
    TextView mDeviceName;
    @BindView(R.id.iv_audio)
    ImageView mVoice;

    private int mMediaProjectionPermissionResultCode;
    private Intent mMediaProjectionPermissionResultData;
    private ControlHandler controlHandler;

    private int socketPort;
    private int requestPort;
    private String socketKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //  屏幕常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_screen_share);

        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView() {
        displaySize = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(displaySize);
        socketUrl = getIntent().getStringExtra("url");
        socketName = getIntent().getStringExtra("name");
        requestPort = getIntent().getIntExtra("port", 0);
        if (socketUrl == null || socketUrl.isEmpty()) {
            RxToast.error(getString(R.string.abnormal_device_parameters));
            exitActivity();
        }
        mTips.setText(R.string.request_mirror);
        mDeviceName.setText(socketName);

    }


    @Override
    protected void initData() {
        controlHandler = new ControlHandler();
        requestScreenMirror();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(NetWorkMessageEvent event) {
        switch (event.currentState) {
            case DISCONNECTED: {
                disConnection(getString(R.string.connection_disconnected_please_reconnect), getString(R.string.sure), 0);
                break;
            }
            case UNKNOWN: {
                if(event.getReason().isEmpty()){
                    disConnection(getString(R.string.remote_disconnect), getString(R.string.sure), 0);
                    break;
                }
                disConnection(event.getReason(), getString(R.string.sure), 1);
            }
            default: {
                break;
            }
        }
    }


    @Override
    public void onBackPressed() {
        showSureCancelAlertDialog(getString(R.string.exit_projection_screen), getString(R.string.exit), getString(R.string.cancle), 1);
    }

    @Override
    protected void onDestroy() {
        FloatGuardService.requestQuit(sMe);
        Settings.System.putInt(sMe.getContentResolver(), Settings.Global.WIFI_SLEEP_POLICY, Settings.Global.WIFI_SLEEP_POLICY_NEVER_WHILE_PLUGGED);
        stopService(new Intent(this, ScreenShareService.class));
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onDestroy();
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_PERMISSION_REQUEST_CODE) {
            mMediaProjectionPermissionResultCode = resultCode;
            mMediaProjectionPermissionResultData = data;
            startScreenCapture(mMediaProjectionPermissionResultData, mMediaProjectionPermissionResultCode);
            if (XXPermissions.isGrantedPermission(sMe, Permission.SYSTEM_ALERT_WINDOW) && Settings.canDrawOverlays(sMe)) {
                FloatGuardService.requestShow(sMe, "");
            }
        } else if (requestCode == EXITE_ACTIVITY && resultCode == EXITE_ACTIVITY) {

        } else if (requestCode == DISCONNECT_DIALOG) {
            if (resultCode == 0){
                exitActivity();
            }

        }
    }

    @OnClick({R.id.iv_exit, R.id.iv_audio, R.id.iv_about})
    public void onClock(View view) {
        switch (view.getId()) {
            case R.id.iv_exit: {
                showSureCancelAlertDialog(getString(R.string.exit_projection_screen), getString(R.string.exit), getString(R.string.cancle), 1);
                break;
            }
            case R.id.iv_audio: {
                break;
            }
            case R.id.iv_about: {
                Intent intent = new Intent();
                intent.setClass(this, AboutActivity.class);
                intent.setPackage(this.getPackageName());
                startActivity(intent);
                break;
            }
        }
    }


    private ControlSocketThread socketThread;

    /**
     * 创建投屏请求
     */
    private void requestScreenMirror() {
        socketThread = new ControlSocketThread(socketUrl, requestPort, controlHandler);
        socketThread.start();
    }


    private void dealAudioTrack(boolean audioTrack) {
//        if (audioTrack) {
//            boolean hasPermission = XXPermissions.isGrantedPermission(this, Permission.RECORD_AUDIO);
//            if (!hasPermission) {
//                XXPermissions.with(this).permission(Permission.RECORD_AUDIO).request(new OnPermissionCallback() {
//                    @Override
//                    public void onGranted(List<String> permissions, boolean all) {
//                        setAudioTrack(audioTrack);
//                    }
//
//                    @Override
//                    public void onDenied(List<String> permissions, boolean never) {
//                        showSureCancelAlertDialog(getString(R.string.request_audio), getString(R.string.authorization), getString(R.string.cancle), never ? 3 : 2);
//                    }
//                });
//                return;
//            }
//        }
//        setAudioTrack(audioTrack);
    }

    private void setAudioTrack(boolean audioTrack) {
        boolean canUse = CommonUtils.validateMicAvailability();
        if (!canUse) {
            RxToast.error(getString(R.string.microphone_is_occupied));
            return;
        }
    }

    /*录屏请求*/
    public void createScreenCaptureIntent() {
        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);
    }


    /**
     * 启动截屏服务
     *
     * @param mMediaProjectionPermissionResultData
     * @param mMediaProjectionPermissionResultCode
     */
    public void startScreenCapture(Intent mMediaProjectionPermissionResultData, int mMediaProjectionPermissionResultCode) {
        if (mMediaProjectionPermissionResultCode == Activity.RESULT_OK && mMediaProjectionPermissionResultData != null) {
            Settings.System.putInt(sMe.getContentResolver(), Settings.Global.WIFI_SLEEP_POLICY, Settings.Global.WIFI_SLEEP_POLICY_NEVER);

            serviceIntent = new Intent(mContext, ScreenShareService.class);
            serviceIntent.putExtra(ScreenShareService.EXTRA_CDM, ScreenShareService.POP_START);
            serviceIntent.putExtra("md_code", mMediaProjectionPermissionResultCode);
            serviceIntent.putExtra("md_data", mMediaProjectionPermissionResultData);
            serviceIntent.putExtra("socket_point", socketPort);
            serviceIntent.putExtra("socket_url", socketUrl);
            serviceIntent.putExtra("socket_key", socketKey);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mContext.startForegroundService(serviceIntent);
            } else {
                mContext.startService(serviceIntent);
            }
        } else {
            showSureCancelAlertDialog(getString(R.string.cancelled_screen_request), getString(R.string.continue_to), getString(R.string.exit_for_screen), 4);
        }
    }

    /**
     * 界面对话框（双按钮）
     *
     * @param content
     * @param cancel
     * @param sure
     * @param type    1：退出界面；2：声音通道设置 3：打开权限详情页面；4：截屏请求 5:wifi断开连接
     */
    private void showSureCancelAlertDialog(String content, String cancel, String sure, int type) {
        if (ScreenShareActivityNew.this.isFinishing() || ScreenShareActivityNew.this.isDestroyed())
            return;
        RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(ScreenShareActivityNew.this);
        rxDialogSureCancel.setContent(content);
        rxDialogSureCancel.getContentView().setLinksClickable(true);
        rxDialogSureCancel.getContentView().setTextSize(16.0f);
        rxDialogSureCancel.setCancel(cancel);
        rxDialogSureCancel.getCancelView().setTextColor(ContextCompat.getColor(this, R.color.colorBlue));
        rxDialogSureCancel.getCancelView().setTextSize(14.0f);
        rxDialogSureCancel.getSureView().setTextSize(14.0f);
        rxDialogSureCancel.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type) {
                    case 1: {
                        //退出界面(取消投屏)
                        exitActivity();
                        break;
                    }
                    case 2: {
                        //打开\关闭语音通道
                        dealAudioTrack(true);
                        break;
                    }
                    case 3: {
                        //跳转至应用详情
                        XXPermissions.startApplicationDetails(mContext);
                        break;
                    }
                    case 4: {
                        //录屏请求(投屏初始化)
                        createScreenCaptureIntent();
                        break;
                    }
                }
                rxDialogSureCancel.cancel();
            }
        });

        rxDialogSureCancel.setSure(sure);
        rxDialogSureCancel.setSureListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type) {
                    case 4: {
                        //退出界面(取消投屏)
                        exitActivity();
                    }
                }
                rxDialogSureCancel.cancel();
            }
        });
        rxDialogSureCancel.show();
    }


    private int dialogCount = 0;

    private void disConnection(String content, String sure, int type) {
        if (ScreenShareActivityNew.this.isFinishing() || ScreenShareActivityNew.this.isDestroyed())
            return;
        if (dialogCount > 0) return;
        dialogCount++;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && sMe.isBackGround()) {
            RxToast.warning(content, 4000);
        }
        Intent intent = new Intent(this, DisConnectDialog.class);
        intent.putExtra("content", content);
        intent.putExtra("sure", sure);
        intent.putExtra("type", type);
        startActivityForResult(intent, DISCONNECT_DIALOG);
    }


    private void exitActivity() {
        if (controlHandler != null) {
            controlHandler.sendEmptyMessage(HANDLER_STOP);
        }
        ScreenShareActivityNew.this.finish();
    }


    public static final int HANDLER_START = 2001;
    public static final int HANDLER_STOP = 2002;

    class ControlHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_START: {
                    ControlMessageEntity.DataBean bean = (ControlMessageEntity.DataBean) msg.obj;

                    if (bean == null) {
                        return;
                    }

                    socketPort = bean.getPort();
                    socketKey = bean.getKey();

                    createScreenCaptureIntent();
                    break;
                }
                case HANDLER_STOP: {
                    if (socketThread == null) return;
                    if (socketThread.isInterrupted()) return;

                    new Thread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        socketThread.sendSocketMsg("1.0", 2, null);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    ).start();
                    break;
                }
            }
        }
    }


}