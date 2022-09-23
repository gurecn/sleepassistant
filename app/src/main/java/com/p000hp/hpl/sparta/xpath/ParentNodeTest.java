package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.ParentNodeTest */
public class ParentNodeTest extends NodeTest {
    static final ParentNodeTest INSTANCE = new ParentNodeTest();

    private ParentNodeTest() {
    }

    @Override // com.p000hp.hpl.sparta.xpath.NodeTest
    public void accept(Visitor visitor) throws XPathException {
        visitor.visit(this);
    }

    @Override // com.p000hp.hpl.sparta.xpath.NodeTest
    public boolean isStringValue() {
        return false;
    }

    public String toString() {
        return "..";
    }
}
