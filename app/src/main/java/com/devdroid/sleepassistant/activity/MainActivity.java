package com.devdroid.sleepassistant.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SwitchCompat;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.adapter.CalendarViewAdapter;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.listener.NavigationItemSelectedListener;
import com.devdroid.sleepassistant.mode.CustomDate;
import com.devdroid.sleepassistant.view.CalendarCard;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements CalendarCard.OnCellClickListener{
    private SwitchCompat mSwNetSetting;
    private ViewPager mViewPager;
    private CalendarViewAdapter<CalendarCard> adapter;
    private int mCurrentIndex = 1000;
    private CalendarCard[] mShowViews;
    private SildeDirection mDirection = SildeDirection.NO_SILDE;

    enum SildeDirection {
        RIGHT, LEFT, NO_SILDE;
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
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.main_navigation_view);
        mSwNetSetting = (SwitchCompat)navigationView.getHeaderView(0).findViewById(R.id.switch_nav_header_net);
        NavigationItemSelectedListener navigationItemSelectedListener = new NavigationItemSelectedListener(this);
        navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
        mViewPager = (ViewPager) this.findViewById(R.id.vp_calendar);
        CalendarCard[] views = new CalendarCard[3];
        for (int i = 0; i < 3; i++) {
            views[i] = new CalendarCard(this, this);
        }
        adapter = new CalendarViewAdapter<>(views);
        setViewPager();
    }

    private void setViewPager() {
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(1000);
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
     * @param arg0
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
        mShowViews = adapter.getAllItems();
        if (mDirection == SildeDirection.RIGHT) {
            mShowViews[arg0 % mShowViews.length].rightSlide();
        } else if (mDirection == SildeDirection.LEFT) {
            mShowViews[arg0 % mShowViews.length].leftSlide();
        }
        mDirection = SildeDirection.NO_SILDE;
    }

    // 日历点击
    @Override
    public void clickDate(CustomDate date) {

    }

    @Override
    public void changeDate(CustomDate date) {

    }
}