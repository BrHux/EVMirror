package cn.ieway.evmirror.modules.main;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.tamsiree.rxkit.RxNetTool;
import com.tamsiree.rxkit.RxTool;
import com.tamsiree.rxkit.view.RxToast;
import com.tamsiree.rxui.view.dialog.RxDialogEditSureCancel;
import com.tamsiree.rxui.view.dialog.RxDialogSureCancel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.ieway.evmirror.R;
import cn.ieway.evmirror.application.BaseConfig;
import cn.ieway.evmirror.application.MirrorApplication;
import cn.ieway.evmirror.base.BaseActivity;
import cn.ieway.evmirror.entity.DeviceBean;
import cn.ieway.evmirror.entity.eventbus.NetWorkMessageEvent;
import cn.ieway.evmirror.modules.link.LinkActivity;
import cn.ieway.evmirror.modules.about.AboutActivity;
import cn.ieway.evmirror.modules.other.WebViewActivity;
import cn.ieway.evmirror.net.DeviceSearcher;
import cn.ieway.evmirror.receiver.NetWorkStateReceiver;
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
    private IntentFilter intentFilter;
    NetWorkStateReceiver netWorkStateReceiver;

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

        netWorkStateReceiver = new NetWorkStateReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, intentFilter);


        wifiName = NetWorkUtil.getConnectWifiSsid();

        mDeviceId.setText(getString(R.string.text_device_id, BaseConfig.brandModel));
        mNetName.setText(getString(R.string.network_name, wifiName));
        mInstruction.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //设置下划线

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (netWorkStateReceiver != null) {
            unregisterReceiver(netWorkStateReceiver);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onMessageEvent(NetWorkMessageEvent event) {
        switch (event.creentState) {
            case DISCONNECTED: {
                mNetName.setText(getString(R.string.network_name, "WIFI已断开"));
                RxToast.info("WIFI未连接或WIFI已关闭");
                break;
            }
            case CONNECTED: {
                mNetName.setText(getString(R.string.network_name, NetWorkUtil.getConnectWifiSsid()));
                break;
            }
            default: {
                break;
            }
        }
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
                if (MirrorApplication.sMe.isWlanOpen) {
                    Intent intent = new Intent(MainActivity.this, LinkActivity.class);
                    intent.setPackage(mContext.getPackageName());
                    MainActivity.this.startActivity(intent);
                    break;
                }

                showTips("检测到未开启WIFI，请开启", "开启", 0);

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


    private void showTips(String title, String actStr, final int type) {
        final RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(this);
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
                //获取wifi管理服务
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                //获取wifi开关状态
                int status = wifiManager.getWifiState();
                if (status == WifiManager.WIFI_STATE_ENABLED) {
                    //wifi打开状态则关闭
//                    wifiManager.setWifiEnabled(false);
//                    Toast.makeText(MainActivity.this, "wifi已关闭", Toast.LENGTH_SHORT).show();
                } else {
                    //关闭状态则打开
                    wifiManager.setWifiEnabled(true);
//                    Toast.makeText(MainActivity.this, "wifi已打开", Toast.LENGTH_SHORT).show();
                    RxToast.success("wifi已打开");
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
                }
            });
        }
        rxDialogSureCancel.show();
    }

}