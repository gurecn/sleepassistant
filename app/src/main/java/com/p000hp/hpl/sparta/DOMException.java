package com.p000hp.hpl.sparta;

/* renamed from: com.hp.hpl.sparta.DOMException */
public class DOMException extends Exception {
    public static final short DOMSTRING_SIZE_ERR = 2;
    public static final short HIERARCHY_REQUEST_ERR = 3;
    public static final short NOT_FOUND_ERR = 8;
    public short code;

    public DOMException(short code2, String message) {
        super(message);
        this.code = code2;
    }
}
