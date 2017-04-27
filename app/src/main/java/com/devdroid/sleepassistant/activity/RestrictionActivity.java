package com.devdroid.sleepassistant.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.mode.AppLockBean;
import com.devdroid.sleepassistant.utils.AppUtils;
import java.util.LinkedList;
import java.util.List;

/**
 * 限制界面
 */
public class RestrictionActivity extends BaseActivity{
    private List<AppLockBean> mAppLockBeens;      //所有应用
    private List<Integer> mClickPosition;       //操作过的应用坐标
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restriction);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
//                    ApplockManager.mLockerDao.unlockItem(unLockList);
//                    ApplockManager.unLockPackage(unLockList);
                }
                if (lockList.size() > 0) {
//                    ApplockManager.mLockerDao.lockItem(lockList);
//                    ApplockManager.lockPackage(lockList);
                }
//                Intent intent = new Intent(this,AppLockTimeActivity.class);
//                startActivity(intent);

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mClickPosition = new LinkedList<>();
//        ApplockManager.initSingleton(this);
        GridView gv = (GridView)findViewById(R.id.gridView_activity_restriction_app);
//        List<String> componentNames = ApplockManager.mLockerDao.queryLockerInfo();
        List<String> installedPackages = AppUtils.getLauncherAppPackageNames(this);
        if (installedPackages != null) {
            mAppLockBeens = new LinkedList<>();
            for (String packageName:installedPackages){
//                if (packageName != null && componentNames.contains(packageName)) {
//                    mAppLockBeens.add(new AppLockBean(true, packageName));
//                    continue;
//                }
                mAppLockBeens.add(new AppLockBean(false,packageName));
            }
            GridViewAdapter adapter = new GridViewAdapter(this, mAppLockBeens);
            gv.setAdapter(adapter);
        }
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GridViewAdapter adapter = (GridViewAdapter)adapterView.getAdapter();
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

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }


    class GridViewAdapter extends BaseAdapter {

        //上下文对象
        private Context context;
        private List<AppLockBean> list;
        private LayoutInflater mInflater;
        public GridViewAdapter(Context context,List<AppLockBean> list) {
            this.context = context;
            this.list = list;
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list == null ? 0:list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if (view == null) {
                holder=new ViewHolder();
                view = mInflater.inflate(R.layout.app_lock_grid_item, null);
                holder.appIcon = (ImageView)view.findViewById(R.id.image_icon);
                holder.appName = (TextView)view.findViewById(R.id.text_name);
                holder.appLock = (ImageView)view.findViewById(R.id.image_lock);
                view.setTag(holder);
            } else {
                holder = (ViewHolder)view.getTag();
            }
            AppLockBean appLockBean = list.get(i);
            String packageName = appLockBean.getPackageName();
            holder.appIcon.setImageBitmap(AppUtils.loadAppIcon(context,packageName));
            String appNameString = AppUtils.getAppName(context,packageName);
            holder.appName.setText(appNameString);
            if (appLockBean.isLock()) {
                holder.appLock.setVisibility(View.VISIBLE);
            } else {
                holder.appLock.setVisibility(View.GONE);
            }
            return view;
        }

        public final class ViewHolder{
            public ImageView appIcon;
            public TextView appName;
            public ImageView appLock;
        }
    }

}
