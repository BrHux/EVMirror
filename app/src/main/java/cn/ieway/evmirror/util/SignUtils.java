package cn.ieway.evmirror.util;

import android.os.Build;

import com.alibaba.fastjson.JSONObject;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import cn.ieway.evmirror.application.Const;

/**
 * FileName: SginUtils
 * Author: Admin
 * Date: 2020/7/21 9:09
 * Description:
 */
public class SignUtils {


    public static String createSign(JSONObject jsonObject) throws JSONException {
        Set<String> keys = jsonObject.keySet();
        List<String> keyList = new ArrayList<String>();
        for (String key : keys) {
            if (key.equals("sign")) {
                continue;
            }
            keyList.add(key);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            keyList.sort(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
        } else {
            Collections.sort(keyList, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
        }
        String signStr = "";
        for (String str : keyList) {
            signStr += str + "=" + jsonObject.get(str).toString() + "&";
        }

        signStr += "&" + Const.MD5_SIGN_KEY;
        return MD5Utils.encrypt(signStr.getBytes());
    }

}
