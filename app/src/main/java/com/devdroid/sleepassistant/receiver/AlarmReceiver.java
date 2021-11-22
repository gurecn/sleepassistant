package com.devdroid.sleepassistant.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.utils.AppUtils;
import com.devdroid.sleepassistant.utils.LockerManagerUtils;

import java.util.List;

public class AlarmReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    // an Intent broadcast.
    Log.d("111111111111", "Action:" + intent.getAction());
    String packageName = AppUtils.getTaskPackageName(context);
//    Log.d("111111111111", "topComponent:" + topComponent);
//    String packageName = topComponent.getPackageName();
    Log.d("111111111111", "packageName:" + packageName);
    List<String> lockedApp = LauncherModel.getInstance().getLockerDao().queryLockerInfo();

    AppUtils.gotoLauncherWithoutChoice(context, "");
//    String launcherPackageName = AppUtils.getDefaultLauncher(context);

//    Log.d("111111111111", "launcherPackageName:" + launcherPackageName);
    if(lockedApp.contains(packageName)){
      Log.d("111111111111", "杀死应用");
//      LockerManagerUtils.getInstance().choiceCancel(packageName);
    }

  }
}