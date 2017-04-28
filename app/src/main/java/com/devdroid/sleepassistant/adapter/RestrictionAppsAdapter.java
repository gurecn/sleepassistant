package com.devdroid.sleepassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.mode.AppLockBean;
import com.devdroid.sleepassistant.utils.AppUtils;
import java.util.List;

/**
 * 限制应用适配器
 */

public class RestrictionAppsAdapter extends BaseAdapter {
    //上下文对象
    private Context context;
    private List<AppLockBean> list;
    private LayoutInflater mInflater;
    public RestrictionAppsAdapter(Context context,List<AppLockBean> list) {
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
        RestrictionAppsAdapter.ViewHolder holder;
        if (view == null) {
            holder=new RestrictionAppsAdapter.ViewHolder();
            view = mInflater.inflate(R.layout.app_lock_grid_item, null);
            holder.appIcon = (ImageView)view.findViewById(R.id.image_icon);
            holder.appName = (TextView)view.findViewById(R.id.text_name);
            holder.appLock = (ImageView)view.findViewById(R.id.image_lock);
            view.setTag(holder);
        } else {
            holder = (RestrictionAppsAdapter.ViewHolder)view.getTag();
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

    private final class ViewHolder{
        ImageView appIcon;
        TextView appName;
        ImageView appLock;
    }
}