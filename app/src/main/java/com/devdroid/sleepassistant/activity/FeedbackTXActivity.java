package com.devdroid.sleepassistant.activity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.BaseActivity;

public class FeedbackTXActivity extends BaseActivity {
  private WebView mWebView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feedback_txactivity);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
  }

  @Override
  protected void onStart() {
    super.onStart();
    init();
  }

  private void init(){
    mWebView = (WebView) findViewById(R.id.web_feed_back);
    ProgressBar progressBar = findViewById(R.id.progressBar);
    mWebView.getSettings().setJavaScriptEnabled(true);
    mWebView.getSettings().setDomStorageEnabled(true);
    String url = "https://support.qq.com/product/365802";
    /* WebView 内嵌 Client 可以在APP内打开网页而不是跳出到浏览器 */
    WebViewClient webViewClient = new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        super.shouldOverrideUrlLoading(view, url);
        view.loadUrl(url);
        return true;
      }
    };
    mWebView.setWebViewClient(webViewClient);

    WebChromeClient webChromeClient = new WebChromeClient(){
      @Override
      public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if(newProgress==100){
          progressBar.setVisibility(View.GONE);
        } else{
          progressBar.setVisibility(View.VISIBLE);
          progressBar.setProgress(newProgress);
        }
      }
    };
    mWebView.setWebChromeClient(webChromeClient);
    mWebView.loadUrl(url);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()){
      case android.R.id.home:
        finish();
        break;
    }
    return true;
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
      mWebView.goBack();//返回上个页面
      return true;
    }
    return super.onKeyDown(keyCode, event);//退出整个Activity
  }
}