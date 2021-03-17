package cn.ieway.evmirror.modules.welcome;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.hjq.toast.ToastUtils;
import com.tamsiree.rxkit.RxSPTool;

import cn.ieway.evmirror.R;
import cn.ieway.evmirror.modules.other.WebViewActivity;
import cn.ieway.evmirror.application.Const;


public class ShowClauseActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    TextView tv_agree, tv_text_desc, tv_exit, tv_text_content;
    LinearLayout checkBoxLayout;
    CheckBox checkbox;
    public static int STATEMENT = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_clause);

        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);

        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;   //高度设置为屏幕的1.0
        p.width = (int) (displaySize.x * 0.85);    //宽度设置为屏幕的0.8
        p.gravity = Gravity.CENTER;
        getWindow().setAttributes(p);

        boolean isAgree = RxSPTool.getBoolean(ShowClauseActivity.this, Const.IS_AGREE_CLAUSE);
        if (isAgree) {
            finish();
        }
        initData();
    }

    private void initData() {
        tv_agree = findViewById(R.id.tv_agree);
        tv_agree.setOnClickListener(this);
        tv_text_content = findViewById(R.id.tv_text_content);
        tv_text_desc = findViewById(R.id.tv_text_desc);
        tv_exit = findViewById(R.id.tv_exit);
        checkBoxLayout = findViewById(R.id.ll_checkbox);
        checkbox = findViewById(R.id.checkbox);


        setAgreementClickable(checkbox.isChecked());


        tv_text_content.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv_text_content.setText(
                "欢迎使用EV投屏 ！\n" +
                        "\n" +
                        "为了保障EV投屏的正常运行，我们需要授权以下权限。请放心，我们会严格保护您的隐私安全。\n" +
                        "\n" +
                        "相机功能，用于二维码识别；\n" +
                        "悬浮窗功能，用于保证功能正常运行；\n" +
                        "读取部分手机信息，用于区别设备的唯一性；\n" +
                        "屏幕录制，仅用于局域网投屏，不会上传或存储屏幕信息；\n" +
                        "定位权限，仅用于获取Wifi名称，不会上传或存储位置信息；\n" +
                        "\n" +
                        "以上权限都是系统公开权限，您可以参考《服务协议》和《隐私政策》。"
        );

        checkbox.setOnCheckedChangeListener(this);

        checkBoxLayout.setOnClickListener(this::onClick);
        tv_exit.setOnClickListener(this::exitClick);

        final SpannableStringBuilder text = new SpannableStringBuilder();
        text.append("我已阅读并同意");
        tv_text_desc.setText(text);
        final SpannableStringBuilder style = new SpannableStringBuilder();
        //设置文字
        style.append("服务协议与隐私政策。");
        //设置部分文字点击事件
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(ShowClauseActivity.this, WebViewActivity.class);
                intent.putExtra("title", "服务协议");
                intent.putExtra("url", getString(R.string.service_agreement));
                ShowClauseActivity.this.startActivity(intent);
            }
        };
        style.setSpan(clickableSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置部分文字颜色
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent));
//        URLSpan foregroundColorSpan = new URLSpan("web:https://www.ieway.cn/avow/service_agreement.html");
        style.setSpan(foregroundColorSpan, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //设置部分文字点击事件
        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(ShowClauseActivity.this, WebViewActivity.class);
                intent.putExtra("title", "隐私政策");
                intent.putExtra("url", getString(R.string.privacy_policy));
                ShowClauseActivity.this.startActivity(intent);
            }
        };
        style.setSpan(clickableSpan2, 5, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置部分文字颜色
        ForegroundColorSpan foregroundColorSpan2 = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorAccent));
        style.setSpan(foregroundColorSpan2, 5, 9, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        //配置给TextView
        tv_text_desc.setMovementMethod(LinkMovementMethod.getInstance());
        tv_text_desc.append(style);
    }

    private void exitClick(View view) {
        if (view.getId() == R.id.tv_exit) {
            RxSPTool.putBoolean(ShowClauseActivity.this, Const.IS_AGREE_CLAUSE, false);
            setResult(0);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (doubleClickExit()) {
            RxSPTool.putBoolean(ShowClauseActivity.this, Const.IS_AGREE_CLAUSE, false);
            exitAPP();
        } else {
            Toast.makeText(this, "再次点击返回退出应用", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_agree:{
                if (checkbox.isChecked()){
                    RxSPTool.putBoolean(ShowClauseActivity.this, Const.IS_AGREE_CLAUSE, true);
                    setResult(1);
                    finish();
                }else {
//                    ToastUtils.show("请勾选并同意《服务协议》和《隐私政策》");
                }

                break;
            }
        }
    }

    private long cureent;

    private boolean doubleClickExit() {
        if ((System.currentTimeMillis() - cureent) > 2000) {
            Toast.makeText(ShowClauseActivity.this, "再按一次退出", Toast.LENGTH_LONG);
            cureent = System.currentTimeMillis();
            return false;
        }
        return true;
    }

    private void exitAPP() {
        this.finish();
        System.exit(0);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.checkbox:{
                setAgreementClickable(isChecked);
            }
        }
    }

    private void setAgreementClickable(boolean checked) {
        if (checked){
            tv_agree.setTextColor(ContextCompat.getColor(this,R.color.White));
            tv_agree.setBackground(ContextCompat.getDrawable(this,R.drawable.shap_blue_10dp_bg));
        }else {
            tv_agree.setTextColor(ContextCompat.getColor(this,R.color.color_text_99));
            tv_agree.setBackground(ContextCompat.getDrawable(this,R.drawable.shap_lightgray_10dp_bg));
        }
    }
}
