package com.p000hp.hpl.sparta.xpath;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

/* renamed from: com.hp.hpl.sparta.xpath.XPath */
public class XPath {
    private static final int ASSERTION = 0;
    private static Hashtable cache_ = new Hashtable();
    private boolean absolute_;
    private Stack steps_;
    private String string_;

    private XPath(boolean isAbsolute, Step[] steps) {
        this.steps_ = new Stack();
        for (Step step : steps) {
            this.steps_.addElement(step);
        }
        this.absolute_ = isAbsolute;
        this.string_ = null;
    }

    private XPath(String s) throws XPathException {
        this(s, new InputStreamReader(new ByteArrayInputStream(s.getBytes())));
    }

    private XPath(String s, Reader reader) throws XPathException {
        boolean multiLevel;
        boolean multiLevel2;
        this.steps_ = new Stack();
        try {
            this.string_ = s;
            SimpleStreamTokenizer toks = new SimpleStreamTokenizer(reader);
            toks.ordinaryChar('/');
            toks.ordinaryChar('.');
            toks.wordChars(':', ':');
            toks.wordChars('_', '_');
            if (toks.nextToken() == 47) {
                this.absolute_ = true;
                if (toks.nextToken() == 47) {
                    multiLevel = true;
                    toks.nextToken();
                } else {
                    multiLevel = false;
                }
            } else {
                this.absolute_ = false;
                multiLevel = false;
            }
            this.steps_.push(new Step(this, multiLevel, toks));
            while (toks.ttype == 47) {
                if (toks.nextToken() == 47) {
                    multiLevel2 = true;
                    toks.nextToken();
                } else {
                    multiLevel2 = false;
                }
                this.steps_.push(new Step(this, multiLevel2, toks));
            }
            if (toks.ttype != -1) {
                throw new XPathException(this, "at end of XPATH expression", toks, "end of expression");
            }
        } catch (IOException e) {
            throw new XPathException(this, e);
        }
    }

    public String toString() {
        if (this.string_ == null) {
            this.string_ = generateString();
        }
        return this.string_;
    }

    private String generateString() {
        StringBuffer result = new StringBuffer();
        boolean first = true;
        Enumeration i = this.steps_.elements();
        while (i.hasMoreElements()) {
            Step step = (Step) i.nextElement();
            if (!first || this.absolute_) {
                result.append('/');
                if (step.isMultiLevel()) {
                    result.append('/');
                }
            }
            result.append(step.toString());
            first = false;
        }
        return result.toString();
    }

    public boolean isAbsolute() {
        return this.absolute_;
    }

    public boolean isStringValue() {
        return ((Step) this.steps_.peek()).isStringValue();
    }

    public Enumeration getSteps() {
        return this.steps_.elements();
    }

    public String getIndexingAttrName() throws XPathException {
        BooleanExpr predicate = ((Step) this.steps_.peek()).getPredicate();
        if (predicate instanceof AttrExistsExpr) {
            return ((AttrExistsExpr) predicate).getAttrName();
        }
        throw new XPathException(this, "has no indexing attribute name (must end with predicate of the form [@attrName]");
    }

    public String getIndexingAttrNameOfEquals() throws XPathException {
        BooleanExpr predicate = ((Step) this.steps_.peek()).getPredicate();
        if (predicate instanceof AttrEqualsExpr) {
            return ((AttrEqualsExpr) predicate).getAttrName();
        }
        return null;
    }

    public Object clone() {
        Step[] steps = new Step[this.steps_.size()];
        Enumeration step = this.steps_.elements();
        for (int i = 0; i < steps.length; i++) {
            steps[i] = (Step) step.nextElement();
        }
        return new XPath(this.absolute_, steps);
    }

    public static XPath get(String xpathString) throws XPathException {
        XPath result;
        synchronized (cache_) {
            result = (XPath) cache_.get(xpathString);
            if (result == null) {
                result = new XPath(xpathString);
                cache_.put(xpathString, result);
            }
        }
        return result;
    }

    public static XPath get(boolean isAbsolute, Step[] steps) {
        XPath created = new XPath(isAbsolute, steps);
        String xpathString = created.toString();
        synchronized (cache_) {
            XPath inCache = (XPath) cache_.get(xpathString);
            if (inCache != null) {
                return inCache;
            }
            cache_.put(xpathString, created);
            return created;
        }
    }

    public static boolean isStringValue(String xpathString) throws XPathException, IOException {
        return get(xpathString).isStringValue();
    }
}
