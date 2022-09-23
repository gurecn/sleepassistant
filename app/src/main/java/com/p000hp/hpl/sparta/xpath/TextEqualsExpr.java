package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.TextEqualsExpr */
public class TextEqualsExpr extends TextCompareExpr {
    TextEqualsExpr(String value) {
        super(value);
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExpr
    public void accept(BooleanExprVisitor visitor) throws XPathException {
        visitor.visit(this);
    }

    public String toString() {
        return toString("=");
    }
}
