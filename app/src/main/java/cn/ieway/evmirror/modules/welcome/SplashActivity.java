package cn.ieway.evmirror.modules.welcome;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSONObject;
import com.tamsiree.rxkit.RxSPTool;
import com.tamsiree.rxkit.view.RxToast;
import com.tamsiree.rxui.view.dialog.RxDialogSureCancel;

import cn.ieway.evmirror.R;
import cn.ieway.evmirror.application.BaseConfig;
import cn.ieway.evmirror.application.Const;
import cn.ieway.evmirror.entity.AppVersion;
import cn.ieway.evmirror.modules.main.MainActivity;
import cn.ieway.evmirror.net.util.DataUtils;
import cn.ieway.evmirror.net.CommonRequest;
import cn.ieway.evmirror.net.okhttp.CallBackUtil;
import cn.ieway.evmirror.util.CommonUtils;
import okhttp3.Call;

public class SplashActivity extends AppCompatActivity {

    private long sysTimeTag;
    private String html;
    private long defaultDelay = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!this.isTaskRoot()) {
            Intent mainIntent = getIntent();
            String action = mainIntent.getAction();
            if (mainIntent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }

        setContentView(R.layout.activity_splash);
        sysTimeTag = System.currentTimeMillis();
        startNext();
    }

    private void checkVersion() {
        try {
            CommonRequest.checkAPPVersion(new CallBackUtil.CallBackString() {
                @Override
                public void onFailure(Call call, Exception e) {
                    enterHomeActivity();
                }

                @Override
                public void onResponse(String response) {
                    if (CommonUtils.isNotJSONString(response)) {
                        enterHomeActivity();
                        return;
                    }
                    if (DataUtils.getErroCode(response) != 0) {
                        RxToast.error(DataUtils.getErroMsg(response));
                        enterHomeActivity();
                    } else {
                        String result = DataUtils.dealResponse(response, false);
                        AppVersion version = JSONObject.parseObject(result, AppVersion.class);
                        if (version == null) {
                            enterHomeActivity();
                            return;
                        }
                        String currentVer = version.getCurrent_version().replace(".", "");
                        String localVer = BaseConfig.APP_VERSION.replace(".", "");
                        int cv = Integer.parseInt(currentVer);
                        int lv = Integer.parseInt(localVer);

                        if (version.getForce_update() == 1) { //需强制更新
                            html = version.getDownload_url();
                            showTips(version.getEnd_error(), "下载新版本", 1);
                        } else if (cv > lv) {
                            showTips(version.getUpdate_brief(), "好的", 2);
                        } else {
                            enterHomeActivity();
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            enterActivity();
        }
    }

    private void startNext() {
        long time = System.currentTimeMillis() - sysTimeTag;
        long delay = defaultDelay - time > 0 ? defaultDelay - time : 10;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                enterActivity();
            }
        }, delay);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                checkVersion();
            } else {
                finish();
                System.exit(0);
            }
        }
    }

    private void showClauseActivity() {
        Intent intent = new Intent(SplashActivity.this, ShowClauseActivity.class);
        startActivityForResult(intent, 1);
    }

    private void enterActivity() {
        // 如果没有展示用户协议和隐私政策则提示
        if (!RxSPTool.getBoolean(SplashActivity.this, Const.IS_AGREE_CLAUSE)) {
            showClauseActivity();
        } else {
            checkVersion();
        }
    }

    private void enterHomeActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }


    private void showTips(String title, String actStr, final int type) {
        final RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(SplashActivity.this);
        rxDialogSureCancel.setContent(title);
        rxDialogSureCancel.getContentView().setLinksClickable(true);
        rxDialogSureCancel.getContentView().setTextSize(16.0f);
        rxDialogSureCancel.setCancel(actStr);
        rxDialogSureCancel.getCancelView().setTextColor(ContextCompat.getColor(this, R.color.colorBlue));
        rxDialogSureCancel.getCancelView().setTextSize(14.0f);
        rxDialogSureCancel.getSureView().setTextSize(14.0f);
        rxDialogSureCancel.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (type) {
                    case 1: { //登录

                        if (!html.contains("http")) {
                            html = "https://" + html;
                        }
                        Uri uri = Uri.parse(html);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        SplashActivity.this.startActivity(intent);
                        SplashActivity.this.finish();
                        break;
                    }
                    case 2: {
                        enterHomeActivity();
                        break;
                    }
                }
                rxDialogSureCancel.cancel();
            }
        });

        if (type == 2) {
            rxDialogSureCancel.getSureView().setVisibility(View.GONE);
        } else {
            rxDialogSureCancel.setSure("取消");
            rxDialogSureCancel.setSureListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rxDialogSureCancel.cancel();
                    finish();
                }
            });
        }


        rxDialogSureCancel.show();
    }
}
