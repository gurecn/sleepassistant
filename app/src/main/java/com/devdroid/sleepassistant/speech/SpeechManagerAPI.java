package com.devdroid.sleepassistant.speech;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;

import com.devdroid.sleepassistant.speech.local.TextToSpeechCallback;

import java.util.List;
import java.util.Locale;

public interface SpeechManagerAPI {

  void SpeechManagerInit(Context context, int mode, TextToSpeech.OnInitListener onInitListener);

  void init(Context context, int mode, TextToSpeech.OnInitListener onInitListener);

  boolean isSpeaking();

  void say(String message);

  void say(String message, TextToSpeechCallback callback);

  void stop();

  void setAudioStream(int audioStream);

  void setOnInitListener(TextToSpeech.OnInitListener onInitListener);

  void setPitch(float pitch);

  void setSpeechRate(float rate);
}
