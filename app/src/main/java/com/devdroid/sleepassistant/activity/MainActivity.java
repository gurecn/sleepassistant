package com.devdroid.sleepassistant.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.adapter.CalendarViewAdapter;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.listener.NavigationItemSelectedListener;
import com.devdroid.sleepassistant.mode.SleepDataMode;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;
import com.devdroid.sleepassistant.utils.DateUtil;
import com.devdroid.sleepassistant.view.CalendarCard;
import com.devdroid.sleepassistant.view.chart.GeneralSplineChartView;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements CalendarCard.OnCellClickListener{
    private ViewPager mViewPager;
    private CalendarViewAdapter<CalendarCard> adapter;
    private int mCurrentIndex = 1000;
    private SildeDirection mDirection = SildeDirection.NO_SILDE;
    private TextView mTvDateLable;
    private GeneralSplineChartView mGSCWeek;
    private DrawerLayout mDrawerLayout;

    enum SildeDirection {
        RIGHT, LEFT, NO_SILDE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        if(intent != null){
            String action = intent.getStringExtra("action");
            if("create_sleep_time_new".equals(action)){
                createSleepTimeNew();
                mViewPager.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 500);
            }
        }
    }

    private void createSleepTimeNew() {
        SleepDataMode date = new SleepDataMode();
        Calendar calendar = Calendar.getInstance();
        date.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        date.setMinute(calendar.get(Calendar.MINUTE));
        List<SleepDataMode> insertList = new LinkedList<>();
        if(date.getHour() < 6){
            date.setHour(24 + date.getHour());
        }
        insertList.add(date);
        LauncherModel.getInstance().getSleepDataDao().insertSleepDataItem(insertList);
        LauncherModel.getInstance().getSharedPreferencesManager().commitBoolean(IPreferencesIds.KEY_SLEEP_TIME_HAS_SET, true);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.getItem(mCurrentIndex % adapter.getAllItems().length).update(false);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.main_navigation_view);
        NavigationItemSelectedListener navigationItemSelectedListener = new NavigationItemSelectedListener(this, mDrawerLayout);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
        mViewPager = (ViewPager) this.findViewById(R.id.vp_calendar);
        mTvDateLable = (TextView) this.findViewById(R.id.tv_content_main_date_lable);
        mGSCWeek = (GeneralSplineChartView) this.findViewById(R.id.gsc_activity_main_week);
        CalendarCard[] views = new CalendarCard[3];
        for (int i = 0; i < 3; i++) {
            views[i] = new CalendarCard(this, this);
        }
        adapter = new CalendarViewAdapter<>(views);
        setViewPager();
        initGengralSplineChart();
        boolean isClick = LauncherModel.getInstance().getSharedPreferencesManager().getBoolean(IPreferencesIds.KEY_SLEEP_TIME_HAS_SET, false);
        if(!isClick){
            addGuideImage(R.id.drawer_layout, R.mipmap.main_guide);
        }
    }

    private void setViewPager() {
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mCurrentIndex);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                measureDirection(position);
                updateCalendarView(position);
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout != null && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 计算方向
     */
    private void measureDirection(int arg0) {
        if (arg0 > mCurrentIndex) {
            mDirection = SildeDirection.RIGHT;
        } else if (arg0 < mCurrentIndex) {
            mDirection = SildeDirection.LEFT;
        }
        mCurrentIndex = arg0;
    }

    // 更新日历视图
    private void updateCalendarView(int arg0) {
        CalendarCard[] mShowViews = adapter.getAllItems();
        if (mDirection == SildeDirection.RIGHT) {
            mShowViews[arg0 % mShowViews.length].rightSlide();
        } else if (mDirection == SildeDirection.LEFT) {
            mShowViews[arg0 % mShowViews.length].leftSlide();
        }
        mDirection = SildeDirection.NO_SILDE;
    }

    // 日历点击
    @Override
    public void clickDate(SleepDataMode date) {
        if(date.getHour() > 24) {
            Toast.makeText(this, "睡眠时间为：第二天凌晨" + (date.getHour() - 24) + ":" + date.getMinute(), Toast.LENGTH_SHORT).show();
        } else if(date.getHour() > 0){
            Toast.makeText(this, "睡眠时间为：" + date.getHour() + ":" + date.getMinute(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "点击日期：" + date.getYear() + "-" + date.getMonth() + "-" + date.getDay(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 长按日期
     */
    @Override
    public void longClickDate(final SleepDataMode date) {
        Calendar calendar = Calendar.getInstance();
        new TimePickerDialog(MainActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        List<SleepDataMode> insertList = new LinkedList<>();
                        SleepDataMode sleepDataMode = new SleepDataMode(date.getYear(), date.getMonth(), date.getDay(), hourOfDay, minute);
                        if(hourOfDay < 6){  //认为,时间设置为 24 + 实际时间。如：凌晨1点，保存为：25
//                            sleepDataMode = DateUtil.getPreviousDate(sleepDataMode);
                            sleepDataMode.setHour(24 + sleepDataMode.getHour());
                        }
                        insertList.add(sleepDataMode);
                        LauncherModel.getInstance().getSleepDataDao().insertSleepDataItem(insertList);
                        adapter.getItem(mCurrentIndex % adapter.getAllItems().length).update(false);
                        LauncherModel.getInstance().getSharedPreferencesManager().commitBoolean(IPreferencesIds.KEY_SLEEP_TIME_HAS_SET, true);
                        mViewPager.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                initGengralSplineChart();
                            }
                        }, 500);
                    }
                },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show();
    }

    @Override
    public void changeDate(SleepDataMode date) {
        mTvDateLable.setText(String.format("%s年%s月",date.getYear(),date.getMonth()));
    }


    private void initGengralSplineChart(){
        SleepDataMode currentData = new SleepDataMode();
        LinkedList<SleepDataMode> sleepDataModes = new LinkedList<>();
        for(int i = 0; i < 7; i ++ ) {
            currentData = DateUtil.getPreviousDate(currentData);
            currentData.setHour(-1);
            currentData.setMinute(-1);
            SleepDataMode sleepDataMode = LauncherModel.getInstance().getSleepDataDao().querySleepDataInfo(currentData.getYear(), currentData.getMonth(), currentData.getDay());
            if(sleepDataMode != null) {
                sleepDataMode.setWeek(currentData.getWeek());
                currentData = sleepDataMode;
            }
            sleepDataModes.add(0, currentData);
        }
        mGSCWeek.chartDataSet(sleepDataModes, true);
    }
}