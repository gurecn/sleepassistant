package com.devdroid.sleepassistant.database;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * 
 * <br>类描述:数据库读写接口
 * <br>功能详细描述:
 * 
 * @author  liuheng
 * @date  [2012-9-4]
 */
public interface IDatabaseObject {

	/**
	 * 写入数据库
	 * 
	 * @param values
	 */
	public abstract void writeObject(ContentValues values, String table);

	/**
	 * 从Cursor中读取数据
	 * 
	 * @param cursor
	 */
	public abstract void readObject(Cursor cursor, String table);
}
