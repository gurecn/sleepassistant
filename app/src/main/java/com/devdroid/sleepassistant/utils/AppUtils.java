package com.devdroid.sleepassistant.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.devdroid.sleepassistant.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 类名称：AppUtils 类描述：应用程序相关操作
 */
public class AppUtils {
	public final static int NETTYPE_MOBILE = 0; // 中国移动 //CHECKSTYLE IGNORE
	public final static int NETTYPE_UNICOM = 1; // 中国联通 //CHECKSTYLE IGNORE
	public final static int NETTYPE_TELECOM = 2; // 中国电信 //CHECKSTYLE IGNORE
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

	/**
	 * 获取语言包请求的product id <li>
	 * 产品id请查看后台：http://pbasi18n01.rmz.gomo.com:8088/admin
	 *
	 * @return
	 */
	public static String getLangProductID() {
		return "1004";
	}


	/**
	 * 获取SIM卡所在的国家
	 *
	 * @author xiedezhi
	 * @param context
	 * @return 当前手机sim卡所在的国家，如果没有sim卡，取本地语言代表的国家
	 */
	public static String getLocal(Context context) {
		String local = null;
		try {
			TelephonyManager telManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (telManager != null) {
				local = telManager.getSimCountryIso();
			}
		} catch (Throwable e) {
		}
		if (TextUtils.isEmpty(local)) {
			local = Locale.getDefault().getCountry().toUpperCase(Locale.US);
		}
		if (TextUtils.isEmpty(local)) {
			local = "US";
		}
		return local;
	}

	/**
	 * 获取网络类型
	 *
	 * @author huyong
	 * @param context
	 * @return 1 for 移动，2 for 联通，3 for 电信，-1 for 不能识别
	 */
	public static int getNetWorkType(Context context) {
		int netType = -1;
		// 从系统服务上获取了当前网络的MCC(移动国家号)，进而确定所处的国家和地区
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String simOperator = manager.getSimOperator();
		if (simOperator != null) {
			if (simOperator.startsWith("46000") || simOperator.startsWith("46002")) {
				// 因为移动网络编号46000下的IMSI已经用完，
				// 所以虚拟了一个46002编号，134/159号段使用了此编号
				// 中国移动
				netType = NETTYPE_MOBILE;
			} else if (simOperator.startsWith("46001")) {
				// 中国联通
				netType = NETTYPE_UNICOM;
			} else if (simOperator.startsWith("46003")) {
				// 中国电信
				netType = NETTYPE_TELECOM;
			}
		}
		return netType;
	}
	/**
	 * 根据包名获取应用Icon
	 */
	public static Bitmap loadAppIcon(Context context, String appPackageName) {
		Bitmap bitmap = null;
		BitmapDrawable drawable = (BitmapDrawable) getApplicationDrawable(
				context, appPackageName);
		if (drawable != null) {
			bitmap = drawable.getBitmap();
		} else if (drawable == null) {
			// 兼容未安装应用
			BitmapDrawable drawable2 = (BitmapDrawable) getApplicationDrawableIfNotInstalled(
					context, appPackageName);
			if (drawable2 != null) {
				bitmap = drawable2.getBitmap();
			} else {
				BitmapDrawable drawable3 = (BitmapDrawable) context
						.getResources().getDrawable(
								R.drawable.zhuopin_logo);
				bitmap = drawable3.getBitmap();
			}
		}
		return bitmap;
	}

	private static Drawable getApplicationDrawable(Context context,
												   String pkgName) {
		PackageManager pm = context.getPackageManager();
		Drawable drawable = null;
		try {
			drawable = pm.getApplicationIcon(pkgName);
			if (!(drawable instanceof BitmapDrawable)) {
				drawable = null;
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return drawable;
	}

	private static Drawable getApplicationDrawableIfNotInstalled(
			Context context, String path) {
		PackageManager pm = context.getPackageManager();
		try {
			PackageInfo packageInfo = pm.getPackageArchiveInfo(path,
					PackageManager.GET_ACTIVITIES);

			if (packageInfo != null) {
				ApplicationInfo appInfo = packageInfo.applicationInfo;
				appInfo.sourceDir = path;
				appInfo.publicSourceDir = path;
				try {
					return appInfo.loadIcon(pm);
				} catch (OutOfMemoryError e) {
				}
			}
		} catch (Exception e) {

		}
		return null;
	}
	public static String getAppName(final Context context,
									final String packageName) {
		PackageInfo info = getAppPackageInfo(context, packageName);
		return getAppName(context, info);
	}

	public static String getAppName(final Context context, PackageInfo info) {
		if (info != null) {
			return info.applicationInfo.loadLabel(context.getPackageManager())
					.toString();
		}
		return "";
	}

	/**
	 * 获取app包信息
	 *
	 * @param context
	 * @param packageName 包名
	 * @return
	 */
	public static PackageInfo getAppPackageInfo(final Context context,
												final String packageName) {
		PackageInfo info = null;
		try {
			info = context.getPackageManager().getPackageInfo(packageName, 0);
		} catch (Exception e) {
			info = null;
			e.printStackTrace();
		}
		return info;
	}
	public static List<String> getLauncherAppPackageNames(Context context) {
		List<ResolveInfo> infos = AppUtils.getLauncherApps(context);
		List<String> packNames = new ArrayList<String>();
		if (infos != null) {
			for (int i = 0; i < infos.size(); i++) {
				ResolveInfo packageInfo = infos.get(i);
				if (packageInfo != null) {
					String packageName = packageInfo.activityInfo.packageName;
					if (!packNames.contains(packageName)) {
						packNames.add(packageName);
					}
				}
			}
		}

		return packNames;
	}
	/**
	 * 获取在功能菜单出现的程序列表
	 *
	 * @param context 上下文
	 * @return 程序列表，类型是 List<ResolveInfo>
	 */
	public static List<ResolveInfo> getLauncherApps(Context context) {
		List<ResolveInfo> infos = null;
		PackageManager packageMgr = context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		try {
			infos = packageMgr.queryIntentActivities(intent, 0);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		packageMgr = null;
		return infos;
	}
}
