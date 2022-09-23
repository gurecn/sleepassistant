package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.AttrGreaterExpr */
public class AttrGreaterExpr extends AttrRelationalExpr {
    public AttrGreaterExpr(String attrName, int attrValue) {
        super(attrName, attrValue);
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExpr
    public void accept(BooleanExprVisitor visitor) throws XPathException {
        visitor.visit(this);
    }

    @Override // com.p000hp.hpl.sparta.xpath.AttrExpr
    public String toString() {
        return toString(">");
    }
}
