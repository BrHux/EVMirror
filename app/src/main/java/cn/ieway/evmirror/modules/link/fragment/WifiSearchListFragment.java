package cn.ieway.evmirror.modules.link.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.tamsiree.rxkit.view.RxToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ieway.evmirror.R;
import cn.ieway.evmirror.application.BaseConfig;
import cn.ieway.evmirror.entity.DeviceBean;
import cn.ieway.evmirror.entity.eventbus.NetWorkMessageEvent;
import cn.ieway.evmirror.modules.link.LinkActivity;
import cn.ieway.evmirror.modules.link.adapter.IpAddressAdapter;
import cn.ieway.evmirror.net.DeviceSearcher;
import cn.ieway.evmirror.util.NetWorkUtil;

import static android.content.ContentValues.TAG;

public class WifiSearchListFragment extends Fragment {
    private static Fragment fragment;

    private final static int UNDISCOVERED = 100;
    private final static int REFRESHING = 101;
    private final static int REFRESHED = 102;

    @BindView(R.id.tv_wifi_name)
    TextView wifiName;
    @BindView(R.id.tv_status)
    TextView tvStatus;
    @BindView(R.id.iv_status_img)
    ImageView imgStatus;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;


    private SwipeRefreshLayout swipeRefreshLayout;
    private List<DeviceBean> mDeviceList = new ArrayList<>();
    private IpAddressAdapter addressAdapter;
    private View view;
    private boolean isConnceted;


    public static Fragment getFragment() {
        if (fragment != null) {
            return fragment;
        }
        Log.d(TAG, "getFragment: " + fragment);
        fragment = new WifiSearchListFragment();
        Log.d(TAG, "getFragment: " + fragment);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_wifi, container, false);
//        recyclerView=view.findViewById(R.id.recyclerView);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }

    private void initView(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRedreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent,R.color.colorPrimaryDark);
        setWifiName();
        initRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().removeAllStickyEvents();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onMessageEvent(NetWorkMessageEvent event){
        setWifiName();
    }

    private void initData() {
        search();
        //下拉刷新获取列表
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //具体操作
                Log.d(TAG, "onRefresh: ");
                search();
            }
        });
    }

    private void setWifiName(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int state = NetWorkUtil.getNetWorkState(getContext());
                if (state != 1){
                    wifiName.setText("未连接Wifi");
                    return;
                }
                search();
                wifiName.setText(getString(R.string.wifi_name, NetWorkUtil.getConnectWifiSsid()));
            }
        },500);
    }

    private void initRecyclerView() {
        addressAdapter = new IpAddressAdapter(getActivity(), mDeviceList);
        addressAdapter.setItemClickListener(new IpAddressAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onSharebtnPress(mDeviceList.get(position).getName(), mDeviceList.get(position).getUrl());
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(addressAdapter);
    }


    /**
     * 搜索局域网内可连接设备
     */
    private void search() {
        DeviceSearcher deviceSearcher = new DeviceSearcher() {
            @Override
            public void onSearchStart() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateStatusAre(REFRESHING);
                        if (swipeRefreshLayout.isRefreshing()) return;
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
            }

            @Override
            public void onSearchFinish(Set deviceSet) {
                if (fragment == null || fragment.isRemoving() || fragment.isDetached()) return;
                mDeviceList.clear();
                mDeviceList.addAll(deviceSet);

                try {
                    if(getActivity() == null || getActivity().isFinishing() || getActivity().isDestroyed()) return;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //关闭刷新图标
                            if (swipeRefreshLayout.isRefreshing()) swipeRefreshLayout.setRefreshing(false);
                            if (mDeviceList.size() > 0){
                                updateStatusAre(REFRESHED);
                                if (addressAdapter != null) {
                                    if (recyclerView.getVisibility() != View.VISIBLE) {
                                        recyclerView.setVisibility(View.VISIBLE);
                                    }
                                    addressAdapter.notifyDataSetChanged();
                                }
                            }else {
                                updateStatusAre(UNDISCOVERED);
                                if (recyclerView.getVisibility() == View.VISIBLE) {
                                    recyclerView.setVisibility(View.GONE);
                                }
                                RxToast.normal("未发现设备");
                            }
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        deviceSearcher.start();
        updateStatusAre(REFRESHING);
    }

    private void updateStatusAre(int status) {

        switch (status) {
            case REFRESHING: {
                imgStatus.setImageResource(R.drawable.search);
                tvStatus.setText(R.string.searching_device);
                break;
            }
            case REFRESHED: {
                imgStatus.setImageResource(R.drawable.ic_refresh_agin);
                tvStatus.setText(R.string.search_again);
                break;
            }
            case UNDISCOVERED: {
                imgStatus.setImageResource(R.drawable.ic_search_agin);
                tvStatus.setText(R.string.undiscovered_device);
                break;
            }
        }
    }


    /**
     * 列表点击事件处理
     */
    private void onSharebtnPress(@NonNull String nickName, @NonNull String url) {
        Log.d(TAG, "onSharebtnPress: " + nickName + "  / " + url);
        if (fragment.isRemoving()) return;

        WIfiFSearchragment wIfiFSearchragment = (WIfiFSearchragment) fragment.getParentFragment();
        if(wIfiFSearchragment instanceof  WIfiFSearchragment){
            LinkActivity linkActivity = (LinkActivity) wIfiFSearchragment.getActivity();
            linkActivity.checkConfiguration(nickName,url);
        }
//        FragmentManager manager = getFragmentManager();//获取到父fragment的管理器
//        //获取到父parentFragment
//        WIfiFSearchragment home = (WIfiFSearchragment) manager.getFragments().get(0);
        //获取Activit


    }


}

