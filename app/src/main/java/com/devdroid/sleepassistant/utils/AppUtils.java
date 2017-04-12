package com.devdroid.sleepassistant.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import java.util.List;

/**
 * 类名称：AppUtils 类描述：应用程序相关操作
 */
public class AppUtils {
	/**
	 * 获得当前所在进程的进程名<br>
	 */
	public static String getCurrentProcessName(Context cxt) {
		ActivityManager actMgr = (ActivityManager) cxt
				.getSystemService(Context.ACTIVITY_SERVICE);
		if (actMgr == null) {
			return null;
		}
		final List<RunningAppProcessInfo> runningAppProcesses = actMgr
				.getRunningAppProcesses();
		if (runningAppProcesses == null) {
			return null;
		}
		final int pid = android.os.Process.myPid();
		for (RunningAppProcessInfo appProcess : runningAppProcesses) {
			if (appProcess != null && appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

	private static String sChannel;
	/**
	 * 获取桌面渠道号的方法
	 */
	static String getChannel(Context ctx) {
		if (sChannel == null) {
			try {
				ApplicationInfo appInfo = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
				sChannel = appInfo.metaData.getString("channel");
			} catch (Exception e) {
				sChannel = "";
			}
		}
		return sChannel;
	}

	/**
	 * 获取版本名
	 */
	static String getVersionName(Context context) {
		String name = "";
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			name = info.versionName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}

	/**
	 * 获取版本号
	 */
	public static int getVersionCode(Context context) {
		int code = 0;
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo info = pm.getPackageInfo(context.getPackageName(), 0);
			code = info.versionCode;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return code;
	}
}
