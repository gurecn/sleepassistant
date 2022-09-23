package com.p000hp.hpl.sparta.xpath;

import java.io.IOException;

/* renamed from: com.hp.hpl.sparta.xpath.Step */
public class Step {
    public static Step DOT = new Step(ThisNodeTest.INSTANCE, TrueExpr.INSTANCE);
    private final boolean multiLevel_;
    private final NodeTest nodeTest_;
    private final BooleanExpr predicate_;

    Step(NodeTest nodeTest, BooleanExpr predicate) {
        this.nodeTest_ = nodeTest;
        this.predicate_ = predicate;
        this.multiLevel_ = false;
    }

    Step(XPath xpath, boolean multiLevel, SimpleStreamTokenizer toks) throws XPathException, IOException {
        this.multiLevel_ = multiLevel;
        switch (toks.ttype) {
            case SimpleStreamTokenizer.TT_WORD:
                if (toks.sval.equals("text")) {
                    if (toks.nextToken() == 40 && toks.nextToken() == 41) {
                        this.nodeTest_ = TextTest.INSTANCE;
                        break;
                    } else {
                        throw new XPathException(xpath, "after text", toks, "()");
                    }
                } else {
                    this.nodeTest_ = new ElementTest(toks.sval);
                    break;
                }
            case 42:
                this.nodeTest_ = AllElementTest.INSTANCE;
                break;
            case 46:
                if (toks.nextToken() != 46) {
                    toks.pushBack();
                    this.nodeTest_ = ThisNodeTest.INSTANCE;
                    break;
                } else {
                    this.nodeTest_ = ParentNodeTest.INSTANCE;
                    break;
                }
            case 64:
                if (toks.nextToken() == -3) {
                    this.nodeTest_ = new AttrTest(toks.sval);
                    break;
                } else {
                    throw new XPathException(xpath, "after @ in node test", toks, "name");
                }
            default:
                throw new XPathException(xpath, "at begininning of step", toks, "'.' or '*' or name");
        }
        if (toks.nextToken() == 91) {
            toks.nextToken();
            this.predicate_ = ExprFactory.createExpr(xpath, toks);
            if (toks.ttype != 93) {
                throw new XPathException(xpath, "after predicate expression", toks, "]");
            }
            toks.nextToken();
            return;
        }
        this.predicate_ = TrueExpr.INSTANCE;
    }

    public String toString() {
        return this.nodeTest_.toString() + this.predicate_.toString();
    }

    public boolean isMultiLevel() {
        return this.multiLevel_;
    }

    public boolean isStringValue() {
        return this.nodeTest_.isStringValue();
    }

    public NodeTest getNodeTest() {
        return this.nodeTest_;
    }

    public BooleanExpr getPredicate() {
        return this.predicate_;
    }
}
