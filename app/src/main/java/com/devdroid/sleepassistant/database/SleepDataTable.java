package com.devdroid.sleepassistant.database;
/**
 * 睡眠时间表
 */
public class SleepDataTable {
	public static final String ID = "_id";
	public static final String SLEEP_YEAR = "sleep_date";
	public static final String SLEEP_MONTH = "sleep_date";
	public static final String SLEEP_DAY = "sleep_date";
	public static final String SLEEP_TYPE = "sleep_type";
	public static final String SLEEP_TIME = "sleep_time";

	/**
	 * ============== 表名 ==============
	 */
	public static final String TABLE_NAME = "sleep_data_text";

	/**
	 * ============== 构造表的语句 ==============
	 */
	public static final String CREATE_TABLE = "create table " + TABLE_NAME + " ("
			+ ID + " text, "
			+ SLEEP_YEAR + " Integer,"
			+ SLEEP_MONTH + " Integer,"
			+ SLEEP_DAY + " Integer,"
			+ SLEEP_TYPE + " text,"
			+ SLEEP_TIME + " text,"
			+ " PRIMARY KEY("+ID+"))";
}
