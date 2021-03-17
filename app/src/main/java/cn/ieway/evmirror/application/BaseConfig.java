package cn.ieway.evmirror.application;

import android.content.Context;
import android.content.SharedPreferences;

import com.tamsiree.rxkit.RxAppTool;
import com.tamsiree.rxkit.RxDataTool;
import com.tamsiree.rxkit.RxDeviceTool;
import com.tamsiree.rxkit.RxSPTool;

import java.util.HashMap;
import java.util.Map;

import cn.ieway.evmirror.util.MachineCodeUtils;

import static cn.ieway.evmirror.application.MirrorApplication.sMe;

public class BaseConfig {
//    public static final int NOT = -1;
    private static final String CLASSNAME = BaseConfig.class.getName();

//    public final static String appName = RxAppTool.getAppName(sMe); //应用名称
    public final static String appVersionName = RxAppTool.getAppVersionName(sMe); //版本名称
//    public final static int appVersionNo = RxAppTool.getAppVersionCode(sMe); //版本号
//    public final static String macAddress = RxDeviceTool.getMacAddress(sMe); //设备 MAC 地址
    public final static String deviceName = RxDeviceTool.getBuildBrand() + " " + RxDeviceTool.getBuildBrandModel(); //设备名
    public final static String brandModel = RxDeviceTool.getBuildBrandModel(); //型号名
//    public final static String machineId = MachineCodeUtils.getMachineId(); //机器码
    public  static String serialId = "123456";//序列号(android_id)

    //Coturn信息
//    public static String TurnURL = "turn:124.232.150.19";
//    public static String TurnID = "test";
//    public static String TurnPassword = "test";
//    public static String TurnRoom_ID = "";

    //-- 渠道 id (key键应与build.gradle一致)--
    public static Map<String, Integer> FLAVORS = new HashMap<String, Integer>();
    /**
     * 每次启动均初始化配置文件
     *
     * @param mContext
     */
    public static void init(Context mContext) {
        serialId = RxDeviceTool.getAndroidId(mContext);
        setFlavors();
    }

    private static void setFlavors() {
        if (FLAVORS == null) FLAVORS = new HashMap<String,Integer>();
        FLAVORS.clear();
        FLAVORS.put("ieway", 0);    //一位官方平台
        FLAVORS.put("qihu360", 10);    //360软件中心
        FLAVORS.put("tencent", 20); //腾讯应用宝
        FLAVORS.put("huawei", 30);  //华为应用市场
        FLAVORS.put("xiaomi", 40);  //小米应用市场
        FLAVORS.put("vivo", 50);    //vivo应用市场
//            FLAVORS.put("apple", 60);
        FLAVORS.put("meizu", 70);   //魅族
        FLAVORS.put("oppo", 80);    //OPPO
        FLAVORS.put("samsung", 90); //三星
        FLAVORS.put("lenovo", 100); //联想
        FLAVORS.put("ali", 110);    //阿里应用开放平台
        FLAVORS.put("soguo", 120);  //搜狗
        FLAVORS.put("baidu", 130);  //百度助手
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
