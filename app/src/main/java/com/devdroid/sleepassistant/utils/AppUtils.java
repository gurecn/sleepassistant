package com.devdroid.sleepassistant.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.activity.FeedbackTXActivity;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.application.TheApplication;
import com.devdroid.sleepassistant.constant.CustomConstant;
import com.devdroid.sleepassistant.mode.AppLockBean;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * 类名称：AppUtils 类描述：应用程序相关操作
 */
public class AppUtils {
	private final static int NETTYPE_MOBILE = 0; // 中国移动 //CHECKSTYLE IGNORE
	private final static int NETTYPE_UNICOM = 1; // 中国联通 //CHECKSTYLE IGNORE
	private final static int NETTYPE_TELECOM = 2; // 中国电信 //CHECKSTYLE IGNORE
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
	static int getVersionCode(Context context) {
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
	 * 获取SIM卡所在的国家
	 * @return 当前手机sim卡所在的国家，如果没有sim卡，取本地语言代表的国家
	 */
	static String getLocal(Context context) {
		String local = null;
		try {
			TelephonyManager telManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			if (telManager != null) {
				local = telManager.getSimCountryIso();
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	 * @return 1 for 移动，2 for 联通，3 for 电信，-1 for 不能识别
	 */
	static int getNetWorkType(Context context) {
		int netType = -1;
		// 从系统服务上获取了当前网络的MCC(移动国家号)，进而确定所处的国家和地区
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String simOperator = manager.getSimOperator();
		if (simOperator != null) {
			if (simOperator.startsWith("46000") || simOperator.startsWith("46002")) {
				netType = NETTYPE_MOBILE;
			} else if (simOperator.startsWith("46001")) {
				netType = NETTYPE_UNICOM;
			} else if (simOperator.startsWith("46003")) {
				netType = NETTYPE_TELECOM;
			}
		}
		return netType;
	}
	/**
	 * 根据包名获取应用Icon
	 */
	public static Bitmap loadAppIcon(Context context, String appPackageName) {
		Bitmap bitmap;
		BitmapDrawable drawable = (BitmapDrawable) getApplicationDrawable(context, appPackageName);
		if (drawable != null) {
			bitmap = drawable.getBitmap();
		} else {
			// 兼容未安装应用
			BitmapDrawable drawable2 = (BitmapDrawable) getApplicationDrawableIfNotInstalled(context, appPackageName);
			if (drawable2 != null) {
				bitmap = drawable2.getBitmap();
			} else {
				BitmapDrawable drawable3 = (BitmapDrawable) context.getResources().getDrawable(R.mipmap.ic_launcher);
				bitmap = drawable3.getBitmap();
			}
		}
		return bitmap;
	}

	private static Drawable getApplicationDrawable(Context context, String pkgName) {
		PackageManager pm = context.getPackageManager();
		Drawable drawable = null;
		try {
			drawable = pm.getApplicationIcon(pkgName);
			if (!(drawable instanceof BitmapDrawable)) {
				drawable = null;
			}
		} catch (PackageManager.NameNotFoundException | OutOfMemoryError e) {
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
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String getAppName(final Context context,
									final String packageName) {
		PackageInfo info = getAppPackageInfo(context, packageName);
		return getAppName(context, info);
	}

	private static String getAppName(final Context context, PackageInfo info) {
		if (info != null) {
			return info.applicationInfo.loadLabel(context.getPackageManager())
					.toString();
		}
		return "";
	}

	/**
	 * 获取app包信息
	 */
	private static PackageInfo getAppPackageInfo(final Context context, final String packageName) {
		PackageInfo info;
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
		List<String> packNames = new ArrayList<>();
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
	 * 获取安装的应用，返回AppLockBean
	 * @param context context对象
	 * @return
	 */
	public static List<AppLockBean> getAppPackages(Context context) {
		List<ResolveInfo> infos = AppUtils.getLauncherApps(context);
		List<AppLockBean> launcherApps = new ArrayList<>();
		List<String> packNames = new ArrayList<>();
		PackageManager pm = context.getPackageManager();
		List<String> lockedApp = LauncherModel.getInstance().getLockerDao().queryLockerInfo();
		List<String>  homePackageNames = getLauncherPackageNames(context);
		if (infos != null) {
			for (int i = 0; i < infos.size(); i++) {
				ResolveInfo packageInfo = infos.get(i);
				if (packageInfo != null) {
					Drawable drawable = packageInfo.loadIcon(pm);
					String packageName = packageInfo.activityInfo.packageName;
					if (!homePackageNames.contains(packageName) && !packNames.contains(packageName) && !packageName.equals(CustomConstant.PACKAGE_NAME)) {
						packNames.add(packageName);
						launcherApps.add(new AppLockBean(lockedApp.contains(packageName),packageName, drawable));
					}
				}
			}
		}
		return launcherApps;
	}

	/**
	 * 获取在功能菜单出现的程序列表
	 * @param context 上下文
	 * @return 程序列表，类型是 List<ResolveInfo>
	 */
	private static List<ResolveInfo> getLauncherApps(Context context) {
		List<ResolveInfo> infos = null;
		PackageManager packageMgr = context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		try {
			infos = packageMgr.queryIntentActivities(intent, 0);
		} catch (OutOfMemoryError | Exception e) {
			e.printStackTrace();
		}
		return infos;
	}

	public static void gotoLauncherWithoutChoice(Context context, String usePkgname) {
		try {
			Intent intent;
			String launcher = getDefaultLauncher(context);
			if (null == launcher && !android.text.TextUtils.isEmpty(usePkgname)) {
				launcher = usePkgname;
			}
			if (null != launcher) {
				Intent intentToResolve = new Intent(Intent.ACTION_MAIN);
				intentToResolve.addCategory(Intent.CATEGORY_HOME);
				intentToResolve.setPackage(launcher);
				ResolveInfo ri = context.getPackageManager().resolveActivity(intentToResolve, 0);
				if (ri != null) {
					intent = new Intent(intentToResolve);
					intent.setClassName(ri.activityInfo.applicationInfo.packageName, ri.activityInfo.name);
					intent.setAction(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
				} else {
					intent = context.getPackageManager().getLaunchIntentForPackage(launcher);
					if (null == intent) {
						intent = new Intent(Intent.ACTION_MAIN);
						intent.setPackage(launcher);
					}
				}
			} else {
				intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
			}
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取默认运行桌面包名（注：存在多个桌面时且未指定默认桌面时，该方法返回Null,使用时需处理这个情况）
	 */
	private static String getDefaultLauncher(Context context) {
		final Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		final ResolveInfo res = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
		if (res.activityInfo == null) {
			// should not happen. A home is always installed, isn't it?
			return null;
		}
		if (res.activityInfo.packageName.equals("android")) {
			// 有多个桌面程序存在，且未指定默认项时；
			return null;
		} else {
			return res.activityInfo.packageName;
		}
	}
	/**
	 * 判断是否获取了android.permission.PACKAGE_USAGE_STAT权限<br>
	 * 该权限是系统级别的权限, 不授予第三方应用, 但是第三方应用可以让用户主动授权该权限<br>
	 * 用于5.1版本或以上版本<br>
	 */
	public static boolean isPermissionPackageUsageStatsGrandedLollipopMr1(Context context) {
		return Machine.HAS_SDK_5_1_1 && isPermissionPackageUsageStatsGranded(getSystemServiceUsageStatsManager(context));
	}
	/**
	 * 判断是否获取了android.permission.PACKAGE_USAGE_STAT权限<br>
	 * 该权限是系统级别的权限, 不授予第三方应用, 但是第三方应用可以让用户主动授权该权限<br>
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private static boolean isPermissionPackageUsageStatsGranded(UsageStatsManager usageStatsManager) {
		if (usageStatsManager != null && Machine.HAS_SDK_LOLLIPOP) {
			long endTime = System.currentTimeMillis();
			long beginTime = endTime - AlarmManager.INTERVAL_DAY;
			List<UsageStats> usageStatses = null;
			try {
				usageStatses = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginTime, endTime);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			return usageStatses != null && !usageStatses.isEmpty();
		}
		return false;
	}
	/**
	 * 获取系统应用信息管理实例UsageStatsManager<br>
	 * 只建议于5.0或以上使用<br>
	 */
	private static UsageStatsManager getSystemServiceUsageStatsManager(Context context) {
		UsageStatsManager usageStatsManager = null;
		if (Machine.HAS_SDK_5_1_1) {
			usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
		} else {
			try {
				usageStatsManager = (UsageStatsManager) context.getSystemService("usagestats");
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		return usageStatsManager;
	}

	/**
	 * 获取栈顶的应用的ComponentName<br>
	 * 注意只适用于5.1或以上<br>
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
	private static ComponentName getFrontActivityLollipopMr1(Context context) {
		return getFrontActivityLollipop(getSystemServiceUsageStatsManager(context));
	}

	/**
	 * 获取栈顶的应用的ComponentName<br>
	 * 注意只适用于5.0<br>
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private static ComponentName getFrontActivityOnLollipop(Context context) {
		ComponentName frontActivity = null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
			frontActivity = getFrontActivityLollipop(getSystemServiceUsageStatsManager(context));
		}
		if (frontActivity == null) {
			frontActivity = getFrontActivityOnLollipopByTrick(context);
		}
		return frontActivity;
	}

	/**
	 * 获取栈顶的应用的ComponentName<br>
	 * 注意只适用于5.0或以上<br>
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private static ComponentName getFrontActivityLollipop(UsageStatsManager usageStatsManager) {
		if (usageStatsManager == null) {
			return null;
		}
		if (Machine.HAS_SDK_LOLLIPOP) {
			long endTime = System.currentTimeMillis();
			long beginTime = endTime - 10000; // 获取10秒内的事件
			UsageEvents.Event event = new UsageEvents.Event();
			String packageName = null;
			String className = null;
			UsageEvents usageEvents = null;
			try {
				usageEvents = usageStatsManager.queryEvents(beginTime, endTime);
			} catch (Throwable e) {
				e.printStackTrace();
			}
			if (usageEvents != null) {
				while (usageEvents.hasNextEvent()) {
					usageEvents.getNextEvent(event);
					if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
						packageName = event.getPackageName();
						className = event.getClassName();
					}
				}
			}
			if (TextUtils.isEmpty(className)) {
				className = "";
			}
			if (!TextUtils.isEmpty(packageName)) {
				return new ComponentName(packageName, className);
			}
		}
		return null;
	}

	/**
	 * 在5.0系统使用的获取栈顶应用的补充方法<br>
	 */
	private static ComponentName getFrontActivityOnLollipopByTrick(Context context) {
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> pis = activityManager.getRunningAppProcesses();
			if (pis != null) {
				for (ActivityManager.RunningAppProcessInfo pi : pis) {
					if (pi.pkgList.length == 1) {
						return new ComponentName(pi.pkgList[0], "");
					}
				}
			}
		}
		return new ComponentName(getForegroundAppByProcFiles(context), "");
	}
	private final static FilenameFilter FILENAME_FILTER_NUMS = new FilenameFilter() {
		/**
		 * 匹配模式，只要数字
		 */
		private Pattern mPattern = Pattern.compile("^[0-9]+$");
		@Override
		public boolean accept(File dir, String filename) {
			return mPattern.matcher(filename).matches();
		}
	};
	/**
	 * first app user
	 */
	private static final int AID_APP = 10000;

	/**
	 * offset for uid ranges for each user
	 */
	private static final int AID_USER = 100000;
	/**
	 * 获取正在前台运行的应用的包名<br>
	 * 通过分析进程文件估算，可靠性有待考量, 可作为补充使用<br>
	 */
	private static String getForegroundAppByProcFiles(Context context) {
		File[] files = new File("/proc").listFiles(FILENAME_FILTER_NUMS);
		if (files == null || files.length == 0) {
			return "";
		}
		int lowestOomScore = Integer.MAX_VALUE;
		int foregroundProcessUid = -1;

		for (File file : files) {
			if (!file.isDirectory()) {
				continue;
			}
			int pid;
			try {
				pid = Integer.parseInt(file.getName());
			} catch (NumberFormatException e) {
				continue;
			}
			try {
				String cgroup = readFile(String.format("/proc/%d/cgroup", pid));

				String[] lines = cgroup.split("\n");

				if (lines.length != 2) {
					continue;
				}
				String cpuSubsystem = lines[0];
				String cpuAcctSubsystem = lines[1];
				if (!cpuAcctSubsystem.endsWith(Integer.toString(pid))) {
					continue;
				}
				if (cpuSubsystem.endsWith("bg_non_interactive")) {
					continue;
				}
				int uid = Integer.parseInt(cpuAcctSubsystem.split(":")[2].split("/")[1].replace("uid_", ""));
				if (uid >= 1000 && uid <= 1038) {
					// system process
					continue;
				}
				int appId = uid - AID_APP;
				while (appId > AID_USER) {
					appId -= AID_USER;
				}
				if (appId < 0) {
					continue;
				}
				File oomScoreAdj = new File(String.format("/proc/%d/oom_score_adj", pid));
				if (oomScoreAdj.canRead()) {
					int oomAdj = Integer.parseInt(readFile(oomScoreAdj.getAbsolutePath()));
					if (oomAdj != 0) {
						continue;
					}
				}
				int oomScore = Integer.parseInt(readFile(String.format("/proc/%d/oom_score", pid)));
				if (oomScore < lowestOomScore) {
					lowestOomScore = oomScore;
					foregroundProcessUid = uid;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (foregroundProcessUid != -1) {
			String[] uIds = context.getPackageManager().getPackagesForUid(foregroundProcessUid);
			if(uIds != null && uIds.length > 0) {
				return uIds[0];
			}
		}
		return "";
	}
	private static String readFile(String path) throws IOException {
		StringBuilder output = new StringBuilder();
		BufferedReader reader = new BufferedReader(new FileReader(path));
		output.append(reader.readLine());
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			output.append('\n').append(line);
		}
		reader.close();
		return output.toString();
	}

	/**
	 * 获取桌面类应用的包名.<br>
	 */
	public static List<String> getLauncherPackageNames(Context context) {
		List<String> packages = new ArrayList<>();
		PackageManager packageManager = context.getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = null;
		try {
			resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (resolveInfo != null && !resolveInfo.isEmpty()) {
			for (ResolveInfo info : resolveInfo) {
				// 过滤掉一些名不符实的桌面
				if (!TextUtils.isEmpty(info.activityInfo.packageName)) {
					packages.add(info.activityInfo.packageName);
				}
			}
		}
		return packages;
	}

	/**
	 * 判断是否获取了android.permission.PACKAGE_USAGE_STAT权限<br>
	 * 该权限是系统级别的权限, 不授予第三方应用, 但是第三方应用可以让用户主动授权该权限<br>
	 * 用于5.0版本<br>
	 */
	public static boolean isPermissionPackageUsageStatsGrandedOnLollipop(Context context) {
		return Machine.HAS_SDK_LOLLIPOP && isPermissionPackageUsageStatsGranded(getSystemServiceUsageStatsManager(context));
	}

	/**
	 * 获取当前前台Activity的应用的ComponentName.<br>
	 */
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private static ComponentName getTopActivity(Context context) {
		if (Machine.HAS_SDK_LOLLIPOP) {
			throw new IllegalStateException("getTopActivity() has no mean for above LOLLIPOP!");
		}
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
		if (taskInfo == null || taskInfo.isEmpty()) {
			return null;
		}
		return taskInfo.get(0).topActivity;
	}

	private final static String INVALID_PACKAGE_NAME = "invalid_package_name";
	private final static String INVALID_ACTIVITY_NAME = "invalid_activity_name";
	private final static ComponentName INVALID_COMPONENT_NAME = new ComponentName(INVALID_PACKAGE_NAME, INVALID_ACTIVITY_NAME);
	public static ComponentName getTopComponentName(Context context){
		ComponentName topActivity = null;
		if (Machine.HAS_SDK_5_1_1) {        // 5.1或以上
			if (AppUtils.isPermissionPackageUsageStatsGrandedLollipopMr1(context)) {
				topActivity = AppUtils.getFrontActivityLollipopMr1(context);
			}
		} else if (Machine.HAS_SDK_LOLLIPOP) {        // 5.0
			if (AppUtils.isPermissionPackageUsageStatsGrandedOnLollipop(context)) {
				topActivity = AppUtils.getFrontActivityOnLollipop(context);
			}
		} else {      // 5.0以下
			topActivity = AppUtils.getTopActivity(context);
		}
		if (topActivity == null) {
			topActivity = INVALID_COMPONENT_NAME;
			if (Machine.HAS_SDK_LOLLIPOP) {
				Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
				context.startActivity(intent);
			}
		}
		return topActivity;
	}


	/**
	 * 打开google play客户端跳转到助理详情页<br>
	 *
	 * @param context
	 */
	public static void openGP(Context context) {
		openGooglePlay(context, CustomConstant.PACKAGE_NAME);
	}

	/**
	 * 跳转到指定应用的google play的详情页<br>
	 *
	 */
	public static boolean openGooglePlay(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName)) {
			return false;
		}
		return openGooglePlay(context, "market://details?id=" + packageName, "https://play.google.com/store/apps/details?id=" + packageName);
	}

	/**
	 * 跳转到google play, 优先跳转到客户端，若失败尝试跳转到网页
	 *
	 */
	public static boolean openGooglePlay(Context context, String uriString, String urlString) {
		boolean isOk = false;
		if (!TextUtils.isEmpty(uriString)) {
			// 先尝试打开客户端
			isOk = openActivitySafely(context, Intent.ACTION_VIEW, uriString, "com.android.vending");
			if (!isOk) {
				isOk = openActivitySafely(context, Intent.ACTION_VIEW, uriString, null);
			}
		}
		if (!isOk) {
			if (!TextUtils.isEmpty(urlString)) {
				// 试试打开浏览器
				isOk = openActivitySafely(context, Intent.ACTION_VIEW, urlString, null);
			}
		}
		return isOk;
	}



	/**
	 * 安全地打开外部的activity
	 *
	 * @param action      如Intent.ACTION_VIEW
	 * @param uri
	 * @param packageName 可选，明确要跳转的程序的包名
	 * @return 是否成功
	 */
	public static boolean openActivitySafely(Context context, String action, String uri, String packageName) {
		boolean isOk = true;
		try {
			Uri uriData = Uri.parse(uri);
			final Intent intent = new Intent(action, uriData);
			if (!TextUtils.isEmpty(packageName)) {
				intent.setPackage(packageName);
			}
			if (!(context instanceof Activity)) {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			context.startActivity(intent);
		} catch (Throwable e) {
			e.printStackTrace();
			isOk = false;
		}
		return isOk;
	}

	/**
	 * 反馈对话框
	 */
	public static void feedbackDialog(Context context) {

		AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
		normalDialog.setTitle(context.getString(R.string.nav_string_feedback));
		normalDialog.setMessage(context.getString(R.string.dialog_feed_back_content));
		normalDialog.setNeutralButton(context.getString(R.string.dialog_feed_back_button_good),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Context context = ((AlertDialog)dialog).getContext();
						AppUtils.openGP(context);
					}
				});
		normalDialog.setPositiveButton(context.getString(R.string.dialog_feed_back_button_rant),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Context context = ((AlertDialog)dialog).getContext();
						context.startActivity(new Intent(context, FeedbackTXActivity.class));
					}
				});
		normalDialog.show();
	}


	public static String getTaskPackageName(Context context) {
		String currentApp = "CurrentNULL";
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			@SuppressLint("WrongConstant") UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
			long time = System.currentTimeMillis();
			List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
			if (appList != null && appList.size() > 0) {
				SortedMap<Long, UsageStats> mySortedMap = new TreeMap<>();
				for (UsageStats usageStats : appList) {
					if ("android".equals(usageStats.getPackageName())) {
						continue;
					}
					mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
				}
				if (!mySortedMap.isEmpty()) {
					currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
				}
			}
		} else {
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
			currentApp = tasks.get(0).processName;
		}
		Log.d("getTaskPackageName", "Current App in foreground is: " + currentApp);
		return currentApp;
	}
}
