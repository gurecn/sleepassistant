package com.devdroid.sleepassistant.database;
/**
 * 应用锁表
 *
 */
public class LockerTable {
	
	public static final String COMPONENTNAME = "componentname";

	/**
	 * ============== 表名 ==============
	 */
	public static final String TABLE_NAME = "applock_locker";

	/**
	 * ============== 构造表的语句 ==============
	 */
	public static final String CREATE_TABLE = "create table " + TABLE_NAME + " ("
			+ COMPONENTNAME + " text"
			+ ")";
}
