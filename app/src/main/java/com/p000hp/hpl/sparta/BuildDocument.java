package com.p000hp.hpl.sparta;

/* renamed from: com.hp.hpl.sparta.BuildDocument */
class BuildDocument implements DocumentSource, ParseHandler {
    private Element currentElement_;
    private final Document doc_;
    private final ParseLog log_;
    private ParseSource parseSource_;

    public BuildDocument() {
        this(null);
    }

    public BuildDocument(ParseLog log) {
        this.currentElement_ = null;
        this.doc_ = new Document();
        this.parseSource_ = null;
        this.log_ = log == null ? DEFAULT_LOG : log;
    }

    @Override // com.p000hp.hpl.sparta.ParseHandler
    public void setParseSource(ParseSource ps) {
        this.parseSource_ = ps;
        this.doc_.setSystemId(ps.toString());
    }

    @Override // com.p000hp.hpl.sparta.ParseHandler
    public ParseSource getParseSource() {
        return this.parseSource_;
    }

    @Override // com.p000hp.hpl.sparta.ParseSource
    public String toString() {
        if (this.parseSource_ != null) {
            return "BuildDoc: " + this.parseSource_.toString();
        }
        return null;
    }

    @Override // com.p000hp.hpl.sparta.ParseSource
    public String getSystemId() {
        if (this.parseSource_ != null) {
            return this.parseSource_.getSystemId();
        }
        return null;
    }

    @Override // com.p000hp.hpl.sparta.ParseSource
    public int getLineNumber() {
        if (this.parseSource_ != null) {
            return this.parseSource_.getLineNumber();
        }
        return -1;
    }

    @Override // com.p000hp.hpl.sparta.DocumentSource
    public Document getDocument() {
        return this.doc_;
    }

    @Override // com.p000hp.hpl.sparta.ParseHandler
    public void startDocument() {
    }

    @Override // com.p000hp.hpl.sparta.ParseHandler
    public void endDocument() {
    }

    @Override // com.p000hp.hpl.sparta.ParseHandler
    public void startElement(Element element) {
        if (this.currentElement_ == null) {
            this.doc_.setDocumentElement(element);
        } else {
            this.currentElement_.appendChild(element);
        }
        this.currentElement_ = element;
    }

    @Override // com.p000hp.hpl.sparta.ParseHandler
    public void endElement(Element element) {
        this.currentElement_ = this.currentElement_.getParentNode();
    }

    @Override // com.p000hp.hpl.sparta.ParseHandler
    public void characters(char[] buf, int offset, int len) {
        Element element = this.currentElement_;
        if (element.getLastChild() instanceof Text) {
            ((Text) element.getLastChild()).appendData(buf, offset, len);
        } else {
            element.appendChildNoChecking(new Text(new String(buf, offset, len)));
        }
    }
}
