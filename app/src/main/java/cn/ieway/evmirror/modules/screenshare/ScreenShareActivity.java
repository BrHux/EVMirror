package cn.ieway.evmirror.modules.screenshare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.tamsiree.rxkit.view.RxToast;
import com.tamsiree.rxui.view.dialog.RxDialogSureCancel;

import org.webrtc.EglBase;

import java.util.Objects;

import butterknife.BindView;
import cn.ieway.evmirror.R;
import cn.ieway.evmirror.application.MirrorApplication;
import cn.ieway.evmirror.base.BaseActivity;
import cn.ieway.evmirror.modules.about.AboutActivity;
import cn.ieway.evmirror.util.LogUtil;
import cn.ieway.evmirror.webrtcclient.WebRtcClient;
import cn.ieway.evmirror.webrtcclient.entity.PeerConnectionParameters;

import static cn.ieway.evmirror.application.MirrorApplication.webRtcClient;

public class ScreenShareActivity extends BaseActivity {
    private static final String VIDEO_CODEC_VP9 = "VP9";
    private static final String AUDIO_CODEC_OPUS = "opus";
    private static final int CAPTURE_PERMISSION_REQUEST_CODE = 100;
    public static final int EXITE_ACTIVITY = 101;
    private int VIDEO_FPS = 24;

    private String socketUrl;
    private Point displaySize;
    private EglBase.Context eglBaseContext;
    private WebRtcListener webRtcListener;


    @BindView(R.id.tv_tips)
    TextView mTips;
    private int mMediaProjectionPermissionResultCode;
    private Intent mMediaProjectionPermissionResultData;
    private PeerConnectionParameters params;
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        屏幕常亮
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_screen_share);
    }

    @Override
    protected void initView() {
        displaySize = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(displaySize);

        socketUrl = getIntent().getStringExtra("url");
        if (socketUrl == null || socketUrl.isEmpty()) {
            RxToast.error("设备参数异常请重试！");
            finish();
        }

        initTitle("");
        mTips.setText("正在请求投屏");
    }

    private void initTitle(String title) {
        TextView pageTitle = findViewById(R.id.tv_title);
        LinearLayout layout = findViewById(R.id.left_img);
        pageTitle.setText(title);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setExiteActivity();
            }
        });
    }

    @Override
    protected void initData() {
        webRtcListener = new WebRtcListener();
        eglBaseContext = EglBase.create().getEglBaseContext();
        params = new PeerConnectionParameters(
                true, false, false, displaySize.x, displaySize.y,
                VIDEO_FPS, 1, VIDEO_CODEC_VP9, true, 1,
                AUDIO_CODEC_OPUS, true);
//        webRtcClient = new WebRtcClient(webRtcListener, socketUrl, params, eglBaseContext, this);
//        webRtcClient.initLocalMs();
//        remoteView.setEnableHardwareScaler(true);
        createScreenCaptureIntent();
    }

    @Override
    public void onBackPressed() {
        setExiteActivity();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webRtcClient != null) {
            webRtcClient.onDestroy();
        }
        stopService(new Intent(this, ScreenShareService.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_PERMISSION_REQUEST_CODE) {
            mMediaProjectionPermissionResultCode = resultCode;
            mMediaProjectionPermissionResultData = data;
            startScreenCapture(mMediaProjectionPermissionResultData, mMediaProjectionPermissionResultCode);
        } else if (requestCode == EXITE_ACTIVITY && resultCode == EXITE_ACTIVITY) {

        }
    }

    /*录屏请求*/
    public void createScreenCaptureIntent() {
        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager) getApplication().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);
    }

    MediaProjection mMediaProjection;
    MediaProjectionManager mMediaProjectionManager;

    public void startScreenCapture(Intent mMediaProjectionPermissionResultData, int mMediaProjectionPermissionResultCode) {
        if (mMediaProjectionPermissionResultCode == Activity.RESULT_OK && mMediaProjectionPermissionResultData != null) {
            webRtcClient = new WebRtcClient(webRtcListener, socketUrl, params, eglBaseContext, this);
            webRtcClient.initLocalMs();
            if (webRtcClient != null) {
                serviceIntent = new Intent(mContext, ScreenShareService.class);
                serviceIntent.putExtra("code", mMediaProjectionPermissionResultCode);
                serviceIntent.putExtra("data", mMediaProjectionPermissionResultData);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mContext.startForegroundService(serviceIntent);
                } else {
                    mContext.startService(serviceIntent);
                }
            } else {
                RxToast.error("启动共享失败,请重新启动");
                initData();
            }
        } else {

        }
    }


    private void setExiteActivity() {
        if (ScreenShareActivity.this.isDestroyed() || ScreenShareActivity.this.isFinishing()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        builder.setTitle("提示");
        builder.setMessage("您即将退出投屏");
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

//        RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(ScreenShareActivity.this);
//        rxDialogSureCancel.setContent("您即将退出投屏");
//        rxDialogSureCancel.getContentView().setLinksClickable(true);
//        rxDialogSureCancel.getContentView().setTextSize(16.0f);
//        rxDialogSureCancel.setCancel("退出");
//        rxDialogSureCancel.getCancelView().setTextColor(ContextCompat.getColor(this, R.color.colorBlue));
//        rxDialogSureCancel.getCancelView().setTextSize(14.0f);
//        rxDialogSureCancel.getSureView().setTextSize(14.0f);
//        rxDialogSureCancel.setCancelListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rxDialogSureCancel.cancel();
//            }
//        });
//
//        rxDialogSureCancel.setSure("取消");
//        rxDialogSureCancel.setSureListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rxDialogSureCancel.cancel();
//                finish();
//            }
//        });
//        rxDialogSureCancel.show();
    }
}