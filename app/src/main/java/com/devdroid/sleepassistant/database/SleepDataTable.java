package com.devdroid.sleepassistant.database;
/**
 * 睡眠时间表
 */
public class SleepDataTable {
	public static final String ID = "_id";
	public static final String SLEEP_YEAR = "sleep_year";
	public static final String SLEEP_MONTH = "sleep_month";
	public static final String SLEEP_DAY = "sleep_day";
	public static final String SLEEP_HOUR = "sleep_hour";
	public static final String SLEEP_MINUTE = "sleep_minute";
	public static final String SLEEP_TYPE = "sleep_type";

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
			+ SLEEP_HOUR + " Integer,"
			+ SLEEP_MINUTE + " Integer,"
			+ SLEEP_TYPE + " text,"
			+ " PRIMARY KEY("+ID+"))";
}
