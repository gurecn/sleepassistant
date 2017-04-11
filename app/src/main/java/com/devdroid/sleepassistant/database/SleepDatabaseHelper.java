package com.devdroid.sleepassistant.database;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.devdroid.sleepassistant.mode.SleepDataMode;

public class SleepDatabaseHelper {

	public static final String TAG = SleepDatabaseHelper.class.getSimpleName();

	private BaseDataProvider mHelper = null;

	public SleepDatabaseHelper(Context context, BaseDataProvider dataProvider) {
		mHelper = dataProvider;
	}
    /**
     * 查询当前加锁应用信息
     * @return
     */
    public List<SleepDataMode> querySleepDataInfo() {
        List<SleepDataMode> list = new ArrayList<>();
        Cursor cursor = mHelper.query(SleepDataTable.TABLE_NAME, null, null, null, SleepDataTable.ID + " DESC");
        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    int year = cursor.getInt(cursor.getColumnIndex(SleepDataTable.SLEEP_YEAR));
                    int month = cursor.getInt(cursor.getColumnIndex(SleepDataTable.SLEEP_MONTH));
                    int day = cursor.getInt(cursor.getColumnIndex(SleepDataTable.SLEEP_DAY));
                    int hour = cursor.getInt(cursor.getColumnIndex(SleepDataTable.SLEEP_HOUR));
                    int minute = cursor.getInt(cursor.getColumnIndex(SleepDataTable.SLEEP_MINUTE));
                    int sleepType = cursor.getInt(cursor.getColumnIndex(SleepDataTable.SLEEP_TYPE));
                    SleepDataMode sleepDataMode = new SleepDataMode(year, month, day, hour, minute, sleepType);
                    if (null != sleepDataMode) {
                        list.add(sleepDataMode);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 查询当前加锁应用信息
     * @return
     */
    public List<SleepDataMode> querySleepDataInfo(int year, int month) {
        List<SleepDataMode> list = new ArrayList<>();
        String selection = SleepDataTable.SLEEP_YEAR + " = ? and " +  SleepDataTable.SLEEP_MONTH + " = ?";
        String[] selectionArgs = new  String[]{ year + "", month + ""};
        Cursor cursor = mHelper.query(SleepDataTable.TABLE_NAME, null, selection, selectionArgs, SleepDataTable.ID + " DESC");
        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    int day = cursor.getInt(cursor.getColumnIndex(SleepDataTable.SLEEP_DAY));
                    int hour = cursor.getInt(cursor.getColumnIndex(SleepDataTable.SLEEP_HOUR));
                    int minute = cursor.getInt(cursor.getColumnIndex(SleepDataTable.SLEEP_MINUTE));
                    int sleepType = cursor.getInt(cursor.getColumnIndex(SleepDataTable.SLEEP_TYPE));
                    SleepDataMode sleepDataMode = new SleepDataMode(year, month, day, hour, minute, sleepType);
                    if (null != sleepDataMode) {
                        list.add(sleepDataMode);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return list;
    }

    /**
     * 检查是否存在该内容
     */
    private boolean checkExist(String sql) {
        Cursor cursor = mHelper.rawQuery(sql);
        if (null != cursor) {
            try {
                if (cursor.moveToNext()) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return false;
    }
    public void insertSleepDataItem(List<SleepDataMode> insertSnssdkList) {
        ArrayList<InsertParams> list = new ArrayList<InsertParams>();
        for (SleepDataMode sleepDataMode : insertSnssdkList) {
            ContentValues values = new ContentValues();
            values.put(SleepDataTable.SLEEP_YEAR,sleepDataMode.getYear());
            values.put(SleepDataTable.SLEEP_MONTH,sleepDataMode.getMonth());
            values.put(SleepDataTable.SLEEP_DAY,sleepDataMode.getDay());
            values.put(SleepDataTable.SLEEP_HOUR,sleepDataMode.getHour());
            values.put(SleepDataTable.SLEEP_MINUTE,sleepDataMode.getMinute());
            values.put(SleepDataTable.SLEEP_TYPE,sleepDataMode.getSleepType());
            InsertParams insert = new InsertParams(SleepDataTable.TABLE_NAME, values);
            list.add(insert);
        }
        if (!list.isEmpty()) {
            mHelper.insert(list);
        }
    }

    public void deleteSleepDataItem(List<SleepDataMode> deleteSnssdkList) {
        ArrayList<DeletePamas> list = new ArrayList<DeletePamas>();
        for (SleepDataMode sleepDataMode : deleteSnssdkList) {
            DeletePamas delete = new DeletePamas(SleepDataTable.TABLE_NAME, SleepDataTable.SLEEP_YEAR + "= ?", new String[]{sleepDataMode.getYear() + ""});
            list.add(delete);
        }
        if (!list.isEmpty()) {
            mHelper.delete(list);
        }
    }

    public void insertSleepDataItem(SleepDataMode sleepDataMode) {
        ArrayList<InsertParams> list = new ArrayList<InsertParams>();
        ContentValues values = new ContentValues();
        values.put(SleepDataTable.SLEEP_YEAR,sleepDataMode.getYear());
        values.put(SleepDataTable.SLEEP_MONTH,sleepDataMode.getMonth());
        values.put(SleepDataTable.SLEEP_DAY,sleepDataMode.getDay());
        values.put(SleepDataTable.SLEEP_HOUR,sleepDataMode.getHour());
        values.put(SleepDataTable.SLEEP_MINUTE,sleepDataMode.getMinute());
        values.put(SleepDataTable.SLEEP_TYPE,sleepDataMode.getSleepType());
        InsertParams insert = new InsertParams(SleepDataTable.TABLE_NAME, values);
        list.add(insert);
        if (!list.isEmpty()) {
            mHelper.insert(list);
        }
    }

    public void deleteSleepDataItem(SleepDataMode sleepDataMode) {
    	ArrayList<DeletePamas> list = new ArrayList<DeletePamas>();
        	DeletePamas delete = new DeletePamas(SleepDataTable.TABLE_NAME, SleepDataTable.SLEEP_YEAR + "=?", new String[]{sleepDataMode.getYear() + ""});
            list.add(delete);
        if (!list.isEmpty()) {
        	mHelper.delete(list);
        }
    }
}
