package com.devdroid.sleepassistant.activity;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;
public class AppLockTimeActivity extends AppCompatActivity{

    private TimePicker timePickerStart;
    private TimePicker timePickerEnd;
    private TextView mTvTimeLable;
    private String startTime = "0:0";
    private String endTime = "7:0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock_time);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initData();
    }

    private void initData() {
        timePickerStart = (TimePicker)super.findViewById(R.id.timepicker_start);
        mTvTimeLable = (TextView)findViewById(R.id.tv_applock_time_lable);
        timePickerStart.setIs24HourView(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePickerStart.setHour(23);
            timePickerStart.setMinute(0);
        }else {
            timePickerStart.setCurrentHour(23);
            timePickerStart.setCurrentMinute(0);
        }
        timePickerStart.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                startTime = i + ":" +i1;
                upDate();
            }
        });

        timePickerEnd = (TimePicker)super.findViewById(R.id.timepicker_end);
        timePickerEnd.setIs24HourView(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePickerEnd.setHour(7);
            timePickerEnd.setMinute(0);
        }else {
            timePickerEnd.setCurrentHour(7);
            timePickerEnd.setCurrentMinute(0);
        }
        timePickerEnd.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                endTime = i + ":" +i1;
                upDate();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_applock, menu);
        return true;
    }

    private void upDate() {
        mTvTimeLable.setText("应用将在" + startTime + " - " + endTime + "不可用");
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_applock_quit:
            case android.R.id.home:
                finish();
                break;
            case R.id.item_applock_submit:
                int startMinue = timePickerStart.getCurrentMinute();
                int startTime = Integer.parseInt(timePickerStart.getCurrentHour( )+ "" + (startMinue > 9 ? startMinue:("0" + startMinue)));
                int endMinue = timePickerEnd.getCurrentMinute();
                int endTime = Integer.parseInt(timePickerEnd.getCurrentHour() + "" + (endMinue > 9 ? endMinue:("0" + endMinue)));
                if (startTime == endTime){
                    Toast.makeText(this,"开始时间不能与结束时间相同",Toast.LENGTH_SHORT).show();
                    return true;
                }
                LauncherModel.getInstance().getSharedPreferencesManager().commitInt(IPreferencesIds.APP_LOCK_RESTRICTION_SRART_TIME, startTime);
                LauncherModel.getInstance().getSharedPreferencesManager().commitInt(IPreferencesIds.APP_LOCK_RESTRICTION_END_TIME, endTime);
                this.finish();
                break;
        }
        return true;
    }
}
