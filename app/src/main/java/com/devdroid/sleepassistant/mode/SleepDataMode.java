package com.devdroid.sleepassistant.mode;

import com.devdroid.sleepassistant.utils.DateUtil;

/**
 * Created by Gaolei on 2017/3/15.
 */

public class SleepDataMode {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int sleepType;

    private int week;  //图表显示判断周

    public SleepDataMode(){
        this.year = DateUtil.getYear();
        this.month = DateUtil.getMonth();
        this.day = DateUtil.getCurrentMonthDay();
        this.hour = -1;
        this.minute = -1;
        this.week = DateUtil.getWeekDay();
    }

    public SleepDataMode(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public SleepDataMode(int year, int month, int day, int hour, int minute, int sleepType) {
        this(year, month, day, hour, minute);
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

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSleepType() {
        return sleepType;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setSleepType(int sleepType) {
        this.sleepType = sleepType;
    }

    public static SleepDataMode modifiDayForObject(SleepDataMode date,int day){
        SleepDataMode modifiDate = new SleepDataMode(date.year, date.month, day, date.getHour(), date.getMinute());
        return modifiDate;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    @Override
    public String toString() {
        return  "" + this.getYear() + this.getMonth() + this.getDay();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SleepDataMode that = (SleepDataMode) o;
        if (day != that.day) return false;
        if (month != that.month) return false;
        if (year != that.year) return false;
        return true;
    }

}
