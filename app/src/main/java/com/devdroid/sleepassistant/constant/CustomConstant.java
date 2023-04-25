package com.devdroid.sleepassistant.constant;

import com.devdroid.sleepassistant.BuildConfig;

/**
 * Created by Gaolei on 2017/4/12.
 */

public class CustomConstant {
    private CustomConstant() {}
    public final static String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    public final static String PROCESS_NAME_MAIN = PACKAGE_NAME;
    public final static String IFLYTEK_SPEECH_APPID = "appid=5dd162a1";



    //数据备份相关变量
    public static final String COMMAND_BACKUP_INTERNAL_STORAGE = "backup_database_internal_storage";//备份到内存
    public static final String COMMAND_RESTORE_INTERNAL_STORAGE = "restroe_database_internal_storage";//读取内存数据
}
