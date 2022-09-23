package net.sourceforge.pinyin4j;

import com.devdroid.sleepassistant.application.TheApplication;

import java.io.BufferedInputStream;
import java.io.IOException;

class ResourceHelper {
    ResourceHelper() {
    }

    static BufferedInputStream getResourceInputStream(String resourceName) {
        try {
            return new BufferedInputStream(TheApplication.getAppContext().getAssets().open(resourceName));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
