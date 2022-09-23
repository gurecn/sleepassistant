package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.AttrTest */
public class AttrTest extends NodeTest {
    private final String attrName_;

    AttrTest(String attrName) {
        this.attrName_ = attrName;
    }

    @Override // com.p000hp.hpl.sparta.xpath.NodeTest
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override // com.p000hp.hpl.sparta.xpath.NodeTest
    public boolean isStringValue() {
        return true;
    }

    public String getAttrName() {
        return this.attrName_;
    }

    public String toString() {
        return "@" + this.attrName_;
    }
}
