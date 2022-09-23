package com.p000hp.hpl.sparta;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;

/* renamed from: com.hp.hpl.sparta.Text */
public class Text extends Node {
    private StringBuffer text_;

    public Text(String data) {
        this.text_ = new StringBuffer(data);
    }

    public Text(char ch) {
        this.text_ = new StringBuffer();
        this.text_.append(ch);
    }

    @Override // com.p000hp.hpl.sparta.Node
    public Object clone() {
        return new Text(this.text_.toString());
    }

    public void appendData(String s) {
        this.text_.append(s);
        notifyObservers();
    }

    public void appendData(char ch) {
        this.text_.append(ch);
        notifyObservers();
    }

    public void appendData(char[] cbuf, int offset, int len) {
        this.text_.append(cbuf, offset, len);
        notifyObservers();
    }

    public String getData() {
        return this.text_.toString();
    }

    public void setData(String data) {
        this.text_ = new StringBuffer(data);
        notifyObservers();
    }

    /* access modifiers changed from: package-private */
    @Override // com.p000hp.hpl.sparta.Node
    public void toXml(Writer writer) throws IOException {
        String s = this.text_.toString();
        if (s.length() < 50) {
            htmlEncode(writer, s);
            return;
        }
        writer.write("<![CDATA[");
        writer.write(s);
        writer.write("]]>");
    }

    /* access modifiers changed from: package-private */
    @Override // com.p000hp.hpl.sparta.Node
    public void toString(Writer writer) throws IOException {
        writer.write(this.text_.toString());
    }

    @Override // com.p000hp.hpl.sparta.Node
    public Enumeration xpathSelectElements(String xpath) {
        throw new Error("Sorry, not implemented");
    }

    @Override // com.p000hp.hpl.sparta.Node
    public Enumeration xpathSelectStrings(String xpath) {
        throw new Error("Sorry, not implemented");
    }

    @Override // com.p000hp.hpl.sparta.Node
    public Element xpathSelectElement(String xpath) {
        throw new Error("Sorry, not implemented");
    }

    @Override // com.p000hp.hpl.sparta.Node
    public String xpathSelectString(String xpath) {
        throw new Error("Sorry, not implemented");
    }

    public boolean equals(Object thatO) {
        if (this == thatO) {
            return true;
        }
        if (!(thatO instanceof Text)) {
            return false;
        }
        return this.text_.toString().equals(((Text) thatO).text_.toString());
    }

    /* access modifiers changed from: protected */
    @Override // com.p000hp.hpl.sparta.Node
    public int computeHashCode() {
        return this.text_.toString().hashCode();
    }
}
