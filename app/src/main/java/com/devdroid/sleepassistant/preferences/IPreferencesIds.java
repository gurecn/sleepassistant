package com.devdroid.sleepassistant.preferences;

/**
 * prefers的文件名，key
 *
 * @author Administrator
 */
public class IPreferencesIds {

    // ============================== 文件名 ====================================
    public static final String DEFAULT_SHAREPREFERENCES_FILE = "default_cfg";
    //
    public static final String SLEEP_PLAN_TYPE= "sleep_plan_type";
    public static final String SLEEP_PLAN_SLEEP_TIME= "sleep_plan_sleep_time";
    public static final String SLEEP_PLAN_GET_UP_TIME= "sleep_plan_get_up_time";


    /**
     * 程序首次运行的时间，也可粗略作为首次安装应用的时间, 单位毫秒<br>
     * 值类型: long<br>
     */
    public final static String KEY_FIRST_START_APP_TIME = "key_first_start_app_time"; //应用第一次启动时间
    public final static String KEY_LAST_INSTALL_APP_CODE = "key_last_install_app_code"; //上次安装的应用版本
}
