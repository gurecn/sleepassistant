package com.devdroid.sleepassistant.database;

import android.content.ContentValues;
/**
 * SQL插入参数
 * 
 * 类名称：InstertParams
 * 类描述：
 * 创建人：makai
 * 修改人：makai
 * 修改时间：2014年9月5日 下午4:20:05
 * 修改备注：
 * @version 1.0.0
 *
 */
public class InsertParams {
	
	private String mTableName;
	
	private ContentValues mContentValues;

	public InsertParams(String mTableName, ContentValues mContentValues) {
		this.mTableName = mTableName;
		this.mContentValues = mContentValues;
	}
	
	public String getTableName() {
		return mTableName;
	}
	
	public ContentValues getContentValues() {
		return mContentValues;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("mTableName : ").append(mTableName);
		buffer.append("mContentValues : ").append(mContentValues.toString());
		return buffer.toString();
	}
}
