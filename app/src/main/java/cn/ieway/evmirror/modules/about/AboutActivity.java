package cn.ieway.evmirror.modules.about;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.hjq.toast.ToastUtils;
import com.tamsiree.rxkit.view.RxToast;
import com.tamsiree.rxui.view.dialog.RxDialogSureCancel;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ieway.evmirror.R;
import cn.ieway.evmirror.application.BaseConfig;
import cn.ieway.evmirror.base.BaseActivity;
import cn.ieway.evmirror.entity.AppVersion;
import cn.ieway.evmirror.modules.other.WebViewActivity;
import cn.ieway.evmirror.modules.welcome.SplashActivity;
import cn.ieway.evmirror.net.CommonRequest;
import cn.ieway.evmirror.net.okhttp.CallBackUtil;
import cn.ieway.evmirror.net.util.DataUtils;
import cn.ieway.evmirror.util.CommonUtils;
import okhttp3.Call;
import okhttp3.Response;

public class AboutActivity extends BaseActivity {
    private static final String TAG = AboutActivity.class.getSimpleName();
    @BindView(R.id.tv_app_name)
    TextView appName;
    @BindView(R.id.tv_app_version)
    TextView appVersion;
    @BindView(R.id.tv_agreement)
    TextView tvAgreement;

    @BindView(R.id.cl_check_version)
    ConstraintLayout checkVersion;
    @BindView(R.id.cl_app_share)
    ConstraintLayout appShare;
    @BindView(R.id.cl_grade_us)
    ConstraintLayout gradeUs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    @Override
    protected void initView() {
        initTitle("关于我们");
        initAgreement();

        appVersion.setText("V" + BaseConfig.appVersionName);


    }

    private void initAgreement() {
        final SpannableStringBuilder agreement = new SpannableStringBuilder();
        String agreeStr = getString(R.string.ev_agreement);
        agreement.append(agreeStr);
        //设置部分文字点击事件
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(AboutActivity.this, WebViewActivity.class);
                intent.putExtra("title", "服务协议");
                intent.putExtra("url", getString(R.string.service_agreement));
                AboutActivity.this.startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        agreement.setSpan(clickableSpan, 0, agreeStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //配置给TextView
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        tvAgreement.append(agreement);
        tvAgreement.append(" 和 ");

        final SpannableStringBuilder policy = new SpannableStringBuilder();
        String policyStr = getString(R.string.ev_privacy_policy);
        policy.append(policyStr);
        //设置部分文字点击事件
        ClickableSpan policySpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(AboutActivity.this, WebViewActivity.class);
                intent.putExtra("title", "隐私政策");
                intent.putExtra("url", getString(R.string.privacy_policy));
                AboutActivity.this.startActivity(intent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setUnderlineText(false);
            }
        };
        policy.setSpan(policySpan, 0, policyStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //配置给TextView
        tvAgreement.setMovementMethod(LinkMovementMethod.getInstance());
        tvAgreement.append(policy);
    }

    private void initTitle(String title) {
        TextView pageTitle = findViewById(R.id.tv_title);
        LinearLayout layout = findViewById(R.id.left_img);
        pageTitle.setText(title);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutActivity.this.finish();
            }
        });
    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.cl_grade_us, R.id.cl_check_version, R.id.cl_app_share,})
    public void onItemClick(View view) {
        switch (view.getId()) {
            case R.id.cl_check_version: {
                checkAppVersion();
                break;
            }
            case R.id.cl_app_share: {
                shareToOther();
                break;
            }
            case R.id.cl_grade_us: {
                starApp();
                break;
            }
            default:
                break;
        }
    }

    private void starApp() {
        Log.d(TAG, "starApp: ");
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("market://details?id="+mContext.getPackageName()));
            startActivity(i);
        } catch (Exception e) {
            ToastUtils.show( "您的手机上没有安装应用市场");
            e.printStackTrace();
        }
    }

    private void shareToOther() {
        Log.d(TAG, "shareToOther: ");
    }

    private String html;
    private void checkAppVersion() {
        Log.d(TAG, "checkAppVersion: ");

        try {
            CommonRequest.checkAPPVersion(new CallBackUtil.CallBackString()  {
                @Override
                public void onFailure(Call call, Exception e) {
                   ToastUtils.show("未检测到新版本");
                }

                @Override
                public void onResponse(String response) {
                    if (CommonUtils.isNotJSONString(response)) {
                        ToastUtils.show("未检测到新版本");
                        return;
                    }
                    if (DataUtils.getErroCode(response) != 0) {
                        RxToast.error(DataUtils.getErroMsg(response));
                    } else {
                        String result = DataUtils.dealResponse(response, false);
                        AppVersion version = JSONObject.parseObject(result, AppVersion.class);
                        if (version == null) {
                            ToastUtils.show("未检测到新版本");
                            return;
                        }
                        String currentVer = version.getCurrent_version().replace(".", "");
                        String localVer = BaseConfig.appVersionName.replace(".", "");
                        int cv = Integer.parseInt(currentVer);
                        int lv = Integer.parseInt(localVer);

                        if (version.getForce_update() == 1) { //需强制更新
                            html = version.getDownload_url();
                            showTips(version.getEnd_error(), "下载新版本", 1);
                        } else if (cv > lv) {
                            showTips(version.getUpdate_brief(), "好的", 2);
                        } else {
                            ToastUtils.show("当前已是最新版本");
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }finally {

        }


    }

    private void showTips(String title, String actStr, final int type) {
        final RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(AboutActivity.this);
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
                        AboutActivity.this.startActivity(intent);
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