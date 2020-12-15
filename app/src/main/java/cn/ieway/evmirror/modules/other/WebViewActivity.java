package cn.ieway.evmirror.modules.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import cn.ieway.evmirror.R;


public class WebViewActivity extends AppCompatActivity {

    WebView webView;
    String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(getColor(R.color.color_transparent_4c));
//        }
        Intent intent = getIntent();
        String result = intent.getStringExtra("url");
        title = intent.getStringExtra("title");

        initTitle(title);

        webView = findViewById(R.id.web_view);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        //支持App内部javascript交互
        webView.getSettings().setJavaScriptEnabled(true);
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        webView.getSettings().setLoadWithOverviewMode(true);
        //设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        //设置是否出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.loadUrl(result);
    }

    private void initTitle(String title) {
        TextView pageTitle = findViewById(R.id.tv_title);
        LinearLayout layout = findViewById(R.id.left_img);
        pageTitle.setText(title);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WebViewActivity.this.finish();
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
