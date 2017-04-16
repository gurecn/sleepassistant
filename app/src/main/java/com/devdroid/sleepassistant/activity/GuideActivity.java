package com.devdroid.sleepassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;
import android.widget.TextView;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 应用引导页
 */
public class GuideActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setContentView(R.layout.activity_guide);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                GuideActivity.this.startActivity(intent);
                GuideActivity.this.finish();
            }
        }, 1000);
        initData();
    }

    private void initData() {
        TextView tvDayLable = (TextView)this.findViewById(R.id.tv_activity_guide_time_lable);
        long appInstallTime = LauncherModel.getInstance().getSharedPreferencesManager().getLong(IPreferencesIds.KEY_FIRST_START_APP_TIME, (long)0);
        if (appInstallTime != 0) {
            int datepoor = (int)((System.currentTimeMillis() - appInstallTime)/1000/60/60/24 + 1);
            String datePoorString = "遇见 " + datepoor + " 天";
            tvDayLable.setText(datePoorString);
        } else {
            String datePoorString = "遇见 1 天";
            tvDayLable.setText(datePoorString);
        }
    }
}
