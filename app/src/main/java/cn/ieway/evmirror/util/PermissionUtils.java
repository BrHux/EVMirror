package cn.ieway.evmirror.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.tamsiree.rxui.view.dialog.RxDialogSureCancel;

import java.util.Collections;
import java.util.List;

import cn.ieway.evmirror.R;
import cn.ieway.evmirror.application.BaseConfig;
import cn.ieway.evmirror.application.Const;
import cn.ieway.evmirror.entity.PermissionInfo;
import cn.ieway.evmirror.modules.main.MainActivity;

import static cn.ieway.evmirror.application.MirrorApplication.sMe;


/**
 * FileName: PermissionUtils
 * Author: Admin
 * Date: 2020/12/16 11:10
 * Description:
 */
public class PermissionUtils {

    public static void startApplicationDetails(Context context) {
        XXPermissions.startApplicationDetails(context);
    }

    public static void showPermissionDialog(Activity activity, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.Theme_AppCompat_Light_Dialog_Alert);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("去授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startApplicationDetails(activity);
            }
        });
        builder.setNegativeButton("取消", null);
    }

    public static boolean hasPermissionGranted(Context context, String permission) {
        return XXPermissions.isGrantedPermission(context, permission);
    }

    public static void checPermission(Activity activity, String permission, OnPermissionCallback onPermission) {
        if (hasPermissionGranted(activity, permission)) {
            onPermission.onGranted(Collections.singletonList(permission), true);
            return;
        }
        XXPermissions.with(activity).permission(permission).request(onPermission);
    }


    /**
     * >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
     */

    public static void showPermissionTips(Activity activity, String permission, String title, String content, String leftBtn, String rightBtn, OnPermissionCallback callback) {
        showPermissionTips(activity, permission, title, content, leftBtn, rightBtn, callback, null);
    }

    public static void showPermissionTips(Activity activity, String permission, String title, String content, String leftBtn, String rightBtn, OnPermissionCallback callback, View.OnClickListener clickListener) {
        try {

            final RxDialogSureCancel rxDialogSureCancel = new RxDialogSureCancel(activity);
            String infoStr = BaseConfig.get(sMe, permission);
            PermissionInfo info;
            if (infoStr == null) {
                info = new PermissionInfo(permission);
            } else {
                info = JSON.parseObject(infoStr, PermissionInfo.class);
            }

            rxDialogSureCancel.setTitle(title);
            rxDialogSureCancel.getTitleView().setTextColor(ContextCompat.getColor(activity, R.color.colorBlue));
            rxDialogSureCancel.getTitleView().setTextSize(16.0f);
            rxDialogSureCancel.setContent(content);
            rxDialogSureCancel.getContentView().setGravity(Gravity.LEFT);
            rxDialogSureCancel.getContentView().setTextColor(ContextCompat.getColor(activity, R.color.color_text_66));
            rxDialogSureCancel.getContentView().setLinksClickable(true);
            rxDialogSureCancel.getContentView().setTextSize(16.0f);
            rxDialogSureCancel.setCancel(rightBtn);
            rxDialogSureCancel.getCancelView().setTextColor(ContextCompat.getColor(activity, R.color.colorBlue));
            rxDialogSureCancel.getCancelView().setTextSize(14.0f);
            rxDialogSureCancel.getSureView().setTextSize(14.0f);
            rxDialogSureCancel.getSureView().setTextColor(ContextCompat.getColor(activity, R.color.color_text_99));
            rxDialogSureCancel.setCancelListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    requestPemission(activity, permission, new OnPermissionCallback() {
                        @Override
                        public void onGranted(List<String> permissions, boolean all) {

                        }

                        @Override
                        public void onDenied(List<String> permissions, boolean never) {
                            info.setNever(never);
                            BaseConfig.put(sMe, permission, JSON.toJSONString(info));
                            if (callback != null) callback.onDenied(permissions, never);
                        }
                    });
                    cancelTips(clickListener, rxDialogSureCancel);
                }
            });
            rxDialogSureCancel.setSure(leftBtn);
            rxDialogSureCancel.setSureListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelTips(clickListener, rxDialogSureCancel);
                }
            });
            info.setLastTime(System.currentTimeMillis());
            BaseConfig.put(sMe, permission, JSON.toJSONString(info));
            rxDialogSureCancel.show();
        } catch (Exception exception) {

        }
    }

    private static void cancelTips(View.OnClickListener clickListener, RxDialogSureCancel rxDialogSureCancel) {
        if (clickListener != null) clickListener.onClick(null);
        rxDialogSureCancel.cancel();
    }

    /**
     * <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
     */


    public static void requestPemission(Activity activity, String permission, OnPermissionCallback callback) {
        XXPermissions.with(activity).permission(permission).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean all) {
                if (callback != null) callback.onGranted(permissions, all);
            }

            @Override
            public void onDenied(List<String> permissions, boolean never) {
                if (callback != null) callback.onDenied(permissions, never);
            }
        });
    }

    /**
     * 检查权限提示弹窗间隔 (相同权限48小时时间间隔)
     *
     * @param activity
     * @param permission
     * @return
     */
    public static boolean needShowPermission(Activity activity, String permission) {
        try {
            boolean isGrant = XXPermissions.isGrantedPermission(sMe, permission);
            //已经授权不需要权限提示
            if (isGrant) return false;

            String infoStr = BaseConfig.get(sMe, permission);
            PermissionInfo info = JSON.parseObject(infoStr, PermissionInfo.class);
            if (info == null) return true;
            if (info.isNever()) return false;

            long lastTime = info.getLastTime();
            long time = System.currentTimeMillis() - lastTime;
            if (time > Const.DURATION_TIME) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.d("TAG", "needShowPermission: " + e.toString());
            return false;
        }
    }


}
