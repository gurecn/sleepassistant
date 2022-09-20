package com.devdroid.sleepassistant.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import com.devdroid.sleepassistant.R;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;

public class DatePickerDialog extends AlertDialog implements OnClickListener, OnDateChangedListener {

    private static final String START_YEAR = "start_year";
    private static final String START_MONTH = "start_month";
    private static final String START_DAY = "start_day";

    private final DatePicker mDatePickerStart;
    private final OnDateSetListener mCallBack;

    public interface OnDateSetListener {
        void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear, int startDayOfMonth);
    }

    public DatePickerDialog(Context context, int theme, OnDateSetListener callBack, int year, int monthOfYear,
                            int dayOfMonth) {
        super(context, theme);

        mCallBack = callBack;

        Context themeContext = getContext();
        setButton(BUTTON_NEUTRAL, context.getString(R.string.date_picker_all_data), this);
        setButton(BUTTON_POSITIVE, context.getString(R.string.date_picker_submit), this);
        setButton(BUTTON_NEGATIVE, context.getString(R.string.date_picker_cancel), this);
        setIcon(0);

        LayoutInflater inflater = (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_date_picker, null);
        setView(view);
        mDatePickerStart = (DatePicker) view.findViewById(R.id.datePickerStart);
        mDatePickerStart.init(year, monthOfYear, dayOfMonth, this);

        hideDay(mDatePickerStart);
    }

    private void hideDay(DatePicker mDatePicker) {
        try {
            /* 处理android5.0以上的特殊情况 */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                if (daySpinnerId != 0) {
                    View daySpinner = mDatePicker.findViewById(daySpinnerId);
                    if (daySpinner != null) {
                        daySpinner.setVisibility(View.GONE);
                    }
                }
            } else {
                Field[] datePickerfFields = mDatePicker.getClass().getDeclaredFields();
                for (Field datePickerField : datePickerfFields) {
                    if ("mDaySpinner".equals(datePickerField.getName()) || ("mDayPicker").equals(datePickerField.getName())) {
                        datePickerField.setAccessible(true);
                        Object dayPicker = new Object();
                        try {
                            dayPicker = datePickerField.get(mDatePicker);
                        } catch (IllegalAccessException | IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                        ((View) dayPicker).setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case BUTTON_POSITIVE:
                tryNotifyDateSet(false);
                break;
            case BUTTON_NEUTRAL:
                tryNotifyDateSet(true);
                break;

        }
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        if (view.getId() == R.id.datePickerStart)
            mDatePickerStart.init(year, month, day, this);
    }

    private void tryNotifyDateSet(boolean isAllData) {
        if (mCallBack != null) {
            mDatePickerStart.clearFocus();
            if(!isAllData) {
                mCallBack.onDateSet(mDatePickerStart, mDatePickerStart.getYear(), mDatePickerStart.getMonth(), mDatePickerStart.getDayOfMonth());
            } else {
                mCallBack.onDateSet(null, -1, -1, -1);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(START_YEAR, mDatePickerStart.getYear());
        state.putInt(START_MONTH, mDatePickerStart.getMonth());
        state.putInt(START_DAY, mDatePickerStart.getDayOfMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int start_year = savedInstanceState.getInt(START_YEAR);
        int start_month = savedInstanceState.getInt(START_MONTH);
        int start_day = savedInstanceState.getInt(START_DAY);
        mDatePickerStart.init(start_year, start_month, start_day, this);
    }
}