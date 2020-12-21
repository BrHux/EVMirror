package cn.ieway.evmirror.modules.screenshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.tamsiree.rxkit.view.RxToast;
import com.tamsiree.rxui.view.dialog.RxDialogSureCancel;

import org.webrtc.EglBase;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ieway.evmirror.R;
import cn.ieway.evmirror.base.BaseActivity;
import cn.ieway.evmirror.modules.about.AboutActivity;
import cn.ieway.evmirror.util.CommonUtils;
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
    @BindView(R.id.tv_device_name)
    TextView mDeviceName;
    @BindView(R.id.iv_audio)
    ImageView mVoice;

    private int mMediaProjectionPermissionResultCode;
    private Intent mMediaProjectionPermissionResultData;
    private PeerConnectionParameters params;
    private Intent serviceIntent;
    private String socketName = "";

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
        socketName = getIntent().getStringExtra("name");
        if (socketUrl == null || socketUrl.isEmpty()) {
            RxToast.error(getString(R.string.abnormal_device_parameters));
            finish();
        }

//        initTitle("EV投屏");
        mTips.setText(R.string.request_mirror);
        mDeviceName.setText(socketName);
    }

    private void initTitle(String title) {
        TextView pageTitle = findViewById(R.id.tv_title);
        LinearLayout layout = findViewById(R.id.left_img);
        pageTitle.setText(title);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(getString(R.string.exit_projection_screen), getString(R.string.exit), getString(R.string.cancle), 1);
            }
        });
    }

    @Override
    protected void initData() {
        webRtcListener = new WebRtcListener();
        eglBaseContext = EglBase.create().getEglBaseContext();
        params = new PeerConnectionParameters(
                true, true, false, displaySize.x, displaySize.y,
                VIDEO_FPS, 1, VIDEO_CODEC_VP9, true, 1,
                AUDIO_CODEC_OPUS, true);
        createScreenCaptureIntent();
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(getString(R.string.exit_projection_screen), getString(R.string.exit), getString(R.string.cancle), 1);
    }

    @Override
    protected void onDestroy() {
        if (webRtcClient != null) {
            webRtcClient.onDestroy();
            webRtcClient = null;
        }
        stopService(new Intent(this, ScreenShareService.class));
        super.onDestroy();

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

    @OnClick({R.id.iv_exit, R.id.iv_audio, R.id.iv_about})
    public void onClock(View view) {
        switch (view.getId()) {
            case R.id.iv_exit: {
                showAlertDialog(getString(R.string.exit_projection_screen), getString(R.string.exit), getString(R.string.cancle), 1);
                break;
            }
            case R.id.iv_audio: {
//                RxToast.info("音频");
                if (webRtcClient == null) return;
                dealAudioTrack(!webRtcClient.isAudioTrack());
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

    private void dealAudioTrack(boolean audioTrack) {
        if (audioTrack) {
            boolean hasPermission = XXPermissions.isGrantedPermission(this, Permission.RECORD_AUDIO);
            if (!hasPermission) {
                XXPermissions.with(this).permission(Permission.RECORD_AUDIO).request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        setAudioTrack(audioTrack);
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        showAlertDialog(getString(R.string.request_audio), getString(R.string.authorization), getString(R.string.cancle), never ? 3 : 2);
                    }
                });
                return;
            }
        }
        setAudioTrack(audioTrack);
    }

    private void setAudioTrack(boolean audioTrack) {
        boolean canUse = CommonUtils.validateMicAvailability();
        if (!canUse) {
            RxToast.error(getString(R.string.microphone_is_occupied));
            return;
        }

        if (webRtcClient != null) {
            webRtcClient.setAudioTrack(audioTrack);
        }
        if (webRtcClient.isAudioTrack()) {
            mVoice.setImageResource(R.drawable.selector_voice_op);
        } else {
            mVoice.setImageResource(R.drawable.selector_silence_voice_op);
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
                mTips.setText(getString(R.string.screen_mirror));
            } else {
                RxToast.error(getString(R.string.retry));
                initData();
            }
        } else {
            showAlertDialog(getString(R.string.cancelled_screen_request), getString(R.string.continue_to), getString(R.string.exit_for_screen), 4);
        }
    }


    /**
     * 界面对话框
     *
     * @param content
     * @param cancel
     * @param sure
     * @param type    1：退出界面；2：声音通道设置 3：打开权限详情页面；4：截屏请求
     */
    private void showAlertDialog(String content, String cancel, String sure, int type) {
        RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(ScreenShareActivity.this);
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
                        finish();
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
                        finish();
                    }
                }
                rxDialogSureCancel.cancel();
            }
        });
        rxDialogSureCancel.show();
    }
}