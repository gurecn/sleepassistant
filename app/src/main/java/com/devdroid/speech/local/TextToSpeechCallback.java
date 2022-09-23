package com.devdroid.speech.local;

/**
 * Contains the methods which are called to notify text to speech progress status.
 *
 * @author Aleksandar Gotev
 */
public interface TextToSpeechCallback {
    void onStart();
    void onCompleted();
    void onError();
}
