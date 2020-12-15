package cn.ieway.evmirror.base;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tamsiree.rxui.view.dialog.RxDialogLoading;

import butterknife.ButterKnife;
import cn.ieway.evmirror.R;

/**
 * FileName: BaseActivity
 * Author: Admin
 * Date: 2020/12/9 12:10
 * Description:
 */
public abstract class BaseActivity extends AppCompatActivity {
    private RxDialogLoading loadingDialog;
    protected Context mContext;

    protected void openActivity(String action) {
        openActivity(action, null);
    }

    protected void openActivity(String action, Object o) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        mContext = this;
    }

    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    public  void setContentView(View view) {
        super.setContentView(view);
        initView();
        initData();
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initView();
        initData();
    }

    protected abstract void initView();

    protected abstract void initData();

    public void showHUD() {
        showHUD(false);
    }

    public void showHUD(boolean cancelable) {
        showHUD(cancelable,null);
    }

    public void showHUD(boolean cancelable, String title) {
        if (loadingDialog == null) {
            loadingDialog = new RxDialogLoading(this);
            loadingDialog.getDialogContentView().setBackgroundResource(R.color.TransColor);
            loadingDialog.getDialogContentView().setPadding(3, 3, 3, 3);
//            loadingDialog.getLoadingView().setColor(R.color.colorRed);
            loadingDialog.setCancelable(cancelable);
            loadingDialog.getTextView().setTextColor(this.getColor(R.color.White));
            loadingDialog.getTextView().setText(title==null?"":title);
        }
        loadingDialog.show();
    }

    public void dismissHUD() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
