package cn.ieway.evmirror.net;

import com.alibaba.fastjson.JSONObject;
import com.tamsiree.rxkit.RxDataTool;
import com.tamsiree.rxkit.view.RxToast;

import java.io.UnsupportedEncodingException;

import cn.ieway.evmirror.util.AesUtils;
import cn.ieway.evmirror.util.CommonUtils;

/**
 * FileName: DataUtils
 * Author: Admin
 * Date: 2020/7/27 17:05
 * Description:
 */
public class DataUtils {

    public static String getError(String response) {
        try {
            JSONObject object = JSONObject.parseObject(response);
            int errorCode = object.getIntValue("errcode");
            String errorMsg = null;
            if (errorCode != 0) {
                errorMsg = object.getString("errmsg");
            }
            return errorMsg;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }



    /**返回数据解密
     * @param response
     * @return
     */
    public static JSONObject getObject(String response) {
        String result = decryptResult(getResultString(response));
        return JSONObject.parseObject(result);
    }

    /**返回数据解密
     * @param jsonStr
     * @return
     */
    public static String decryptResult(String jsonStr) {
        String deviceString = "";
        try {
            byte[] result = AesUtils.decrypt(jsonStr);
            deviceString = new String(result, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceString;
    }

    /**
     * 服务器返回数据获取result
     *
     * @param response
     * @return
     */
    public static String getResultString(String response) {
        JSONObject object = JSONObject.parseObject(response);
        return object.getString("result");
    }

    /**
     * 服务器返回数据获取errcode
     *
     * @param response
     * @return
     */
    public static int getErroCode(String response) {
        JSONObject object = JSONObject.parseObject(response);
        return object.getIntValue("errcode");
    }

    /**
     * 服务器返回数据获取errmsg
     *
     * @param response
     * @return
     */
    public static String getErroMsg(String response) {
        JSONObject object = JSONObject.parseObject(response);
        return object.getString("errmsg");
    }

    /**
     * 服务器回调数据处理(加密内容解密)
     *
     * @param response
     * @return
     */
    public static String dealResponse(String response) {
        return  dealResponse(response,true);
    }

    /**
     * 服务器回调数据处理(加密内容解密)
     *
     * @param response
     * @param showmMsg
     * @return
     */
    public static String dealResponse(String response, boolean showmMsg) {
        if (RxDataTool.isNullString(response)) return "";
        if (CommonUtils.isNotJSONString(response)) return "";
        String result = "";
        try {
            JSONObject jsonObject = JSONObject.parseObject(response);
            int errorCode = jsonObject.getIntValue("errcode");
            int encrypt = jsonObject.getIntValue("encrypt");
            if (errorCode == 0) {
                if (encrypt == 1){
                    String data = jsonObject.getString("result");
                    byte[] bytes = new byte[0];
                    bytes = AesUtils.decrypt(data);
                    result = new String(bytes, "utf-8");
                }else {
                    String data = jsonObject.getString("result");
                    result = data;
                }
            }
            else {
                if (showmMsg) RxToast.error(errorCode + ": " + jsonObject.getString("errmsg"));
            }
        } catch (Exception e) {
            RxToast.error(e.toString());
            e.printStackTrace();
        }
        return result;
    }

}
