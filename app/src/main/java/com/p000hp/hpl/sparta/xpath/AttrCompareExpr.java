package com.p000hp.hpl.sparta.xpath;

import com.p000hp.hpl.sparta.Sparta;

/* renamed from: com.hp.hpl.sparta.xpath.AttrCompareExpr */
public abstract class AttrCompareExpr extends AttrExpr {
    private final String attrValue_;

    AttrCompareExpr(String attrName, String attrValue) {
        super(attrName);
        this.attrValue_ = Sparta.intern(attrValue);
    }

    public String getAttrValue() {
        return this.attrValue_;
    }

    /* access modifiers changed from: protected */
    public String toString(String op) {
        return "[" + super.toString() + op + "'" + this.attrValue_ + "']";
    }
}
