package cn.ieway.evmirror.net.util;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by eway on 2018/6/1.
 */

public class MD5Utils {


    public static byte[] encryptMD5(byte[] data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data);
        return md5.digest();
    }

    public static String encrypt(byte[] data){
        BigInteger md5Data = null;
        try {
            md5Data = new BigInteger(1, encryptMD5(data));
        }catch (Exception e){
            e.printStackTrace();
        }
        String md5Str = md5Data.toString(16);
        if(md5Str.length()<32){
            md5Str = 0 + md5Str;
        }
        return md5Str;
    }

}
