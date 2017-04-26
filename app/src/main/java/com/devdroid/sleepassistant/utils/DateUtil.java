package com.devdroid.sleepassistant.utils;

import android.annotation.SuppressLint;

import com.devdroid.sleepassistant.SleepState;
import com.devdroid.sleepassistant.mode.SleepDataMode;
import com.devdroid.sleepassistant.view.CalendarCard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
/**
 * Created with IntelliJ IDEA.
 * User: Gaolei
 * Date: 2015/12/17
 * Email: pdsfgl@live.com
 */
public class DateUtil {

    public static int getMonthDays(int year, int month) {
        if (month > 12) {
            month = 1;
            year += 1;
        } else if (month < 1) {
            month = 12;
            year -= 1;
        }
        int[] arr = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        int days = 0;

        if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
            arr[1] = 29; // 闰年2月29天
        }
        try {
            days = arr[month - 1];
        } catch (Exception e) {
            e.getStackTrace();
        }

        return days;
    }

    public static int getYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getMonth() {
        return Calendar.getInstance().get(Calendar.MONTH) + 1;
    }

    public static int getCurrentMonthDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static int getWeekDay() {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0)
            dayOfWeek = 7;
        return dayOfWeek;
    }

    public static int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }
    public static int getMinute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }
    public static SleepDataMode getNextSunday() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7 - getWeekDay()+1);
        SleepDataMode date = new SleepDataMode(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR), c.get(Calendar.MINUTE));
        return date;
    }

    public static int getWeekDayFromDate(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getDateFromString(year, month));
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return week_index;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date getDateFromString(int year, int month) {
        String dateString = year + "-" + (month > 9 ? month : ("0" + month)) + "-01";
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return date;
    }
    public static boolean isToday(SleepDataMode date){
        return(date.getYear() == DateUtil.getYear() && date.getMonth() == DateUtil.getMonth() && date.getDay() == DateUtil.getCurrentMonthDay());
    }

    public static boolean isCurrentMonth(SleepDataMode date){
        return(date.getYear() == DateUtil.getYear() && date.getMonth() == DateUtil.getMonth());
    }

    /**
     * 获取前一日的时间
     */
    public static SleepDataMode getPreviousDate(SleepDataMode date){
        SleepDataMode tempDate = SleepDataMode.modifiDayForObject(date, 1);
        if(date.getDay() == 1){
            if(date.getMonth() == 1){
                tempDate.setYear(date.getYear() - 1);
                tempDate.setMonth(12);
                tempDate.setDay(31);
            } else {
                tempDate.setMonth(date.getMonth() - 1);
                tempDate.setDay(getMonthDays(tempDate.getYear(), tempDate.getMonth()));
            }
        } else {
            tempDate.setDay(date.getDay() - 1);
        }
        if(date.getWeek() == 1){
            tempDate.setWeek(7);
        } else {
            tempDate.setWeek(date.getWeek() - 1);
        }
        return tempDate;
    }

    /**
     * 时间转化成状态
     */
    public static SleepState transformState(SleepDataMode sleepDataMode) {
        int minutes = sleepDataMode.getHour() * 60 + sleepDataMode.getMinute();
        if(minutes >= 1170 && minutes <= 1350){//19:30--22:30
            return SleepState.GREAT;
        }else if(minutes > 1350 && minutes <= 1420){//22:30--23:40
            return SleepState.WARN;
        } else {
            return SleepState.BAD;//其他时间
        }
    }
}
