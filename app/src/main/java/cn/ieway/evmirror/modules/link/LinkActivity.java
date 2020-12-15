package cn.ieway.evmirror.modules.link;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;

import cn.ieway.evmirror.R;
import cn.ieway.evmirror.modules.link.fragment.USBLinkFragment;
import cn.ieway.evmirror.modules.link.fragment.WIfiFSearchragment;

public class LinkActivity extends AppCompatActivity {
    private SmartTabLayout smartTabLayout=null;

    private ViewPager viewPager;
    private Fragment[] mFragmentArrays = new Fragment[2];

    private String[] mTabTitles = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.link_activity);
        smartTabLayout = findViewById(R.id.viewpagertab);
        viewPager=findViewById(R.id.viewpager);
        initView();
    }
    private void initView() {
        mTabTitles[0] = "WiFi连接";
        mTabTitles[1] = "USB连接";

        //设置tablayout距离上下左右的距离
        //tab_title.setPadding(20,20,20,20);
        mFragmentArrays[0] = WIfiFSearchragment.getFragment();
        mFragmentArrays[1] = USBLinkFragment.newInstance();
        PagerAdapter pagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        //将ViewPager和TabLayout绑定
        smartTabLayout.setViewPager(viewPager);
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
}