package com.devdroid.sleepassistant.listener;

import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User:Gaolei  gurecn@gmail.com
 * Date:2016/10/11
 * I'm glad to share my knowledge with you all.
 */
public interface OnDismissAndShareListener {
    void onItemDismiss(int position);
    void onItemShare(int position, View currentView);
}
