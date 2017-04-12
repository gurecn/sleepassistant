package com.devdroid.sleepassistant.adapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
/**
 * Created with IntelliJ IDEA.
 * User: Gaolei
 * Date: 2015/12/17
 * Email: pdsfgl@live.com
 */

public class CalendarViewAdapter<V extends View> extends PagerAdapter {
    private V[] views;

    public CalendarViewAdapter(V[] views) {
        super();
        this.views = views;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        if (((ViewPager) container).getChildCount() == views.length) {
            ((ViewPager) container).removeView(views[position % views.length]);
        }
        ((ViewPager) container).addView(views[position % views.length], 0);
        return views[position % views.length];
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((View) container);
    }

    public V[] getAllItems() {
        return views;
    }

    public V getItem(int position) {
        if(views.length > position) {
            return views[position];
        }
        return null;
    }
}
