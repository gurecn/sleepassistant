package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.TrueExpr */
public class TrueExpr extends BooleanExpr {
    static final TrueExpr INSTANCE = new TrueExpr();

    private TrueExpr() {
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExpr
    public void accept(BooleanExprVisitor visitor) {
        visitor.visit(this);
    }

    public String toString() {
        return "";
    }
}
