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


    public final static String APP_LOCK_RESTRICTION_SRART_TIME = "applock_restriction_start_time"; //应用使用限制开始时间
    public final static String APP_LOCK_RESTRICTION_END_TIME = "applock_restriction_end_time"; //应用使用限制结束时间


    public final static String KEY_SLEEP_TIME_HAS_SET = "key_sleep_time_set"; //用户已经设置过睡眠时间

    public final static String KEY_THEME_NIGHT_MODE = "key_theme_night_mode"; //设置主题模式


    public final static String KEY_SHICI_TIME_LAST = "key_shici_time_last"; //上次更新诗词的时间：112124：月日时
    public final static String KEY_SHICI_CONTENT_LAST = "key_shici_content_last"; //上次更新诗词的内容
    public final static String KEY_SHICI_SUMMARY_LAST = "key_shici_summary_last"; //上次更新诗词的主题

    public final static String KEY_APP_WIDGET_ISHICI_ID = "key_app_widget_ishici_id"; //缓存爱诗词widgetid

    public final static String KEY_APP_WIDGET_ISLEEP_ID = "key_app_widget_isleep_id"; //缓存爱睡眠widgetid
}
