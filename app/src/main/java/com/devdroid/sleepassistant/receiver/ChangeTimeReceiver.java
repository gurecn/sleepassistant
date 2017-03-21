package com.devdroid.sleepassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 监听时间变化
 */
public class ChangeTimeReceiver extends BroadcastReceiver {
    public ChangeTimeReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_TIME_TICK)){
        }
    }
}
