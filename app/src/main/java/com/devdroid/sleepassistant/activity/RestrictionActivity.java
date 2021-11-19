package com.devdroid.sleepassistant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.adapter.RestrictionAppsAdapter;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.mode.AppLockBean;
import com.devdroid.sleepassistant.utils.AppUtils;
import com.devdroid.sleepassistant.utils.thread.ThreadPoolUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 限制界面
 */
public class RestrictionActivity extends BaseActivity{
    private List<AppLockBean> mAppLockBeens;      //所有应用
    private List<Integer> mClickPosition;       //操作过的应用坐标
    private GridView mGvRestrictionApps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restriction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mGvRestrictionApps = (GridView)findViewById(R.id.gridView_activity_restriction_app);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        ThreadPoolUtils.executeSingleton(new Runnable() {
            @Override
            public void run() {
                mAppLockBeens = AppUtils.getAppPackages(RestrictionActivity.this);
                mGvRestrictionApps.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                    }
                }, 0);
            }
        });
    }

    private void initData(){
        mClickPosition = new LinkedList<>();
        if (mAppLockBeens != null) {
            RestrictionAppsAdapter adapter = new RestrictionAppsAdapter(this, mAppLockBeens);
            mGvRestrictionApps.setAdapter(adapter);
        }
        mGvRestrictionApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RestrictionAppsAdapter adapter = (RestrictionAppsAdapter)adapterView.getAdapter();
                AppLockBean appLockBean = (AppLockBean)adapter.getItem(i);
                if (appLockBean.isLock()) {
                    appLockBean.setLock(false);
                } else {
                    appLockBean.setLock(true);
                }
                if (!mClickPosition.contains(i)) {
                    mClickPosition.add(i);
                }
                adapter.notifyDataSetInvalidated();
            }
        });
        Toast.makeText(this, getString(R.string.wait_next_version), Toast.LENGTH_LONG).show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_restriction, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
            case R.id.item_restriction_quit:
                finish();
                break;
            case R.id.item_restriction_add:
                List<String> unLockList = new LinkedList<>();
                List<String> lockList = new LinkedList<>();
                for (int position : mClickPosition) {
                    if (mAppLockBeens.size() > position) {
                        AppLockBean appLockBean = mAppLockBeens.get(position);
                        if (appLockBean.isLock()) {
                            lockList.add(appLockBean.getPackageName());
                        } else {
                            unLockList.add(appLockBean.getPackageName());
                        }
                    }
                }
                if (unLockList.size() > 0) {
                    LauncherModel.getInstance().getLockerDao().unlockItem(unLockList);
                }
                if (lockList.size() > 0) {
                    LauncherModel.getInstance().getLockerDao().lockItem(lockList);
                }
                Intent intent = new Intent(this, AppLockTimeActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }
}
