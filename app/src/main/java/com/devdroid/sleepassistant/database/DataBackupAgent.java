package com.devdroid.sleepassistant.database;


import android.app.backup.BackupAgentHelper;
import android.app.backup.FileBackupHelper;

/**
 * User:Gaolei  gurecn@gmail.com
 * Date:2017/11/22
 * I'm glad to share my knowledge with you all.
 */
public class DataBackupAgent extends BackupAgentHelper {
	static final String DATA_BASE_FILENAME = "boost.db";
	
	static final String FILES_BACKUP_KEY = "database";
	
	@Override
	public void onCreate() {
		FileBackupHelper helper = new FileBackupHelper(this, DATA_BASE_FILENAME);
		addHelper(FILES_BACKUP_KEY, helper);
	}
}
