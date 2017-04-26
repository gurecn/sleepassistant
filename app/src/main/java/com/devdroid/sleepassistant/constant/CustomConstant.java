package com.devdroid.sleepassistant.constant;

/**
 * Created by Gaolei on 2017/4/12.
 */

public class CustomConstant {
    private CustomConstant() {}
    public final static String PACKAGE_NAME = "com.devdroid.sleepassistant";
    public final static String PROCESS_NAME_MAIN = PACKAGE_NAME;


    //数据备份相关变量
    public static final String COMMAND_BACKUP_INTERNAL_STORAGE = "backup_database_internal_storage";//备份到内存
    public static final String COMMAND_RESTORE_INTERNAL_STORAGE = "restroe_database_internal_storage";//读取内存数据
}
