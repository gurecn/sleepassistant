package com.devdroid.sleepassistant.database;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.devdroid.sleepassistant.mode.SleepDataMode;

public class SleepDatabaseHelper {
	private BaseDataProvider mHelper = null;

	public SleepDatabaseHelper(Context context, BaseDataProvider dataProvider) {
		mHelper = dataProvider;
	}
    /**
     * @return
     */
    public List<SleepDataMode> querySleepDataInfo() {
        List<SleepDataMode> list = new ArrayList<>();
        Cursor cursor = mHelper.query(SleepDataTable.TABLE_NAME, null, null, null, SleepDataTable.SLEEP_YEAR + "," + SleepDataTable.SLEEP_MONTH + "," + SleepDataTable.SLEEP_DAY);
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
     * 查询
     * @return
     */
    public List<SleepDataMode> querySleepDataInfo(int year, int month) {
        List<SleepDataMode> list = new ArrayList<>();
        String selection = SleepDataTable.SLEEP_YEAR + " = ? and " +  SleepDataTable.SLEEP_MONTH + " = ?";
        String[] selectionArgs = new  String[]{ year + "", month + ""};
        Cursor cursor = mHelper.query(SleepDataTable.TABLE_NAME, null, selection, selectionArgs,  SleepDataTable.SLEEP_YEAR + "," + SleepDataTable.SLEEP_MONTH + "," + SleepDataTable.SLEEP_DAY);
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
     * 查询
     * @return
     */
    public SleepDataMode querySleepDataInfo(int year, int month, int day) {
        String selection = SleepDataTable.SLEEP_YEAR + " = ? and " +  SleepDataTable.SLEEP_MONTH + " = ? and " +  SleepDataTable.SLEEP_DAY + " = ?";
        String[] selectionArgs = new  String[]{ year + "", month + "", day + ""};
        Cursor cursor = mHelper.query(SleepDataTable.TABLE_NAME, null, selection, selectionArgs, SleepDataTable.ID + " DESC");
        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    int hour = cursor.getInt(cursor.getColumnIndex(SleepDataTable.SLEEP_HOUR));
                    int minute = cursor.getInt(cursor.getColumnIndex(SleepDataTable.SLEEP_MINUTE));
                    int sleepType = cursor.getInt(cursor.getColumnIndex(SleepDataTable.SLEEP_TYPE));
                    SleepDataMode sleepDataMode = new SleepDataMode(year, month, day, hour, minute, sleepType);
                    return sleepDataMode;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }
        }
        return null;
    }
    public void insertSleepDataItem(List<SleepDataMode> insertSnssdkList) {
        ArrayList<InsertParams> list = new ArrayList<InsertParams>();
        for (SleepDataMode sleepDataMode : insertSnssdkList) {
            deleteSleepDataItem(sleepDataMode);
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
            String selection = SleepDataTable.SLEEP_YEAR + " = ? and " +  SleepDataTable.SLEEP_MONTH + " = ? and " +  SleepDataTable.SLEEP_DAY + " = ?";
            String[] selectionArgs = new  String[]{ sleepDataMode.getYear() + "", sleepDataMode.getMonth() + "", sleepDataMode.getDay() + ""};
            DeletePamas delete = new DeletePamas(SleepDataTable.TABLE_NAME, selection, selectionArgs);
            list.add(delete);
        }
        if (!list.isEmpty()) {
            mHelper.delete(list);
        }
    }

    public void deleteSleepDataItem(SleepDataMode deleteSleepMode) {
        ArrayList<DeletePamas> list = new ArrayList<DeletePamas>();
        String selection = SleepDataTable.SLEEP_YEAR + " = ? and " +  SleepDataTable.SLEEP_MONTH + " = ? and " +  SleepDataTable.SLEEP_DAY + " = ?";
        String[] selectionArgs = new  String[]{ deleteSleepMode.getYear() + "", deleteSleepMode.getMonth() + "", deleteSleepMode.getDay() + ""};
        DeletePamas delete = new DeletePamas(SleepDataTable.TABLE_NAME, selection, selectionArgs);
        list.add(delete);
        mHelper.delete(list);
    }

    public void insertSleepDataItem(SleepDataMode sleepDataMode) {
        deleteSleepDataItem(sleepDataMode);
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

    public void closeBackup(){
        mHelper.closeBackup();
    }
}
