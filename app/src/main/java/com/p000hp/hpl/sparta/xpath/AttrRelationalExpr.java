package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.AttrRelationalExpr */
public abstract class AttrRelationalExpr extends AttrExpr {
    private final int attrValue_;

    AttrRelationalExpr(String attrName, int attrValue) {
        super(attrName);
        this.attrValue_ = attrValue;
    }

    public double getAttrValue() {
        return (double) this.attrValue_;
    }

    /* access modifiers changed from: protected */
    public String toString(String op) {
        return "[" + super.toString() + op + "'" + this.attrValue_ + "']";
    }
}
