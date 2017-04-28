package com.devdroid.sleepassistant.database;

import java.util.List;
import android.content.Context;

/**
 * 关于锁信息的数据管理
 * @author zhanghuijun
 *
 */
public class LockerDao {
	/**
	 * Locker表的数据库管理
	 */
	private LockerDatabaseHelper mLockerDatabaseHelper = null;
	/**
	 * Context
	 */
	private Context mContext = null;
	
	public LockerDao(Context context, BaseDataProvider dataProvider) {
		mContext = context;
		mLockerDatabaseHelper = new LockerDatabaseHelper(context, dataProvider);
	}

	
	public List<String> queryLockerInfo() {
		return mLockerDatabaseHelper.queryLockerInfo();
	}

    /**
     * 加锁一组选项
     */
    public void lockItem(List<String> lockList) {

    	mLockerDatabaseHelper.lockItem(lockList);
    }

    /**
     * 解锁一组选项
     */
    public void unlockItem(List<String> unLockList) {
    	mLockerDatabaseHelper.unlockItem(unLockList);
    }
    
    /**
     * 解锁一个选项
     */
    public void unlockItem(String packageName) {
    	mLockerDatabaseHelper.unlockItem(packageName);
    }

	/**
	 * 加锁一个选项
	 */
	public void lockItem(String packageName) {
		mLockerDatabaseHelper.lockItem(packageName);
	}

}
