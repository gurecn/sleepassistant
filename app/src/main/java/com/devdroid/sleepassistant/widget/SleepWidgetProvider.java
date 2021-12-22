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
import com.devdroid.sleepassistant.activity.MainActivity;
import com.devdroid.sleepassistant.activity.ShiciActivity;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;

public final class SleepWidgetProvider extends AppWidgetProvider {
  public static final String REGEX = "(:|：|，|,|\\.|。|;|；|\\?|？|！|!)";
  private static final String TAG = "111111111";
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

  /**
   * 窗口小部件更新
   */
  private void onWidgetUpdate(Context context, AppWidgetManager appWidgeManger, int appWidgetId) {
    Log.i(TAG, "appWidgetId = " + appWidgetId);
    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout_sleep);
    Intent intent4 = new Intent(context, MainActivity.class);
    intent4.putExtra("action", "create_sleep_time_new");
    intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    remoteViews.setOnClickPendingIntent(R.id.d17, PendingIntent.getActivity(context, 4, intent4, PendingIntent.FLAG_UPDATE_CURRENT));
    appWidgeManger.updateAppWidget(appWidgetId, remoteViews);
  }
}
