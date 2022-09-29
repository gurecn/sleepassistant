package com.devdroid.sleepassistant.activity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.devdroid.hanlp.HanLP;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.adapter.CalendarViewAdapter;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.listener.NavigationItemSelectedListener;
import com.devdroid.sleepassistant.mode.SleepDataMode;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;
import com.devdroid.sleepassistant.utils.DateUtil;
import com.devdroid.sleepassistant.utils.DevicesUtils;
import com.devdroid.sleepassistant.utils.Logger;
import com.devdroid.sleepassistant.view.CalendarCard;
import com.devdroid.sleepassistant.view.chart.GeneralSplineChartView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.jinrishici.sdk.android.JinrishiciClient;
import com.jinrishici.sdk.android.listener.JinrishiciCallback;
import com.jinrishici.sdk.android.model.DataBean;
import com.jinrishici.sdk.android.model.JinrishiciRuntimeException;
import com.jinrishici.sdk.android.model.OriginBean;
import com.jinrishici.sdk.android.model.PoetySentence;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity implements CalendarCard.OnCellClickListener, View.OnClickListener {
    private ViewPager mViewPager;
    private CalendarViewAdapter<CalendarCard> adapter;
    private int mCurrentIndex = 1000;
    private SildeDirection mDirection = SildeDirection.NO_SILDE;
    private TextView mTvDateLable;
    private GeneralSplineChartView mGSCWeek;
    private DrawerLayout mDrawerLayout;
    private TextView mTvJinRiShiCi;
//    private OriginBean mBeanOrigin;

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
                mViewPager.postDelayed(this::finish, 500);
            }
        }
        JinrishiciClient client = JinrishiciClient.getInstance();
        int lastUpdateTime = LauncherModel.getInstance().getSharedPreferencesManager().getInt(IPreferencesIds.KEY_SHICI_TIME_LAST, 0);
        int currTime = DateUtil.getFormatDate();
        if(lastUpdateTime == currTime){
            String shiciSummary = LauncherModel.getInstance().getSharedPreferencesManager().getString(IPreferencesIds.KEY_SHICI_SUMMARY_LAST, "");
            mTvJinRiShiCi.setText(shiciSummary);
        } else {
            client.getOneSentenceBackground(new JinrishiciCallback() {
                @Override
                public void done(PoetySentence poetySentence) {
                    DataBean dataBean = poetySentence.getData();
                    OriginBean beanOrigin = dataBean.getOrigin();
                    if (dataBean != null) {
                        String shici = poetySentence.getData().getContent();
                        if (!TextUtils.isEmpty(shici)) {
                            shici = shici.replaceAll("(:|：|，|,|\\.|。|;|；|\\?|？)", "$1\n");
                            mTvJinRiShiCi.setText(shici);
                            String dataString = new Gson().toJson(beanOrigin);
                            Log.d("11111111", "beanOrigin:" + dataString);
                            LauncherModel.getInstance().getSharedPreferencesManager().commitString(IPreferencesIds.KEY_SHICI_CONTENT_LAST,dataString);
                            LauncherModel.getInstance().getSharedPreferencesManager().commitString(IPreferencesIds.KEY_SHICI_SUMMARY_LAST,shici);
                            LauncherModel.getInstance().getSharedPreferencesManager().commitInt(IPreferencesIds.KEY_SHICI_TIME_LAST, DateUtil.getFormatDate());
                        }
                    }
                }
                @Override
                public void error(JinrishiciRuntimeException e) {
                }
            });
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
        View headerView = navigationView.getHeaderView(0);
        mTvJinRiShiCi = (TextView)headerView.findViewById(R.id.tv_nav_header_jinrishici);
        //从asset 读取字体
        AssetManager mgr = getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/STKAITI.TTF");//仿宋
        mTvJinRiShiCi.setTypeface(tf);
        CheckBox cbNightMode = (CheckBox)headerView.findViewById(R.id.cb_night_mode);
        boolean isChecked = LauncherModel.getInstance().getSharedPreferencesManager().getBoolean(IPreferencesIds.KEY_THEME_NIGHT_MODE, false);
        cbNightMode.setChecked(isChecked);
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
        new TimePickerDialog(MainActivity.this, (view, hourOfDay, minute) -> {
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
            mViewPager.postDelayed(this::initGengralSplineChart, 500);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cb_night_mode:
                boolean isCheck = ((CheckBox) v).isChecked();
                LauncherModel.getInstance().getSharedPreferencesManager().commitBoolean(IPreferencesIds.KEY_THEME_NIGHT_MODE, isCheck);
                restartApplication(isCheck);
                break;
            case R.id.tv_nav_header_main_devdroid:
            case R.id.tv_nav_header_main_email:
                startActivity(new Intent(this, WebActivity.class));
                break;
            case R.id.tv_nav_header_jinrishici:
                Intent intent = new Intent(this, ShiciActivity.class);
//                intent.putExtra("BeanOrigin", mBeanOrigin);
                startActivity(intent);
                break;
        }
    }

    // 重启应用
    private void restartApplication(boolean isNightMode) {
        AppCompatDelegate.setDefaultNightMode(isNightMode?AppCompatDelegate.MODE_NIGHT_YES:AppCompatDelegate.MODE_NIGHT_NO);
        Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}