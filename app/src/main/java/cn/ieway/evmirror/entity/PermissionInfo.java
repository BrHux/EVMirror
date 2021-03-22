package cn.ieway.evmirror.entity;

/**
 * FileName: PermissionInfo
 * Author: Admin
 * Date: 2021/3/22 17:14
 * Description:
 */
public class PermissionInfo {
    private String key ;
    private long lastTime;
    private boolean never;
    public  PermissionInfo(){

    }

    public PermissionInfo(String key) {
        this.key = key;
    }

    public PermissionInfo(String key, long lastTime, boolean never) {
        this.key = key;
        this.lastTime = lastTime;
        this.never = never;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public boolean isNever() {
        return never;
    }

    public void setNever(boolean never) {
        this.never = never;
    }

}
