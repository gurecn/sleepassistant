package com.devdroid.sleepassistant.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
    webView.getSettings().setJavaScriptEnabled(true);
    webView.getSettings().setDomStorageEnabled(true);
    String url = "https://gurecn.gitee.io/";
    WebViewClient webViewClient = new WebViewClient() {
      @Override
      public boolean shouldOverrideUrlLoading(WebView view, String url) {
        super.shouldOverrideUrlLoading(view, url);
        view.loadUrl(url);
        return true;
      }
    };
    webView.setWebViewClient(webViewClient);

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