package com.devdroid.sleepassistant.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.activity.ShiciActivity;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;
import com.devdroid.sleepassistant.utils.DateUtil;
import com.google.gson.Gson;
import com.jinrishici.sdk.android.JinrishiciClient;
import com.jinrishici.sdk.android.listener.JinrishiciCallback;
import com.jinrishici.sdk.android.model.DataBean;
import com.jinrishici.sdk.android.model.JinrishiciRuntimeException;
import com.jinrishici.sdk.android.model.OriginBean;
import com.jinrishici.sdk.android.model.PoetySentence;

public final class ShiciWidgetProvider extends AppWidgetProvider {
  public static final String REGEX = "(:|：|，|,|\\.|。|;|；|\\?|？|！|!)";
  @Override
  public void onDisabled(Context context) {
    super.onDisabled(context);
    Log.d("11111111111", "onDisabled");
  }

  @Override
  public void onEnabled(Context context) {
    super.onEnabled(context);
    Log.d("11111111111", "onEnabled");
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    super.onReceive(context, intent);
    Log.d("11111111111", "onReceive : action = " + intent.getAction());
    String action = intent.getAction();
    if("com.devdroid.sleepassistant.widget.ACTION_PLAY_PAUSE".equals(action)) {
      Log.d("11111111111", "updateShici start");
      updateShici();
      Log.d("11111111111", "updateShici ok");
      AppWidgetManager appWidgeManger = AppWidgetManager.getInstance(context);
      int appWidgetId = LauncherModel.getInstance().getSharedPreferencesManager().getInt(IPreferencesIds.KEY_APP_WIDGET_ID, 0);
      if (appWidgetId != 0) {
        onWidgetUpdate(context, appWidgeManger, appWidgetId);
      }
    }
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    super.onUpdate(context, appWidgetManager, appWidgetIds);
    Log.d("11111111111", "onUpdate");
    final int counter = appWidgetIds.length;
    Log.i("11111111111", "counter = " + counter);
    for (int i = 0; i < counter; i++) {
      int appWidgetId = appWidgetIds[i];
      LauncherModel.getInstance().getSharedPreferencesManager().commitInt(IPreferencesIds.KEY_APP_WIDGET_ID, appWidgetId);
      onWidgetUpdate(context, appWidgetManager, appWidgetId);
    }
  }

  @Override
  public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
    super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    Log.d("11111111111", "onAppWidgetOptionsChanged");
    onWidgetUpdate(context, appWidgetManager, appWidgetId);
  }

  /**
   * 窗口小部件更新
   *
   * @param context
   * @param appWidgeManger
   * @param appWidgetId
   */
  private void onWidgetUpdate(Context context, AppWidgetManager appWidgeManger, int appWidgetId) {
    Log.i("11111111111", "appWidgetId = " + appWidgetId);
    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout_shici);
    Intent intent = new Intent(context, ShiciWidgetProvider.class);
    intent.setAction("com.devdroid.sleepassistant.widget.ACTION_PLAY_PAUSE");
    remoteViews.setOnClickPendingIntent(R.id.ibPlay, PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    Context context4 = context;
    Intent intent4 = new Intent(context, ShiciActivity.class);
    intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    remoteViews.setOnClickPendingIntent(R.id.rlContent, PendingIntent.getActivity(context4, 4, intent4, PendingIntent.FLAG_UPDATE_CURRENT));

    long currentTime = System.currentTimeMillis();
    if(currentTime%2 == 0) {
      remoteViews.setImageViewResource(R.id.ivPlayState, R.drawable.ic_system_widgets_status_led_on);
    } else {
      remoteViews.setImageViewResource(R.id.ivPlayState, R.drawable.ic_system_widgets_status_led_off);
    }
    String shiciContext = LauncherModel.getInstance().getSharedPreferencesManager().getString(IPreferencesIds.KEY_SHICI_CONTENT_LAST, "");
    OriginBean originBean = new Gson().fromJson(shiciContext, OriginBean.class);
    if(originBean != null) {
      StringBuilder sb = new StringBuilder();
      for (String str:originBean.getContent()){
        sb.append(str.replaceAll(REGEX, "$1\n"));
      }

      Log.i("11111111111", "onWidgetUpdate = " + sb.toString());
      remoteViews.setTextViewText(R.id.tvEpi, sb.toString());
    }

    Log.i("11111111111", "onWidgetUpdate = " + 3);
    appWidgeManger.updateAppWidget(appWidgetId, remoteViews);
  }

  private void updateShici() {
    JinrishiciClient client = JinrishiciClient.getInstance();
    client.getOneSentenceBackground(new JinrishiciCallback() {
      @Override
      public void done(PoetySentence poetySentence) {
        DataBean dataBean = poetySentence.getData();
        OriginBean beanOrigin = dataBean.getOrigin();
        String shici = poetySentence.getData().getContent();
        if (!TextUtils.isEmpty(shici)) {

          Log.i("11111111111", "shici = " + shici);
          shici = shici.replaceAll("(:|：|，|,|\\.|。|;|；|\\?|？)", "$1\n");
          String dataString = new Gson().toJson(beanOrigin);

          Log.i("11111111111", "dataString = " + dataString);
          LauncherModel.getInstance().getSharedPreferencesManager().commitString(IPreferencesIds.KEY_SHICI_CONTENT_LAST,dataString);
          LauncherModel.getInstance().getSharedPreferencesManager().commitString(IPreferencesIds.KEY_SHICI_SUMMARY_LAST,shici);
          LauncherModel.getInstance().getSharedPreferencesManager().commitInt(IPreferencesIds.KEY_SHICI_TIME_LAST, DateUtil.getFormatDate());
        }
      }
      @Override
      public void error(JinrishiciRuntimeException e) {
      }
    });
  }

}
