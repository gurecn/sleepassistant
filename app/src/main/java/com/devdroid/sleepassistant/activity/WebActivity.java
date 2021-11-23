package com.devdroid.sleepassistant.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.BaseActivity;

public class WebActivity extends BaseActivity {
  private WebView mWebView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_web);
  }

  @Override
  protected void onStart() {
    super.onStart();
    init();
  }

  private void init(){

    mWebView = (WebView) findViewById(R.id.web_view);
    ProgressBar progressBar = findViewById(R.id.progressBar);
    mWebView.getSettings().setJavaScriptEnabled(true);
    mWebView.getSettings().setDomStorageEnabled(true);
    String url = getIntent().getStringExtra("url");
    if(TextUtils.isEmpty(url)) {
      url = "https://devdroid.cn/";
    }
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