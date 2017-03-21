package com.devdroid.sleepassistant.preferences;

import java.util.Map;
import java.util.Set;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;

import com.devdroid.sleepassistant.application.TheApplication;


/**
 * sp管理类，提供对sp操作的包装，提交时统一做处理，2.2版本以上使用apply方法；
 * 
 * @author chenshihang
 * @version 1.0 9/15/04
 */
public final class PreferencesManager {
	private SharedPreferences mPreferences;
	private Editor mEditor;

	private PreferencesManager() {

	}

	/**
	 * 获取默认的sp文件default_cfg.xml，默认模式为${@link Context#MODE_PRIVATE}
	 * 
	 * @param context
	 * @return
	 */
	public static PreferencesManager getDefaultSharedPreference(Context context) {
		return getSharedPreference(context,
				IPreferencesIds.DEFAULT_SHAREPREFERENCES_FILE,
				Context.MODE_PRIVATE);
	}

	/**
	 * 获取sp
	 * 
	 * @param context
	 * @param name
	 *            文件名称
	 * @param mode
	 *            模式
	 * @return
	 */
	public static PreferencesManager getSharedPreference(Context context,
			String name, int mode) {
		if (context != null) {
			try {
				PreferencesManager preferencesManager = new PreferencesManager();
				preferencesManager.mPreferences = context.getSharedPreferences(
						name, mode);
				preferencesManager.mEditor = preferencesManager.mPreferences
						.edit();
				return preferencesManager;
			} catch (Exception e) {
				// CSH-TODO send error report
			}
		}
		return null;
	}
	
	/**
	 * 获取sp
	 *
	 */
	public static PreferencesManager getSharedPreference(String name) {
		return getSharedPreference(TheApplication.getAppContext(), name, Context.MODE_MULTI_PROCESS);
	}

	/**
	 * 清除数据
	 */
	public void clear() {
		if (mEditor != null) {
			mEditor.clear().commit();
		} else if (mPreferences != null) {
			mEditor = mPreferences.edit();
			mEditor.clear().commit();
		}
	}

	public void remove(String key) {
		mPreferences.edit().remove(key).commit();
	}

	public Map<String, ?> getAll() {
		return mPreferences.getAll();
	}

	public boolean contains(String key) {
		if (mPreferences == null || key == null) {
			return false;
		}
		return mPreferences.contains(key);
	}

	public boolean getBoolean(String key, boolean defValue) {
		if (mPreferences != null) {
			return mPreferences.getBoolean(key, defValue);
		}
		return defValue;
	}

	public float getFloat(String key, float defValue) {
		if (mPreferences != null) {
			return mPreferences.getFloat(key, defValue);
		}
		return defValue;
	}

	public int getInt(String key, int defValue) {
		if (mPreferences != null) {
			return mPreferences.getInt(key, defValue);
		}
		return defValue;
	}

	public long getLong(String key, long defValue) {
		if (mPreferences != null) {
			return mPreferences.getLong(key, defValue);
		}
		return defValue;
	}

	public String getString(String key, String defValue) {
		if (mPreferences != null) {
			return mPreferences.getString(key, defValue);
		}
		return defValue;
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public Set<String> getStringSet(String key, Set<String> defValues) {
		if (mPreferences != null) {
			return mPreferences.getStringSet(key, defValues);
		}
		return defValues;
	}

	public void registerOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		mPreferences.registerOnSharedPreferenceChangeListener(listener);
	}

	public void unregisterOnSharedPreferenceChangeListener(
			OnSharedPreferenceChangeListener listener) {
		mPreferences.unregisterOnSharedPreferenceChangeListener(listener);
	}

	public void putBoolean(String key, boolean b) {
		if (mEditor != null) {
			mEditor.putBoolean(key, b);
		}
	}

	public void putInt(String key, int i) {
		if (mEditor != null) {
			mEditor.putInt(key, i);
		}
	}

	public void putFloat(String key, float f) {
		if (mEditor != null) {
			mEditor.putFloat(key, f);
		}
	}

	public void putLong(String key, long l) {
		if (mEditor != null) {
			mEditor.putLong(key, l);
		}
	}

	public void putString(String key, String s) {
		if (mEditor != null) {
			mEditor.putString(key, s);
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void putStringSet(String key, Set<String> values) {
		if (mEditor != null) {
			mEditor.putStringSet(key, values);
		}
	}

	/**
	 * 同步提交，当需要返回结果时才使用此方法，否则建议使用{@link #commit()}
	 * 
	 * @see #commit()
	 * @return true 提交成功，false 提交失败
	 */
	public boolean commitImmediate() {
		boolean bRet = false;
		if (mEditor != null) {
			bRet = mEditor.commit();
		}
		return bRet;
	}

	/**
	 * 异步提交.<br>
	 */
	public void commit() {
		if (mEditor != null) {
			apply(mEditor);
		}
	}

	@SuppressLint("CommitPrefEdits")
	public void edit() {
		if (mEditor == null && mPreferences != null) {
			mEditor = mPreferences.edit();
		}
	}

	/**
	 * 异步提交.<br>
	 *
	 * @param editor
	 */
	private static void apply(final Editor editor) {
		editor.commit();
	}

	/**
	 * 加载指定的sp，用于桌面启动时预加载，减少在UI操作过程中加载sp导致的卡顿等问题。此方法需要放到异步线程执行。
	 * 
	 * @param context
	 * @param spNames
	 *            sp名称
	 * @param modes
	 *            sp对应的加载模式
	 */
	public static void loadSharedPreferences(Context context, String[] spNames,
			int[] modes) {
		if (spNames == null || modes == null || spNames.length != modes.length) {
			throw new IllegalArgumentException("counts must be equal");
		}
		final int len = spNames.length;
		for (int i = 0; i < len; i++) {
			getSharedPreference(context, spNames[i], modes[i]);
		}
	}
}
