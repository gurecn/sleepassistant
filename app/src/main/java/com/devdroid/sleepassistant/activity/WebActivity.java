package com.devdroid.sleepassistant.activity;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;

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
      boolean isNightMode = LauncherModel.getInstance().getSharedPreferencesManager().getBoolean(IPreferencesIds.KEY_THEME_NIGHT_MODE, false);
      url = "https://devdroid.cn?nightmode="+(isNightMode?1:0);
    }
    WebViewClient webViewClient = new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if(!url.startsWith("http:") && !url.startsWith("https:")){
          return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
      }

      @Override
      public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        if(!url.startsWith("http:") && !url.startsWith("https:")){
          return true;
        }
        return super.shouldOverrideUrlLoading(view, url);
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