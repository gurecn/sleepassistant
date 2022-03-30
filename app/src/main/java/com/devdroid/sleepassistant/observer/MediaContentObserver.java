package com.devdroid.sleepassistant.observer;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import com.devdroid.sleepassistant.application.TheApplication;
import com.devdroid.sleepassistant.listener.OnScreenShotListener;
import java.util.regex.Pattern;

public class MediaContentObserver extends ContentObserver {
  public static String TAG = "MediaContentObserver";
  private Context mContext;
  private int imageNums;
  private static MediaContentObserver instance;
  OnScreenShotListener shotListener;
  private  boolean isRegister = false;
  public MediaContentObserver(Context context){
    super(null);
    mContext = context;
  }

  public static MediaContentObserver getInstance() {
    Log.d(TAG, "getInstance");
    if(instance == null){
      instance = new MediaContentObserver(TheApplication.getAppContext());
    }
    return instance;
  }

  public  void stareObserve(OnScreenShotListener shotListener){
    Log.d(TAG, "stareObserve");
    this.shotListener = shotListener;
    if(isRegister) return;
    if(instance == null){
      instance = new MediaContentObserver(TheApplication.getAppContext());
    }
    instance.register();
  }

  public void stopObserve(){
    Log.d(TAG, "stopObserve");
    isRegister = false;
    if(instance != null) {
      instance.unregister();
    }
  }

  private void register() {
    Log.d(TAG, "register");
    isRegister = true;
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) { //Android 9及以下版本,否则不会回调onChange
      mContext.getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, this);
      mContext.getContentResolver().registerContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, false, this);
    } else {  //Android 10，11以上版本
      // 注册内容观察者
      mContext.getContentResolver().registerContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, true, this);
      mContext.getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, this);
    }
  }

  private void unregister(){
    mContext.getContentResolver().unregisterContentObserver(this);
  }

  @Override
  public void onChange(boolean selfChange) {
    super.onChange(selfChange);
    Log.d(TAG, "onChange");
    if(!TheApplication.getApplication().isForegroundStack()){
      return;
    }
    Log.d(TAG, "isForegroundStack");
    String[] columns = { MediaStore.MediaColumns.DATE_ADDED, MediaStore.MediaColumns.DATA };
    Cursor cursor = null;
    try{
      cursor = mContext.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,null,null, MediaStore.MediaColumns.DATE_MODIFIED + " desc");
      if(cursor == null) return;
      int count = cursor.getCount();
      Log.d(TAG, "imageNums:" + imageNums + " count:" + count);
      if(imageNums == 0){
        imageNums = count;
      } else if(imageNums >= count){
        imageNums = count;
        return;
      }
      //咨询客服
      imageNums = count;
      if(cursor.moveToFirst()){
        Pattern p = Pattern.compile("screenshot|screenshots|screen_shot|screen-shot|screen shot|screencapture|screen_capture|screen-capture|screen capture|screencap|screen_cap|screen-cap|screen cap|截屏", Pattern.CASE_INSENSITIVE);
        String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        long addTime = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED));
        if(p.matcher(filePath).find() && matchTime(addTime)){
          this.shotListener.onShot(filePath);
        }
      }
    } catch (Exception e){
      e.printStackTrace();
    } finally {
      if(cursor != null){
        cursor.close();
      }
    }
  }

  /**
   * 匹配时间：1.5s内的图片匹配
   * @param addTime
   * @return
   */
  private boolean matchTime(long addTime){
    return System.currentTimeMillis() - addTime *1000 < 1500;
  }
}
