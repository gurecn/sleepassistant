package com.devdroid.sleepassistant.database;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 数据库工具类(封装共有方法及操作)
 * 
 * 类名称：DatabaseUtils
 * 类描述：
 * 创建人：makai
 * 修改人：makai
 * 修改时间：2014年8月25日 上午10:51:17
 * 修改备注：
 * @version 1.0.0
 *
 */
public class DatabaseUtils {

	public static boolean insert(SQLiteOpenHelper sqLiteOpenHelper, List<InsertParams> list)  throws DatabaseException {
		boolean isSucces = false;
		if (null != list && !list.isEmpty()) {
			SQLiteDatabase db = null;
			try {
				db = sqLiteOpenHelper.getWritableDatabase();
				db.beginTransaction();
				for (InsertParams instertParam : list) {
					db.insert(instertParam.getTableName(), null, instertParam.getContentValues());
				}
				db.setTransactionSuccessful();
				isSucces = true;
			} catch (Exception e) {
				throw new DatabaseException(e);
			} finally {
				if (null != db) {
					if (null != db) {
						try {
							db.endTransaction();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return isSucces;
	}
	/**
	 * 批量删除
	 * @param sqLiteOpenHelper
	 * @param list
	 * @return
	 * @throws DatabaseException
	 */
	public static boolean delete(SQLiteOpenHelper sqLiteOpenHelper, List<DeletePamas> list)  throws DatabaseException {
		boolean isSucces = false;
		if (null != list && !list.isEmpty()) {
			SQLiteDatabase db = null;
			try {
				db = sqLiteOpenHelper.getWritableDatabase();
				db.beginTransaction();
				for (DeletePamas deletePamas : list) {
					db.delete(deletePamas.getTableName(), deletePamas.getSelection(), deletePamas.getWhereArgs());
				}
				db.setTransactionSuccessful();
				isSucces = true;
			} catch (Exception e) {
				throw new DatabaseException(e);
			} finally {
				if (null != db) {
					if (null != db) {
						try {
							db.endTransaction();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return isSucces;
	}
	
	public static Cursor rawQuery(SQLiteOpenHelper sqLiteOpenHelper, String sql, String[] selectionArgs) {
		Cursor result = null;
		try {
			SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
			result = db.rawQuery(sql, selectionArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 用于单表查询
	 */
	public static Cursor query(SQLiteOpenHelper sqLiteOpenHelper, String tableName, String[] projection, String selection, String[] selectionArgs, 
				String groupBy, String having, String sortOrder) {
		Cursor result = null;
		try {
			SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
			result = db.query(tableName, projection, selection, selectionArgs, groupBy, having, sortOrder);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * 用于单表查询
	 */
	public static Cursor query(SQLiteOpenHelper sqLiteOpenHelper, String tableName, String[] projection, String selection, String[] selectionArgs,
							   String groupBy, String having, String sortOrder, String limit) {
		Cursor result = null;
		try {
			SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
			result = db.query(tableName, projection, selection, selectionArgs, groupBy, having, sortOrder,limit);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		return result;
	}
}
