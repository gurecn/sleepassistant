package com.devdroid.sleepassistant.mode;

/**
 * Created by Gaolei on 2017/3/15.
 */

public class SleepDataMode {
    private int year;
    private int month;
    private int day;
    private long sleepTime;
    private int sleepType;

    public SleepDataMode(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public SleepDataMode(int year, int month, int day, long sleepTime, int sleepType) {
        this(year, month, day);
        this.sleepTime = sleepTime;
        this.sleepType = sleepType;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public int getSleepType() {
        return sleepType;
    }
}
