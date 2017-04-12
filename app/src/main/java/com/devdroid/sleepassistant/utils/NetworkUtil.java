package com.devdroid.sleepassistant.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * 网络工具类
 *
 * @author chenbenbin
 *
 */
public class NetworkUtil {

	/**
	 * 检查当前网络状态是否可用
	 */
	public static boolean isNetworkOK(Context mContext) {
		boolean result = false;
		if (mContext != null) {
			ConnectivityManager cm = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm != null) {
				android.net.NetworkInfo networkInfo = cm.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					result = true;
				}
			}
		}
		return result;
	}

}
