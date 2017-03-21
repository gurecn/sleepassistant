package com.devdroid.sleepassistant.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.devdroid.sleepassistant.BuildConfig;
import com.devdroid.sleepassistant.constant.ApiConstant;
import com.devdroid.sleepassistant.receiver.ChangeTimeReceiver;
import com.devdroid.sleepassistant.receiver.DaemonHelperReceiver;
import com.devdroid.sleepassistant.receiver.DaemonReceiver;
import com.devdroid.sleepassistant.receiver.ScreenBroadcastReceiver;
import com.devdroid.sleepassistant.service.DaemonHelperService;
import com.devdroid.sleepassistant.service.GuardService;
import com.devdroid.sleepassistant.utils.CrashHandler;
import com.jiubang.commerce.daemon.DaemonClient;
import com.jiubang.commerce.daemon.DaemonConfigurations;

/**
 * Created by Gaolei on 2017/3/13.
 */

public class TheApplication extends Application {
    private static TheApplication sInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        onInitDataChildThread();
    }

    public TheApplication() {
        sInstance = this;
    }
    public static Context getAppContext() {
        return sInstance.getApplicationContext();
    }

    private void onInitDataChildThread() {
        CrashHandler.getInstance().init(this, ApiConstant.LOG_DIR);
        LauncherModel.initSingleton(this);
        startServerAndReceiver();
    }

    private void startServerAndReceiver() {
        ScreenBroadcastReceiver mScreenReceiver = new ScreenBroadcastReceiver();
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(mScreenReceiver, filter);
        IntentFilter timeChangefilter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(new ChangeTimeReceiver(), timeChangefilter);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (BuildConfig.DEBUG) {
            DaemonClient.getInstance().setDebugMode();
        }
        DaemonClient.getInstance().init(createDaemonConfigurations());
        DaemonClient.getInstance().onAttachBaseContext(base);
    }

    /**
     * 构建守护配置
     * @return
     */
    private DaemonConfigurations createDaemonConfigurations() {
        //构建被守护进程配置信息
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "com.devdroid.sleepassistant",
                GuardService.class.getCanonicalName(),
                DaemonReceiver.class.getCanonicalName());
        //构建辅助进程配置信息
        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "com.devdroid.sleepassistant:DaemonHelperService",
                DaemonHelperService.class.getCanonicalName(),
                DaemonHelperReceiver.class.getCanonicalName());
        //listener can be null
        return new DaemonConfigurations(configuration1, configuration2);
    }
}
