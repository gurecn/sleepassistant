package com.devdroid.sleepassistant.base;

import android.app.Activity;

public interface IApplication {
  void addActivityToStack(Activity activity);

  void removeActivityFromStack(Activity activity);

  void addActivityStart(Activity activity);

  void addActivityPause(Activity activity);

  void finishAllActivity();

  boolean isForegroundStack();
}
