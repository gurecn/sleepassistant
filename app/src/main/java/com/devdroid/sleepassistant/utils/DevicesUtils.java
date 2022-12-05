package com.devdroid.sleepassistant.utils;

import java.io.File;
import java.util.Properties;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
/**
 * 
 * 
 * 类名称：DevicesUtils
 * 类描述：
 * 创建人：makai
 * 修改人：makai
 * 修改时间：2014年6月3日 下午5:17:52
 * 修改备注：
 * @version 1.0.0
 *
 */
public class DevicesUtils {

	private static final String TAG = "DevicesUtils";

	private static final String VERSION_NAME_KEY = "VersionName";
	private static final String VERSION_CODE_KEY = "VersionCode";
	private static final String PACKAGE_NAME_KEY = "PackageName";
	private static final String SVN_CODE = "SVN";
	private static final String FILE_PATH_KEY = "FilePath";
	private static final String PHONE_MODEL_KEY = "PhoneModel";
	private static final String ANDROID_VERSION_KEY = "AndroidVersion";
	private static final String BOARD_KEY = "BOARD";
	private static final String BRAND_KEY = "BRAND";
	private static final String DEVICE_KEY = "DEVICE";
	private static final String DISPLAY_KEY = "DISPLAY";
	private static final String FINGERPRINT_KEY = "FINGERPRINT";
	private static final String HOST_KEY = "HOST";
	private static final String ID_KEY = "ID";
	private static final String MODEL_KEY = "MODEL";
	private static final String PRODUCT_KEY = "PRODUCT";
	private static final String TAGS_KEY = "TAGS";
	private static final String TIME_KEY = "TIME";
	private static final String TYPE_KEY = "TYPE";
	private static final String USER_KEY = "USER";
	private static final String TOTAL_MEM_SIZE_KEY = "TotalMemSize";
	private static final String AVAILABLE_MEM_SIZE_KEY = "AvaliableMemSize";
	private static final String DENSITY_KEY = "DENSITY";
	private static final String CURRENT_MEM_KEY = "Current Heap";
	private static final String MACHINE_MEMORY_INFOS = "Mem Infos";
	private static final String UID_KEY = "uid";
	/**
	 * 用户反馈时收集的设备信息<br>
	 * @param context
	 * @return
	 */
	public static String getFeedbackDeviceInfo(Context context,String type) {
		StringBuffer info = new StringBuffer();
		info.append("\nFeedback Type:"+type);
		info.append("\nVersion Code:"+String.valueOf(AppUtils.getVersionCode(context)));
		info.append("\nVersion Name:"+ AppUtils.getVersionName(context));
		info.append("\nUid=" + AppUtils.getChannel(context));
		info.append("\nNetwork=" + AppUtils.getNetWorkType(context));
		info.append("\nProduct=" + android.os.Build.PRODUCT);
		info.append("\nPhoneModel=" + android.os.Build.MODEL);
		info.append("\nROM=" + android.os.Build.DISPLAY);
		info.append("\nBoard=" + android.os.Build.BOARD);
		info.append("\nDevice=" + android.os.Build.DEVICE);
		info.append("\nDensity=" + String.valueOf(context.getResources().getDisplayMetrics().density));
		info.append("\nPackageName=" + context.getPackageName());
		info.append("\nAndroidVersion=" + android.os.Build.VERSION.RELEASE);
		info.append("\nTotalMemSize="
				+ (DevicesUtils.getTotalInternalMemorySize() / 1024 / 1024)
				+ "MB");
		info.append("\nFreeMemSize="
				+ (DevicesUtils.getAvailableInternalMemorySize() / 1024 / 1024)
				+ "MB");
		info.append("\nRom App Heap Size="
				+ Integer
				.toString((int) (Runtime.getRuntime().maxMemory() / 1024L / 1024L))
				+ "MB");
		info.append("\nCountry=" + AppUtils.getLocal(context));
		return info.toString();
	}
	/**
	 * Collects crash data.
	 * 
	 * @param context
	 *            The application context.
	 */
	public static Properties retrieveCrashData(Context context, String channel) {
		Properties mCrashProperties = new Properties();
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi;
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			if (pi != null) {
				mCrashProperties.put(VERSION_NAME_KEY, pi.versionName != null ? pi.versionName : "not set");
				mCrashProperties.put(VERSION_CODE_KEY, pi.versionCode != 0 ? pi.versionCode + " " : "not set");
			} else {
				mCrashProperties.put(PACKAGE_NAME_KEY, "Package info unavailable");
			}

			// Application Package name
			mCrashProperties.put(PACKAGE_NAME_KEY, context.getPackageName());
			// Device model
			mCrashProperties.put(PHONE_MODEL_KEY, android.os.Build.MODEL);
			// Android version
			mCrashProperties.put(ANDROID_VERSION_KEY, android.os.Build.VERSION.RELEASE);

			// Android build data
			mCrashProperties.put(BOARD_KEY, android.os.Build.BOARD);
			mCrashProperties.put(BRAND_KEY, android.os.Build.BRAND);
			mCrashProperties.put(DEVICE_KEY, android.os.Build.DEVICE);
			mCrashProperties.put(DISPLAY_KEY, android.os.Build.DISPLAY);
			mCrashProperties.put(FINGERPRINT_KEY, android.os.Build.FINGERPRINT);
			mCrashProperties.put(HOST_KEY, android.os.Build.HOST);
			mCrashProperties.put(ID_KEY, android.os.Build.ID);
			mCrashProperties.put(MODEL_KEY, android.os.Build.MODEL);
			mCrashProperties.put(PRODUCT_KEY, android.os.Build.PRODUCT);
			mCrashProperties.put(TAGS_KEY, android.os.Build.TAGS);
			mCrashProperties.put(TIME_KEY, "" + android.os.Build.TIME);
			mCrashProperties.put(TYPE_KEY, android.os.Build.TYPE);
			mCrashProperties.put(USER_KEY, android.os.Build.USER);
			mCrashProperties.put(UID_KEY, channel);

			// Device Memory
			mCrashProperties.put(TOTAL_MEM_SIZE_KEY, "" + getTotalInternalMemorySize());
			mCrashProperties.put(AVAILABLE_MEM_SIZE_KEY, "" + getAvailableInternalMemorySize());

			// Application file path
			mCrashProperties.put(FILE_PATH_KEY, context.getFilesDir().getAbsolutePath());

			String infos = null;
			if (null != infos) {
				mCrashProperties.put(MACHINE_MEMORY_INFOS, infos);
			} else {
				mCrashProperties.put(MACHINE_MEMORY_INFOS, "error");
			}
			
			mCrashProperties.put(DENSITY_KEY, String.valueOf(context.getResources().getDisplayMetrics().density));
			mCrashProperties.put(CURRENT_MEM_KEY, Integer.toString((int) (Runtime.getRuntime().maxMemory() / 1024L / 1024L)) + "MB");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mCrashProperties;
	}

	/**
	 * 获取渠道的方法
	 *
	 * @param ctx  上下文Context
	 * @return 渠道名
	 */
	public static String getChannelName(Context ctx) {
		String sChannel;
		try {
			ApplicationInfo appInfo = ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
			sChannel = appInfo.metaData.getString("channel");
		} catch (Exception e) {
			sChannel = "404";
		}
		return sChannel;
	}
	
	/**
	 * Calculates the total memory of the device. This is based on an inspection
	 * of the filesystem, which in android devices is stored in RAM.
	 * 
	 * @return Total number of bytes.
	 */
	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}
	
	/**
	 * Calculates the free memory of the device. This is based on an inspection
	 * of the filesystem, which in android devices is stored in RAM.
	 * 
	 * @return Number of bytes available.
	 */
	public static long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}
	
	/**
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		return imei;
	}
	
}
