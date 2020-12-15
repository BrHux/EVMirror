package cn.ieway.evmirror.modules.link.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import cn.ieway.evmirror.R;
import cn.ieway.evmirror.entity.DeviceBean;
import cn.ieway.evmirror.net.DeviceSearcher;

import static android.content.ContentValues.TAG;

public class WifiSearchListFragment extends Fragment {
    private static Fragment fragment;
    DeviceSearcher deviceSearcher;

    public static Fragment  getFragment() {
        if(fragment !=null){
            return  fragment;
        }
        Log.d(TAG, "getFragment: "+fragment);
        fragment  = new WifiSearchListFragment();
        Log.d(TAG, "getFragment: "+fragment);
        return fragment;
    }
    RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_wifi, container, false);
        SwipeRefreshLayout swipeRefreshLayout=view.findViewById(R.id.swipeRedreshLayout);
        recyclerView=view.findViewById(R.id.recyclerView);
                deviceSearcher = new DeviceSearcher() {
                    @Override
                    public void onSearchStart() {

                    }
                    @Override
                    public void onSearchFinish(Set deviceSet) {
                        Log.d(TAG," onSearchFinish:" +deviceSet);
                        if (getActivity(). isDestroyed()||getActivity(). isFinishing())
                            return;
                        for (Object o: deviceSet){

                            DeviceBean bean = (DeviceBean) o;
                            Log.d(TAG,"onSearchFinish:"+ bean.getName());


                        }
                    }
                };
                deviceSearcher.start();
        //下拉刷新获取列表
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //具体操作


                //定时停止刷新
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: ");
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },2000);

            }
        });


        return view;
    }


}

