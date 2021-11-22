package com.devdroid.sleepassistant.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.devdroid.sleepassistant.receiver.AlarmReceiver;

public class AlarmManagerUtils {
  public static void startAlarm(Context context){
    AlarmManager alarmMgr;
    PendingIntent alarmIntent;
    Log.d("111111111111", "startAlarm1");
    alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(context, AlarmReceiver.class);
    alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
    long time = 10 * 1000;
    alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + time, time, alarmIntent);
    Log.d("111111111111", "startAlarm2");
  }
}
