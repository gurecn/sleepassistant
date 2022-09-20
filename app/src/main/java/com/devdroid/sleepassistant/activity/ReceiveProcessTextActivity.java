package com.devdroid.sleepassistant.activity;

import android.content.Intent;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.BaseActivity;

public class ReceiveProcessTextActivity extends BaseActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_receive_process_text);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    CharSequence charSequenceExtra = getIntent().getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
    boolean readonly = getIntent().getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false);
    if (charSequenceExtra != null) {
      TextView tvReceiveProcess = findViewById(R.id.tv_receive_process_text);
      tvReceiveProcess.setText(charSequenceExtra);
    }
  }
}