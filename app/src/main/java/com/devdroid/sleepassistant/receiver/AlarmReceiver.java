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
    String packageName = AppUtils.getTaskPackageName(context);
    List<String> lockedApp = LauncherModel.getInstance().getLockerDao().queryLockerInfo();
    AppUtils.gotoLauncherWithoutChoice(context, "");
    if(lockedApp.contains(packageName)){
      LockerManagerUtils.getInstance().choiceCancel(packageName);
    }

  }
}