package cn.ieway.evmirror.modules.about;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.tamsiree.rxkit.RxAppTool;
import com.tamsiree.rxkit.view.RxToast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.ieway.evmirror.R;
import cn.ieway.evmirror.entity.AppInfoVo;
import cn.ieway.evmirror.util.PermissionUtils;

import static cn.ieway.evmirror.application.MirrorApplication.sMe;

public class IntentShareActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TYPE_VIDEO = "video/*";
    public static final String TYPE_IMAGE = "image/*";
    public static final String TYPE_TEXT = "text/plain";

    private List<AppInfoVo> appInfoVos = new ArrayList<>();
    private List<AppInfoVo> mApps = new ArrayList<>();
    private List<ResolveInfo> allList;
    private GridView gridView;
    private String shareType;
    private String contentStr;
    private ShareAdapter shareAdapter;
    private PackageManager pm;
    private WindowManager.LayoutParams layoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_share);
//
        Point displaySize = new Point();
        getWindowManager().getDefaultDisplay().getSize(displaySize);
//
        //获取对话框当前的参数值
        layoutParams = getWindow().getAttributes();
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;  //高度设置为屏幕的1.0
//        p.width = (int) (displaySize.x * 0.8);    //宽度设置为屏幕的0.8
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;    //宽度设置为屏幕的0.8
        layoutParams.gravity = Gravity.BOTTOM;
        getWindow().setAttributes(layoutParams);
        initView();
        intDate();
    }

    private void checkPermission(){
//        PermissionUtils.checPermission(this, Permission.WRITE_EXTERNAL_STORAGE, new OnPermissionCallback() {
//            @Override
//            public void onGranted(List<String> permissions, boolean all) {
//
//            }
//
//            @Override
//            public void onDenied(List<String> permissions, boolean never) {
//                RxToast.warning("您拒绝了应用存储权限,分享功能不可用，请重启应用或进入权限设置开启权限。",2000);
//            }
//        });
    }

    private void initView() {
        gridView = findViewById(R.id.dialog_share_grid_view);
        pm = sMe.getPackageManager();
    }

    private void intDate() {
        allList = (List<ResolveInfo>) getIntent().getSerializableExtra("shareTargets");
        shareType = getIntent().getStringExtra("shareType");
        contentStr = getIntent().getStringExtra("filePath");
        if (allList == null || allList.size() == 0) {
            finish();
            return;
        }
        setResolveInfoList(allList);

        shareAdapter = new ShareAdapter();
        gridView.setAdapter(shareAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mApps.get(position).isMore()){
                    mApps.clear();
                    mApps.addAll(appInfoVos);
                    shareAdapter.notifyDataSetChanged();
                }else {
                    if (shareType.equals(TYPE_TEXT)){
                        shareNew(IntentShareActivity.this, mApps.get(position), contentStr);
                        return;
                    }
//                    shareNew(IntentShareActivity.this, mApps.get(position), new File(contentStr));
                }
            }
        });
    }

    /**
     * 系统Intent分享列表处理
     *
     * @param allList
     */
    private void setResolveInfoList(List<ResolveInfo> allList) {
        if (appInfoVos != null) appInfoVos.clear();
        if (mApps != null) mApps.clear();
        //获取关键属性
        for (int i = 0; i < allList.size(); i++) {
            AppInfoVo appInfoVo = new AppInfoVo();
            ResolveInfo resolveInfo = allList.get(i);
            String name = resolveInfo.activityInfo.name;
            String packageName = resolveInfo.activityInfo.packageName;
            appInfoVo.setAppName(resolveInfo.loadLabel(pm).toString());
            appInfoVo.setIcon(resolveInfo.loadIcon(pm));
            appInfoVo.setPackageName(packageName);
            appInfoVo.setLauncherName(name);
            if (name.contains("com.tencent.mm.ui.tools.ShareImgUI")) { //微信
                appInfoVo.setIndex(-8);
            } else if (name.contains("com.tencent.mobileqq.activity.JumpActivity")) {//QQ
                appInfoVo.setIndex(-7);
            } else {
                appInfoVo.setIndex(i);
            }
            appInfoVos.add(appInfoVo);
        }
        //排序
        Collections.sort(appInfoVos, new Comparator<AppInfoVo>() {
            @Override
            public int compare(AppInfoVo o1, AppInfoVo o2) {
                return o1.getIndex() < o2.getIndex() ? -1 : 0;
            }
        });
        //超过两行时默认显示两行(2x4)
        if (appInfoVos.size() > 8){
            for (int i = 0;i<7;i++){
                mApps.add(appInfoVos.get(i));
            }
            AppInfoVo appInfoVo = new AppInfoVo();
            appInfoVo.setIcon(getDrawable(R.drawable.ic_more));
            appInfoVo.setAppName(getString(R.string.more));
            appInfoVo.setMore(true);
            mApps.add(appInfoVo);
        }else {
            mApps.addAll(appInfoVos);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onClick(View v) {

    }

    /*public void shareNew(Context context, AppInfoVo resolveInfo, File file) {
        if(!PermissionUtils.hasPermissionGranted(context, Permission.READ_EXTERNAL_STORAGE)){
            checkPermission();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (TYPE_VIDEO.equals(shareType)) {
                fileUri = getVideoContentUri(context, file);
            } else {
                fileUri = FileProvider.getUriForFile(context, RxAppTool.getAppPackageName(context) + ".fileProvider", file);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            fileUri = Uri.fromFile(file);
        }
        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
        intent.setType(shareType);
        String pkg = resolveInfo.getPackageName();
        String cls = resolveInfo.getLauncherName();
        intent.setComponent(new ComponentName(pkg, cls));
        context.startActivity(intent);
    }*/

    public void shareNew(Context context, AppInfoVo resolveInfo, String textStr) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, textStr);
        intent.setType(shareType);
        String pkg = resolveInfo.getPackageName();
        String cls = resolveInfo.getLauncherName();
        intent.setComponent(new ComponentName(pkg, cls));
        context.startActivity(intent);
    }


    private static Uri getVideoContentUri(Context context, File file) {
        String filePath = file.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Video.Media._ID}, MediaStore.Video.Media.DATA + "=?",
                new String[]{filePath}, null);
        Uri uri = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                Uri baseUri = Uri.parse("content://media/external/video/media");
                uri = Uri.withAppendedPath(baseUri, "" + id);
            }
            cursor.close();
        }

        //如果使用fileProvider获取失败，则使用此方法
        if (uri == null) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.DATA, filePath);
            uri = context.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        }

        if (uri == null) {
            uri = FileProvider.getUriForFile(context, RxAppTool.getAppPackageName(context) + ".fileProvider", file);
        }
        return uri;
    }

    class ShareAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mApps.size();
        }

        @Override
        public Object getItem(int i) {
            return mApps.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = View.inflate(IntentShareActivity.this, R.layout.item_share_grid_view, null);
            ImageView img = ((ImageView) itemView.findViewById(R.id.item_share_img));
            TextView textView = ((TextView) itemView.findViewById(R.id.item_share_tv));
            img.setImageDrawable(mApps.get(position).getIcon());
            textView.setText(mApps.get(position).getAppName());
            return itemView;
        }
    }


    /**
     * @param title
     * @param msg
     * @param sureStr
     * @param netivStr
     * @param type     1:打开摄像头悬浮穿；2：手动设置权限；3：请求悬浮窗权限并设置倒计时默认值-3秒；4：请求悬浮窗权限
     * @param object
     */
//    private void showDialog(@Nullable String title, @Nullable String msg, @Nullable String sureStr, @Nullable String netivStr, int type, @Nullable Object object) {
//        if (mainActivity == null) return;
//        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity, R.style.Theme_AppCompat_Light_Dialog_Alert);
//        if (title != null) {
//            builder.setTitle(title);
//        }
//        if (msg != null) {
//            builder.setMessage(msg);
//        }
//
//        if (sureStr != null) {
//            builder.setPositiveButton(sureStr, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (type == 1) {
//                        FloatMenuService.requestCamera(mainActivity);
//                        return;
//                    }
//                    if (type == 2 && object != null) {
//                        XXPermissions.startApplicationDetails(com.example.ev_capture.IntentShareActivity.this);
//                        return;
//                    }
//                    if ((type == 4 || type == 3) && mainActivity != null) {
//                        PermissionUtils.requestFloatPermission(mainActivity, type == 3 ? true : false);
//                    }
//                }
//            });
//        }
//
//        if (netivStr != null) {
//            builder.setNegativeButton(netivStr, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (type == 2) {
//                        ToastUtil.INSTANCE.displayLong(getString(R.string.tips_waive_permission));
//                    }
//                }
//            });
//        }
//        builder.show();
//    }

}
