package cn.ieway.evmirror.net.util;

import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import cn.ieway.evmirror.application.Const;

/**
 * Created by free宇  2017/7/27. 11:44
 * author:free宇
 * email:freexiaoyu@foxmail.com
 * describe: AES加密算法
 */

public class AesUtils {

    public static final String ALGORITHM = "AES";

    public static String AesKey(int version) {
        if(version == 100){
            return Const.AES_SIGN_KEY;
        }
        else return "";
    }

    public static int AesVersion() {
        return  100;
    }


    public static byte[] decryptBASE64(String key) throws Exception {
        return Base64.decode(key, Base64.DEFAULT);
    }

    public static String encryptBASE64(byte[] key) throws Exception {
        return Base64.encodeToString(key, Base64.DEFAULT);
    }

    private static Key toKey(byte[] key) throws Exception {
        //DESKeySpec dks = new DESKeySpec(key);
        //SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
        //SecretKey secretKey = keyFactory.generateSecret(dks);

        // 当使用其他对称加密算法时，如AES、Blowfish等算法时，用下述代码替换上述三行代码
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);

        return secretKey;
    }

    public static byte[] encrypt(byte[] data, String key) throws Exception {
        Key k = toKey(decryptBASE64(key));
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, k);
        return cipher.doFinal(data);
    }

    public static String encrypt(byte[] data) throws Exception {
        byte[] outputData = new byte[0];
        String desKey = encryptBASE64(AesKey(AesVersion()).getBytes());
        outputData = encrypt(data,desKey);
        String result = new String(encryptBASE64(outputData).getBytes(),"utf-8");
        result = result.replaceAll("\r","");
        result = result.replaceAll("\n","");
        return result;
    }

    public static byte[] decrypt(byte[] data, String key) throws Exception {
        Key k = toKey(decryptBASE64(key));
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(String data) throws Exception {
        Key k = toKey(AesKey(AesVersion()).getBytes());
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, k);

        return cipher.doFinal(decryptBASE64(data));
    }


}
