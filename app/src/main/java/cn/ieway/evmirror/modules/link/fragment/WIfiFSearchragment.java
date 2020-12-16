package cn.ieway.evmirror.modules.link.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;

import java.util.List;

import cn.ieway.evmirror.R;
import cn.ieway.evmirror.modules.link.zxing.CaptureFragment;
import cn.ieway.evmirror.util.PermissionUtils;

import static android.content.ContentValues.TAG;

public class WIfiFSearchragment extends Fragment implements View.OnClickListener {
    TextView search;
    TextView scan;
    Fragment currentFragment = WifiSearchListFragment.getFragment();

    private WIfiFSearchragment() {

    }

    private static Fragment Wfragment;

    public static Fragment getFragment() {
        if (Wfragment != null) {
            return Wfragment;
        }
        Wfragment = new WIfiFSearchragment();
        return Wfragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wifi, container, false);
        scan = view.findViewById(R.id.scan);
        search = view.findViewById(R.id.search);
        scan.setOnClickListener(this);
        search.setOnClickListener(this);
        initFragment(currentFragment);
        return view;
    }

    void initFragment(Fragment targetFragment) {
        Log.d(TAG, "changeFragment: " + targetFragment);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.add(R.id.fragment_container,targetFragment);
//        transaction.show(targetFragment);

        transaction.replace(R.id.fragment_container, targetFragment);
        transaction.commit();
        setWifiSearchView();
    }

    /*
     * 切換fragment
     * */
    @NonNull
    void changeFragment(Fragment fragment) {
        Log.d(TAG, "changeFragment: " + fragment);
        if (currentFragment == fragment) return;//  判断传入的fragment是不是当前的currentFragmentgit
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.fragment_container,fragment);
//        transaction.commit();
        transaction.hide(currentFragment);//  不是则隐藏
        currentFragment = fragment;  //  然后将传入的fragment赋值给currentFragment
        if (!fragment.isAdded()) { //  判断传入的fragment是否已经被add()过
            transaction.add(R.id.fragment_container, fragment).show(fragment).commit();
        } else {
            transaction.show(fragment).commit();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.scan) {

//            scan.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_corner1));
//            search.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_corner));
//            scan.setTextColor(0xff00ffff);
//            search.setTextColor(0xFFFFFFFF);
            goToScanner(getActivity(), CaptureFragment.newInstance());
//            changeFragment(CaptureFragment.newInstance());
        } else if (v.getId() == R.id.search) {
            setWifiSearchView();
        }
    }

    /**
     * 相机权限检测及目标页面跳转
     *
     * @param context
     * @param fragment
     */
    private void goToScanner(Context context, Fragment fragment) {
        if (XXPermissions.isGrantedPermission(context, Permission.CAMERA)) {
            setScannerPage();
            changeFragment(fragment);
            return;
        }

        XXPermissions.with(this).permission(Permission.CAMERA).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean all) {
                setScannerPage();
                changeFragment(fragment);
            }

            @Override
            public void onDenied(List<String> permissions, boolean never) {
                if (never) {
                    PermissionUtils.showPermissionDialog(getActivity(), "重要权限", getString(R.string.denied_permission, "相机", "请手动开启权限"));
                    return;
                }
                ToastUtils.show(getString(R.string.denied_permission, "相机", "无法使用扫码功能"));
            }
        });

    }

    /**
     * 扫码连接样式
     */
    private void setScannerPage() {
        scan.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_corner1));
        search.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_corner));
        scan.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorBlue));
        search.setTextColor(ContextCompat.getColor(getActivity(),R.color.White));
    }

    /**
     * wifi搜索连接样式
     */
    private void setWifiSearchView() {
        search.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_corner1));
        scan.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_corner));
        changeFragment(WifiSearchListFragment.getFragment());
        search.setTextColor(ContextCompat.getColor(getActivity(),R.color.colorBlue));
        scan.setTextColor(ContextCompat.getColor(getActivity(),R.color.White));
    }


}
