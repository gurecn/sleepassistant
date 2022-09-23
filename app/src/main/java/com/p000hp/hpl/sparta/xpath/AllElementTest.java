package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.AllElementTest */
public class AllElementTest extends NodeTest {
    static final AllElementTest INSTANCE = new AllElementTest();

    private AllElementTest() {
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
        return "*";
    }
}
