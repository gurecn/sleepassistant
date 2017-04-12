package com.devdroid.sleepassistant.listener;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.activity.AboutActivity;
import com.devdroid.sleepassistant.activity.FeedbackActivity;

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
            case R.id.nav_text :
                break;
            case R.id.nav_gallery :

                break;
            case R.id.nav_video :

                break;
            case R.id.nav_edit :
                break;
            case R.id.nav_share :
                shareText();
                break;
            case R.id.nav_send :
                mAppCompatActivity.startActivity(new Intent(mAppCompatActivity, FeedbackActivity.class));
                break;
            case R.id.nav_about :
                mAppCompatActivity.startActivity(new Intent(mAppCompatActivity, AboutActivity.class));
                break;
        }
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
