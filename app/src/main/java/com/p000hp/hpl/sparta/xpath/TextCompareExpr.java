package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.TextCompareExpr */
public abstract class TextCompareExpr extends BooleanExpr {
    private final String value_;

    TextCompareExpr(String value) {
        this.value_ = value;
    }

    public String getValue() {
        return this.value_;
    }

    /* access modifiers changed from: protected */
    public String toString(String op) {
        return "[text()" + op + "'" + this.value_ + "']";
    }
}
