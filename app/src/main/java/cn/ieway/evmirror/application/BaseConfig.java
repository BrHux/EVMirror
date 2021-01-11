package cn.ieway.evmirror.application;

import android.content.Context;
import android.content.SharedPreferences;

import com.tamsiree.rxkit.RxAppTool;
import com.tamsiree.rxkit.RxDataTool;
import com.tamsiree.rxkit.RxDeviceTool;
import com.tamsiree.rxkit.RxSPTool;

import cn.ieway.evmirror.util.MachineCodeUtils;

import static cn.ieway.evmirror.application.MirrorApplication.sMe;

public class BaseConfig {
    public static final int NOT = -1;
    private static final String CLASSNAME = BaseConfig.class.getName();

    public final static String appName = RxAppTool.getAppName(sMe); //应用名称
    public final static String appVersionName = RxAppTool.getAppVersionName(sMe); //版本名称
    public final static int appVersionNo = RxAppTool.getAppVersionCode(sMe); //版本号
    public final static String macAddress = RxDeviceTool.getMacAddress(sMe); //设备 MAC 地址
    public final static String deviceName = RxDeviceTool.getBuildBrand() + " " + RxDeviceTool.getBuildBrandModel(); //设备名
    public final static String brandModel = RxDeviceTool.getBuildBrandModel(); //型号名
    public final static String machineId = MachineCodeUtils.getMachineId(); //机器码
    public final static String serialId = RxDeviceTool.getSerialNumber(); //序列号

    //Coturn信息
    public static String TurnURL = "turn:124.232.150.19";
    public static String TurnID = "test";
    public static String TurnPassword = "test";
    public static String TurnRoom_ID = "";


    /**
     * 每次启动均初始化配置文件
     *
     * @param mContext
     */
    public static void init(Context mContext) {

    }


    //=================================================================================================
    public static void put(Context mContext, String key, String value) {
        SharedPreferences.Editor storage = mContext.getSharedPreferences(
                CLASSNAME, 0).edit();
        storage.putString(key, value);
        storage.commit();
    }

    public static void putInt(Context mContext, String key, int value) {
        SharedPreferences.Editor storage = mContext.getSharedPreferences(
                CLASSNAME, 0).edit();
        storage.putInt(key, value);
        storage.commit();
    }

    public static void putLong(Context mContext, String key, long value) {
        SharedPreferences.Editor storage = mContext.getSharedPreferences(
                CLASSNAME, 0).edit();
        storage.putLong(key, value);
        storage.commit();
    }

    public static void putBoolean(Context mContext, String key, boolean value) {
        SharedPreferences.Editor storage = mContext.getSharedPreferences(
                CLASSNAME, 0).edit();
        storage.putBoolean(key, value);
        storage.commit();
    }

    public static String get(Context mContext, String key) {
        SharedPreferences storage = mContext.getSharedPreferences(CLASSNAME, 0);
        String value = storage.getString(key, null);
        return value;
    }

    public static boolean getBoolean(Context mContext, String key) {
        SharedPreferences storage = mContext.getSharedPreferences(CLASSNAME, 0);
        return storage.getBoolean(key, false);
    }

    public static int getInt(Context mContext, String key) {
        SharedPreferences storage = mContext.getSharedPreferences(CLASSNAME, 0);
        return storage.getInt(key, 0);
    }

    public static long getLong(Context mContext, String key) {
        SharedPreferences storage = mContext.getSharedPreferences(CLASSNAME, 0);
        return storage.getLong(key, 0);
    }
}
