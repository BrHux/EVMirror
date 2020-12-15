package cn.ieway.evmirror.modules.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hjq.toast.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ieway.evmirror.R;
import cn.ieway.evmirror.base.BaseActivity;
import cn.ieway.evmirror.modules.other.WebViewActivity;
import cn.ieway.evmirror.modules.welcome.ShowClauseActivity;

public class HelpTipsActivity extends BaseActivity {

    @BindView(R.id.tv_download_url)
    TextView tvUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_tips);
    }

    @Override
    protected void initView() {

        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);

        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;   //高度设置
//        p.width = (int) (displaySize.x * 0.8);    //宽度设置为屏幕的0.8
        p.width = WindowManager.LayoutParams.MATCH_PARENT;     //宽度设置为屏幕的0.8
        p.gravity = Gravity.CENTER;
        getWindow().setAttributes(p);

    }

    @Override
    protected void initData() {

        final SpannableStringBuilder text = new SpannableStringBuilder();
        text.append(getString(R.string.download_url));
        tvUrl.setText(text);

        final SpannableStringBuilder style = new SpannableStringBuilder();
        String url = "iewayaskdj.cn";
        //设置文字
        style.append(url);
        //设置部分文字点击事件
//        ClickableSpan clickableSpan = new ClickableSpan() {
//            @Override
//            public void onClick(View widget) {
////                Intent intent = new Intent(HelpTipsActivity.this, WebViewActivity.class);
////                intent.putExtra("title", "服务协议");
////                intent.putExtra("url", getString(R.string.service_agreement));
////                HelpTipsActivity.this.startActivity(intent);
//                ToastUtils.show("clickableSpan");
//            }
//        };
//        style.setSpan(clickableSpan, 0, url.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //设置部分文字颜色
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorBlue));
        style.setSpan(foregroundColorSpan, 0, url.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //配置给TextView
        tvUrl.setMovementMethod(LinkMovementMethod.getInstance());
        tvUrl.append(style);
    }


    @OnClick({R.id.tv_ok, R.id.tv_download_url})
    public void onBtnCick(View view) {
        switch (view.getId()) {
            case R.id.tv_ok: {
                finish();
                break;
            }
            case R.id.tv_download_url: {
                ToastUtils.show("iewayaskdj");
                break;
            }
            default:
                break;
        }
    }
}