package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.TextExistsExpr */
public class TextExistsExpr extends BooleanExpr {
    static final TextExistsExpr INSTANCE = new TextExistsExpr();

    private TextExistsExpr() {
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExpr
    public void accept(BooleanExprVisitor visitor) throws XPathException {
        visitor.visit(this);
    }

    public String toString() {
        return "[text()]";
    }
}
