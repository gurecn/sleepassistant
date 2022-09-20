package com.devdroid.sleepassistant.service;

import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.TileService;
import android.util.Log;
import com.devdroid.sleepassistant.activity.MainActivity;
import com.devdroid.sleepassistant.application.TheApplication;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.N)
public class QsServer extends TileService {
  private static final String TAG = QsServer.class.getSimpleName();
  @Override
  public void onClick() {
    Log.d(TAG, "onClick");
    // 打开指定页面
    Intent intent = new Intent(TheApplication.getAppContext(), MainActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    // 跳转指定页面，并关闭下拉菜单
    startActivityAndCollapse(intent);
  }
}
