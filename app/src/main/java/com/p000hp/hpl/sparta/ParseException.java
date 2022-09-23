package com.p000hp.hpl.sparta;

/* renamed from: com.hp.hpl.sparta.ParseException */
public class ParseException extends Exception {
    private Throwable cause_;
    private int lineNumber_;

    public ParseException(String msg) {
        super(msg);
        this.lineNumber_ = -1;
        this.cause_ = null;
    }

    public ParseException(String msg, Throwable cause) {
        super(msg + " " + cause);
        this.lineNumber_ = -1;
        this.cause_ = null;
        this.cause_ = cause;
    }

    public ParseException(String systemId, int lineNumber, int lastCharRead, String history, String msg) {
        super(toMessage(systemId, lineNumber, lastCharRead, history, msg));
        this.lineNumber_ = -1;
        this.cause_ = null;
        this.lineNumber_ = lineNumber;
    }

    public ParseException(ParseLog log, String systemId, int lineNumber, int lastCharRead, String history, String msg) {
        this(systemId, lineNumber, lastCharRead, history, msg);
        log.error(msg, systemId, lineNumber);
    }

    public ParseException(ParseCharStream source, String msg) {
        this(source.getLog(), source.getSystemId(), source.getLineNumber(), source.getLastCharRead(), source.getHistory(), msg);
    }

    public ParseException(ParseCharStream source, char actual, char expected) {
        this(source, "got '" + actual + "' instead of expected '" + expected + "'");
    }

    public ParseException(ParseCharStream source, char actual, char[] expected) {
        this(source, "got '" + actual + "' instead of " + toString(expected));
    }

    public ParseException(ParseCharStream source, char actual, String expected) {
        this(source, "got '" + actual + "' instead of " + expected + " as expected");
    }

    public ParseException(ParseCharStream source, String actual, String expected) {
        this(source, "got \"" + actual + "\" instead of \"" + expected + "\" as expected");
    }

    private static String toString(char[] chars) {
        StringBuffer result = new StringBuffer();
        result.append(chars[0]);
        for (int i = 1; i < chars.length; i++) {
            result.append("or " + chars[i]);
        }
        return result.toString();
    }

    public ParseException(ParseCharStream source, String actual, char[] expected) {
        this(source, actual, new String(expected));
    }

    public int getLineNumber() {
        return this.lineNumber_;
    }

    public Throwable getCause() {
        return this.cause_;
    }

    private static String toMessage(String systemId, int lineNumber, int lastCharRead, String history, String msg) {
        return systemId + "(" + lineNumber + "): \n" + history + "\nLast character read was '" + charRepr(lastCharRead) + "'\n" + msg;
    }

    static String charRepr(int ch) {
        return ch == -1 ? "EOF" : "" + ((char) ch);
    }
}
