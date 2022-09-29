package com.devdroid.sleepassistant.application;

import android.content.Context;
import android.util.Log;

import com.devdroid.sleepassistant.database.BaseDataProvider;
import com.devdroid.sleepassistant.database.LockerDao;
import com.devdroid.sleepassistant.database.SleepDataDao;
import com.devdroid.sleepassistant.preferences.SharedPreferencesManager;

import de.greenrobot.event.EventBus;

/**
 * Created with IntelliJ IDEA.
 * User:Gaolei  gurecn@gmail.com
 * Date:2016/10/10
 * I'm glad to share my knowledge with you all.
 */
public class LauncherModel {
    private static LauncherModel sInstance;
    private final SharedPreferencesManager mSharedPreferencesManager;
    private EventBus GLOBAL_EVENT_BUS = EventBus.getDefault();
    private final SleepDataDao mSleepDao;
    private final Context mContext;
    private LockerDao mLockerDao;

    private LauncherModel(Context context) {
        mContext = context;
        mSharedPreferencesManager = new SharedPreferencesManager(mContext);
        BaseDataProvider dataProvider = new BaseDataProvider(mContext);
        mSleepDao = new SleepDataDao(mContext, dataProvider);
        mLockerDao = new LockerDao(mContext, dataProvider);
    }
    /**
     * 初始化单例,在程序启动时调用<br>
     */
    static void initSingleton(Context context) {
        sInstance = new LauncherModel(context);
    }
    /**
     * 获取实例<br>
     */
    public static LauncherModel getInstance() {
        return sInstance;
    }

    public SharedPreferencesManager getSharedPreferencesManager() {
        return mSharedPreferencesManager;
    }
    public SleepDataDao getSleepDataDao() {
        return mSleepDao;
    }

    /**
     * 获取备份数据库操作实例
     */
    public SleepDataDao getBackupContactsDao(int dataVersion) {
        BaseDataProvider backupProvider = new BaseDataProvider(mContext, true, dataVersion);
        return new SleepDataDao(mContext, backupProvider);
    }
    /**
     * 获取备份数据库操作实例
     */
    public LockerDao getLockerDao() {
         return mLockerDao;
    }


    /**
     * 获取一个全局的EventBus实例<br>
     */
    public EventBus getGlobalEventBus() {
        return GLOBAL_EVENT_BUS;
    }

    /**
     * 使用全局EventBus post一个事件<br>
     */
    public void postEvent(Object event) {
        GLOBAL_EVENT_BUS.post(event);
    }
}
