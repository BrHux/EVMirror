package cn.ieway.evmirror.modules.about;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
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

import butterknife.BindView;
import butterknife.OnClick;
import cn.ieway.evmirror.R;
import cn.ieway.evmirror.application.BaseConfig;
import cn.ieway.evmirror.base.BaseActivity;
import cn.ieway.evmirror.modules.other.WebViewActivity;

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

        appVersion.setText("V"+BaseConfig.appVersionName);


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

    @OnClick({R.id.cl_grade_us,R.id.cl_check_version,R.id.cl_app_share,})
    public void onItemClick(View view){
        switch (view.getId()){
            case R.id.cl_check_version:{
                checkAppVersion();
                break;
            }
            case R.id.cl_app_share:{
                shareToOther();
                break;
            }
            case R.id.cl_grade_us:{
                starApp();
                break;
            }
            default:
                break;
        }
    }

    private void starApp() {
        Log.d(TAG, "starApp: ");
    }

    private void shareToOther() {
        Log.d(TAG, "shareToOther: ");
    }

    private void checkAppVersion() {
        Log.d(TAG, "checkAppVersion: ");

    }
}