package com.devdroid.sleepassistant.database;

import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
	protected BackupBaseDatabaseHelper mBackupDBHelper;
	
	public BaseDataProvider(Context context) {
		mDBHelper = new BaseDatabaseHelper(context);
		mLock = new Object();
	}

	public BaseDataProvider(Context context, boolean isBackup, int dataVersion) {
		if(isBackup) {
			mBackupDBHelper = new BackupBaseDatabaseHelper(context, dataVersion);
			mLock = new Object();
		} else {
			mDBHelper = new BaseDatabaseHelper(context);
			mLock = new Object();
		}
	}

	public Cursor query(String table, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		synchronized (mLock) {
			if(mDBHelper != null) {
				return mDBHelper.query(table, projection, selection, selectionArgs, sortOrder);
			} else {
				return mBackupDBHelper.query(table, projection, selection, selectionArgs, sortOrder);
			}
		}
	}

	public boolean delete(List<DeletePamas> list) {
		synchronized (mLock) {
			boolean isSuccess = false;
			try {
				if(mDBHelper != null) {
					isSuccess = mDBHelper.delete(list);
				}
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
				if(mDBHelper != null) {
					mDBHelper.insert(list);
				}
				isSuccess = true;
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
			return isSuccess;
		}
	}

	public Cursor rawQuery(String sql, String[] selectionArgs) {
		synchronized (mLock) {
			if(mDBHelper != null) {
				return mDBHelper.rawQuery(sql, selectionArgs);
			} else {
				return mBackupDBHelper.rawQuery(sql, selectionArgs);
			}
		}
	}

	public Cursor rawQuery(String sql) {
		return rawQuery(sql, null);
	}

	public void closeDB() {
		if (mDBHelper != null) {
			try {
				SQLiteDatabase db = mDBHelper.getWritableDatabase();
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mDBHelper = null;
		} else if(mBackupDBHelper != null){
			try {
				SQLiteDatabase db = mBackupDBHelper.getWritableDatabase();
				db.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mBackupDBHelper = null;
		}
	}
	public void closeBackup() {
		if (mBackupDBHelper != null) {
			try {
				SQLiteDatabase db = mBackupDBHelper.getWritableDatabase();
				db.execSQL("DELETE FROM " + SleepDataTable.TABLE_NAME + ";");
				mBackupDBHelper.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
