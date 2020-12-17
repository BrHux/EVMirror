package cn.ieway.evmirror.modules.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import cn.ieway.evmirror.R;
import cn.ieway.evmirror.base.BaseActivity;
import cn.ieway.evmirror.modules.about.AboutActivity;
import cn.ieway.evmirror.modules.link.zxing.CaptureFragment;

public class ScanningActivity extends BaseActivity{

//    @BindView(R.id.fragment_container)
    View fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);
    }

    @Override
    protected void initView() {
        initTitle("");
        fragment = findViewById(R.id.fragment_container);
    }
    private void initTitle(String title) {
        TextView pageTitle = findViewById(R.id.tv_title);
        LinearLayout layout = findViewById(R.id.left_img);
        pageTitle.setText(title);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanningActivity.this.finish();
            }
        });
    }

    @Override
    protected void initData() {
    }


    /**
     * 客户端连接预处理
     * @param url
     */
    public boolean checkConfiguration(String url){
        showHUD(true,"正在检测连接配置.."+url);

        return true;
    }


}