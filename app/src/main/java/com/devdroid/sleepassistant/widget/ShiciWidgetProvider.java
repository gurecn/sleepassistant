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
import com.devdroid.sleepassistant.preferences.SharedPreferencesManager;
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
  private static final String TAG = "1111111111111";
  private static int[] mAppWidgetIds;
  private boolean isSync = false;
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
    Log.d(TAG, "onReceive");
    String action = intent.getAction();
    Log.d(TAG, "onReceive action：" + action);
    if("com.devdroid.sleepassistant.widget.ACTION_PLAY_PAUSE".equals(action)) {
      updateShici(context);
    }
  }

  @Override
  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    super.onUpdate(context, appWidgetManager, appWidgetIds);
    Log.d(TAG, "onUpdate");
    mAppWidgetIds = appWidgetIds;
    if(appWidgetIds.length > 0){
      Log.d(TAG, "onUpdate1");
      SharedPreferencesManager preferencesManager = getSharedPreferencesManager();
      if(preferencesManager != null) {
        Log.d(TAG, "onUpdate2");
        preferencesManager.commitInt(IPreferencesIds.KEY_APP_WIDGET_ISHICI_ID, appWidgetIds[0]);
      }
    }
    Log.d(TAG, "onUpdate3");
    updateShici(context);
  }

  @Override
  public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
    super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    Log.d(TAG, "onAppWidgetOptionsChanged");
  }

  /**
   * 窗口小部件更新
   */
  private void onWidgetUpdate(Context context, String shici) {
    Log.d(TAG, "onWidgetUpdate");
    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout_shici);
    Intent intent = new Intent(context, ShiciWidgetProvider.class);
    intent.setAction("com.devdroid.sleepassistant.widget.ACTION_PLAY_PAUSE");
    remoteViews.setOnClickPendingIntent(R.id.ibPlay, PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    Context context4 = context;
    Intent intent4 = new Intent(context, ShiciActivity.class);
    intent4.putExtra("shici", shici);
    intent4.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
    remoteViews.setOnClickPendingIntent(R.id.rlContent, PendingIntent.getActivity(context4, 4, intent4, PendingIntent.FLAG_UPDATE_CURRENT));
    OriginBean originBean = new Gson().fromJson(shici, OriginBean.class);
    if(originBean != null) {
      StringBuilder sb = new StringBuilder();
      for (String str:originBean.getContent()){
        sb.append(str.replaceAll(REGEX, "$1\n"));
      }
      remoteViews.setTextViewText(R.id.tvEpi, sb.toString());
    }
    AppWidgetManager appWidgeManger = AppWidgetManager.getInstance(context);
    if(mAppWidgetIds == null || mAppWidgetIds.length == 0){
      Log.d(TAG, "onWidgetUpdate  appWidgetId is null");
      SharedPreferencesManager preferencesManager = getSharedPreferencesManager();
      if(preferencesManager != null) {
        int appWidgetId = preferencesManager.getInt(IPreferencesIds.KEY_APP_WIDGET_ISHICI_ID, 0);
        if (appWidgetId != 0) {
          Log.d(TAG, "onWidgetUpdate  appWidgetId:" + appWidgetId);
          appWidgeManger.updateAppWidget(appWidgetId, remoteViews);
        }
      }
    } else {
      for (int appWidgetId : mAppWidgetIds) {
        appWidgeManger.updateAppWidget(appWidgetId, remoteViews);
        Log.d(TAG, "onWidgetUpdate  appWidgetId:" + appWidgetId);
      }
    }
  }

  private SharedPreferencesManager getSharedPreferencesManager() {
    int times = 10;
    LauncherModel launcherModel = LauncherModel.getInstance();
    while (times > 0 && launcherModel == null){
      Log.d(TAG, "launcherModel is null");
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      launcherModel = LauncherModel.getInstance();
      times--;
    }
    SharedPreferencesManager preferencesManager = launcherModel.getSharedPreferencesManager();
    while (times > 0 && preferencesManager == null){
      Log.d(TAG, "preferencesManager is null");
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      preferencesManager = launcherModel.getSharedPreferencesManager();
      times--;
    }
    return preferencesManager;
  }

  private void updateShici(Context context) {
    Log.d(TAG, "updateShici");
    if(isSync)return;
    isSync = true;
    JinrishiciClient client = JinrishiciClient.getInstance();
    client.getOneSentenceBackground(new JinrishiciCallback() {
      @Override
      public void done(PoetySentence poetySentence) {
        DataBean dataBean = poetySentence.getData();
        OriginBean beanOrigin = dataBean.getOrigin();
        String shici = poetySentence.getData().getContent();
        if (!TextUtils.isEmpty(shici)) {
          String dataString = new Gson().toJson(beanOrigin);
          LauncherModel.getInstance().getSharedPreferencesManager().commitString(IPreferencesIds.KEY_SHICI_CONTENT_LAST,dataString);
          LauncherModel.getInstance().getSharedPreferencesManager().commitString(IPreferencesIds.KEY_SHICI_SUMMARY_LAST,shici);
          LauncherModel.getInstance().getSharedPreferencesManager().commitInt(IPreferencesIds.KEY_SHICI_TIME_LAST, DateUtil.getFormatDate());
          onWidgetUpdate(context, dataString);
          Log.d(TAG, "updateShici 2");
        }
        isSync = false;
      }
      @Override
      public void error(JinrishiciRuntimeException e) {
        Log.d(TAG, "updateShici JinrishiciRuntimeException");
        String dataString = "{\"author\":\"辛弃疾\","
            + "\"content\":[\"渡江天马南来，几人真是经纶手。长安父老，新亭风景，可怜依旧。夷甫诸人，神州沉陆，几曾回首。算平戎万里，功名本是，真儒事、君知否。\","
            + "\"况有文章山斗。对桐阴、满庭清昼。当年堕地，而今试看，风云奔走。绿野风烟，平泉草木，东山歌酒。待他年，整顿乾坤事了，为先生寿。\"],\"dynasty\":\"宋代\",\"title\":\"水龙吟·甲辰岁寿韩南涧尚书\"}";
        dataString = LauncherModel.getInstance().getSharedPreferencesManager().getString(IPreferencesIds.KEY_SHICI_CONTENT_LAST,dataString);
        onWidgetUpdate(context, dataString);
        isSync = false;
      }
    });
  }
}
