package com.devdroid.sleepassistant.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Gaolei on 2017/4/19.
 */

public class BackupBaseDatabaseHelper extends SQLiteOpenHelper {
    /**
     * 数据库名
     */
    private static final String DATABASE_NAME = "boost_backup.db";

    public BackupBaseDatabaseHelper(Context context, int dataVersion) {
        this(context, DATABASE_NAME, dataVersion);
    }

    public BackupBaseDatabaseHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    /**
     * 查询 query
     */
    public Cursor query(String tableName, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return query(tableName, projection, selection, selectionArgs, null,
                null, sortOrder);
    }

    /**
     * 查询 query
     */
    public Cursor query(String tableName, String[] projection,
                        String selection, String[] selectionArgs, String groupBy,
                        String having, String sortOrder) {
        return DatabaseUtils.query(this, tableName, projection, selection,
                selectionArgs, groupBy, having, sortOrder);
    }


    /**
     * 通过SQL查询 rawQuery
     */
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return DatabaseUtils.rawQuery(this, sql, selectionArgs);
    }

    /**
     * 查询 query
     */
    public Cursor query(String tableName, String[] projection, String selection, String[] selectionArgs, String sortOrder, String limit) {
        return query(tableName, projection, selection, selectionArgs, null, null, sortOrder,limit);
    }

    /**
     * 查询 query
     */
    public Cursor query(String tableName, String[] projection, String selection, String[] selectionArgs, String groupBy, String having, String sortOrder, String limit) {
        return DatabaseUtils.query(this, tableName, projection, selection, selectionArgs, groupBy, having, sortOrder,limit);
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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
