package cn.ieway.evmirror.net;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.ieway.evmirror.application.BaseConfig;
import cn.ieway.evmirror.application.Const;
import cn.ieway.evmirror.net.okhttp.CallBackUtil;
import cn.ieway.evmirror.net.okhttp.OkhttpUtil;
import cn.ieway.evmirror.net.util.AesUtils;
import cn.ieway.evmirror.net.util.SignUtils;
import cn.ieway.evmirror.util.MachineCodeUtils;
import okhttp3.Call;

import static cn.ieway.evmirror.application.MirrorApplication.sMe;

/**
 * FileName: MineNetUtils
 * Author: Admin
 * Date: 2020/7/31 17:21
 * Description: '我的’模块相关选项网络请求
 */
public class CommonRequest {

    public static void checkAPPVersion(CallBackUtil callBackUtil) throws Exception {
        JSONObject jsonObject = baseJsonObject();
        jsonObject.put("app_name", "EVScreenMirror");
        jsonObject.put("machine_code", MachineCodeUtils.getMachineId());

        String bufStr = SignUtils.createSign(jsonObject);
        jsonObject.put("sign", bufStr);

        String result = "";
        result = AesUtils.encrypt(jsonObject.toString().getBytes());

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("version", "100");
        paramsMap.put("params", result);
        OkhttpUtil.okHttpPost(ApiHelper.CHECK_APP_VERSION, paramsMap, new CallBackUtil.CallBackString() {
            @Override
            public void onFailure(Call call, Exception e) {
                callBackUtil.onFailure(call, e);
            }

            @Override
            public void onResponse(String response) {
                callBackUtil.onResponse(response);
            }
        });
    }

    public static JSONObject baseJsonObject(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("req_time", System.currentTimeMillis());
        jsonObject.put("product_type", Const.PRODUCT_TYPE);
        jsonObject.put("channel_id", getChannelId());
        jsonObject.put("platform", Const.PLATFORM_TYPE);
        jsonObject.put("app_version", BaseConfig.appVersionName);
        jsonObject.put("device_name", BaseConfig.deviceName);
        return jsonObject;
    }




    /**
     * 获取渠道版本号
     */
    private static int getChannelId() {
        ApplicationInfo info = null;
        try {
            info = sMe.getPackageManager().getApplicationInfo(sMe.getPackageName(), PackageManager.GET_META_DATA);
        } catch (Exception e) {
            return 0;
        }
        if (info == null) return 0;
        String umeng_channel = info.metaData.getString("UMENG_CHANNEL");
        Integer channelId = BaseConfig.FLAVORS.get(umeng_channel);
        if (channelId == null) channelId = 0;
        return channelId;
    }

}
