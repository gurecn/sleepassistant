package com.devdroid.speech.local.engine;

import android.content.Context;
import android.speech.RecognitionListener;

import com.devdroid.speech.local.SpeechDelegate;
import com.devdroid.speech.local.GoogleVoiceTypingDisabledException;
import com.devdroid.speech.local.SpeechRecognitionNotAvailable;
import com.devdroid.speech.local.ui.SpeechProgressView;

import java.util.Locale;

public interface SpeechRecognitionEngine extends RecognitionListener {

    void init(Context context);

    void clear();

    String getPartialResultsAsString();

    void initSpeechRecognizer(Context context);

    void startListening(SpeechProgressView progressView, SpeechDelegate delegate) throws SpeechRecognitionNotAvailable, GoogleVoiceTypingDisabledException;

    void stopListening();

    void returnPartialResultsAndRecreateSpeechRecognizer();

    void setPartialResults(boolean getPartialResults);

    void shutdown();

    boolean isListening();

    Locale getLocale();

    void setLocale(Locale locale);

    void setPreferOffline(boolean preferOffline);

    void setTransitionMinimumDelay(long milliseconds);

    void setStopListeningAfterInactivity(long milliseconds);

    void setCallingPackage(String callingPackage);

    void unregisterDelegate();
}
