package cn.ieway.evmirror.modules.screenshare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ieway.evmirror.R;
import cn.ieway.evmirror.base.BaseActivity;

public class DisConnectDialog extends BaseActivity {

    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_sure)
    TextView tvSure;
    private int type = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dis_connect);
    }

    @Override
    protected void initView() {
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);

        WindowManager.LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;   //高度设置
        p.width = (int) (displaySize.x * 0.8);    //宽度设置为屏幕的0.8
        p.gravity = Gravity.CENTER;
        getWindow().setAttributes(p);
    }

    @Override
    protected void initData() {
        tvContent.setText(getIntent().getStringExtra("content"));
        tvSure.setText(getIntent().getStringExtra("sure"));
        type = getIntent().getIntExtra("type",-1);
    }

    @OnClick({R.id.tv_sure})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.tv_sure:{
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        setResult(type);
        super.onDestroy();
    }
}