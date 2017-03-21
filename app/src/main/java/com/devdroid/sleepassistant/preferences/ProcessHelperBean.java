package com.devdroid.sleepassistant.preferences;

/**
 * Created by miwo on 2016/8/9.
 */
public class ProcessHelperBean {
    /**
     * 进程id
     */
    private int mPid = -1;
    /**
     * 进程名
     */
    private String mProcessName = "";
    /**
     * PPid
     */
    private int mPPid = -1;
    /**
     * Uid
     */
    private int mUid = -1;
    /**
     * 包名数组
     */
    private String[] mPkgLists = null;
    /**
     * OOM_ADJ
     */
    private int mOomAdj = -99;
    /**
     * Oom_Score
     */
    private int mOomScore = -99;
    /**
     * Oom_Score_Adj
     */
    private int mOomScoreAdj = -99;
    /**
     * 文件名
     */
    private String mProcFilename = "";

    public int getPid() {
        return mPid;
    }
    public void setPid(int mPid) {
        this.mPid = mPid;
    }
    public String getProcessName() {
        return mProcessName;
    }
    public void setProcessName(String mProcessName) {
        this.mProcessName = mProcessName;
    }
    public int getPPid() {
        return mPPid;
    }
    public void setPPid(int mPPid) {
        this.mPPid = mPPid;
    }
    public int getUid() {
        return mUid;
    }
    public void setUid(int mUid) {
        this.mUid = mUid;
    }
    public String[] getPkgLists() {
        return mPkgLists;
    }
    public void setPkgLists(String[] mPkgLists) {
        this.mPkgLists = mPkgLists;
    }
    public int getOomAdj() {
        return mOomAdj;
    }
    public void setOomAdj(int mOomAdj) {
        this.mOomAdj = mOomAdj;
    }
    public int getOomScore() {
        return mOomScore;
    }
    public void setOomScore(int mOomScore) {
        this.mOomScore = mOomScore;
    }
    public int getOomScoreAdj() {
        return mOomScoreAdj;
    }
    public void setOomScoreAdj(int mOomScoreAdj) {
        this.mOomScoreAdj = mOomScoreAdj;
    }
    public String getProcFilename() {
        return mProcFilename;
    }
    public void setProcFilename(String mProcFilename) {
        this.mProcFilename = mProcFilename;
    }
}
