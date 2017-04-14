package com.devdroid.sleepassistant.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.view.chart.SplineChartView;

/**
 * 图表界面
 */
public class ChartActivity extends BaseActivity {

    private SplineChartView mChartView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initView();
    }

    private void initView() {
        mChartView = (SplineChartView)findViewById(R.id.chart_view_activity_chart);
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
        switch (item.getItemId()){
            case 0:

                break;
            case 1:

                break;
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
            case 5:

                break;
            case 6:
                finish();
                break;
        }
        return true;
    }
}
