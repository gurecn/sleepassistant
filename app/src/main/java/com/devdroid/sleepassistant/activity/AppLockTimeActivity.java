package com.devdroid.sleepassistant.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.application.TheApplication;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;
import com.devdroid.sleepassistant.utils.AlarmManagerUtils;
import com.devdroid.sleepassistant.utils.AppUtils;

import androidx.appcompat.widget.Toolbar;

public class AppLockTimeActivity extends BaseActivity {

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
                if(!AppUtils.isPermissionPackageUsageStatsGrandedLollipopMr1(this)){
                    applyPermissionDialog(this);
                } else {
                    submiTime();
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 记录时间
     */
    private void submiTime() {

        int startMinue = timePickerStart.getCurrentMinute();
        int startTime = Integer.parseInt(timePickerStart.getCurrentHour( )+ "" + (startMinue > 9 ? startMinue:("0" + startMinue)));
        int endMinue = timePickerEnd.getCurrentMinute();
        int endTime = Integer.parseInt(timePickerEnd.getCurrentHour() + "" + (endMinue > 9 ? endMinue:("0" + endMinue)));
        if (startTime == endTime){
            Toast.makeText(this,"开始时间不能与结束时间相同",Toast.LENGTH_SHORT).show();
            return;
        }
        LauncherModel.getInstance().getSharedPreferencesManager().commitInt(IPreferencesIds.APP_LOCK_RESTRICTION_SRART_TIME, startTime);
        LauncherModel.getInstance().getSharedPreferencesManager().commitInt(IPreferencesIds.APP_LOCK_RESTRICTION_END_TIME, endTime);
        AlarmManagerUtils.startAlarm(TheApplication.getAppContext());
        Log.d("111111111111", "startAlarm");
        this.finish();
    }


    /**
     * 开启PACKAGE_USAGE_STAT权限对话框
     */
    private void applyPermissionDialog(Context context) {

        AlertDialog.Builder normalDialog = new AlertDialog.Builder(context);
        normalDialog.setMessage(context.getString(R.string.dialog_lock_time_content));
        normalDialog.setNeutralButton(context.getString(R.string.dialog_lock_time_open_method),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Context context = ((AlertDialog)dialog).getContext();
                    Intent intent = new Intent(AppLockTimeActivity.this, WebActivity.class);
                    //TODO 暂时使用钉钉通知权限指导替代
                    intent.putExtra("url", "https://csmobile.alipay.com/detailSolution.htm?knowledgeType=1&scene=dd_gdwt&questionId=201602241513");
                    context.startActivity(intent);
                }
            });
        normalDialog.setNegativeButton(context.getString(R.string.dialog_lock_time_ok),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Context context = ((AlertDialog)dialog).getContext();
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    context.startActivity(intent);
                }
            });
        normalDialog.setPositiveButton(context.getString(R.string.dialog_lock_time_cancel),
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        normalDialog.show();
    }
}
