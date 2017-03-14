package com.devdroid.sleepassistant.listener;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.devdroid.sleepassistant.R;

/**
 * 侧滑菜单事件监听
 * User:Gaolei  gurecn@gmail.com
 * Date:2016/10/10
 * I'm glad to share my knowledge with you all.
 */
public class NavigationItemSelectedListener  implements NavigationView.OnNavigationItemSelectedListener {
    private AppCompatActivity mAppCompatActivity;
    public NavigationItemSelectedListener(AppCompatActivity appCompatActivity) {
        this.mAppCompatActivity = appCompatActivity;
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_gallery :
                break;
            case R.id.nav_share :
                break;
            case R.id.nav_send :
                break;
        }
        return true;
    }
}
