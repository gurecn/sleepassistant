package com.devdroid.sleepassistant.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.BaseActivity;

/**
 * 图表界面
 */
public class ChartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,"折线图");
        menu.add(0,1,0,"曲线图");
        menu.add(0,2,0,"柱状图");
        menu.add(0,3,0,"饼形图");
        menu.add(0,4,0,"区域图");
        menu.add(0,5,0,"堆积效果图");
        menu.add(0,6,0,"退出");
        return true;
    }
    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().equals("退出")){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
