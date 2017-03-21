package com.devdroid.sleepassistant.database;

import java.util.List;
import android.content.Context;

import com.devdroid.sleepassistant.mode.SleepDataMode;

/**
 * 关于数据管理
 * @author zhanghuijun
 *
 */
public class SleepDataDao {

    private SleepDatabaseHelper mSnssdkDatabaseHelper = null;
    /**
     * Context
     */
    private Context mContext = null;

    public SleepDataDao(Context context, BaseDataProvider dataProvider) {
        mContext = context;
        mSnssdkDatabaseHelper = new SleepDatabaseHelper(context, dataProvider);
    }

    public List<SleepDataMode> querySleepDataInfo() {
        return mSnssdkDatabaseHelper.querySleepDataInfo();
    }

    public void insertSleepDataItem(List<SleepDataMode> insertList) {
        mSnssdkDatabaseHelper.insertSleepDataItem(insertList);
    }

    public void deleteSleepDataItem(List<SleepDataMode> deleteList) {
        mSnssdkDatabaseHelper.deleteSleepDataItem(deleteList);
    }


    public void insertSleepDataItem(SleepDataMode sleepDataMode) {
        mSnssdkDatabaseHelper.insertSleepDataItem(sleepDataMode);
    }

}
