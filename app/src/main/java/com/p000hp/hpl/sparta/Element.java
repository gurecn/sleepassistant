package com.p000hp.hpl.sparta;

import com.p000hp.hpl.sparta.xpath.Step;
import com.p000hp.hpl.sparta.xpath.XPath;
import com.p000hp.hpl.sparta.xpath.XPathException;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* renamed from: com.hp.hpl.sparta.Element */
public class Element extends Node {
    private static final boolean DEBUG = false;
    private Vector attributeNames_ = null;
    private Hashtable attributes_ = null;
    private Node firstChild_ = null;
    private Node lastChild_ = null;
    private String tagName_ = null;

    public Element(String tagName) {
        this.tagName_ = Sparta.intern(tagName);
    }

    Element() {
    }

    @Override // com.p000hp.hpl.sparta.Node
    public Object clone() {
        return cloneElement(true);
    }

    public Element cloneShallow() {
        return cloneElement(false);
    }

    public Element cloneElement(boolean deep) {
        Element result = new Element(this.tagName_);
        if (this.attributeNames_ != null) {
            Enumeration i = this.attributeNames_.elements();
            while (i.hasMoreElements()) {
                String name = (String) i.nextElement();
                result.setAttribute(name, (String) this.attributes_.get(name));
            }
        }
        if (deep) {
            for (Node n = this.firstChild_; n != null; n = n.getNextSibling()) {
                result.appendChild((Node) n.clone());
            }
        }
        return result;
    }

    public String getTagName() {
        return this.tagName_;
    }

    public void setTagName(String tagName) {
        this.tagName_ = Sparta.intern(tagName);
        notifyObservers();
    }

    public Node getFirstChild() {
        return this.firstChild_;
    }

    public Node getLastChild() {
        return this.lastChild_;
    }

    public Enumeration getAttributeNames() {
        if (this.attributeNames_ == null) {
            return Document.EMPTY;
        }
        return this.attributeNames_.elements();
    }

    public String getAttribute(String name) {
        if (this.attributes_ == null) {
            return null;
        }
        return (String) this.attributes_.get(name);
    }

    public void setAttribute(String name, String value) {
        if (this.attributes_ == null) {
            this.attributes_ = new Hashtable();
            this.attributeNames_ = new Vector();
        }
        if (this.attributes_.get(name) == null) {
            this.attributeNames_.addElement(name);
        }
        this.attributes_.put(name, value);
        notifyObservers();
    }

    public void removeAttribute(String name) {
        if (this.attributes_ != null) {
            this.attributes_.remove(name);
            this.attributeNames_.removeElement(name);
            notifyObservers();
        }
    }

    /* access modifiers changed from: package-private */
    public void appendChildNoChecking(Node addedChild) {
        Element oldParent = addedChild.getParentNode();
        if (oldParent != null) {
            oldParent.removeChildNoChecking(addedChild);
        }
        addedChild.insertAtEndOfLinkedList(this.lastChild_);
        if (this.firstChild_ == null) {
            this.firstChild_ = addedChild;
        }
        addedChild.setParentNode(this);
        this.lastChild_ = addedChild;
        addedChild.setOwnerDocument(getOwnerDocument());
    }

    public void appendChild(Node addedChild) {
        if (!canHaveAsDescendent(addedChild)) {
            addedChild = (Element) addedChild.clone();
        }
        appendChildNoChecking(addedChild);
        notifyObservers();
    }

    /* access modifiers changed from: package-private */
    public boolean canHaveAsDescendent(Node node) {
        if (node == this) {
            return false;
        }
        Element parent = getParentNode();
        if (parent == null) {
            return true;
        }
        return parent.canHaveAsDescendent(node);
    }

    private boolean removeChildNoChecking(Node childToRemove) {
        int i = 0;
        for (Node child = this.firstChild_; child != null; child = child.getNextSibling()) {
            if (child.equals(childToRemove)) {
                if (this.firstChild_ == child) {
                    this.firstChild_ = child.getNextSibling();
                }
                if (this.lastChild_ == child) {
                    this.lastChild_ = child.getPreviousSibling();
                }
                child.removeFromLinkedList();
                child.setParentNode(null);
                child.setOwnerDocument(null);
                return true;
            }
            i++;
        }
        return false;
    }

    public void removeChild(Node childToRemove) throws DOMException {
        if (!removeChildNoChecking(childToRemove)) {
            throw new DOMException((short) 8, "Cannot find " + childToRemove + " in " + this);
        }
        notifyObservers();
    }

    public void replaceChild(Element newChild, Node oldChild) throws DOMException {
        replaceChild_(newChild, oldChild);
        notifyObservers();
    }

    public void replaceChild(Text newChild, Node oldChild) throws DOMException {
        replaceChild_(newChild, oldChild);
        notifyObservers();
    }

    private void replaceChild_(Node newChild, Node oldChild) throws DOMException {
        int i = 0;
        for (Node child = this.firstChild_; child != null; child = child.getNextSibling()) {
            if (child == oldChild) {
                if (this.firstChild_ == oldChild) {
                    this.firstChild_ = newChild;
                }
                if (this.lastChild_ == oldChild) {
                    this.lastChild_ = newChild;
                }
                oldChild.replaceInLinkedList(newChild);
                newChild.setParentNode(this);
                oldChild.setParentNode(null);
                return;
            }
            i++;
        }
        throw new DOMException((short) 8, "Cannot find " + oldChild + " in " + this);
    }

    /* access modifiers changed from: package-private */
    @Override // com.p000hp.hpl.sparta.Node
    public void toString(Writer writer) throws IOException {
        for (Node i = this.firstChild_; i != null; i = i.getNextSibling()) {
            i.toString(writer);
        }
    }

    @Override // com.p000hp.hpl.sparta.Node
    public void toXml(Writer writer) throws IOException {
        writer.write("<" + this.tagName_);
        if (this.attributeNames_ != null) {
            Enumeration i = this.attributeNames_.elements();
            while (i.hasMoreElements()) {
                String name = (String) i.nextElement();
                writer.write(" " + name + "=\"");
                htmlEncode(writer, (String) this.attributes_.get(name));
                writer.write("\"");
            }
        }
        if (this.firstChild_ == null) {
            writer.write("/>");
            return;
        }
        writer.write(">");
        for (Node i2 = this.firstChild_; i2 != null; i2 = i2.getNextSibling()) {
            i2.toXml(writer);
        }
        writer.write("</" + this.tagName_ + ">");
    }

    private XPathVisitor visitor(String xpath, boolean expectStringValue) throws XPathException {
        XPath parseTree = XPath.get(xpath);
        if (parseTree.isStringValue() == expectStringValue) {
            return new XPathVisitor(this, parseTree);
        }
        throw new XPathException(parseTree, "\"" + parseTree + "\" evaluates to " + (expectStringValue ? "evaluates to element not string" : "evaluates to string not element"));
    }

    @Override // com.p000hp.hpl.sparta.Node
    public Enumeration xpathSelectElements(String xpath) throws ParseException {
        try {
            return visitor(xpath, false).getResultEnumeration();
        } catch (XPathException e) {
            throw new ParseException("XPath problem", e);
        }
    }

    @Override // com.p000hp.hpl.sparta.Node
    public Enumeration xpathSelectStrings(String xpath) throws ParseException {
        try {
            return visitor(xpath, true).getResultEnumeration();
        } catch (XPathException e) {
            throw new ParseException("XPath problem", e);
        }
    }

    public boolean xpathEnsure(String xpath) throws ParseException {
        Element parent;
        try {
            if (xpathSelectElement(xpath) != null) {
                return false;
            }
            XPath parseTree = XPath.get(xpath);
            int stepCount = 0;
            Enumeration i = parseTree.getSteps();
            while (i.hasMoreElements()) {
                i.nextElement();
                stepCount++;
            }
            Step[] parentSteps = new Step[(stepCount - 1)];
            Enumeration i2 = parseTree.getSteps();
            for (int j = 0; j < parentSteps.length; j++) {
                parentSteps[j] = (Step) i2.nextElement();
            }
            Step lastStep = (Step) i2.nextElement();
            if (parentSteps.length == 0) {
                parent = this;
            } else {
                String parentXPath = XPath.get(parseTree.isAbsolute(), parentSteps).toString();
                xpathEnsure(parentXPath.toString());
                parent = xpathSelectElement(parentXPath);
            }
            parent.appendChildNoChecking(makeMatching(parent, lastStep, xpath));
            return true;
        } catch (XPathException e) {
            throw new ParseException(xpath, e);
        }
    }

    @Override // com.p000hp.hpl.sparta.Node
    public Element xpathSelectElement(String xpath) throws ParseException {
        try {
            return visitor(xpath, false).getFirstResultElement();
        } catch (XPathException e) {
            throw new ParseException("XPath problem", e);
        }
    }

    @Override // com.p000hp.hpl.sparta.Node
    public String xpathSelectString(String xpath) throws ParseException {
        try {
            return visitor(xpath, true).getFirstResultString();
        } catch (XPathException e) {
            throw new ParseException("XPath problem", e);
        }
    }

    public boolean equals(Object thatO) {
        int thatAttrCount;
        if (this == thatO) {
            return true;
        }
        if (!(thatO instanceof Element)) {
            return false;
        }
        Element that = (Element) thatO;
        if (!this.tagName_.equals(that.tagName_)) {
            return false;
        }
        int thisAttrCount = this.attributes_ == null ? 0 : this.attributes_.size();
        if (that.attributes_ == null) {
            thatAttrCount = 0;
        } else {
            thatAttrCount = that.attributes_.size();
        }
        if (thisAttrCount != thatAttrCount) {
            return false;
        }
        if (this.attributes_ != null) {
            Enumeration i = this.attributes_.keys();
            while (i.hasMoreElements()) {
                String key = (String) i.nextElement();
                if (!((String) this.attributes_.get(key)).equals((String) that.attributes_.get(key))) {
                    return false;
                }
            }
        }
        Node thisChild = this.firstChild_;
        Node thatChild = that.firstChild_;
        while (thisChild != null) {
            if (!thisChild.equals(thatChild)) {
                return false;
            }
            thisChild = thisChild.getNextSibling();
            thatChild = thatChild.getNextSibling();
        }
        return true;
    }

    /* access modifiers changed from: protected */
    @Override // com.p000hp.hpl.sparta.Node
    public int computeHashCode() {
        int hash = this.tagName_.hashCode();
        if (this.attributes_ != null) {
            Enumeration i = this.attributes_.keys();
            while (i.hasMoreElements()) {
                String key = (String) i.nextElement();
                hash = (((hash * 31) + key.hashCode()) * 31) + ((String) this.attributes_.get(key)).hashCode();
            }
        }
        for (Node i2 = this.firstChild_; i2 != null; i2 = i2.getNextSibling()) {
            hash = (hash * 31) + i2.hashCode();
        }
        return hash;
    }

    private void checkInvariant() {
    }
}
