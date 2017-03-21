package com.devdroid.sleepassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.devdroid.sleepassistant.utils.Logger;

public class ScreenBroadcastReceiver extends BroadcastReceiver {
    private final String TAG = ScreenBroadcastReceiver.class.getSimpleName();
    public ScreenBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_SCREEN_ON: // 屏幕开启
                Logger.d(TAG, "ACTION_SCREEN_ON");
                break;
            case Intent.ACTION_SCREEN_OFF: // 屏幕关闭
                Logger.d(TAG, "ACTION_SCREEN_OFF");
                break;
            case Intent.ACTION_USER_PRESENT: // 屏幕解锁
                Logger.d(TAG, "ACTION_USER_PRESENT");
                break;
            default:
                break;
        }
    }
}
