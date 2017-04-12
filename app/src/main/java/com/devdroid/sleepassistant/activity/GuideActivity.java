package com.devdroid.sleepassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.BaseActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 应用引导页
 */
public class GuideActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
    }
}
