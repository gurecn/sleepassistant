package com.devdroid.sleepassistant.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * User:Gaolei  gurecn@gmail.com
 * Date:2016/9/20
 * I'm glad to share my knowledge with you all.
 * 全局异常处理.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static CrashHandler instance;
    // String content;
    private Context ctx;
    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context ctx) {
        this.ctx = ctx;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread arg0, Throwable arg1) {
//        String logdir;
//        if (Environment.getExternalStorageDirectory() != null) {
//            logdir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "sleepassistant";
//            File file = new File(logdir);
//            boolean mkSuccess;
//            if (!file.isDirectory()) {
//                mkSuccess = file.mkdirs();
//                if (!mkSuccess) {
//                    mkSuccess = file.mkdirs();
//                }
//            }
//            try {
//                FileWriter fw = new FileWriter(logdir + File.separator + "log.txt", true);
//                if (fw != null) {
//                    fw.write(new Date() + "\n");
//                    StackTraceElement[] stackTrace = arg1.getStackTrace();
//                    fw.write(arg1.getMessage() + "\n");
//                    for (int i = 0; i < stackTrace.length; i++) {
//                        fw.write("file:" + stackTrace[i].getFileName() + " class:" + stackTrace[i].getClassName()
//                                + " method:" + stackTrace[i].getMethodName() + " line:" + stackTrace[i].getLineNumber()
//                                + "\n");
//                    }
//                    fw.write("\n");
//                    fw.close();
//                }
//            } catch (IOException e) {
//                Logger.e("crash handler", "load file failed...", e.getCause());
//            }
//        }
//        arg1.printStackTrace();
//        System.exit(0);
    }
}