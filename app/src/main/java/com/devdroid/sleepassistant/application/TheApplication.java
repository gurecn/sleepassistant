package com.devdroid.sleepassistant.application;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

import com.devdroid.sleepassistant.BuildConfig;
import com.devdroid.sleepassistant.constant.CustomConstant;
import com.devdroid.sleepassistant.manager.ApplockManager;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;
import com.devdroid.sleepassistant.preferences.SharedPreferencesManager;
import com.devdroid.sleepassistant.utils.AppUtils;
import com.devdroid.sleepassistant.utils.CrashHandler;
import com.devdroid.sleepassistant.utils.LockerManagerUtils;
import com.squareup.leakcanary.LeakCanary;

public class TheApplication extends Application {
    private static TheApplication sInstance;
    private static String sCurrentProcessName;
    @Override
    public void onCreate() {
        super.onCreate();
        // 记录当前进程名
        sCurrentProcessName = AppUtils.getCurrentProcessName(getApplicationContext());
        // 如果是主进程，初始化主进程的相关功能类实例
        if (isRunningOnMainProcess()) {
            onCreateForMainProcess();
            onInitDataChildThread();
        }
    }

    public TheApplication() {
        sInstance = this;
    }
    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    private void onCreateForMainProcess() {
        startServerAndReceiver();
    }

    private void onInitDataChildThread() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                CrashHandler.getInstance().init(getAppContext());
                LeakCanary.install(sInstance);
                LauncherModel.initSingleton(getAppContext());
                LockerManagerUtils.initSingleton(getAppContext());
                ApplockManager.initSingleton(getAppContext());
                boolean isNightMode = LauncherModel.getInstance().getSharedPreferencesManager().getBoolean(IPreferencesIds.KEY_THEME_NIGHT_MODE, false);
                AppCompatDelegate.setDefaultNightMode(isNightMode?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                checkInitOnce();
//              ApplockManager.initSingleton(getAppContext());
            }
        });
        thread.start();
    }

    private void startServerAndReceiver() {
//        ScreenBroadcastReceiver mScreenReceiver = new ScreenBroadcastReceiver();
//        final IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_SCREEN_ON);
//        registerReceiver(mScreenReceiver, filter);
    }

    /**
     * 是否在主进程运行<br>
     */
    public static boolean isRunningOnMainProcess() {
        return CustomConstant.PROCESS_NAME_MAIN.equals(sCurrentProcessName);
    }
    /**
     * 判断初次运行
     */
    private void checkInitOnce() {
        SharedPreferencesManager preferencesManager = LauncherModel.getInstance().getSharedPreferencesManager();
        long appInstallTime = preferencesManager.getLong(IPreferencesIds.KEY_FIRST_START_APP_TIME, (long)0);
        if (appInstallTime == 0) {
            LauncherModel.getInstance().getSharedPreferencesManager().commitLong(IPreferencesIds.KEY_FIRST_START_APP_TIME, System.currentTimeMillis());
        }
        preferencesManager.commitInt(IPreferencesIds.KEY_LAST_INSTALL_APP_CODE, BuildConfig.VERSION_CODE);
    }
}
