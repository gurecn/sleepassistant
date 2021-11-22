package com.devdroid.sleepassistant.utils;

import android.app.ActivityManager;
import android.content.Context;

import com.devdroid.sleepassistant.application.LauncherModel;

import java.util.List;

public class LockerManagerUtils {
  /**
   * Context
   */
  private Context mContext = null;
  private static LockerManagerUtils sInstance;

  public LockerManagerUtils(Context context) {
    mContext = context;
  }


  /**
   * 初始化单例,在程序启动时调用<br>
   */
  public static void initSingleton(Context context) {
    sInstance = new LockerManagerUtils(context);
  }
  /**
   * 获取实例<br>
   */
  public static LockerManagerUtils getInstance() {
    return sInstance;
  }

  /**
   * 选择退出
   * @param pkgName
   */
  public void choiceCancel(String pkgName) {
   // AppUtils.gotoLauncherWithoutChoice(mContext, mUseLauncherPackageName);
    ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
    if (/*!pkgName.equals(LockerEnv.Package.PACKAGE_NAME) && */!pkgName.equals(mContext.getPackageName())) {
      List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
      for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
        if (null != runningAppProcessInfo.pkgList && runningAppProcessInfo.pkgList.length > 0) {
          for (String value : runningAppProcessInfo.pkgList) {
            if (pkgName.equals(value)) {
              android.os.Process.killProcess(runningAppProcessInfo.pid);
              break;
            }
          }
        }
      }
      activityManager.killBackgroundProcesses(pkgName);
    } else {
//			List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
//			for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
//				if (LockerEnv.Package.MAIN_PROCESS_NAME.equals(runningAppProcessInfo.processName)) {
//					Process.killProcess(runningAppProcessInfo.pid);
//				}
//			}
    }
  }
}
