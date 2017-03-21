package com.devdroid.sleepassistant.preferences;

import java.util.HashMap;
import android.content.Context;

/**
 * 用于管理SharedPreferences的数据加载与更新.<br>
 */
public class SharedPreferencesManager {

	private PreferencesManager mDefaultPM;
	private HashMap<String, Object> mSPData = new HashMap<String, Object>();

	public SharedPreferencesManager(Context context) {
		mDefaultPM = PreferencesManager.getDefaultSharedPreference(context);
	}

	public boolean getBoolean(String key, boolean defValue) {
		boolean value = defValue;
		if (mSPData.containsKey(key)) {
			value = (Boolean) mSPData.get(key);
		} else {
			if (mDefaultPM != null) {
				value = mDefaultPM.getBoolean(key, defValue);
				mSPData.put(key, value);
			}
		}
		return value;
	}

	public float getFloat(String key, float defValue) {
		float value = defValue;
		if (mSPData.containsKey(key)) {
			value = (Float) mSPData.get(key);
		} else {
			if (mDefaultPM != null) {
				value = mDefaultPM.getFloat(key, defValue);
				mSPData.put(key, value);
			}
		}
		return value;
	}

	public int getInt(String key, int defValue) {
		int value = defValue;
		if (mSPData.containsKey(key)) {
			value = (Integer) mSPData.get(key);
		} else {
			if (mDefaultPM != null) {
				value = mDefaultPM.getInt(key, defValue);
				mSPData.put(key, value);
			}
		}
		return value;
	}

	public long getLong(String key, long defValue) {
		long value = defValue;
		if (mSPData.containsKey(key)) {
			value = (Long) mSPData.get(key);
		} else {
			if (mDefaultPM != null) {
				value = mDefaultPM.getLong(key, defValue);
				mSPData.put(key, value);
			}
		}
		return value;
	}

	public String getString(String key, String defValue) {
		String value = defValue;
		if (mSPData.containsKey(key)) {
			value = (String) mSPData.get(key);
		} else {
			if (mDefaultPM != null) {
				value = mDefaultPM.getString(key, defValue);
				mSPData.put(key, value);
			}
		}
		return value;
	}

	public void commitBoolean(String key, boolean value) {
		mDefaultPM.edit();
		mDefaultPM.putBoolean(key, value);
		mDefaultPM.commit();
		mSPData.put(key, value);
	}

	public void commitInt(String key, int value) {
		mDefaultPM.edit();
		mDefaultPM.putInt(key, value);
		mDefaultPM.commit();
		mSPData.put(key, value);
	}

	public void commitFloat(String key, float value) {
		mDefaultPM.edit();
		mDefaultPM.putFloat(key, value);
		mDefaultPM.commit();
		mSPData.put(key, value);
	}

	public void commitLong(String key, long value) {
		mDefaultPM.edit();
		mDefaultPM.putLong(key, value);
		mDefaultPM.commit();
		mSPData.put(key, value);
	}

	public void commitString(String key, String value) {
		mDefaultPM.edit();
		mDefaultPM.putString(key, value);
		mDefaultPM.commit();
		mSPData.put(key, value);
	}


	public void remove(String key) {
		mDefaultPM.edit();
		mDefaultPM.remove(key);
		mDefaultPM.commit();
		mSPData.remove(key);
	}
}
