package com.devdroid.sleepassistant.activity;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.BaseActivity;

public class SkipActivity extends BaseActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_skip);
  }
  @Override
  protected void onResume() {
    super.onResume();
    Intent intent = getIntent();
    if(intent != null){
      String action = intent.getStringExtra("action");
      if("set".equals(action)) {
        Intent inten = new Intent(this, SettingsActivity.class) ;
        inten.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        startActivity(inten);
      }
    }
  }



}