package com.devdroid.sleepassistant.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.BaseActivity;

public class WebActivity extends BaseActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_web);
    init();
  }

  private void init(){
    WebView webView = (WebView) findViewById(R.id.web_view);
    ProgressBar progressBar = findViewById(R.id.progressBar);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setDomStorageEnabled(true);
    String url = "https://devdroid.cn/";
    WebViewClient webViewClient = new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        super.shouldOverrideUrlLoading(view, url);
        view.loadUrl(url);
        return true;
      }
    };
    webView.setWebViewClient(webViewClient);
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
    webView.setWebChromeClient(webChromeClient);
    webView.loadUrl(url);
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()){
      case android.R.id.home:
        finish();
        break;
    }
    return true;
  }
}