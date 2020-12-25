package cn.ieway.evmirror.modules.link;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.tamsiree.rxkit.view.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.OnClick;
import cn.ieway.evmirror.R;
import cn.ieway.evmirror.base.BaseActivity;
import cn.ieway.evmirror.entity.DeviceBean;
import cn.ieway.evmirror.entity.eventbus.NetWorkMessageEvent;
import cn.ieway.evmirror.modules.link.fragment.USBLinkFragment;
import cn.ieway.evmirror.modules.link.fragment.WIfiFSearchragment;
import cn.ieway.evmirror.modules.screenshare.ScreenShareActivity;

public class LinkActivity extends BaseActivity{
    private SmartTabLayout smartTabLayout = null;

    private ViewPager viewPager;
    private Fragment[] mFragmentArrays = new Fragment[2];

    private String[] mTabTitles = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_activity);
        initView();
    }

    @Override
    protected void  initView() {
        mTabTitles[0] = "WiFi连接";
        mTabTitles[1] = "USB连接";

        smartTabLayout = findViewById(R.id.viewpagertab);
        viewPager = findViewById(R.id.viewpager);
        //设置tablayout距离上下左右的距离
        //tab_title.setPadding(20,20,20,20);
        mFragmentArrays[0] = WIfiFSearchragment.getFragment();
        mFragmentArrays[1] = USBLinkFragment.newInstance();
        PagerAdapter pagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        //将ViewPager和TabLayout绑定
        smartTabLayout.setViewPager(viewPager);
    }

    @Override
    protected void initData() {

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

    @OnClick({R.id.iv_last_page})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.iv_last_page:{
                finish();
                break;
            }
        }
    }

    private boolean isConnceted = true;
    @Subscribe(threadMode = ThreadMode.MAIN , sticky = true)
    public void onMessageEvent(NetWorkMessageEvent event){
        switch (event.creentState) {
            case DISCONNECTED: {
                isConnceted = false;
                break;
            }
            case CONNECTED: {
                isConnceted = true;
                break;
            }
            default: {

                break;
            }
        }
    }



    final class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentArrays[position];
        }

        @Override
        public int getCount() {
            return mFragmentArrays.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];

        }
    }

    /**
     * 客户端连接预处理
     * @param bean
     */
    public boolean checkConfiguration(DeviceBean bean){
       return checkConfiguration(bean.getName(),bean.getUrl());
    }
    /**
     * 客户端连接预处理
     * @param url
     */
    public boolean checkConfiguration(String name,String url){
        if (!isConnceted) {
            RxToast.warning("请打开并连接WIFI");
            return false;
        }

        showHUD(true,"正在检测连接配置..");
        if(url == null || url.isEmpty()){
            RxToast.error("设备信息识别异常请重试！");
            return false;
        }
        Intent intent = new Intent();
        intent.setClass(this, ScreenShareActivity.class);
        intent.putExtra("name",name);
        intent.putExtra("url",url);
        startActivity(intent);
        dismissHUD();
        onBackPressed();
        return true;
    }


}