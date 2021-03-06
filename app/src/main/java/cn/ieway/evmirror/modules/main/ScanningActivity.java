package cn.ieway.evmirror.modules.main;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tamsiree.rxkit.view.RxToast;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.List;

import cn.ieway.evmirror.R;
import cn.ieway.evmirror.base.BaseActivity;
import cn.ieway.evmirror.entity.DeviceBean;
import cn.ieway.evmirror.entity.DeviceBeanMult;
import cn.ieway.evmirror.modules.screenshare.ScreenShareActivityNew;
import cn.ieway.evmirror.util.NetWorkUtil;
import cn.ieway.evmirror.net.websocket.JWebSocketClient;

import static cn.ieway.evmirror.application.MirrorApplication.sMe;

public class ScanningActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning);
    }

    @Override
    protected void initView() {
        initTitle("");
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
     * @param beanMult
     */
    public boolean checkConfiguration(DeviceBeanMult beanMult) {
        showHUD(true, "正在检测连接配置.." + beanMult.getServerName());
        if (NetWorkUtil.getNetWorkState(sMe) != 1){
            RxToast.warning("请打开并连接WIFI");
            dismissHUD();
            return false;
        }

        if (beanMult == null || beanMult.getIp().size() == 0) {
            RxToast.error("设备信息未识别请重试！");
            dismissHUD();
            return false;
        }

        if(beanMult.getIp().size() == 1){
            startShare(new DeviceBean(beanMult.getServerName(),beanMult.getIp().get(0),beanMult.getPort()));
        }else {
            checkSocket(beanMult.getServerName(),beanMult.getIp(),beanMult.getPort(),0);
        }


        return true;
    }

    private void checkSocket(String name, List<String> urls, Integer port, int index){
        if(index >= urls.size()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    RxToast.info("连接失败，请重试。",3500);
                    finish();
                }
            });
            return;
        }
        String url = urls.get(index);
        try {
            JWebSocketClient jWebSocketClient = new JWebSocketClient(URI.create(url)){
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    super.onOpen(handshakedata);
                    startShare(new DeviceBean(name,url,port));
                    this.close();
                }

                @Override
                public void onError(Exception ex) {
                    super.onError(ex);
                    this.close();
                    checkSocket(name,urls, port, index+1);
                }
            };
            jWebSocketClient.setConnectionLostTimeout(3);
            jWebSocketClient.connectBlocking();
        }catch (Exception e){
            checkSocket(name,urls, port, index+1);
        }
    }

    private void startShare(DeviceBean bean){
        dismissHUD();
        Intent intent = new Intent();
        intent.setClass(this, ScreenShareActivityNew.class);
        intent.putExtra("name", bean.getName());
        intent.putExtra("url", bean.getIp());
        intent.putExtra("port", bean.getPort());
        startActivity(intent);
        finish();
    }


}