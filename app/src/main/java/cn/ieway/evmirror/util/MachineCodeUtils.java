package cn.ieway.evmirror.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.provider.Settings.Secure;
import android.util.Log;

import java.util.UUID;

import cn.ieway.evmirror.application.MirrorApplication;

import static cn.ieway.evmirror.application.MirrorApplication.sMe;


public class MachineCodeUtils
{

	private static String TAG = MachineCodeUtils.class.getSimpleName();

    public static String getAndroidId(Context context){
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}

	public static String getSignature(Context context){
		String result = null;
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
			Signature sign = info.signatures[0];
			result = String.valueOf(sign.hashCode());
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getMachineId() {
        Context context = sMe.getApplicationContext();
		final String signature, androidId;
        signature = "" + getSignature(context);
		androidId = "" + getAndroidId(context);
		UUID deviceUuid = new UUID(androidId.hashCode(), ((long) signature.hashCode()));
		String uniqueId = deviceUuid.toString();
		return uniqueId;
	}
}
