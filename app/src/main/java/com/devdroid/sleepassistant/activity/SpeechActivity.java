package com.devdroid.sleepassistant.activity;

import androidx.appcompat.widget.Toolbar;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.constant.CustomConstant;
import com.devdroid.sleepassistant.preferences.IPreferencesIds;
import com.devdroid.sleepassistant.preferences.SharedPreferencesManager;
import com.devdroid.sleepassistant.utils.Logger;
import com.devdroid.speech.SpeechManager;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;

import java.util.Objects;

public class SpeechActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

  String content;
  private EditText mEtSpeechContent;
  private static String TAG = SpeechActivity.class.getSimpleName();

  // 讯飞语音合成对象
  private SpeechSynthesizer mTts;
  // 讯飞引擎类型
  private String mEngineType = SpeechConstant.TYPE_CLOUD;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_speech);
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    mEtSpeechContent = findViewById(R.id.et_speech_content);
    RadioGroup mRGSpeechEngine = findViewById(R.id.rg_speech_engine_select);
    mRGSpeechEngine.setOnCheckedChangeListener(this);
    Button mBtnSpeech = findViewById(R.id.btn_speech_audition);
    SpeechUtility.createUtility(this, CustomConstant.IFLYTEK_SPEECH_APPID);
    // 初始化讯飞合成对象
    mTts = SpeechSynthesizer.createSynthesizer(this, mTtsInitListener);
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.btn_speech_audition) {
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
     if(mode == 2){  // 讯飞语音
       // 设置参数
       setParam();
       // 合成并播放，仅合成保存不播放使用：synthesizeToUri接口
       int code = mTts.startSpeaking(content, mTtsListener);
       if (code != ErrorCode.SUCCESS) {
         Logger.d(TAG, "语音合成失败,错误码: " + code);
       }
     } else {
       SpeechManager.getInstance().init(this, mode, status -> {
         if (status == TextToSpeech.SUCCESS) {
           SpeechManager.getInstance().setSpeechRate(1.0f);
           SpeechManager.getInstance().say(content);
         }
       });
     }
    }
  }

  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
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
    if(mTts.isSpeaking())mTts.stopSpeaking();
    if(SpeechManager.getInstance().isSpeaking())SpeechManager.getInstance().stop();
  }

  /**
   * 讯飞语音合成参数设置
   */
  private void setParam() {
    SharedPreferencesManager spm = LauncherModel.getInstance().getSharedPreferencesManager();
    // 清空参数
    mTts.setParameter(SpeechConstant.PARAMS, null);
    // 根据合成引擎设置相应参数
    if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
      mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
      // 支持实时音频返回，仅在 synthesizeToUri 条件下支持
      mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY, "1");
      //	mTts.setParameter(SpeechConstant.TTS_BUFFER_TIME,"1");
      // 设置在线合成发音人
      mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
      //设置合成语速
      mTts.setParameter(SpeechConstant.SPEED, spm.getString("speed_preference", "50"));
      //设置合成音调
      mTts.setParameter(SpeechConstant.PITCH, spm.getString("pitch_preference", "50"));
      //设置合成音量
      mTts.setParameter(SpeechConstant.VOLUME, spm.getString("volume_preference", "50"));
    } else {
      mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
      mTts.setParameter(SpeechConstant.VOICE_NAME, "");
    }
    //设置播放器音频流类型
    mTts.setParameter(SpeechConstant.STREAM_TYPE, spm.getString("stream_preference", "3"));
    // 设置播放合成音频打断音乐播放，默认为true
    mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");
    // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
    mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "pcm");
    mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
            getExternalFilesDir("msc").getAbsolutePath() + "/tts.pcm");
  }

  /**
   * 初始化监听。
   */
  private InitListener mTtsInitListener = code -> {
    Logger.d(TAG, "InitListener init() code = " + code);
    if (code != ErrorCode.SUCCESS) {
      Logger.d(TAG, "初始化失败,错误码：" + code);
    }
  };

  /**
   * 合成回调监听。
   */
  private SynthesizerListener mTtsListener = new SynthesizerListener() {

    @Override
    public void onSpeakBegin() {
      Logger.d(TAG, "开始播放");
    }

    @Override
    public void onSpeakPaused() {
      Logger.d(TAG, "暂停播放");
    }

    @Override
    public void onSpeakResumed() {
      Logger.d(TAG, "继续播放");
    }

    @Override
    public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
      // 合成进度
      Logger.e(TAG, "MscSpeechLog percent =" + percent);
    }

    @Override
    public void onSpeakProgress(int percent, int beginPos, int endPos) {
      // 播放进度
      Logger.e(TAG, "MscSpeechLog percent =" + percent);
    }

    @Override
    public void onCompleted(SpeechError error) {
      Logger.d(TAG, "播放完成");
      if (error != null) {
        Logger.d(TAG, error.getPlainDescription(true));
        return;
      }
    }

    // 用于问题定位
    @Override
    public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
      //	 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
      //	 若使用本地能力，会话id为null
    }
  };
}