package com.devdroid.sleepassistant.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.SleepState;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.mode.SleepDataMode;
import com.devdroid.sleepassistant.utils.DateUtil;
import com.devdroid.sleepassistant.view.chart.GeneralSplineChartView;
import com.devdroid.sleepassistant.view.chart.PieChartView;
import com.devdroid.sleepassistant.view.chart.SplineChartView;

import java.util.HashMap;
import java.util.List;

/**
 * 图表界面
 */
public class ChartActivity extends BaseActivity {

    private RelativeLayout mRelativeLayoutChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        mRelativeLayoutChart = (RelativeLayout)findViewById(R.id.rl_content_chart_layout);
        initGengralSplineChart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,"折线图");
        menu.add(0,1,0,"曲线图");
//        menu.add(0,2,0,"柱状图");
        menu.add(0,3,0,"饼形图");
//        menu.add(0,4,0,"区域图");
//        menu.add(0,5,0,"堆积效果图");
        menu.add(0,6,0,"退出");
        return true;
    }
    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case 0:
                initGengralSplineChart();
                break;
            case 1:
                initSplineChartView();
                break;
            case 2:

                break;
            case 3:
                initPieChartView();
                break;
            case 4:

                break;
            case 5:

                break;
            case 6:
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    /**
     * 曲线图
     */
    private void initSplineChartView(){
        mRelativeLayoutChart.removeAllViews();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        SplineChartView splineChartView = new SplineChartView(this);
        mRelativeLayoutChart.addView(splineChartView, layoutParams);
        SleepDataMode currentData = new SleepDataMode();
        List<SleepDataMode> sleepDataModes = LauncherModel.getInstance().getSnssdkTextDao().querySleepDataInfo(currentData.getYear(), currentData.getMonth());
        splineChartView.chartDataSet(sleepDataModes);
    }

    /**
     * 折线图
     */
    private void initGengralSplineChart(){
        mRelativeLayoutChart.removeAllViews();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        GeneralSplineChartView generalSplineChartView = new GeneralSplineChartView(this);
        mRelativeLayoutChart.addView(generalSplineChartView, layoutParams);
        SleepDataMode currentData = new SleepDataMode();
        List<SleepDataMode> sleepDataModes = LauncherModel.getInstance().getSnssdkTextDao().querySleepDataInfo(currentData.getYear(), currentData.getMonth());
        generalSplineChartView.chartDataSet(sleepDataModes, false);
    }
    /**
     * 饼型图
     */
    private void initPieChartView(){
        mRelativeLayoutChart.removeAllViews();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        PieChartView pieChartView = new PieChartView(this);
        mRelativeLayoutChart.addView(pieChartView, layoutParams);
        SleepDataMode currentData = new SleepDataMode();
        List<SleepDataMode> sleepDataModes = LauncherModel.getInstance().getSnssdkTextDao().querySleepDataInfo(currentData.getYear(), currentData.getMonth());
        HashMap<String, Integer> sleepMap = new HashMap<>();
        for(SleepDataMode sleepData:sleepDataModes){
            if(sleepData != null && sleepData.getHour() != -1){
                SleepState sleepState = DateUtil.transformState(sleepData);
                sleepMap.put(sleepState.name(), sleepMap.get(sleepState.name()) + 1);
            }
        }
        int total =  sleepMap.get(SleepState.GREAT.name()) + sleepMap.get(SleepState.WARN.name()) + sleepMap.get(SleepState.BAD.name());
        sleepMap.put(SleepState.GREAT.name(), sleepMap.get(SleepState.GREAT.name()) * 100 / total);
        sleepMap.put(SleepState.WARN.name(), sleepMap.get(SleepState.WARN.name())* 100 / total);
        sleepMap.put(SleepState.BAD.name(), sleepMap.get(SleepState.BAD.name())* 100 / total);
        pieChartView.chartDataSet(sleepMap);
    }
}
