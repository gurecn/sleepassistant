package com.devdroid.sleepassistant.mode;

import android.graphics.drawable.Drawable;
/**
 * Created by miwo on 2016/8/10.
 */
public class AppLockBean {
    private boolean isLock = false;
    private String packageName = "";

    public Drawable getDrawable() {
        return mDrawable;
    }
    private Drawable mDrawable;

    public AppLockBean(boolean isLock, String packageName) {
        this.isLock = isLock;
        this.packageName = packageName;
    }

    public AppLockBean(boolean isLock, String packageName, Drawable drawable) {
        this.isLock = isLock;
        this.packageName = packageName;
        this.mDrawable = drawable;
    }

    public boolean isLock() {
        return isLock;
    }

    public void setLock(boolean lock) {
        isLock = lock;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
