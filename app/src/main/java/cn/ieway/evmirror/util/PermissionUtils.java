package cn.ieway.evmirror.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import java.util.Collections;
import java.util.List;

import cn.ieway.evmirror.R;


/**
 * FileName: PermissionUtils
 * Author: Admin
 * Date: 2020/12/16 11:10
 * Description:
 */
public class PermissionUtils {

    public static void startApplicationDetails(Context context){
        XXPermissions.startApplicationDetails(context);
    }

    public static void showPermissionDialog(Activity activity,String title,String msg){
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
        builder.setNegativeButton("取消",null);
    }

    public static boolean hasPermissionGranted(Context context,String permission){
        return XXPermissions.isGrantedPermission(context,permission);
    }

    public static void checPermission(Activity activity,String permission,OnPermissionCallback onPermission){
        if (hasPermissionGranted(activity,permission)) {
            onPermission.onGranted(Collections.singletonList(permission),true);
            return;
        }
        XXPermissions.with(activity).permission(permission).request(onPermission);
    }
}
