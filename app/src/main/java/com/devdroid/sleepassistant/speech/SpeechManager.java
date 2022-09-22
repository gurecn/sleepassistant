package com.devdroid.sleepassistant.speech;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.devdroid.sleepassistant.speech.local.Speech;
import com.devdroid.sleepassistant.speech.local.TextToSpeechCallback;
import com.devdroid.sleepassistant.speech.tensorflow.tts.TtsManager;

public class SpeechManager implements SpeechManagerAPI{

  private static final Object INSTANCE_WRITE_LOCK = new Object();
  private static SpeechManagerAPI instance = null;
  private int mMode;

  @Override
  public void SpeechManagerInit(Context context, int mode, TextToSpeech.OnInitListener onInitListener) {
    this.mMode = mode;
    if(mode == 0){
      Speech.getInstance().init(context, context.getPackageName(), onInitListener);
    } else {
      TtsManager.getInstance().init(context, onInitListener);
    }
  }

  /**
   * Gets speech recognition instance.
   *
   * @return SpeechRecognition instance
   */
  public static SpeechManagerAPI getInstance() {
    if (instance == null) {
      synchronized (INSTANCE_WRITE_LOCK) {
        if (instance == null) {
          instance = new SpeechManager();
        }
      }
    }
    return instance;
  }

  @Override
  public void init(Context context, int mode, TextToSpeech.OnInitListener onInitListener) {
    if(instance != null) {
      instance.SpeechManagerInit(context, mode, onInitListener);
    }
  }

  @Override
  public boolean isSpeaking() {
    if(this.mMode == 0){
      return Speech.getInstance().isSpeaking();
    } else {
      return TtsManager.getInstance().isSpeaking();
    }
  }

  @Override
  public void say(String message) {
    if(this.mMode == 0){
      Speech.getInstance().say(message);
    } else {
      TtsManager.getInstance().speak(message);
    }
  }

  @Override
  public void say(String message, TextToSpeechCallback callback) {

  }

  @Override
  public void stop() {
    if(this.mMode == 0){
      Speech.getInstance().stopTextToSpeech();
    } else {
      TtsManager.getInstance().stopTts();
    }
  }

  @Override
  public void setAudioStream(int audioStream) {

  }

  @Override
  public void setOnInitListener(TextToSpeech.OnInitListener onInitListener) {

  }

  @Override
  public void setPitch(float pitch) {

  }

  @Override
  public void setSpeechRate(float rate) {
    if(this.mMode == 0){
      Speech.getInstance().setTextToSpeechRate(rate);
    } else {
      TtsManager.getInstance().setSpeed(rate *1.2f);
    }
  }
}
