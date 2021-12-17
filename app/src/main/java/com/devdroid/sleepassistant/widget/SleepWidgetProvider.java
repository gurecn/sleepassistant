package com.devdroid.sleepassistant.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;

public final class SleepWidgetProvider extends AppWidgetProvider {
  public static final String REGEX = "(:|：|，|,|\\.|。|;|；|\\?|？|！|!)";
  private static final String TAG = SleepWidgetProvider.class.getSimpleName();
  @Override
  public void onDisabled(Context context) {
    super.onDisabled(context);
    Log.d(TAG, "onDisabled");
  }

  @Override
  public void onEnabled(Context context) {
    super.onEnabled(context);
    Log.d(TAG, "onEnabled");
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    Log.d(TAG, "onReceive : action = " + intent.getAction());
    String action = intent.getAction();
    if("com.devdroid.sleepassistant.widget.ACTION_SLEEP".equals(action)){
      AppWidgetManager appWidgeManger = AppWidgetManager.getInstance(context);
      int appWidgetId = LauncherModel.getInstance().getSharedPreferencesManager().getInt(IPreferencesIds.KEY_APP_WIDGET_ISLEEP_ID, 0);
      if (appWidgetId != 0) {
        onWidgetUpdateData(context, appWidgeManger, appWidgetId);
      }
    }
  }

  /**
   * 窗口小部件更新
   */
  private void onWidgetUpdateData(Context context, AppWidgetManager appWidgeManger, int appWidgetId) {
    Log.i(TAG, "appWidgetId = " + appWidgetId);
    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout_sleep);
    Intent intent = new Intent(context, SleepWidgetProvider.class);
    intent.setAction("com.devdroid.sleepassistant.widget.ACTION_SLEEP");
    remoteViews.setOnClickPendingIntent(R.id.d17, PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT));

    long currentTime = System.currentTimeMillis();
    if(currentTime%2 == 0) {

      Log.i(TAG, "onWidgetUpdateData = 1");
      remoteViews.setViewVisibility(R.id.tvContent, View.VISIBLE);
      remoteViews.setTextViewText(R.id.tvContent,"点击显示显示把价格考虑到各级领导机关两地分居广泛的根据反馈给回访电话" + currentTime);
    } else {
      Log.i(TAG, "onWidgetUpdateData = 2");
      remoteViews.setViewVisibility(R.id.tvContent, View.INVISIBLE);
    }




    appWidgeManger.updateAppWidget(appWidgetId, remoteViews);
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    super.onUpdate(context, appWidgetManager, appWidgetIds);
    Log.d(TAG, "onUpdate");
    final int counter = appWidgetIds.length;
    Log.i(TAG, "counter = " + counter);
    for (int i = 0; i < counter; i++) {
      int appWidgetId = appWidgetIds[i];
      Log.i(TAG, "onUpdate = " + appWidgetId);
      LauncherModel.getInstance().getSharedPreferencesManager().commitInt(IPreferencesIds.KEY_APP_WIDGET_ISLEEP_ID, appWidgetId);
      onWidgetUpdate(context, appWidgetManager, appWidgetId);
    }
  }

  @Override
  public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
    super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    Log.d(TAG, "onAppWidgetOptionsChanged");
    onWidgetUpdate(context, appWidgetManager, appWidgetId);
  }

  /**
   * 窗口小部件更新
   */
  private void onWidgetUpdate(Context context, AppWidgetManager appWidgeManger, int appWidgetId) {
    Log.i(TAG, "appWidgetId = " + appWidgetId);
    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout_sleep);
    Intent intent = new Intent(context, SleepWidgetProvider.class);
    intent.setAction("com.devdroid.sleepassistant.widget.ACTION_SLEEP");
    remoteViews.setOnClickPendingIntent(R.id.d17, PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    appWidgeManger.updateAppWidget(appWidgetId, remoteViews);
  }
}
