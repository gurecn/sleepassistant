package com.devdroid.sleepassistant.application;

import android.content.Context;
import android.content.res.AssetManager;
import android.system.ErrnoException;
import android.system.Os;

import com.devdroid.sleepassistant.BuildConfig;
import com.devdroid.sleepassistant.constant.CustomConstant;
import com.devdroid.sleepassistant.manager.ApplockManager;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;
import com.devdroid.sleepassistant.preferences.SharedPreferencesManager;
import com.devdroid.sleepassistant.utils.AppUtils;
import com.devdroid.sleepassistant.utils.CrashHandler;
import com.devdroid.sleepassistant.utils.LockerManagerUtils;
import com.devdroid.sleepassistant.utils.thread.ThreadPoolUtils;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.io.IIOAdapter;
import com.jinrishici.sdk.android.factory.JinrishiciFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.appcompat.app.AppCompatDelegate;

public class TheApplication extends FrontiaApplication {
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
    public static TheApplication getApplication() {
        return sInstance;
    }

    private void onCreateForMainProcess() {
        startServerAndReceiver();
    }

    private void onInitDataChildThread() {
        ThreadPoolUtils.executeSingleton(new Runnable() {
            @Override
            public void run() {
                CrashHandler.getInstance().init(getAppContext());
//                LeakCanary.install(sInstance);
                LauncherModel.initSingleton(getAppContext());
                LockerManagerUtils.initSingleton(getAppContext());
                ApplockManager.initSingleton(getAppContext());
                boolean isNightMode = LauncherModel.getInstance().getSharedPreferencesManager().getBoolean(IPreferencesIds.KEY_THEME_NIGHT_MODE, false);
                AppCompatDelegate.setDefaultNightMode(isNightMode? AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                checkInitOnce();
                //今日诗词初始化
                JinrishiciFactory.init(getAppContext());
//              ApplockManager.initSingleton(getAppContext());
//                initHanLP();
            }
        });
    }

    private void initHanLP()
    {
        try
        {
            Os.setenv("HANLP_ROOT", "", true);
        }
        catch (ErrnoException e)
        {
            throw new RuntimeException(e);
        }
        final AssetManager assetManager = getAssets();
        HanLP.Config.IOAdapter = new IIOAdapter()
        {
            @Override
            public InputStream open(String path) throws IOException
            {
                return assetManager.open(path);
            }

            @Override
            public OutputStream create(String path) throws IOException
            {
                throw new IllegalAccessError("不支持写入" + path + "！请在编译前将需要的数据放入app/src/main/assets/data");
            }
        };
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
