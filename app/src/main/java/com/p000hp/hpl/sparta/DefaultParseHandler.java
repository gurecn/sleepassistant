package com.p000hp.hpl.sparta;

/* renamed from: com.hp.hpl.sparta.DefaultParseHandler */
public class DefaultParseHandler implements ParseHandler {
    private ParseSource parseSource_ = null;

    @Override // com.p000hp.hpl.sparta.ParseHandler
    public void setParseSource(ParseSource ps) {
        this.parseSource_ = ps;
    }

    @Override // com.p000hp.hpl.sparta.ParseHandler
    public ParseSource getParseSource() {
        return this.parseSource_;
    }

    @Override // com.p000hp.hpl.sparta.ParseHandler
    public void startDocument() throws ParseException {
    }

    @Override // com.p000hp.hpl.sparta.ParseHandler
    public void endDocument() throws ParseException {
    }

    @Override // com.p000hp.hpl.sparta.ParseHandler
    public void startElement(Element element) throws ParseException {
    }

    @Override // com.p000hp.hpl.sparta.ParseHandler
    public void endElement(Element element) throws ParseException {
    }

    @Override // com.p000hp.hpl.sparta.ParseHandler
    public void characters(char[] buf, int off, int len) throws ParseException {
    }
}
