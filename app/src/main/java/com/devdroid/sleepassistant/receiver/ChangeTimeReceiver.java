package com.devdroid.sleepassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.devdroid.sleepassistant.utils.Logger;
/**
 * 监听时间变化
 */
public class ChangeTimeReceiver extends BroadcastReceiver {
    private final String TAG = ScreenBroadcastReceiver.class.getSimpleName();
    public ChangeTimeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_TIME_TICK)){
            long lastTime = System.currentTimeMillis();
            Logger.d(TAG, "ACTION_SCREEN_ON: lastTime:" + lastTime);
        }
    }
}
