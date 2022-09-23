package com.p000hp.hpl.sparta;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/* renamed from: com.hp.hpl.sparta.Parser */
public class Parser {
    public static Document parse(String systemId, Reader reader) throws ParseException, IOException {
        BuildDocument bd = new BuildDocument();
        new ParseCharStream(systemId, reader, (ParseLog) null, (String) null, bd);
        return bd.getDocument();
    }

    public static Document parse(String systemId, Reader reader, ParseLog log) throws ParseException, IOException {
        BuildDocument bd = new BuildDocument();
        new ParseCharStream(systemId, reader, log, (String) null, bd);
        return bd.getDocument();
    }

    public static Document parse(String xml) throws ParseException, IOException {
        return parse(xml.toCharArray());
    }

    public static Document parse(char[] xml) throws ParseException, IOException {
        BuildDocument bd = new BuildDocument();
        new ParseCharStream("file:anonymous-string", xml, (ParseLog) null, (String) null, bd);
        return bd.getDocument();
    }

    public static Document parse(byte[] xml) throws ParseException, IOException {
        BuildDocument bd = new BuildDocument();
        new ParseByteStream("file:anonymous-string", new ByteArrayInputStream(xml), null, null, bd);
        return bd.getDocument();
    }

    public static Document parse(String systemId, Reader reader, ParseLog log, String encoding) throws ParseException, EncodingMismatchException, IOException {
        BuildDocument bd = new BuildDocument();
        new ParseCharStream(systemId, reader, log, encoding, bd);
        return bd.getDocument();
    }

    public static Document parse(String systemId, InputStream istream, ParseLog log) throws ParseException, IOException {
        BuildDocument bd = new BuildDocument();
        new ParseByteStream(systemId, istream, log, null, bd);
        return bd.getDocument();
    }

    public static Document parse(String systemId, InputStream istream) throws ParseException, IOException {
        BuildDocument bd = new BuildDocument();
        new ParseByteStream(systemId, istream, null, null, bd);
        return bd.getDocument();
    }

    public static Document parse(String systemId, InputStream istream, ParseLog log, String guessedEncoding) throws ParseException, IOException {
        BuildDocument bd = new BuildDocument();
        new ParseByteStream(systemId, istream, log, guessedEncoding, bd);
        return bd.getDocument();
    }

    public static void parse(String systemId, Reader reader, ParseHandler ph) throws ParseException, IOException {
        new ParseCharStream(systemId, reader, (ParseLog) null, (String) null, ph);
    }

    public static void parse(String systemId, Reader reader, ParseLog log, ParseHandler ph) throws ParseException, IOException {
        new ParseCharStream(systemId, reader, log, (String) null, ph);
    }

    public static void parse(String xml, ParseHandler ph) throws ParseException, IOException {
        parse(xml.toCharArray(), ph);
    }

    public static void parse(char[] xml, ParseHandler ph) throws ParseException, IOException {
        new ParseCharStream("file:anonymous-string", xml, (ParseLog) null, (String) null, ph);
    }

    public static void parse(byte[] xml, ParseHandler ph) throws ParseException, IOException {
        new ParseByteStream("file:anonymous-string", new ByteArrayInputStream(xml), null, null, ph);
    }

    public static void parse(String systemId, InputStream istream, ParseLog log, ParseHandler ph) throws ParseException, IOException {
        new ParseByteStream(systemId, istream, log, null, ph);
    }

    public static void parse(String systemId, InputStream istream, ParseHandler ph) throws ParseException, IOException {
        new ParseByteStream(systemId, istream, null, null, ph);
    }

    public static void parse(String systemId, InputStream istream, ParseLog log, String guessedEncoding, ParseHandler ph) throws ParseException, IOException {
        new ParseByteStream(systemId, istream, log, guessedEncoding, ph);
    }

    public static void parse(String systemId, Reader reader, ParseLog log, String encoding, ParseHandler ph) throws ParseException, EncodingMismatchException, IOException {
        new ParseCharStream(systemId, reader, log, encoding, ph);
    }
}
