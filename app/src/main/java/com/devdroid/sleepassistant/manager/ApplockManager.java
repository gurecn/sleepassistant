package com.devdroid.sleepassistant.manager;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import com.devdroid.sleepassistant.application.TheApplication;
import com.devdroid.sleepassistant.database.BaseDataProvider;
import com.devdroid.sleepassistant.database.LockerDao;
import com.devdroid.sleepassistant.preferences.PreferencesManager;
import com.devdroid.sleepassistant.utils.AppUtils;
import com.devdroid.sleepassistant.utils.Machine;
import java.util.Date;
import java.util.List;

public class ApplockManager {

    private final static String INVALID_PACKAGE_NAME = "invalid_package_name";
    private final static String INVALID_ACTIVITY_NAME = "invalid_activity_name";
    private final static ComponentName INVALID_COMPONENT_NAME = new ComponentName(INVALID_PACKAGE_NAME, INVALID_ACTIVITY_NAME);
    private Context mContext;
    private final Handler mAsyncHandler;
    public static LockerDao mLockerDao = null;
    private static List<String> componentNames;
    private static int mStartTime;
    private static int mEndTime;
    private static int sTimeSpace = 1000;
    /**
     * 常用桌面
     */
    private String mUseLauncherPackageName = null;
    private final Runnable mCheckFrontApp = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
        @Override
        public void run() {
            ComponentName topActivity = null;
            if (Machine.HAS_SDK_5_1_1) {        // 5.1或以上
                if (AppUtils.isPermissionPackageUsageStatsGrandedLollipopMr1(mContext)) {
                    topActivity = AppUtils.getFrontActivityLollipopMr1(mContext);
                }
            } else if (Machine.HAS_SDK_LOLLIPOP) {        // 5.0
                if (AppUtils.isPermissionPackageUsageStatsGrandedOnLollipop(mContext)) {
                    topActivity = AppUtils.getFrontActivityOnLollipop(mContext);
                }
            } else {      // 5.0以下
                topActivity = AppUtils.getTopActivity(mContext);
            }
            if (topActivity == null) {
                topActivity = INVALID_COMPONENT_NAME;
                PreferencesManager mDefaultPM = PreferencesManager.getDefaultSharedPreference(mContext);
                boolean hasSetting = mDefaultPM.getBoolean("hasSetting",false);
                if (!hasSetting) {
                    mDefaultPM.edit();
                    mDefaultPM.putBoolean("hasSetting", true);
                    mDefaultPM.commit();
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    mContext.startActivity(intent);
                }
            }
            ComponentName mTopComponentName = topActivity;
            String currentPackageName = mTopComponentName.getPackageName();
            List<String> mLauncherList = AppUtils.getLauncherPackageNames(mContext);
            for (String value : mLauncherList) {
                if (value.equals(currentPackageName)) {
                    mUseLauncherPackageName = currentPackageName;
                    break;
                }
            }
            if (isApplyTime()) {
                if (componentNames != null && componentNames.contains(currentPackageName)) {
                    choiceCancel(currentPackageName);
                    Toast toast = Toast.makeText(TheApplication.getAppContext(), "此应用现在不可用，请换其他应用", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
//            scheduleCheck();
        }
    };

    private ApplockManager(Context context) {
        mContext = context;
        HandlerThread mAsyncHandlerThread = new HandlerThread("monitor-thread");
        mAsyncHandlerThread.start();
        mAsyncHandler = new Handler(mAsyncHandlerThread.getLooper());
        scheduleCheck();
    }

    private void scheduleCheck() {
        mAsyncHandler.removeCallbacks(mCheckFrontApp);
        mAsyncHandler.postDelayed(mCheckFrontApp, sTimeSpace);
    }
    /**
     * 初始化单例,在程序启动时调用<br>
     */
    public static void initSingleton(Context context) {
        BaseDataProvider dataProvider = new BaseDataProvider(context);
        mLockerDao = new LockerDao(context, dataProvider);
        componentNames = mLockerDao.queryLockerInfo();
        PreferencesManager mDefaultPM = PreferencesManager.getDefaultSharedPreference(context);
        mStartTime = mDefaultPM.getInt("startTime", 0);
        mEndTime = mDefaultPM.getInt("endTime", 0);
    }


    /**
     * 选择退出
     */
    private void choiceCancel(String pkgName) {
        AppUtils.gotoLauncherWithoutChoice(mContext, mUseLauncherPackageName);
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        if (!pkgName.equals(mContext.getPackageName())) {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
                if (null != runningAppProcessInfo.pkgList && runningAppProcessInfo.pkgList.length > 0) {
                    for (String value : runningAppProcessInfo.pkgList) {
                        if (pkgName.equals(value)) {
                            Process.killProcess(runningAppProcessInfo.pid);
                            break;
                        }
                    }
                }
            }
            activityManager.killBackgroundProcesses(pkgName);
        }
    }


    public static void lockPackage(List<String> packageNames) {
        for (String packageName : packageNames) {
            if (!componentNames.contains(packageName)) {
                componentNames.add(packageName);
            }
        }
    }

    public static void unLockPackage(List<String> packageNames) {
        for (String packageName : packageNames) {
            if (componentNames.contains(packageName)) {
                componentNames.remove(packageName);
            }
        }
    }

    private boolean isApplyTime() {
        Date date = new Date();
        int currentMinue = date.getMinutes();
        int currentTime = Integer.parseInt(date.getHours() + "" +  (currentMinue > 9 ? currentMinue:("0" + currentMinue)));
        Log.d("Applock","开始时间：" + mStartTime + "---->结束时间：" + mEndTime + "---->当前时间：" + currentTime);
        if ((mStartTime < mEndTime && currentTime >= mStartTime && currentTime < mEndTime) ||
            (mStartTime > mEndTime && currentTime >= mStartTime || currentTime < mEndTime)) {
            sTimeSpace = 300;
            return true;
        } else if ((mStartTime > currentTime && mStartTime - currentTime <= 1) || (mStartTime == 0 && currentMinue >= 2359)) {
            sTimeSpace = 300;
        } else {
            sTimeSpace = 1000 * 60;
        }
        return false;
    }
}
