package cn.ieway.evmirror.modules.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.tamsiree.rxkit.RxTool;

import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ieway.evmirror.R;
import cn.ieway.evmirror.application.BaseConfig;
import cn.ieway.evmirror.base.BaseActivity;
import cn.ieway.evmirror.entity.DeviceBean;
import cn.ieway.evmirror.modules.link.LinkActivity;
import cn.ieway.evmirror.modules.about.AboutActivity;
import cn.ieway.evmirror.modules.other.WebViewActivity;
import cn.ieway.evmirror.net.DeviceSearcher;
import cn.ieway.evmirror.util.NetWorkUtil;

public class MainActivity extends BaseActivity {
    private static String TAG = "huangx";

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


    private String wifiName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initView() {
        initPermission();
    }

    @Override
    protected void initData() {
        wifiName = NetWorkUtil.getConnectWifiSsid();

        mDeviceId.setText(getString(R.string.text_device_id, BaseConfig.brandModel));
        mNetName.setText(getString(R.string.network_name, wifiName));
        mInstruction.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //设置下划线
    }

    /**
     * 按钮点击事件监听
     *
     * @param v
     */
    @OnClick({R.id.iv_about, R.id.iv_start_btn, R.id.iv_scanning, R.id.tv_instruction_book})
    public void onViewClick(View v) {
        if (RxTool.isFastClick(1000)) return;
        switch (v.getId()) {
            case R.id.iv_about: {
//                Toast.makeText(MainActivity.this, "关于界面", Toast.LENGTH_LONG).show();
                Intent intent = new Intent();
                intent.setClass(this, AboutActivity.class);
                intent.setPackage(this.getPackageName());
                startActivity(intent);
                break;
            }
            case R.id.iv_start_btn: {
//                Toast.makeText(MainActivity.this, "开始", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, LinkActivity.class);
                intent.setPackage(mContext.getPackageName());
                MainActivity.this.startActivity(intent);
                break;
            }
            case R.id.iv_scanning: {
//                Toast.makeText(MainActivity.this, "扫描", Toast.LENGTH_LONG).show();
                goToScanner(this, ScanningActivity.class);
                break;
            }
            case R.id.tv_instruction_book: {
//                Toast.makeText(MainActivity.this,"步骤",Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, HelpTipsActivity.class));
                break;
            }
        }
    }


    /**
     * 权限请求
     */
    private void initPermission() {
        if (!XXPermissions.isGrantedPermission(this, Permission.ACCESS_FINE_LOCATION)) {
            XXPermissions.with(this).permission(Permission.ACCESS_FINE_LOCATION).request(new OnPermissionCallback() {
                @Override
                public void onGranted(List<String> permissions, boolean all) {
                    mNetName.setText(getString(R.string.network_name, NetWorkUtil.getConnectWifiSsid()));
                }

                @Override
                public void onDenied(List<String> permissions, boolean never) {
                    ToastUtils.show("您未授权应用获取网络位置权限，可能无法获取您的Wifi名称。");
                }
            });
        }
    }

    /**
     * 相机权限检测及目标页面跳转
     *
     * @param context
     * @param clazz
     */
    private void goToScanner(Context context, @Nullable Class clazz) {
        Intent intent = null;
        if (clazz != null) {
            intent = new Intent();
            intent.setClass(context, clazz);
        }
        if (!XXPermissions.isGrantedPermission(this, Permission.CAMERA)) {
            Intent finalIntent = intent;
            XXPermissions.with(this).permission(Permission.CAMERA).request(new OnPermissionCallback() {
                @Override
                public void onGranted(List<String> permissions, boolean all) {
                    if (finalIntent != null) {
                        context.startActivity(finalIntent);
                    }
                }

                @Override
                public void onDenied(List<String> permissions, boolean never) {
                    ToastUtils.show(getString(R.string.denied_permission, "相机", "无法使用摄像头"));
                }
            });
        } else {
            if (intent == null) return;
            context.startActivity(intent);
        }
    }

}