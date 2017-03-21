package com.devdroid.sleepassistant.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * 守护进程辅助进程，什么都不要做
 * Created by zhaobao on 4/11/16.
 */
public class DaemonHelperService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }
}
