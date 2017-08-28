package com.devdroid.sleepassistant.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.RelativeLayout;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.mode.SleepState;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.mode.SleepDataMode;
import com.devdroid.sleepassistant.utils.DateUtil;
import com.devdroid.sleepassistant.view.DatePickerDialog;
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
    private List<SleepDataMode> sleepDataModes;
    private int mChartType = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initView();
        initData();
    }

    private void initView() {
        mRelativeLayoutChart = (RelativeLayout)findViewById(R.id.rl_content_chart_layout);
        sleepDataModes = LauncherModel.getInstance().getSnssdkTextDao().querySleepDataInfo();
        initGengralSplineChart();
    }

    private void initData() {
        switch (mChartType){
            case 0:
                initGengralSplineChart();
                break;
            case 1:
                initSplineChartView();
                break;
            case 2:
                initPieChartView();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chart, menu);
        String date = getString(R.string.date_picker_all_data);
        menu.getItem(0).setTitle(date);
        return true;
    }
    @Override

    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_chart_gengral_spline:
                mChartType = 0;
                initData();
                break;
            case R.id.item_chart_spline:
                mChartType = 1;
                initData();
                break;
            case R.id.item_chart_pie:
                mChartType = 2;
                initData();
                break;
            case R.id.item_chart_quit:
            case android.R.id.home:
                finish();
                break;
            case R.id.item_chart_gengral_date:
                SleepDataMode currentData = new SleepDataMode();
                new DatePickerDialog(this, 0, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear, int startDayOfMonth) {
                        if(startDatePicker == null){
                            item.setTitle(ChartActivity.this.getString(R.string.date_picker_all_data));
                            sleepDataModes = LauncherModel.getInstance().getSnssdkTextDao().querySleepDataInfo();
                        } else {
                            String date = String.format(getString(R.string.menu_chart_item_value_date), startYear, startMonthOfYear + 1);
                            item.setTitle(date);
                            sleepDataModes = LauncherModel.getInstance().getSnssdkTextDao().querySleepDataInfo(startYear, startMonthOfYear + 1);
                        }
                        initData();
                    }
                }, currentData.getYear(), currentData.getMonth() - 1, currentData.getDay()).show();
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
        HashMap<String, Integer> sleepMap = new HashMap<>();
        sleepMap.put(SleepState.GREAT.name(), 0);
        sleepMap.put(SleepState.WARN.name(), 0);
        sleepMap.put(SleepState.BAD.name(), 0);
        for(SleepDataMode sleepData:sleepDataModes){
            if(sleepData != null && sleepData.getHour() != -1){
                SleepState sleepState = DateUtil.transformState(sleepData);
                sleepMap.put(sleepState.name(), sleepMap.get(sleepState.name()) + 1);
            }
        }
        int total =  sleepMap.get(SleepState.GREAT.name()) + sleepMap.get(SleepState.WARN.name()) + sleepMap.get(SleepState.BAD.name());
        if(total == 0) return;
        sleepMap.put(SleepState.GREAT.name(), (int)(sleepMap.get(SleepState.GREAT.name()) * 100d / total + 0.5));
        sleepMap.put(SleepState.WARN.name(), (int)(sleepMap.get(SleepState.WARN.name()) * 100d / total + 0.5));
        pieChartView.chartDataSet(sleepMap);
        new Thread(pieChartView).start();
    }
}
