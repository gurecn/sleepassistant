package com.devdroid.sleepassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 辅助守护进程需要的receiver，不要做任何东西
 * Created by zhaobao on 4/11/16.
 */
public class DaemonHelperReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
