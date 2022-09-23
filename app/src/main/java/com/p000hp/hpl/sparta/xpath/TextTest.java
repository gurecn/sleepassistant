package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.TextTest */
public class TextTest extends NodeTest {
    static final TextTest INSTANCE = new TextTest();

    private TextTest() {
    }

    @Override // com.p000hp.hpl.sparta.xpath.NodeTest
    public void accept(Visitor visitor) throws XPathException {
        visitor.visit(this);
    }

    @Override // com.p000hp.hpl.sparta.xpath.NodeTest
    public boolean isStringValue() {
        return true;
    }

    public String toString() {
        return "text()";
    }
}
