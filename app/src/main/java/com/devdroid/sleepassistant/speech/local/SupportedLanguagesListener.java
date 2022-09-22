package com.devdroid.sleepassistant.speech.local;

import java.util.List;

public interface SupportedLanguagesListener {
    void onSupportedLanguages(List<String> supportedLanguages);
    void onNotSupported(UnsupportedReason reason);
}
