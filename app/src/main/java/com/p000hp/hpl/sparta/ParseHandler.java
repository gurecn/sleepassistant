package com.p000hp.hpl.sparta;

/* renamed from: com.hp.hpl.sparta.ParseHandler */
public interface ParseHandler {
    void characters(char[] cArr, int i, int i2) throws ParseException;

    void endDocument() throws ParseException;

    void endElement(Element element) throws ParseException;

    ParseSource getParseSource();

    void setParseSource(ParseSource parseSource);

    void startDocument() throws ParseException;

    void startElement(Element element) throws ParseException;
}
