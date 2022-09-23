package com.devdroid.speech;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import com.devdroid.speech.local.TextToSpeechCallback;

public interface SpeechManagerAPI {

  void SpeechManagerInit(Context context, int mode, TextToSpeech.OnInitListener onInitListener);

  void init(Context context, int mode, TextToSpeech.OnInitListener onInitListener);

  boolean isSpeaking();

  void say(String message);

  void say(String message, TextToSpeechCallback callback);

  void stop();

  void clear();

  void setAudioStream(int audioStream);

  void setOnInitListener(TextToSpeech.OnInitListener onInitListener);

  void setPitch(float pitch);

  void setSpeechRate(float rate);
}
