package com.p000hp.hpl.sparta;

import com.p000hp.hpl.sparta.Sparta;
import com.p000hp.hpl.sparta.xpath.Step;
import com.p000hp.hpl.sparta.xpath.XPath;
import com.p000hp.hpl.sparta.xpath.XPathException;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* renamed from: com.hp.hpl.sparta.Document */
public class Document extends Node {
    private static final boolean DEBUG = false;
    static final Enumeration EMPTY = new EmptyEnumeration();
    private static final Integer ONE = new Integer(1);
    private final Hashtable indexible_;
    private Sparta.Cache indices_;
    private Vector observers_;
    private Element rootElement_;
    private String systemId_;

    /* renamed from: com.hp.hpl.sparta.Document$Observer */
    public interface Observer {
        void update(Document document);
    }

    Document(String systemId) {
        this.rootElement_ = null;
        this.indices_ = Sparta.newCache();
        this.observers_ = new Vector();
        this.indexible_ = null;
        this.systemId_ = systemId;
    }

    public Document() {
        this.rootElement_ = null;
        this.indices_ = Sparta.newCache();
        this.observers_ = new Vector();
        this.indexible_ = null;
        this.systemId_ = "MEMORY";
    }

    @Override // com.p000hp.hpl.sparta.Node
    public Object clone() {
        Document copy = new Document(this.systemId_);
        copy.rootElement_ = (Element) this.rootElement_.clone();
        return copy;
    }

    public String getSystemId() {
        return this.systemId_;
    }

    public void setSystemId(String systemId) {
        this.systemId_ = systemId;
        notifyObservers();
    }

    @Override // com.p000hp.hpl.sparta.Node
    public String toString() {
        return this.systemId_;
    }

    public Element getDocumentElement() {
        return this.rootElement_;
    }

    public void setDocumentElement(Element rootElement) {
        this.rootElement_ = rootElement;
        this.rootElement_.setOwnerDocument(this);
        notifyObservers();
    }

    private XPathVisitor visitor(String xpath, boolean expectStringValue) throws XPathException {
        if (xpath.charAt(0) != '/') {
            xpath = "/" + xpath;
        }
        return visitor(XPath.get(xpath), expectStringValue);
    }

    /* access modifiers changed from: package-private */
    public XPathVisitor visitor(XPath parseTree, boolean expectStringValue) throws XPathException {
        if (parseTree.isStringValue() == expectStringValue) {
            return new XPathVisitor(this, parseTree);
        }
        throw new XPathException(parseTree, "\"" + parseTree + "\" evaluates to " + (expectStringValue ? "evaluates to element not string" : "evaluates to string not element"));
    }

    @Override // com.p000hp.hpl.sparta.Node
    public Enumeration xpathSelectElements(String xpath) throws ParseException {
        try {
            if (xpath.charAt(0) != '/') {
                xpath = "/" + xpath;
            }
            XPath parseTree = XPath.get(xpath);
            monitor(parseTree);
            return visitor(parseTree, false).getResultEnumeration();
        } catch (XPathException e) {
            throw new ParseException("XPath problem", e);
        }
    }

    /* access modifiers changed from: package-private */
    public void monitor(XPath parseTree) throws XPathException {
    }

    @Override // com.p000hp.hpl.sparta.Node
    public Enumeration xpathSelectStrings(String xpath) throws ParseException {
        try {
            return visitor(xpath, true).getResultEnumeration();
        } catch (XPathException e) {
            throw new ParseException("XPath problem", e);
        }
    }

    @Override // com.p000hp.hpl.sparta.Node
    public Element xpathSelectElement(String xpath) throws ParseException {
        try {
            if (xpath.charAt(0) != '/') {
                xpath = "/" + xpath;
            }
            XPath parseTree = XPath.get(xpath);
            monitor(parseTree);
            return visitor(parseTree, false).getFirstResultElement();
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

    public boolean xpathEnsure(String xpath) throws ParseException {
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
            Enumeration i2 = parseTree.getSteps();
            Step firstStep = (Step) i2.nextElement();
            Step[] rootElemSteps = new Step[(stepCount - 1)];
            for (int j = 0; j < rootElemSteps.length; j++) {
                rootElemSteps[j] = (Step) i2.nextElement();
            }
            if (this.rootElement_ == null) {
                setDocumentElement(makeMatching(null, firstStep, xpath));
            } else if (xpathSelectElement("/" + firstStep) == null) {
                throw new ParseException("Existing root element <" + this.rootElement_.getTagName() + "...> does not match first step \"" + firstStep + "\" of \"" + xpath);
            }
            if (rootElemSteps.length == 0) {
                return true;
            }
            return this.rootElement_.xpathEnsure(XPath.get(false, rootElemSteps).toString());
        } catch (XPathException e) {
            throw new ParseException(xpath, e);
        }
    }

    /* renamed from: com.hp.hpl.sparta.Document$Index */
    public class Index implements Observer {
        private final String attrName_;
        private transient Sparta.Cache dict_ = null;
        private final XPath xpath_;

        Index(XPath xpath) throws XPathException {
            this.attrName_ = xpath.getIndexingAttrName();
            this.xpath_ = xpath;
            Document.this.addObserver(this);
        }

        public synchronized Enumeration get(String attrValue) throws ParseException {
            Vector elemList;
            if (this.dict_ == null) {
                regenerate();
            }
            elemList = (Vector) this.dict_.get(attrValue);
            return elemList == null ? Document.EMPTY : elemList.elements();
        }

        public synchronized int size() throws ParseException {
            if (this.dict_ == null) {
                regenerate();
            }
            return this.dict_.size();
        }

        @Override // com.p000hp.hpl.sparta.Document.Observer
        public synchronized void update(Document doc) {
            this.dict_ = null;
        }

        private void regenerate() throws ParseException {
            try {
                this.dict_ = Sparta.newCache();
                Enumeration i = Document.this.visitor(this.xpath_, false).getResultEnumeration();
                while (i.hasMoreElements()) {
                    Element elem = (Element) i.nextElement();
                    String attrValue = elem.getAttribute(this.attrName_);
                    Vector elemList = (Vector) this.dict_.get(attrValue);
                    if (elemList == null) {
                        elemList = new Vector(1);
                        this.dict_.put(attrValue, elemList);
                    }
                    elemList.addElement(elem);
                }
            } catch (XPathException e) {
                throw new ParseException("XPath problem", e);
            }
        }
    }

    public boolean xpathHasIndex(String xpath) {
        return this.indices_.get(xpath) != null;
    }

    public Index xpathGetIndex(String xpath) throws ParseException {
        try {
            Index index = (Index) this.indices_.get(xpath);
            if (index != null) {
                return index;
            }
            Index index2 = new Index(XPath.get(xpath));
            this.indices_.put(xpath, index2);
            return index2;
        } catch (XPathException e) {
            throw new ParseException("XPath problem", e);
        }
    }

    public void addObserver(Observer observer) {
        this.observers_.addElement(observer);
    }

    public void deleteObserver(Observer observer) {
        this.observers_.removeElement(observer);
    }

    @Override // com.p000hp.hpl.sparta.Node
    public void toString(Writer writer) throws IOException {
        this.rootElement_.toString(writer);
    }

    /* access modifiers changed from: package-private */
    @Override // com.p000hp.hpl.sparta.Node
    public void notifyObservers() {
        Enumeration i = this.observers_.elements();
        while (i.hasMoreElements()) {
            ((Observer) i.nextElement()).update(this);
        }
    }

    @Override // com.p000hp.hpl.sparta.Node
    public void toXml(Writer writer) throws IOException {
        writer.write("<?xml version=\"1.0\" ?>\n");
        this.rootElement_.toXml(writer);
    }

    public boolean equals(Object thatO) {
        if (this == thatO) {
            return true;
        }
        if (!(thatO instanceof Document)) {
            return false;
        }
        return this.rootElement_.equals(((Document) thatO).rootElement_);
    }

    /* access modifiers changed from: protected */
    @Override // com.p000hp.hpl.sparta.Node
    public int computeHashCode() {
        return this.rootElement_.hashCode();
    }
}
