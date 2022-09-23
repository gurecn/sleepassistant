package com.p000hp.hpl.sparta.xpath;

/* renamed from: com.hp.hpl.sparta.xpath.NodeTestVisitor */
public interface NodeTestVisitor {
    void visit(AllElementTest allElementTest);

    void visit(AttrTest attrTest);

    void visit(ElementTest elementTest);

    void visit(ParentNodeTest parentNodeTest) throws XPathException;

    void visit(TextTest textTest);

    void visit(ThisNodeTest thisNodeTest);
}
