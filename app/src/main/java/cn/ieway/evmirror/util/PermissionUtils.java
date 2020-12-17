package cn.ieway.evmirror.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.hjq.permissions.XXPermissions;

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
}