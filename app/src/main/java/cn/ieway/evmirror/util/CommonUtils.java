package cn.ieway.evmirror.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.alibaba.fastjson.JSONObject;
import com.tamsiree.rxkit.RxDataTool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CommonUtils {

    private static final char[] encodeTable = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
            'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9'};

    public static boolean isViewHide(View view) {
        if (view.getVisibility() == View.INVISIBLE || view.getVisibility() == View.GONE) {
            return true;
        }
        return false;
    }

    public static boolean isViewDisplay(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            return true;
        }
        return false;
    }


    /**
     * 显示输入法键盘
     *
     * @param context
     * @param view    目标控件
     */
    public static void showSoftInput(Context context, View view) {
//        if(isInputMetodActive(context)) return;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null && imm != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * 隐藏输入法键盘
     *
     * @param context
     * @param view    目标控件
     */
    public static void hideSoftInput(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (view != null && imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            // imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT); // 或者第二个参数传InputMethodManager.SHOW_IMPLICIT
        }
    }


    public static boolean isInputMetodActive(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        imm.isAcceptingText();
        return isOpen;
    }


    /**
     * 获取随机String
     *
     * @param len
     * @return
     */
    public static String getRandomString(int len) {
        String returnStr = "";
        char[] ch = new char[len];
        Random rd = new Random();
        for (int i = 0; i < len; i++) {
            ch[i] = (char) (rd.nextInt(9) + 65);
            ch[i] = encodeTable[rd.nextInt(36)];
        }
        returnStr = new String(ch);
        return returnStr;
    }


    /**
     * 判断是否为json字符串
     *
     * @param content
     * @return
     */
    public static boolean isJSONString(String content) {
        if (RxDataTool.isEmpty(content)) {
            return false;
        }
//        if (!content.startsWith("{") || !content.endsWith("}")) {
//            return false;
//        }
        try {
            JSONObject.parse(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public static boolean isNotJSONString(String content) {
        if (RxDataTool.isEmpty(content)) {
            return false;
        }
        try {
            JSONObject.parse(content);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * 将秒数转换为日时分秒，
     * @param second
     * @return
     */
    public static String secondToTime(long second){
        long days = second / 86400;            //转换天数
        second = second % 86400;            //剩余秒数
        long hours = second / 3600;            //转换小时
        second = second % 3600;                //剩余秒数
        long minutes = second /60;            //转换分钟
        second = second % 60;                //剩余秒数
        if(days>0){
            return days + "天" + hours + "小时" + minutes + "分" + second + "秒";
        }else{
            return hours + "小时" + minutes + "分" + second + "秒";
        }
    }

    /**
     * 将秒数转换为时分秒，
     * @param second
     * @return
     */
    public static String secondToTime2(long second){
//        long days = second / 86400;            //转换天数
//        second = second % 86400;            //剩余秒数
        long hours = second / 3600;            //转换小时
        second = second % 3600;                //剩余秒数
        long minutes = second /60;            //转换分钟
        second = second % 60;                //剩余秒数
        return hours + "小时" + minutes + "分" + second + "秒";
    }



    /**
     * 将日期转换为日时分秒
     * @param date
     * @return
     */
    public static String dateToTime(String date, String dateStyle){
        SimpleDateFormat format = new SimpleDateFormat(dateStyle);
        try {
            Date oldDate = format.parse(date);
            long time = oldDate.getTime();                    //输入日期转换为毫秒数
            long nowTime = System.currentTimeMillis();        //当前时间毫秒数
            long second = nowTime - time;                    //二者相差多少毫秒
            second = second / 1000;                            //毫秒转换为妙
            long days = second / 86400;
            second = second % 86400;
            long hours = second / 3600;
            second = second % 3600;
            long minutes = second /60;
            second = second % 60;
            if(days>0){
                return days + "天" + hours + "小时" + minutes + "分" + second + "秒";
            }else{
                return hours + "小时" + minutes + "分" + second + "秒";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    /** 检测麦克风是否可用
     * @return
     */
    public static boolean validateMicAvailability() {
        Boolean available = true;
        AudioRecord recorder =
                new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_DEFAULT, 44100);
        if (recorder == null) {
            return false;
        }
        try {
            if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
                available = false;
            }
            recorder.startRecording();
            if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                recorder.stop();
                available = false;
            }
            recorder.stop();
        } catch (Exception e) {
            available = false;
            Log.e("remote","validateMicAvailability exception " + e.toString());
        } finally {
            if (recorder != null) {
                recorder.release();
                recorder = null;
            }
        }
        return available;
    }


    /**
     * 复制内容到剪贴板
     *
     * @param content
     * @param context
     */
    public static void copyContentToClipboard(String content, Context context) {
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("Label", content);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
    }

}
