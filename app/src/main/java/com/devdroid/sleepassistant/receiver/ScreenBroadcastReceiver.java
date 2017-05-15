package com.devdroid.sleepassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.devdroid.sleepassistant.application.TheApplication;
import com.devdroid.sleepassistant.utils.Logger;

public class ScreenBroadcastReceiver extends BroadcastReceiver {
    private final IntentFilter mTimeChangefilter;
    private final ChangeTimeReceiver mChangeTimeReceiver;

    public ScreenBroadcastReceiver() {
        mTimeChangefilter = new IntentFilter();
        mTimeChangefilter.addAction(Intent.ACTION_TIME_TICK);
        mChangeTimeReceiver = new ChangeTimeReceiver();
        TheApplication.getAppContext().registerReceiver(mChangeTimeReceiver, mTimeChangefilter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case Intent.ACTION_SCREEN_ON: // 屏幕开启
                TheApplication.getAppContext().registerReceiver(mChangeTimeReceiver, mTimeChangefilter);
                break;
            case Intent.ACTION_SCREEN_OFF: // 屏幕关闭
                try {
                    TheApplication.getAppContext().unregisterReceiver(mChangeTimeReceiver);
                } catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }
}
