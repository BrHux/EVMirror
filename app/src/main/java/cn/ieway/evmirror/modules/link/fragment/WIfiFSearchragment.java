package cn.ieway.evmirror.modules.link.fragment;

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

import cn.ieway.evmirror.R;
import cn.ieway.evmirror.modules.link.zxing.CaptureFragment;

import static android.content.ContentValues.TAG;

public class WIfiFSearchragment extends Fragment implements View.OnClickListener {
    TextView search;
    TextView scan;
    Fragment currentFragment= new Fragment();
    private WIfiFSearchragment(){

    }
    private static Fragment Wfragment;
    public static Fragment getFragment() {
        if(Wfragment !=null){
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
        initFragment(WifiSearchListFragment.getFragment());
        return view;
    }

    void initFragment(Fragment targetFragment){
        Log.d(TAG, "changeFragment: "+targetFragment);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction  transaction =fragmentManager.beginTransaction();
//        transaction.add(R.id.fragment_container,targetFragment);
//        transaction.show(targetFragment);

        transaction.replace(R.id.fragment_container,targetFragment);
        transaction.commit();
    }

    /*
    * 切換fragment
    * */
    @NonNull
    void changeFragment(Fragment fragment){
        Log.d(TAG, "changeFragment: "+fragment);
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction  transaction =fragmentManager.beginTransaction();
//        transaction.add(R.id.fragment_container,targetFragment);
//        transaction.show(targetFragment);

        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
//        if (currentFragment != fragment){//  判断传入的fragment是不是当前的currentFragmentgit
//            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//            transaction.hide(currentFragment);//  不是则隐藏
//            currentFragment = fragment;  //  然后将传入的fragment赋值给currentFragment
//            if (!fragment.isAdded()){ //  判断传入的fragment是否已经被add()过
//                transaction.add(R.id.fragment_container,fragment).show(fragment).commit();
//            }else{
//                transaction.show(fragment).commit();
//            }
//        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.scan) {
           scan.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_corner1));
           search.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_corner));
           scan.setTextColor(0xff00ffff);
           search .setTextColor(0xFFFFFFFF);
           changeFragment(CaptureFragment.newInstance());
        }  else if(v.getId()== R.id.search){
                search.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_corner1));
                scan.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.shape_corner));
                changeFragment(WifiSearchListFragment.getFragment());
                search.setTextColor(0xff00ffff);
                scan.setTextColor(0xFFFFFFFF);
        }
        }
}
