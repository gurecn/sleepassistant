package com.p000hp.hpl.sparta;

import com.p000hp.hpl.sparta.xpath.AllElementTest;
import com.p000hp.hpl.sparta.xpath.AttrEqualsExpr;
import com.p000hp.hpl.sparta.xpath.AttrExistsExpr;
import com.p000hp.hpl.sparta.xpath.AttrGreaterExpr;
import com.p000hp.hpl.sparta.xpath.AttrLessExpr;
import com.p000hp.hpl.sparta.xpath.AttrNotEqualsExpr;
import com.p000hp.hpl.sparta.xpath.AttrTest;
import com.p000hp.hpl.sparta.xpath.BooleanExpr;
import com.p000hp.hpl.sparta.xpath.ElementTest;
import com.p000hp.hpl.sparta.xpath.ParentNodeTest;
import com.p000hp.hpl.sparta.xpath.PositionEqualsExpr;
import com.p000hp.hpl.sparta.xpath.Step;
import com.p000hp.hpl.sparta.xpath.TextEqualsExpr;
import com.p000hp.hpl.sparta.xpath.TextExistsExpr;
import com.p000hp.hpl.sparta.xpath.TextNotEqualsExpr;
import com.p000hp.hpl.sparta.xpath.TextTest;
import com.p000hp.hpl.sparta.xpath.ThisNodeTest;
import com.p000hp.hpl.sparta.xpath.TrueExpr;
import com.p000hp.hpl.sparta.xpath.Visitor;
import com.p000hp.hpl.sparta.xpath.XPath;
import com.p000hp.hpl.sparta.xpath.XPathException;
import java.util.Enumeration;
import java.util.Vector;

/* access modifiers changed from: package-private */
/* renamed from: com.hp.hpl.sparta.XPathVisitor */
public class XPathVisitor implements Visitor {
    private static final Boolean FALSE = new Boolean(false);
    private static final Boolean TRUE = new Boolean(true);
    private Node contextNode_;
    private final BooleanStack exprStack_;
    private boolean multiLevel_;
    private Object node_;
    private Vector nodelistFiltered_;
    private final NodeListWithPosition nodelistRaw_;
    private Enumeration nodesetIterator_;
    private XPath xpath_;

    private XPathVisitor(XPath xpath, Node context) throws XPathException {
        this.nodelistRaw_ = new NodeListWithPosition();
        this.nodelistFiltered_ = new Vector();
        this.nodesetIterator_ = null;
        this.node_ = null;
        this.exprStack_ = new BooleanStack();
        this.xpath_ = xpath;
        this.contextNode_ = context;
        this.nodelistFiltered_ = new Vector(1);
        this.nodelistFiltered_.addElement(this.contextNode_);
        Enumeration i = xpath.getSteps();
        while (i.hasMoreElements()) {
            Step step = (Step) i.nextElement();
            this.multiLevel_ = step.isMultiLevel();
            this.nodesetIterator_ = null;
            step.getNodeTest().accept(this);
            this.nodesetIterator_ = this.nodelistRaw_.iterator();
            this.nodelistFiltered_.removeAllElements();
            BooleanExpr predicate = step.getPredicate();
            while (this.nodesetIterator_.hasMoreElements()) {
                this.node_ = this.nodesetIterator_.nextElement();
                predicate.accept(this);
                if (this.exprStack_.pop().booleanValue()) {
                    this.nodelistFiltered_.addElement(this.node_);
                }
            }
        }
    }

    public XPathVisitor(Element context, XPath xpath) throws XPathException {
        this(xpath, context);
        if (xpath.isAbsolute()) {
            throw new XPathException(xpath, "Cannot use element as context node for absolute xpath");
        }
    }

    public XPathVisitor(Document context, XPath xpath) throws XPathException {
        this(xpath, context);
    }

    @Override // com.p000hp.hpl.sparta.xpath.NodeTestVisitor
    public void visit(ThisNodeTest a) {
        this.nodelistRaw_.removeAllElements();
        this.nodelistRaw_.add(this.contextNode_, 1);
    }

    @Override // com.p000hp.hpl.sparta.xpath.NodeTestVisitor
    public void visit(ParentNodeTest a) throws XPathException {
        this.nodelistRaw_.removeAllElements();
        Node parent = this.contextNode_.getParentNode();
        if (parent == null) {
            throw new XPathException(this.xpath_, "Illegal attempt to apply \"..\" to node with no parent.");
        }
        this.nodelistRaw_.add(parent, 1);
    }

    @Override // com.p000hp.hpl.sparta.xpath.NodeTestVisitor
    public void visit(AllElementTest a) {
        Vector oldNodeList = this.nodelistFiltered_;
        this.nodelistRaw_.removeAllElements();
        Enumeration i = oldNodeList.elements();
        while (i.hasMoreElements()) {
            Object node = i.nextElement();
            if (node instanceof Element) {
                accumulateElements((Element) node);
            } else if (node instanceof Document) {
                accumulateElements((Document) node);
            }
        }
    }

    private void accumulateElements(Document doc) {
        Element child = doc.getDocumentElement();
        this.nodelistRaw_.add(child, 1);
        if (this.multiLevel_) {
            accumulateElements(child);
        }
    }

    private void accumulateElements(Element element) {
        int position = 0;
        for (Node n = element.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Element) {
                position++;
                this.nodelistRaw_.add(n, position);
                if (this.multiLevel_) {
                    accumulateElements((Element) n);
                }
            }
        }
    }

    @Override // com.p000hp.hpl.sparta.xpath.NodeTestVisitor
    public void visit(TextTest a) {
        Vector oldNodeList = this.nodelistFiltered_;
        this.nodelistRaw_.removeAllElements();
        Enumeration i = oldNodeList.elements();
        while (i.hasMoreElements()) {
            Object node = i.nextElement();
            if (node instanceof Element) {
                for (Node n = ((Element) node).getFirstChild(); n != null; n = n.getNextSibling()) {
                    if (n instanceof Text) {
                        this.nodelistRaw_.add(((Text) n).getData());
                    }
                }
            }
        }
    }

    @Override // com.p000hp.hpl.sparta.xpath.NodeTestVisitor
    public void visit(ElementTest test) {
        String tagName = test.getTagName();
        Vector oldNodeList = this.nodelistFiltered_;
        int n = oldNodeList.size();
        this.nodelistRaw_.removeAllElements();
        for (int i = 0; i < n; i++) {
            Object node = oldNodeList.elementAt(i);
            if (node instanceof Element) {
                accumulateMatchingElements((Element) node, tagName);
            } else if (node instanceof Document) {
                accumulateMatchingElements((Document) node, tagName);
            }
        }
    }

    private void accumulateMatchingElements(Document document, String tagName) {
        Element child = document.getDocumentElement();
        if (child != null) {
            if (child.getTagName() == tagName) {
                this.nodelistRaw_.add(child, 1);
            }
            if (this.multiLevel_) {
                accumulateMatchingElements(child, tagName);
            }
        }
    }

    private void accumulateMatchingElements(Element element, String tagName) {
        int position = 0;
        for (Node n = element.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Element) {
                Element child = (Element) n;
                if (child.getTagName() == tagName) {
                    position++;
                    this.nodelistRaw_.add(child, position);
                }
                if (this.multiLevel_) {
                    accumulateMatchingElements(child, tagName);
                }
            }
        }
    }

    @Override // com.p000hp.hpl.sparta.xpath.NodeTestVisitor
    public void visit(AttrTest test) {
        String attr;
        Vector oldNodeList = this.nodelistFiltered_;
        this.nodelistRaw_.removeAllElements();
        Enumeration i = oldNodeList.elements();
        while (i.hasMoreElements()) {
            Node node = (Node) i.nextElement();
            if ((node instanceof Element) && (attr = ((Element) node).getAttribute(test.getAttrName())) != null) {
                this.nodelistRaw_.add(attr);
            }
        }
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
    public void visit(TrueExpr a) {
        this.exprStack_.push(TRUE);
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
    public void visit(AttrExistsExpr a) throws XPathException {
        if (!(this.node_ instanceof Element)) {
            throw new XPathException(this.xpath_, "Cannot test attribute of document");
        }
        String attrValue = ((Element) this.node_).getAttribute(a.getAttrName());
        this.exprStack_.push(attrValue != null && attrValue.length() > 0 ? TRUE : FALSE);
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
    public void visit(AttrEqualsExpr a) throws XPathException {
        if (!(this.node_ instanceof Element)) {
            throw new XPathException(this.xpath_, "Cannot test attribute of document");
        }
        this.exprStack_.push(a.getAttrValue().equals(((Element) this.node_).getAttribute(a.getAttrName())) ? TRUE : FALSE);
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
    public void visit(AttrNotEqualsExpr a) throws XPathException {
        if (!(this.node_ instanceof Element)) {
            throw new XPathException(this.xpath_, "Cannot test attribute of document");
        }
        this.exprStack_.push(!a.getAttrValue().equals(((Element) this.node_).getAttribute(a.getAttrName())) ? TRUE : FALSE);
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
    public void visit(AttrLessExpr a) throws XPathException {
        if (!(this.node_ instanceof Element)) {
            throw new XPathException(this.xpath_, "Cannot test attribute of document");
        }
        this.exprStack_.push((((double) Long.parseLong(((Element) this.node_).getAttribute(a.getAttrName()))) > a.getAttrValue() ? 1 : (((double) Long.parseLong(((Element) this.node_).getAttribute(a.getAttrName()))) == a.getAttrValue() ? 0 : -1)) < 0 ? TRUE : FALSE);
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
    public void visit(AttrGreaterExpr a) throws XPathException {
        if (!(this.node_ instanceof Element)) {
            throw new XPathException(this.xpath_, "Cannot test attribute of document");
        }
        this.exprStack_.push((((double) Long.parseLong(((Element) this.node_).getAttribute(a.getAttrName()))) > a.getAttrValue() ? 1 : (((double) Long.parseLong(((Element) this.node_).getAttribute(a.getAttrName()))) == a.getAttrValue() ? 0 : -1)) > 0 ? TRUE : FALSE);
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
    public void visit(TextExistsExpr a) throws XPathException {
        if (!(this.node_ instanceof Element)) {
            throw new XPathException(this.xpath_, "Cannot test attribute of document");
        }
        for (Node i = ((Element) this.node_).getFirstChild(); i != null; i = i.getNextSibling()) {
            if (i instanceof Text) {
                this.exprStack_.push(TRUE);
                return;
            }
        }
        this.exprStack_.push(FALSE);
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
    public void visit(TextEqualsExpr a) throws XPathException {
        if (!(this.node_ instanceof Element)) {
            throw new XPathException(this.xpath_, "Cannot test attribute of document");
        }
        for (Node i = ((Element) this.node_).getFirstChild(); i != null; i = i.getNextSibling()) {
            if ((i instanceof Text) && ((Text) i).getData().equals(a.getValue())) {
                this.exprStack_.push(TRUE);
                return;
            }
        }
        this.exprStack_.push(FALSE);
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
    public void visit(TextNotEqualsExpr a) throws XPathException {
        if (!(this.node_ instanceof Element)) {
            throw new XPathException(this.xpath_, "Cannot test attribute of document");
        }
        for (Node i = ((Element) this.node_).getFirstChild(); i != null; i = i.getNextSibling()) {
            if ((i instanceof Text) && !((Text) i).getData().equals(a.getValue())) {
                this.exprStack_.push(TRUE);
                return;
            }
        }
        this.exprStack_.push(FALSE);
    }

    @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
    public void visit(PositionEqualsExpr a) throws XPathException {
        if (!(this.node_ instanceof Element)) {
            throw new XPathException(this.xpath_, "Cannot test position of document");
        }
        this.exprStack_.push(this.nodelistRaw_.position((Element) this.node_) == a.getPosition() ? TRUE : FALSE);
    }

    public Enumeration getResultEnumeration() {
        return this.nodelistFiltered_.elements();
    }

    public Element getFirstResultElement() {
        if (this.nodelistFiltered_.size() == 0) {
            return null;
        }
        return (Element) this.nodelistFiltered_.elementAt(0);
    }

    public String getFirstResultString() {
        if (this.nodelistFiltered_.size() == 0) {
            return null;
        }
        return this.nodelistFiltered_.elementAt(0).toString();
    }

    /* renamed from: com.hp.hpl.sparta.XPathVisitor$BooleanStack */
    private static class BooleanStack {
        private Item top_;

        private BooleanStack() {
            this.top_ = null;
        }

        /* access modifiers changed from: private */
        /* renamed from: com.hp.hpl.sparta.XPathVisitor$BooleanStack$Item */
        public static class Item {
            final Boolean bool;
            final Item prev;

            Item(Boolean b, Item p) {
                this.bool = b;
                this.prev = p;
            }
        }

        /* access modifiers changed from: package-private */
        public void push(Boolean b) {
            this.top_ = new Item(b, this.top_);
        }

        /* access modifiers changed from: package-private */
        public Boolean pop() {
            Boolean result = this.top_.bool;
            this.top_ = this.top_.prev;
            return result;
        }
    }
}
