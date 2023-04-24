package com.devdroid.sleepassistant.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;
import com.devdroid.sleepassistant.utils.Logger;
import com.devdroid.speech.SpeechManager;

public class SpeechActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

  String content;
  private EditText mEtSpeechContent;
  private static String TAG = SpeechActivity.class.getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_speech);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    mEtSpeechContent = findViewById(R.id.et_speech_content);
    RadioGroup mRGSpeechEngine = (RadioGroup)findViewById(R.id.rg_speech_engine_select);
    mRGSpeechEngine.setOnCheckedChangeListener(this);
    Button mBtnSpeech = findViewById(R.id.btn_speech_audition);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_speech_audition:
        content = mEtSpeechContent.getText().toString();
        speechContent();
    }
  }

  private void speechContent(){
    if (content.isEmpty()) {
      content = "无内容";
    }
    if(!SpeechManager.getInstance().isSpeaking()) {
      int mode = LauncherModel.getInstance().getSharedPreferencesManager().getInt(IPreferencesIds.SPEECH_ENGINE_MODE, 0);
      SpeechManager.getInstance().init(this, mode, status -> {
        if (status == TextToSpeech.SUCCESS) {
          SpeechManager.getInstance().setSpeechRate(1.0f);
          SpeechManager.getInstance().say(content);
        }
      });
    }
  }


//  public void setSpeechEngineDialog() {
//
//    AlertDialog.Builder normalDialog = new AlertDialog.Builder(this);
//    normalDialog.setTitle(getString(R.string.dialog_set_speech_engine));
//    normalDialog.setNegativeButton(getString(R.string.local_speech_engine), (dialog, which) -> {
//      dialog.dismiss();
//      LauncherModel.getInstance().getSharedPreferencesManager().commitInt(IPreferencesIds.SPEECH_ENGINE_MODE, 0);
//      SpeechManager.getInstance().clear();
//    });
//    normalDialog.setPositiveButton(getString(R.string.tensorflow_speech_engine), (dialog, which) -> {
//      dialog.dismiss();
//      LauncherModel.getInstance().getSharedPreferencesManager().commitInt(IPreferencesIds.SPEECH_ENGINE_MODE, 1);
//      SpeechManager.getInstance().clear();
//    });
//    normalDialog.show();
//  }

  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()){
      case android.R.id.home:
        finish();
        break;
    }
    return true;
  }

  @Override
  public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
    switch (checkedId){
      case R.id.rb_speech_engine_0:
        LauncherModel.getInstance().getSharedPreferencesManager().commitInt(IPreferencesIds.SPEECH_ENGINE_MODE, 0);
        break;
      case R.id.rb_speech_engine_1:
        LauncherModel.getInstance().getSharedPreferencesManager().commitInt(IPreferencesIds.SPEECH_ENGINE_MODE, 1);
        break;
      case R.id.rb_speech_engine_2:
        LauncherModel.getInstance().getSharedPreferencesManager().commitInt(IPreferencesIds.SPEECH_ENGINE_MODE, 2);
        break;
    }
    Logger.d(TAG, "onCheckedChanged i:" + LauncherModel.getInstance().getSharedPreferencesManager().getInt(IPreferencesIds.SPEECH_ENGINE_MODE, 0));
    SpeechManager.getInstance().clear();
    if(SpeechManager.getInstance().isSpeaking())SpeechManager.getInstance().stop();
  }
}