package cn.ieway.evmirror.modules.link.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cn.ieway.evmirror.R;

public class USBLinkFragment extends Fragment {


    public static Fragment newInstance() {
        USBLinkFragment fragment = new USBLinkFragment();
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.usb, container, false);
        return view;
    }
}
