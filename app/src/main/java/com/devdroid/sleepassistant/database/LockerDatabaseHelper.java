package com.devdroid.sleepassistant.database;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

/**
 * @author zhanghuijun
 *
 */
public class LockerDatabaseHelper {

	public static final String TAG = "zhanghuijun AppLockDatabaseHelper";

	private BaseDataProvider mHelper = null;

	public LockerDatabaseHelper(Context context, BaseDataProvider dataProvider) {
		mHelper = dataProvider;
	}
	
	   /**
     * 查询当前加锁应用信息
     * @return
     */
    public List<String> queryLockerInfo() {
        List<String> list = new ArrayList<String>();
        Cursor cursor = mHelper.query(LockerTable.TABLE_NAME, null, null, null, null);
        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    String component = cursor.getString(cursor.getColumnIndex(LockerTable.COMPONENTNAME));
                    if (null != component) {
                        list.add(component);
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
    public void lockItem(List<String> lockList) {
        ArrayList<InsertParams> list = new ArrayList<InsertParams>();
        for (String pkgName : lockList) {
            if (!checkExist("select * from " + LockerTable.TABLE_NAME + " where " + LockerTable.COMPONENTNAME + "='" + pkgName + "'")) {
                ContentValues values = new ContentValues();
                values.put(LockerTable.COMPONENTNAME,pkgName);
                InsertParams insert = new InsertParams(LockerTable.TABLE_NAME, values);
                list.add(insert);
            }
        }
        if (!list.isEmpty()) {
            mHelper.insert(list);
        }
    }

    public void unlockItem(List<String> unLockList) {
        ArrayList<DeletePamas> list = new ArrayList<DeletePamas>();
        for (String pkgName : unLockList) {
            DeletePamas delete = new DeletePamas(LockerTable.TABLE_NAME, LockerTable.COMPONENTNAME + "=?", new String[]{pkgName});
            list.add(delete);
        }
        if (!list.isEmpty()) {
            mHelper.delete(list);
        }
    }

    public void lockItem(String pkgName) {
        ArrayList<InsertParams> list = new ArrayList<InsertParams>();
        if (!checkExist("select * from " + LockerTable.TABLE_NAME + " where " + LockerTable.COMPONENTNAME + "='" + pkgName + "'")) {
            ContentValues values = new ContentValues();
            values.put(LockerTable.COMPONENTNAME,pkgName);
            InsertParams insert = new InsertParams(LockerTable.TABLE_NAME, values);
            list.add(insert);
        }
        if (!list.isEmpty()) {
            mHelper.insert(list);
        }
    }

    public void unlockItem(String pkgName) {
    	ArrayList<DeletePamas> list = new ArrayList<DeletePamas>();
        	DeletePamas delete = new DeletePamas(LockerTable.TABLE_NAME, LockerTable.COMPONENTNAME + "=?", new String[]{pkgName});
            list.add(delete);
        if (!list.isEmpty()) {
        	mHelper.delete(list);
        }
    }
}
