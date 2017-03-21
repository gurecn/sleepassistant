package com.devdroid.sleepassistant.database;

import java.util.List;
import android.content.Context;
import android.database.Cursor;
/**
 * 基础数据库操作类(所有需要操作数据库的接口需要实现该类)
 * 类名称：BaseDataProvider
 * 创建人：makai
 * 修改时间：2014年10月14日 下午4:10:36
 * @version 1.0.0
 *
 */
public class BaseDataProvider {
	protected Object mLock;
	protected BaseDatabaseHelper mDBHelper;
	
	public BaseDataProvider(Context context) {
		mDBHelper = new BaseDatabaseHelper(context);
		mLock = new Object();
	}
	
	public Cursor query(String table, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		synchronized (mLock) {
			return mDBHelper.query(table, projection, selection, selectionArgs, sortOrder);
		}
	}
	
	public boolean delete(List<DeletePamas> list) {
		synchronized (mLock) {
			boolean isSuccess = false;
			try {
				isSuccess = mDBHelper.delete(list);
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
			return isSuccess;
		}
	}

	public boolean insert(List<InsertParams> list) {
		synchronized (mLock) {
			boolean isSuccess = false;
			try {
				mDBHelper.insert(list);
				isSuccess = true;
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
			return isSuccess;
		}
	}
	
	public Cursor rawQuery(String sql, String[] selectionArgs) {
		synchronized (mLock) {
			return mDBHelper.rawQuery(sql, selectionArgs);
		}
	}
	
	public Cursor rawQuery(String sql) {
		return rawQuery(sql, null);
	}
}
