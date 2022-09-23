package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.TextNotEqualsExpr */
public class TextNotEqualsExpr extends TextCompareExpr {
    TextNotEqualsExpr(String value) {
        super(value);
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExpr
    public void accept(BooleanExprVisitor visitor) throws XPathException {
        visitor.visit(this);
    }

    public String toString() {
        return toString("!=");
    }
}
