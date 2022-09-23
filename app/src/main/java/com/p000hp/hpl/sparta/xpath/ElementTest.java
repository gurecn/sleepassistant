package com.p000hp.hpl.sparta.xpath;

import com.p000hp.hpl.sparta.Sparta;

/* renamed from: com.hp.hpl.sparta.xpath.ElementTest */
public class ElementTest extends NodeTest {
    private final String tagName_;

    ElementTest(String tagName) {
        this.tagName_ = Sparta.intern(tagName);
    }

    @Override // com.p000hp.hpl.sparta.xpath.NodeTest
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override // com.p000hp.hpl.sparta.xpath.NodeTest
    public boolean isStringValue() {
        return false;
    }

    public String getTagName() {
        return this.tagName_;
    }

    public String toString() {
        return this.tagName_;
    }
}
