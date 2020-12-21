package cn.ieway.evmirror.entity;

import android.graphics.drawable.Drawable;

/**
 * FileName: AppInfoVo
 * Author: Admin
 * Date: 2020/10/29 10:25
 * Description:
 */
public class AppInfoVo {
    private int index;
    private Drawable icon;
    private String appName;
    private String packageName;
    private String launcherName;
    private boolean isSystemApp;
    private boolean isMore;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getLauncherName() {
        return launcherName;
    }

    public void setLauncherName(String launcherName) {
        this.launcherName = launcherName;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }
}
