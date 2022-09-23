package com.p000hp.hpl.sparta;

/* renamed from: com.hp.hpl.sparta.EncodingMismatchException */
public class EncodingMismatchException extends ParseException {
    private String declaredEncoding_;

    EncodingMismatchException(String systemId, String declaredEncoding, String assumedEncoding) {
        super(systemId, 0, declaredEncoding.charAt(declaredEncoding.length() - 1), declaredEncoding, "encoding '" + declaredEncoding + "' declared instead of of " + assumedEncoding + " as expected");
        this.declaredEncoding_ = declaredEncoding;
    }

    /* access modifiers changed from: package-private */
    public String getDeclaredEncoding() {
        return this.declaredEncoding_;
    }
}
