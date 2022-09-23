package com.p000hp.hpl.sparta;

/* renamed from: com.hp.hpl.sparta.ParseSource */
public interface ParseSource {
    public static final ParseLog DEFAULT_LOG = new DefaultLog();
    public static final int MAXLOOKAHEAD = ("<?xml version=\"1.0\" encoding=\"\"".length() + 40);

    int getLineNumber();

    String getSystemId();

    String toString();
}
