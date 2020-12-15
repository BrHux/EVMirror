package cn.ieway.evmirror.modules.link.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cn.ieway.evmirror.R;

import static android.content.ContentValues.TAG;

public class ScanLinkFragment extends Fragment {
    private static Fragment fragment;
    public static Fragment  getFragment() {
        if(fragment !=null){
            return  fragment;
        }
        Log.d(TAG, "getFragment: "+fragment);
        fragment  = new ScanLinkFragment();
        Log.d(TAG, "getFragment: "+fragment);
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scan, container, false);
        return view;
    }
}
