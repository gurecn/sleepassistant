package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.PositionEqualsExpr */
public class PositionEqualsExpr extends BooleanExpr {
    private final int position_;

    public PositionEqualsExpr(int position) {
        this.position_ = position;
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExpr
    public void accept(BooleanExprVisitor visitor) throws XPathException {
        visitor.visit(this);
    }

    public int getPosition() {
        return this.position_;
    }

    public String toString() {
        return "[" + this.position_ + "]";
    }
}
