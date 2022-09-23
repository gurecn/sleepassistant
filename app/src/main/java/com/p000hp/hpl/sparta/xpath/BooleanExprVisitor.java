package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.BooleanExprVisitor */
public interface BooleanExprVisitor {
    void visit(AttrEqualsExpr attrEqualsExpr) throws XPathException;

    void visit(AttrExistsExpr attrExistsExpr) throws XPathException;

    void visit(AttrGreaterExpr attrGreaterExpr) throws XPathException;

    void visit(AttrLessExpr attrLessExpr) throws XPathException;

    void visit(AttrNotEqualsExpr attrNotEqualsExpr) throws XPathException;

    void visit(PositionEqualsExpr positionEqualsExpr) throws XPathException;

    void visit(TextEqualsExpr textEqualsExpr) throws XPathException;

    void visit(TextExistsExpr textExistsExpr) throws XPathException;

    void visit(TextNotEqualsExpr textNotEqualsExpr) throws XPathException;

    void visit(TrueExpr trueExpr);
}
