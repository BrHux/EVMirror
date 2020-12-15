package cn.ieway.evmirror.modules.link.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Set;

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



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_wifi, container, false);
        SwipeRefreshLayout swipeRefreshLayout=view.findViewById(R.id.swipeRedreshLayout);
        new Thread(
                new Runnable() {
            @Override
            public void run() {
                deviceSearcher = new DeviceSearcher() {
                    @Override
                    public void onSearchStart() {
                        Log.d( TAG,"onSearchStart:");
                    }
                    @Override
                    public void onSearchFinish(Set deviceSet) {
                        Log.d(TAG," onSearchFinish:" );
                        if (getActivity(). isDestroyed()||getActivity(). isFinishing())
                            return;
                        for (Object o: deviceSet){

                            DeviceBean bean = (DeviceBean) o;
                            Log.d(TAG,"onSearchFinish:"+ bean.getName());
                            deviceSearcher.start();

                        }
                    }
                };
                deviceSearcher.run();
            }
        }

        ).start();

        return view;
    }


}

