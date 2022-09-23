package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.ThisNodeTest */
public class ThisNodeTest extends NodeTest {
    static final ThisNodeTest INSTANCE = new ThisNodeTest();

    private ThisNodeTest() {
    }

    @Override // com.p000hp.hpl.sparta.xpath.NodeTest
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override // com.p000hp.hpl.sparta.xpath.NodeTest
    public boolean isStringValue() {
        return false;
    }

    public String toString() {
        return ".";
    }
}
