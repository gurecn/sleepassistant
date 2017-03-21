package com.devdroid.sleepassistant.database;

/**
 * 数据库异常类(处理数据库操作异常)
 * 
 * 类名称：DatabaseException
 * 修改时间：2014年8月25日 上午10:50:22
 * @version 1.0.0
 *
 */
public class DatabaseException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public DatabaseException(Exception e) {
		super(e);
	}
	
}
