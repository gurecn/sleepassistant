package com.p000hp.hpl.sparta.xpath;

import java.io.IOException;

/* renamed from: com.hp.hpl.sparta.xpath.XPathException */
public class XPathException extends Exception {
    private Throwable cause_;

    public XPathException(XPath xpath, String msg) {
        super(xpath + " " + msg);
        this.cause_ = null;
    }

    XPathException(XPath xpath, String where, SimpleStreamTokenizer toks, String expected) {
        this(xpath, where + " got \"" + toString(toks) + "\" instead of expected " + expected);
    }

    XPathException(XPath xpath, Exception cause) {
        super(xpath + " " + cause);
        this.cause_ = null;
        this.cause_ = cause;
    }

    public Throwable getCause() {
        return this.cause_;
    }

    private static String toString(SimpleStreamTokenizer toks) {
        try {
            StringBuffer result = new StringBuffer();
            result.append(tokenToString(toks));
            if (toks.ttype != -1) {
                toks.nextToken();
                result.append(tokenToString(toks));
                toks.pushBack();
            }
            return result.toString();
        } catch (IOException e) {
            return "(cannot get  info: " + e + ")";
        }
    }

    private static String tokenToString(SimpleStreamTokenizer toks) {
        switch (toks.ttype) {
            case SimpleStreamTokenizer.TT_WORD:
                return toks.sval;
            case SimpleStreamTokenizer.TT_NUMBER:
                return toks.nval + "";
            case SimpleStreamTokenizer.TT_EOF:
                return "<end of expression>";
            default:
                return ((char) toks.ttype) + "";
        }
    }
}
