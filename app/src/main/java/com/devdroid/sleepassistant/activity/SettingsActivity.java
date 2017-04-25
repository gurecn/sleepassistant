package com.devdroid.sleepassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.BaseActivity;

public class SettingsActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.ll_setting_data_export:
                break;
            case R.id.ll_setting_data_import:
                break;
            case R.id.ll_setting_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.ll_setting_feedback:
                startActivity(new Intent(this, FeedbackActivity.class));
                break;
            case R.id.ll_setting_logout:
                finish();
                break;
        }
    }
}
