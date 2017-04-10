package com.devdroid.sleepassistant.mode;


import com.devdroid.sleepassistant.utils.DateUtil;

import java.io.Serializable;
/**
 * Created with IntelliJ IDEA.
 * User: Gaolei
 * Date: 2015/12/17
 * Email: pdsfgl@live.com
 */
public class CustomDate implements Serializable{
    private static final long serialVersionUID = 1L;
    public int year;
    public int month;
    public int day;
    public int week;
    private long sleepTime = 0;
    private int sleepType;

    public CustomDate(int year,int month,int day){
        if(month > 12){
            month = 1;
            year++;
        }else if(month <1){
            month = 12;
            year--;
        }
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public CustomDate(){
        this.year = DateUtil.getYear();
        this.month = DateUtil.getMonth();
        this.day = DateUtil.getCurrentMonthDay();
    }

    public static CustomDate modifiDayForObject(CustomDate date,int day){
        CustomDate modifiDate = new CustomDate(date.year,date.month,day);
        return modifiDate;
    }
    @Override
    public String toString() {
        return year+"-"+month+"-"+day;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getYearMonth() {
        return year+"-"+month;

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomDate that = (CustomDate) o;
        if (day != that.day) return false;
        if (month != that.month) return false;
        if (week != that.week) return false;
        if (year != that.year) return false;
        return true;
    }

}
