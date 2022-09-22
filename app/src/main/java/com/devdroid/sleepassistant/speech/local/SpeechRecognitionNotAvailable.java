package com.devdroid.sleepassistant.speech.local;

/**
 * @author Aleksandar Gotev
 */
public class SpeechRecognitionNotAvailable extends Exception {
    public SpeechRecognitionNotAvailable() {
        super("Speech recognition not available");
    }
}
