package cn.ieway.evmirror.modules.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tamsiree.rxkit.view.RxToast;

import butterknife.BindView;
import cn.ieway.evmirror.R;
import cn.ieway.evmirror.application.MirrorApplication;
import cn.ieway.evmirror.base.BaseActivity;
import cn.ieway.evmirror.entity.DeviceBean;
import cn.ieway.evmirror.modules.about.AboutActivity;
import cn.ieway.evmirror.modules.link.zxing.CaptureFragment;
import cn.ieway.evmirror.modules.screenshare.ScreenShareActivity;
import cn.ieway.evmirror.util.NetWorkUtil;

import static cn.ieway.evmirror.application.MirrorApplication.sMe;

public class ScanningActivity extends BaseActivity {

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
     *
     * @param bean
     */
    public boolean checkConfiguration(DeviceBean bean) {
        showHUD(true, "正在检测连接配置.." + bean.getName());
        if (NetWorkUtil.getNetWorkState(sMe) != 1){
            RxToast.warning("请打开并连接WIFI");
            dismissHUD();
            return false;
        }

        if (bean == null || bean.getUrl().isEmpty()) {
            RxToast.error("设备信息未识别请重试！");
            dismissHUD();
            return false;
        }

        Intent intent = new Intent();
        intent.setClass(this, ScreenShareActivity.class);
        intent.putExtra("name", bean.getName());
        intent.putExtra("url", bean.getUrl());
        startActivity(intent);
        dismissHUD();
        onBackPressed();
        return true;
    }


}