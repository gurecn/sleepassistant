package com.devdroid.sleepassistant.freefont.core.animation;

public interface TA<T> {
        void start();

        void stop();

        long getDuration();

        T getValue();
    }