package com.devdroid.sleepassistant.database;

import java.util.List;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 基础数据库实现类(所有数据库都需要继承该类)
 * 类名称：BaseDatabaseHelper 类描述： 创建人：makai 修改人：makai 修改时间：2014年10月14日 下午4:06:45
 * 修改备注：
 * @version 1.0.0
 *
 */
public class BaseDatabaseHelper extends SQLiteOpenHelper {
	/**
	 * 数据库名
	 */
	private static final String DATABASE_NAME = "boost.db";
	/**
	 * 数据库最小版本号(第一个发布的版本号)
	 */
	public final static int DB_MIN_VERSION = 1;

	public BaseDatabaseHelper(Context context) {
		this(context, DATABASE_NAME, DB_MIN_VERSION);
	}

	public BaseDatabaseHelper(Context context, String name, int version) {
		super(context, name, null, version);
	}
	public boolean insert(List<InsertParams> list) throws DatabaseException {
		return DatabaseUtils.insert(this, list);
	}
	public boolean delete(List<DeletePamas> list)
			throws DatabaseException {
		return DatabaseUtils.delete(this, list);
	}
	/**
	 * 通过SQL查询 rawQuery(这里用一句话描述这个方法的作用) (这里描述这个方法适用条件 – 可选)
	 * @exception
	 * @since 1.0.0
	 */
	public Cursor rawQuery(String sql, String[] selectionArgs) {
		return DatabaseUtils.rawQuery(this, sql, selectionArgs);
	}

	/**
	 * 查询 query(这里用一句话描述这个方法的作用) (这里描述这个方法适用条件 – 可选)
	 */
	public Cursor query(String tableName, String[] projection,
			String selection, String[] selectionArgs, String sortOrder) {
		return query(tableName, projection, selection, selectionArgs, null,
				null, sortOrder);
	}

	/**
	 * 查询 query(这里用一句话描述这个方法的作用) (这里描述这个方法适用条件 – 可选)
	 */
	public Cursor query(String tableName, String[] projection,
			String selection, String[] selectionArgs, String groupBy,
			String having, String sortOrder) {
		return DatabaseUtils.query(this, tableName, projection, selection,
				selectionArgs, groupBy, having, sortOrder);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.beginTransaction();
		try {
			db.execSQL(SleepDataTable.CREATE_TABLE);
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}
	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
	}
}
