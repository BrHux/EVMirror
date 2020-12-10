package cn.ieway.evmirror.modules.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tamsiree.rxfeature.activity.ActivityScanerCode;
import com.tamsiree.rxkit.RxActivityTool;
import com.tamsiree.rxkit.RxTool;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ieway.evmirror.R;
import cn.ieway.evmirror.base.BaseActivity;
import cn.ieway.evmirror.modules.other.WebViewActivity;
import cn.ieway.evmirror.modules.welcome.ShowClauseActivity;

public class MainActivity extends BaseActivity {

    @BindView(R.id.iv_about)
    ImageView mAbout;
    @BindView(R.id.iv_scanning)
    ImageView mScanning;
    @BindView(R.id.tv_device_id)
    TextView mDeviceId;
    @BindView(R.id.tv_net_name)
    TextView mNetName;
    @BindView(R.id.tv_instruction_book)
    TextView mInstruction;
    @BindView(R.id.iv_start_btn)
    ImageView mScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initData() {
        mDeviceId.setText(getString(R.string.text_device_id,"默认"));
        mNetName.setText(getString(R.string.network_name,"默认"));
        mInstruction.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //设置下划线
    }

    @OnClick({R.id.iv_about,R.id.iv_start_btn,R.id.iv_scanning,R.id.tv_instruction_book})
    public void onViewClick(View v) {
        if(RxTool.isFastClick(1000)) return;
        switch (v.getId()){
            case R.id.iv_about:{
                Toast.makeText(MainActivity.this,"关于界面",Toast.LENGTH_LONG).show();
            }
            case R.id.iv_start_btn:{
                Toast.makeText(MainActivity.this,"开始",Toast.LENGTH_LONG).show();
            }
            case R.id.iv_scanning:{
                Toast.makeText(MainActivity.this,"扫描",Toast.LENGTH_LONG).show();
            }
            case R.id.tv_instruction_book:{
                Toast.makeText(MainActivity.this,"步骤",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
                intent.putExtra("title", "ieway");
                intent.putExtra("url", "https://www.ieway.cn/");
                MainActivity.this.startActivity(intent);
            }
        }
    }

}