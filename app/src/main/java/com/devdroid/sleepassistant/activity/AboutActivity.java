package com.devdroid.sleepassistant.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.devdroid.sleepassistant.R;


/**
 * 关于界面
 */
public class AboutActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        TextView tvVersion = (TextView) findViewById(R.id.tv_app_version);
        LinearLayout llAppRight = (LinearLayout)findViewById(R.id.ll_app_right);
        llAppRight.setOnClickListener(this);
        PackageManager pm=getPackageManager();
        PackageInfo info;
        try {
            info = pm.getPackageInfo(getPackageName(), 0);
            tvVersion.setText(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_app_right :
                Intent intent = new Intent(this, AgreementAvtivity.class);
                startActivity(intent);
                break;
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
