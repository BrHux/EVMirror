package cn.ieway.evmirror.net;

import com.alibaba.fastjson.JSONObject;
import com.tamsiree.rxkit.RxDataTool;

import java.util.HashMap;
import java.util.Map;

import cn.ieway.evmirror.application.BaseConfig;
import cn.ieway.evmirror.application.Const;
import cn.ieway.evmirror.net.okhttp.CallBackUtil;
import cn.ieway.evmirror.net.okhttp.OkhttpUtil;
import cn.ieway.evmirror.util.AesUtils;
import cn.ieway.evmirror.util.SignUtils;
import okhttp3.Call;

/**
 * FileName: MineNetUtils
 * Author: Admin
 * Date: 2020/7/31 17:21
 * Description: '我的’模块相关选项网络请求
 */
public class MineNetUtils {

    public static void checkAPPVersion(CallBackUtil callBackUtil) throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("platform", Const.PLATFORM_TYPE);
        jsonObject.put("app_name", "EVRemote");
        jsonObject.put("app_version", BaseConfig.APP_VERSION);
        jsonObject.put("req_time", System.currentTimeMillis());
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

}
