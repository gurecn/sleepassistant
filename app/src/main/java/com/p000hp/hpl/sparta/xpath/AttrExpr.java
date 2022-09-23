package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.AttrExpr */
public abstract class AttrExpr extends BooleanExpr {
    private final String attrName_;

    AttrExpr(String attrName) {
        this.attrName_ = attrName;
    }

    public String getAttrName() {
        return this.attrName_;
    }

    public String toString() {
        return "@" + this.attrName_;
    }
}
