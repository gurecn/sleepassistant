package com.devdroid.sleepassistant.application;

import android.app.Activity;
import android.app.Application;

import com.devdroid.sleepassistant.base.IApplication;

import java.util.LinkedList;
import java.util.List;

public class FrontiaApplication extends Application implements IApplication {
  private static final String TAG = "FrontiaApplication";
  private List<Activity> activitiesInStack = new LinkedList();
  private List<Activity> activitiesInStart = new LinkedList();


  @Override
  public void addActivityToStack(Activity activity) {
    this.activitiesInStack.add(activity);
  }

  @Override
  public void removeActivityFromStack(Activity activity) {
    this.activitiesInStack.remove(activity);
  }

  @Override
  public void addActivityStart(Activity activity) {
    activitiesInStart.add(activity);
  }

  @Override
  public void addActivityPause(Activity activity) {
    activitiesInStart.remove(activity);
  }

  @Override
  public void finishAllActivity() {
    for (int i = 0; i < this.activitiesInStack.size(); i++) {
      Activity activity = this.activitiesInStack.get(i);
      if (activity != null && !activity.isFinishing()) {
        activity.finish();
      }
    }
    this.activitiesInStack.clear();
  }

  @Override
  public boolean isForegroundStack() {
    return activitiesInStart.size() > 0;
  }
}
