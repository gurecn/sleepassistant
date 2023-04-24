package com.devdroid.sleepassistant.listener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.activity.AboutActivity;
import com.devdroid.sleepassistant.activity.ChartActivity;
import com.devdroid.sleepassistant.activity.FeedbackActivity;
import com.devdroid.sleepassistant.activity.FeedbackTXActivity;
import com.devdroid.sleepassistant.activity.MainActivity;
import com.devdroid.sleepassistant.activity.RestrictionActivity;
import com.devdroid.sleepassistant.activity.SettingsActivity;
import com.devdroid.sleepassistant.activity.SpeechActivity;
import com.devdroid.sleepassistant.activity.WebActivity;
import com.devdroid.sleepassistant.utils.AppUtils;
import com.google.android.material.navigation.NavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

/**
 * 侧滑菜单事件监听
 * User:Gaolei  gurecn@gmail.com
 * Date:2016/10/10
 * I'm glad to share my knowledge with you all.
 */
public class NavigationItemSelectedListener  implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout mDrawerLayout;
    private AppCompatActivity mAppCompatActivity;
    public NavigationItemSelectedListener(AppCompatActivity appCompatActivity, DrawerLayout drawer) {
        this.mAppCompatActivity = appCompatActivity;
        this.mDrawerLayout = drawer;
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_calendar :
                mAppCompatActivity.startActivity(new Intent(mAppCompatActivity, MainActivity.class));
                break;
            case R.id.nav_chart :
                mAppCompatActivity.startActivity(new Intent(mAppCompatActivity, ChartActivity.class));
                break;
            case R.id.nav_restriction :
                mAppCompatActivity.startActivity(new Intent(mAppCompatActivity, RestrictionActivity.class));
                break;
            case R.id.nav_set :
                mAppCompatActivity.startActivity(new Intent(mAppCompatActivity, SettingsActivity.class));
                break;
            case R.id.nav_share :
                shareText();
                break;
            case R.id.nav_send :
                AppUtils.feedbackDialog(mAppCompatActivity);
                break;
            case R.id.nav_blogger:
                mAppCompatActivity.startActivity(new Intent(mAppCompatActivity, WebActivity.class));
                break;
            case R.id.nav_about :
                mAppCompatActivity.startActivity(new Intent(mAppCompatActivity, AboutActivity.class));
                break;
            case R.id.nav_speech :
                mAppCompatActivity.startActivity(new Intent(mAppCompatActivity, SpeechActivity.class));
                break;
        }
        item.setCheckable(false);
        item.setChecked(false);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * 分享应用
     */
    public void shareText() {
        Intent shareIntent = new Intent();
        String shareAppText = mAppCompatActivity.getResources().getString(R.string.share_app_text);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TITLE,shareAppText);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareAppText);
        shareIntent.setType("text/plain");
        mAppCompatActivity.startActivity(Intent.createChooser(shareIntent, "分享到"));
    }
}
