package com.devdroid.sleepassistant.eventbus;

/**
 * Created by Gaolei on 2017/4/25.
 */

public class OnUpdateProgressBackup {
    private int progressNum;
    private int typeProgress;
    public OnUpdateProgressBackup(int progressNum, int typeProgress) {
        this.progressNum = progressNum;
        this.typeProgress = typeProgress;
    }

    public int getTypeProgress() {
        return typeProgress;
    }

    public int getProgressNum() {
        return progressNum;
    }
}
