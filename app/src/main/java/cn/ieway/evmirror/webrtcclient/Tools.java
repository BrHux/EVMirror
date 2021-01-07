package cn.ieway.evmirror.webrtcclient;


import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FileName: Tools
 * Author: Admin
 * Date: 2021/1/6 14:42
 * Description:
 */
public class Tools {
    private static String TAG = "webRtcTools";
    private static String TAG1 = "webRtcTools1";

    private static Tools tools;

    public Tools() {
    }

    public static Tools getInstance() {
        if (tools == null) {
            synchronized (Tools.class) {
                if (tools == null) {
                    tools = new Tools();
                }
            }
        }
        return tools;
    }

    public static final String VIDEO_CODEC_VP8 = "VP8";
    public static final String VIDEO_CODEC_VP9 = "VP9";
    public static final String VIDEO_CODEC_H264 = "H264";
    public static final String AUDIO_CODEC_OPUS = "opus";
    public static final String AUDIO_CODEC_ISAC = "ISAC";


    private String preferCodec(String sdpDescription, String codec, boolean isAudio) {
        String[] lines = sdpDescription.split("\r\n");
        int mLineIndex = -1;
        String codecRtpMap = null;
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        String regex = "^a=rtpmap:(\\d+) " + codec + "(/\\d+)+[\r]?$";
        Pattern codecPattern = Pattern.compile(regex);
        String mediaDescription = "m=video ";
        if (isAudio) {
            mediaDescription = "m=audio ";
        }
        for (int i = 0; (i < lines.length)
                && (mLineIndex == -1 || codecRtpMap == null); i++) {
            if (lines[i].startsWith(mediaDescription)) {
                mLineIndex = i;
                continue;
            }
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1);
                continue;
            }
        }
        if (mLineIndex == -1) {
            Log.w(TAG, "No " + mediaDescription + " line, so can't prefer " + codec);
            return sdpDescription;
        }
        if (codecRtpMap == null) {
            Log.w(TAG, "No rtpmap for " + codec);
            return sdpDescription;
        }
        Log.d(TAG, "Found " + codec + " rtpmap " + codecRtpMap + ", prefer at "
                + lines[mLineIndex]);
        String[] origMLineParts = lines[mLineIndex].split(" ");
        if (origMLineParts.length > 3) {
            StringBuilder newMLine = new StringBuilder();
            int origPartIndex = 0;
            // Format is: m=<media> <port> <proto> <fmt> ...
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(origMLineParts[origPartIndex++]).append(" ");
            newMLine.append(codecRtpMap);
            for (; origPartIndex < origMLineParts.length; origPartIndex++) {
                if (!origMLineParts[origPartIndex].equals(codecRtpMap)) {
                    newMLine.append(" ").append(origMLineParts[origPartIndex]);
                }
            }
            lines[mLineIndex] = newMLine.toString();
            Log.d(TAG, "Change media description: " + lines[mLineIndex]);
        } else {
            Log.e(TAG, "Wrong SDP media description format: " + lines[mLineIndex]);
        }
        StringBuilder newSdpDescription = new StringBuilder();
        for (String line : lines) {
            newSdpDescription.append(line).append("\r\n");
        }
        return newSdpDescription.toString();
    }

    public String sortVideoCodec(String sdpDescription, String videoCodec, String sdp) {
        String[] lines = sdpDescription.split("\r\n");
        int mLineIndex = -1;
        String codecRtpMap = null;
        String codecRtpMapVp8 = null;
        String codecRtpMapVp9 = null;
        // a=rtpmap:<payload type> <encoding name>/<clock rate> [/<encoding parameters>]
        String regex = "^a=rtpmap:(\\d+) " + videoCodec + "(/\\d+)+[\r]?$";
        Pattern codecPattern = Pattern.compile(regex);

        String regexVp8 = "^a=rtpmap:(\\d+) " + VIDEO_CODEC_VP8 + "(/\\d+)+[\r]?$";
        Pattern codecPatternVp8 = Pattern.compile(regexVp8);

        String regexVp9 = "^a=rtpmap:(\\d+) " + VIDEO_CODEC_VP9 + "(/\\d+)+[\r]?$";
        Pattern codecPatternVp9 = Pattern.compile(regexVp9);

        String mediaDescription = "m=video ";
        for (int i = 0; (i < lines.length) && (mLineIndex == -1 || codecRtpMap == null || codecRtpMapVp8 == null || codecRtpMapVp9 == null); i++) {

            Log.d(TAG1, " ===== : " + lines[i] + "  / " + i);

            if (lines[i].startsWith(mediaDescription)) {
                mLineIndex = i;
                continue;
            }
            Matcher codecMatcher = codecPattern.matcher(lines[i]);
            if (codecMatcher.matches()) {
                codecRtpMap = codecMatcher.group(1);
                continue;
            }

            Matcher codecMatcherVp8 = codecPatternVp8.matcher(lines[i]);
            if (codecMatcherVp8.matches()) {
                codecRtpMapVp8 = codecMatcherVp8.group(1);
                continue;
            }

            Matcher codecMatcherVp9 = codecPatternVp9.matcher(lines[i]);
            if (codecMatcherVp9.matches()) {
                codecRtpMapVp9 = codecMatcherVp9.group(1);
                continue;
            }
        }


        if (mLineIndex == -1) {
            Log.w(TAG1, "No " + mediaDescription + " line, so can't prefer " + videoCodec);
            return sdpDescription;
        }
        if (codecRtpMap == null) {
            Log.w(TAG1, "No rtpmap for " + videoCodec);
            return sdpDescription;
        }
        Log.d(TAG1, "Found " + videoCodec + " rtpmap " + codecRtpMap + ", prefer at "
                + lines[mLineIndex]);
//        lines[mLineIndex].replace(codecRtpMapVp9,"");
//        lines[mLineIndex].replace(codecRtpMapVp8,"");
        String strV8 = lines[mLineIndex].replace(codecRtpMapVp8 + " ", "");
//         strV8 = lines[mLineIndex].replace(codecRtpMapVp9 + " ", "");
        strV8 = strV8.replace(97 + " ", "");
//        strV8 = strV9.replace(99 + " ", "");
        Log.d(TAG, "sortVideoCodec: -------------- 000 :" + strV8);
        lines[mLineIndex] = strV8;

        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < lines.length; i++) {
            if (
                    lines[i].startsWith("a=rtpmap:" + codecRtpMapVp8) ||
                            lines[i].startsWith("a=rtcp-fb:" + codecRtpMapVp8) ||
                            lines[i].startsWith("apt=" + codecRtpMapVp8)
//                    || lines[i].startsWith("apt=" + codecRtpMapVp9)
//                    lines[i].startsWith("a=rtcp-fb:" + codecRtpMapVp9)
//                    || lines[i].startsWith("a=rtpmap:" + codecRtpMapVp9)
                            || lines[i].startsWith("a=rtpmap:" + 97)
//                    || lines[i].startsWith("a=rtpmap:" + 99)
                            || lines[i].startsWith("a=rtcp-fb:" + 97)
//                    || lines[i].startsWith("a=rtcp-fb:" + 99)
                            || lines[i].startsWith("a=fmtp:" + 97)
//                    || lines[i].startsWith("a=fmtp:" + 99)
            ) {
                continue;
            }
            if (lines[i].contains("apt=" + codecRtpMapVp8)) {
//                String s = lines[i].replace("apt=" + codecRtpMapVp8,"");
//                lines[i] = s;
                continue;
            }
            if (lines[i].contains("apt=" + codecRtpMapVp9)) {
//                String s = lines[i].replace("apt=" + codecRtpMapVp9,"");
//                lines[i] = s;
//                continue;
            }

            stringList.add(lines[i]);
        }

//        String[] headLines = lines[mLineIndex].split(" ");
//        int vp8 = 0;
//        int h264 = 0;
//        int vp9 = 0;
//        for (int i = 0; i < headLines.length; i++) {
//            Log.d(TAG, "sortVideoCodec: -------------- 000  : " + headLines[i]);
//            if (headLines[i].equals(codecRtpMapVp8)) {
//                vp8 = i;
//                continue;
//            }
//            if (headLines[i].equals(codecRtpMapVp9)) {
//                vp9 = i;
//                continue;
//            }
//            if (headLines[i].equals(codecRtpMap)) {
//                h264 = i;
//                continue;
//            }
//        }
//
//        if (vp8 < vp9) {
//            String tem = headLines[vp8];
//            headLines[vp8] = headLines[vp9];
//            headLines[vp9] = tem;
//        }
//
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < headLines.length; i++) {
//            String s = " ";
//            if(i==headLines.length-1){
//                s = "";
//            }
//            sb.append(headLines[i]+s); //append String并不拥有该方法，所以借助StringBuffer
//        }
//        lines[mLineIndex] = sb.toString();

        Log.d(TAG, "sortVideoCodec: -------------- 111 :" + lines[mLineIndex]);

        StringBuilder newSdpDescription = new StringBuilder();
        for (String line : stringList) {
            newSdpDescription.append(line).append("\r\n");
        }
        Log.d(TAG, "sortVideoCodec: --------------  end \n:" + newSdpDescription.toString());
        return newSdpDescription.toString();
    }
}
