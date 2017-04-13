package com.devdroid.sleepassistant.activity;

import android.app.TimePickerDialog;
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
import com.devdroid.sleepassistant.utils.DateUtil;
import com.devdroid.sleepassistant.view.CalendarCard;
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

    enum SildeDirection {
        RIGHT, LEFT, NO_SILDE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.main_navigation_view);
        NavigationItemSelectedListener navigationItemSelectedListener = new NavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
        mViewPager = (ViewPager) this.findViewById(R.id.vp_calendar);
        mTvDateLable = (TextView) this.findViewById(R.id.tv_content_main_date_lable);
        CalendarCard[] views = new CalendarCard[3];
        for (int i = 0; i < 3; i++) {
            views[i] = new CalendarCard(this, this);
        }
        adapter = new CalendarViewAdapter<>(views);
        setViewPager();
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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
                        if(hourOfDay < 6){  //认为是上一天的入睡时间,时间设置为 24 + 实际时间。如：凌晨1点，保存为：25
                            sleepDataMode = DateUtil.getPreviousDate(sleepDataMode);
                            sleepDataMode.setHour(24 + sleepDataMode.getHour());
                        }
                        insertList.add(sleepDataMode);
                        LauncherModel.getInstance().getSnssdkTextDao().insertSleepDataItem(insertList);
                        adapter.getItem(mCurrentIndex % adapter.getAllItems().length).update(false);
                    }
                },calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),true).show();
    }

    @Override
    public void changeDate(SleepDataMode date) {
        mTvDateLable.setText(date.getYear() + "年" + date.getMonth() + "月");
    }
}