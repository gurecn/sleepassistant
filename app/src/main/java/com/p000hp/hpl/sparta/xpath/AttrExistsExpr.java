package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.AttrExistsExpr */
public class AttrExistsExpr extends AttrExpr {
    AttrExistsExpr(String attrName) {
        super(attrName);
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExpr
    public void accept(BooleanExprVisitor visitor) throws XPathException {
        visitor.visit(this);
    }

    @Override // com.p000hp.hpl.sparta.xpath.AttrExpr
    public String toString() {
        return "[" + super.toString() + "]";
    }
}
