package com.p000hp.hpl.sparta;

import com.p000hp.hpl.sparta.xpath.AttrEqualsExpr;
import com.p000hp.hpl.sparta.xpath.AttrExistsExpr;
import com.p000hp.hpl.sparta.xpath.AttrGreaterExpr;
import com.p000hp.hpl.sparta.xpath.AttrLessExpr;
import com.p000hp.hpl.sparta.xpath.AttrNotEqualsExpr;
import com.p000hp.hpl.sparta.xpath.BooleanExprVisitor;
import com.p000hp.hpl.sparta.xpath.ElementTest;
import com.p000hp.hpl.sparta.xpath.NodeTest;
import com.p000hp.hpl.sparta.xpath.PositionEqualsExpr;
import com.p000hp.hpl.sparta.xpath.Step;
import com.p000hp.hpl.sparta.xpath.TextEqualsExpr;
import com.p000hp.hpl.sparta.xpath.TextExistsExpr;
import com.p000hp.hpl.sparta.xpath.TextNotEqualsExpr;
import com.p000hp.hpl.sparta.xpath.TrueExpr;
import com.p000hp.hpl.sparta.xpath.XPath;
import com.p000hp.hpl.sparta.xpath.XPathException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;

/* renamed from: com.hp.hpl.sparta.Node */
public abstract class Node {
    private Object annotation_ = null;
    private Document doc_ = null;
    private int hash_ = 0;
    private Node nextSibling_ = null;
    private Element parentNode_ = null;
    private Node previousSibling_ = null;

    public abstract Object clone();

    /* access modifiers changed from: protected */
    public abstract int computeHashCode();

    /* access modifiers changed from: package-private */
    public abstract void toString(Writer writer) throws IOException;

    /* access modifiers changed from: package-private */
    public abstract void toXml(Writer writer) throws IOException;

    public abstract Element xpathSelectElement(String str) throws ParseException;

    public abstract Enumeration xpathSelectElements(String str) throws ParseException;

    public abstract String xpathSelectString(String str) throws ParseException;

    public abstract Enumeration xpathSelectStrings(String str) throws ParseException;

    /* access modifiers changed from: package-private */
    public void notifyObservers() {
        this.hash_ = 0;
        if (this.doc_ != null) {
            this.doc_.notifyObservers();
        }
    }

    /* access modifiers changed from: package-private */
    public void setOwnerDocument(Document doc) {
        this.doc_ = doc;
    }

    public Document getOwnerDocument() {
        return this.doc_;
    }

    public Element getParentNode() {
        return this.parentNode_;
    }

    public Node getPreviousSibling() {
        return this.previousSibling_;
    }

    public Node getNextSibling() {
        return this.nextSibling_;
    }

    public Object getAnnotation() {
        return this.annotation_;
    }

    public void setAnnotation(Object annotation) {
        this.annotation_ = annotation;
    }

    /* access modifiers changed from: package-private */
    public void setParentNode(Element parentNode) {
        this.parentNode_ = parentNode;
    }

    /* access modifiers changed from: package-private */
    public void insertAtEndOfLinkedList(Node lastChild) {
        this.previousSibling_ = lastChild;
        if (lastChild != null) {
            lastChild.nextSibling_ = this;
        }
    }

    /* access modifiers changed from: package-private */
    public void removeFromLinkedList() {
        if (this.previousSibling_ != null) {
            this.previousSibling_.nextSibling_ = this.nextSibling_;
        }
        if (this.nextSibling_ != null) {
            this.nextSibling_.previousSibling_ = this.previousSibling_;
        }
        this.nextSibling_ = null;
        this.previousSibling_ = null;
    }

    /* access modifiers changed from: package-private */
    public void replaceInLinkedList(Node replacement) {
        if (this.previousSibling_ != null) {
            this.previousSibling_.nextSibling_ = replacement;
        }
        if (this.nextSibling_ != null) {
            this.nextSibling_.previousSibling_ = replacement;
        }
        replacement.nextSibling_ = this.nextSibling_;
        replacement.previousSibling_ = this.previousSibling_;
        this.nextSibling_ = null;
        this.previousSibling_ = null;
    }

    public String toXml() throws IOException {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        Writer w = new OutputStreamWriter(o);
        toXml(w);
        w.flush();
        return new String(o.toByteArray());
    }

    public boolean xpathSetStrings(String xpath, String value) throws ParseException {
        boolean changed = false;
        try {
            int slash = xpath.lastIndexOf(47);
            if (xpath.substring(slash + 1).equals("text()") || xpath.charAt(slash + 1) == '@') {
                String elemXPath = xpath.substring(0, slash);
                if (xpath.charAt(slash + 1) == '@') {
                    String attrName = xpath.substring(slash + 2);
                    if (attrName.length() == 0) {
                        throw new ParseException("Xpath expression \"" + xpath + "\" specifies zero-length attribute name\"");
                    }
                    Enumeration i = xpathSelectElements(elemXPath);
                    while (i.hasMoreElements()) {
                        Element element = (Element) i.nextElement();
                        if (!value.equals(element.getAttribute(attrName))) {
                            element.setAttribute(attrName, value);
                            changed = true;
                        }
                    }
                } else {
                    Enumeration i2 = xpathSelectElements(elemXPath);
                    changed = i2.hasMoreElements();
                    while (i2.hasMoreElements()) {
                        Element parentOfText = (Element) i2.nextElement();
                        Vector textNodes = new Vector();
                        for (Node j = parentOfText.getFirstChild(); j != null; j = j.getNextSibling()) {
                            if (j instanceof Text) {
                                textNodes.addElement((Text) j);
                            }
                        }
                        if (textNodes.size() == 0) {
                            Text text = new Text(value);
                            if (text.getData().length() > 0) {
                                parentOfText.appendChild(text);
                                changed = true;
                            }
                        } else {
                            Text first = (Text) textNodes.elementAt(0);
                            if (!first.getData().equals(value)) {
                                textNodes.removeElementAt(0);
                                first.setData(value);
                                changed = true;
                            }
                            for (int j2 = 0; j2 < textNodes.size(); j2++) {
                                parentOfText.removeChild((Text) textNodes.elementAt(j2));
                                changed = true;
                            }
                        }
                    }
                }
                return changed;
            }
            throw new ParseException("Last step of Xpath expression \"" + xpath + "\" is not \"text()\" and does not start with a '@'. It starts with a '" + xpath.charAt(slash + 1) + "'");
        } catch (DOMException e) {
            throw new Error("Assertion failed " + e);
        } catch (IndexOutOfBoundsException e2) {
            throw new ParseException("Xpath expression \"" + xpath + "\" is not in the form \"xpathExpression/@attributeName\"");
        }
    }

    /* access modifiers changed from: package-private */
    public Element makeMatching(final Element parent, Step step, final String msgContext) throws ParseException, XPathException {
        NodeTest nodeTest = step.getNodeTest();
        if (!(nodeTest instanceof ElementTest)) {
            throw new ParseException("\"" + nodeTest + "\" in \"" + msgContext + "\" is not an element test");
        }
        final String tagName = ((ElementTest) nodeTest).getTagName();
        final Element newChild = new Element(tagName);
        step.getPredicate().accept(new BooleanExprVisitor() {
            /* class com.p000hp.hpl.sparta.Node.C00001 */

            @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
            public void visit(TrueExpr a) {
            }

            @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
            public void visit(AttrExistsExpr a) throws XPathException {
                newChild.setAttribute(a.getAttrName(), "something");
            }

            @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
            public void visit(AttrEqualsExpr a) throws XPathException {
                newChild.setAttribute(a.getAttrName(), a.getAttrValue());
            }

            @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
            public void visit(AttrNotEqualsExpr a) throws XPathException {
                newChild.setAttribute(a.getAttrName(), "not " + a.getAttrValue());
            }

            @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
            public void visit(AttrLessExpr a) throws XPathException {
                newChild.setAttribute(a.getAttrName(), Long.toString(Long.MIN_VALUE));
            }

            @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
            public void visit(AttrGreaterExpr a) throws XPathException {
                newChild.setAttribute(a.getAttrName(), Long.toString(Long.MAX_VALUE));
            }

            @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
            public void visit(TextExistsExpr a) throws XPathException {
                newChild.appendChild(new Text("something"));
            }

            @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
            public void visit(TextEqualsExpr a) throws XPathException {
                newChild.appendChild(new Text(a.getValue()));
            }

            @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
            public void visit(TextNotEqualsExpr a) throws XPathException {
                newChild.appendChild(new Text("not " + a.getValue()));
            }

            @Override // com.p000hp.hpl.sparta.xpath.BooleanExprVisitor
            public void visit(PositionEqualsExpr a) throws XPathException {
                int posn = a.getPosition();
                if (parent != null || posn == 1) {
                    for (int lastPosition = 1; lastPosition < posn; lastPosition++) {
                        parent.appendChild(new Element(tagName));
                    }
                    return;
                }
                throw new XPathException(XPath.get(msgContext), "Position of root node must be 1");
            }
        });
        return newChild;
    }

    public String toString() {
        try {
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            Writer w = new OutputStreamWriter(o);
            toString(w);
            w.flush();
            return new String(o.toByteArray());
        } catch (IOException e) {
            return super.toString();
        }
    }

    protected static void htmlEncode(Writer writer, String string) throws IOException {
        String encoded;
        int n = string.length();
        int writeNext = 0;
        for (int i = 0; i < n; i++) {
            int ch = string.charAt(i);
            if (ch < 128) {
                switch (ch) {
                    case 34:
                        encoded = "&quot;";
                        break;
                    case 38:
                        encoded = "&amp;";
                        break;
                    case 39:
                        encoded = "&#39;";
                        break;
                    case 60:
                        encoded = "&lt;";
                        break;
                    case 62:
                        encoded = "&gt;";
                        break;
                    default:
                        encoded = null;
                        break;
                }
            } else {
                encoded = "&#" + ch + ";";
            }
            if (encoded != null) {
                writer.write(string, writeNext, i - writeNext);
                writer.write(encoded);
                writeNext = i + 1;
            }
        }
        if (writeNext < n) {
            writer.write(string, writeNext, n - writeNext);
        }
    }

    public int hashCode() {
        if (this.hash_ == 0) {
            this.hash_ = computeHashCode();
        }
        return this.hash_;
    }
}
