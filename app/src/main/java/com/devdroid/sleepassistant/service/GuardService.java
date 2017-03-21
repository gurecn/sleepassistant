package com.devdroid.sleepassistant.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.IBinder;
import android.text.TextUtils;

/**
 * 程序核心的长驻的后台服务<br>
 */
public class GuardService extends Service {

    private final static String GOOGLE_PLAY_URL_KEY = "google_play_url_key";
    private final static String APP_AD_ID = "app_ad_id";
    /**
     * 请求码.<br>
     * 值类型:int<br>
     */
    private final static String EXTRA_KEY_COMMAND = "extra_key_command";
    private final static String EXTRA_KEY_CUSTOM_EXTRA = "extra_key_custom_extra";
    public final static int COMMAND_START_TICK = 1;
    private HandlerThread mAsyncHandlerThread;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAsyncHandlerThread = new HandlerThread("monitor-thread");
        mAsyncHandlerThread.start();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        int command = COMMAND_START_TICK;
        if (intent != null) {
            command = intent.getIntExtra(EXTRA_KEY_COMMAND, COMMAND_START_TICK);
        }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 获取一个用于启动当前Service的Intent<br>
     *
     * @param context
     * @param command      {@link #COMMAND_START_TICK} etc.<br>
     * @param customExtras 对应请求码需要的自定义参数[可选]<br>
     * @return Intent
     */
    public static Intent getService(Context context, int command,
                                    Bundle customExtras) {
        Intent intent = new Intent(context, GuardService.class);
        intent.putExtra(EXTRA_KEY_COMMAND, command);
        if (null != customExtras) {
            intent.putExtra(EXTRA_KEY_CUSTOM_EXTRA, customExtras);
        }
        return intent;
    }

    /**
     * 请求启动服务<br>
     *
     * @param context
     * @param command      {@link #COMMAND_START_TICK} etc.<br>
     * @param customExtras 对应请求码需要的自定义参数[可选]<br>
     */
    public static void startService(Context context, int command,
                                    Bundle customExtras) {
        context.startService(getService(context, command, customExtras));
    }

    /**
     * 创建启动该service的intent
     *
     * @param context
     * @return
     */
    public static Intent createIntent(Context context) {
        final Intent serviceIntent = getService(context, -1, null);
        return serviceIntent;
    }
}
