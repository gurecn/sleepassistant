package com.p000hp.hpl.sparta;

import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;

/* access modifiers changed from: package-private */
/* renamed from: com.hp.hpl.sparta.ParseCharStream */
public class ParseCharStream implements ParseSource {
    private static final char[] BEGIN_CDATA = "<![CDATA[".toCharArray();
    private static final char[] BEGIN_ETAG = "</".toCharArray();
    private static final char[] CHARREF_BEGIN = "&#".toCharArray();
    private static final char[] COMMENT_BEGIN = "<!--".toCharArray();
    private static final char[] COMMENT_END = "-->".toCharArray();
    private static final boolean DEBUG = true;
    private static final char[] DOCTYPE_BEGIN = "<!DOCTYPE".toCharArray();
    private static final char[] ENCODING = "encoding".toCharArray();
    private static final char[] END_CDATA = "]]>".toCharArray();
    private static final char[] END_EMPTYTAG = "/>".toCharArray();
    private static final char[] ENTITY_BEGIN = "<!ENTITY".toCharArray();
    public static final int HISTORY_LENGTH = 100;
    private static final boolean H_DEBUG = false;
    private static final int MAX_COMMON_CHAR = 128;
    private static final boolean[] IS_NAME_CHAR = new boolean[MAX_COMMON_CHAR];
    private static final char[] MARKUPDECL_BEGIN = "<!".toCharArray();
    private static final char[] NAME_PUNCT_CHARS = {'.', '-', '_', ':'};
    private static final char[] NDATA = "NDATA".toCharArray();
    private static final char[] PI_BEGIN = "<?".toCharArray();
    private static final char[] PUBLIC = "PUBLIC".toCharArray();
    private static final char[] QU_END = "?>".toCharArray();
    private static final char[] SYSTEM = "SYSTEM".toCharArray();
    private static final int TMP_BUF_SIZE = 255;
    private static final char[] VERSION = "version".toCharArray();
    private static final char[] VERSIONNUM_PUNC_CHARS = {'_', '.', ':', '-'};
    private static final char[] XML_BEGIN = "<?xml".toCharArray();
    private final int CBUF_SIZE;
    private final char[] cbuf_;
    private int ch_;
    private int curPos_;
    private String docTypeName_;
    private final String encoding_;
    private int endPos_;
    private final Hashtable entities_;
    private boolean eos_;
    private final ParseHandler handler_;
    private final CharCircBuffer history_;
    private boolean isExternalDtd_;
    private int lineNumber_;
    private final ParseLog log_;
    private final Hashtable pes_;
    private final Reader reader_;
    private String systemId_;
    private final char[] tmpBuf_;

    public ParseCharStream(String systemId, char[] xmlData, ParseLog log, String encoding, ParseHandler handler) throws ParseException, EncodingMismatchException, IOException {
        this(systemId, null, xmlData, log, encoding, handler);
    }

    public ParseCharStream(String systemId, Reader reader, ParseLog log, String encoding, ParseHandler handler) throws ParseException, EncodingMismatchException, IOException {
        this(systemId, reader, null, log, encoding, handler);
    }

    public ParseCharStream(String systemId, Reader reader, char[] xmlData, ParseLog log, String encoding, ParseHandler handler) throws ParseException, EncodingMismatchException, IOException {
        this.docTypeName_ = null;
        this.entities_ = new Hashtable();
        this.pes_ = new Hashtable();
        this.ch_ = -2;
        this.isExternalDtd_ = H_DEBUG;
        this.CBUF_SIZE = 1024;
        this.curPos_ = 0;
        this.endPos_ = 0;
        this.eos_ = H_DEBUG;
        this.tmpBuf_ = new char[TMP_BUF_SIZE];
        this.lineNumber_ = -1;
        this.lineNumber_ = 1;
        this.history_ = null;
        this.log_ = log == null ? DEFAULT_LOG : log;
        this.encoding_ = encoding == null ? null : encoding.toLowerCase();
        this.entities_.put("lt", "<");
        this.entities_.put("gt", ">");
        this.entities_.put("amp", "&");
        this.entities_.put("apos", "'");
        this.entities_.put("quot", "\"");
        if (xmlData != null) {
            this.cbuf_ = xmlData;
            this.curPos_ = 0;
            this.endPos_ = this.cbuf_.length;
            this.eos_ = DEBUG;
            this.reader_ = null;
        } else {
            this.reader_ = reader;
            this.cbuf_ = new char[1024];
            fillBuf();
        }
        this.systemId_ = systemId;
        this.handler_ = handler;
        this.handler_.setParseSource(this);
        readProlog();
        this.handler_.startDocument();
        Element rootElement = readElement();
        if (this.docTypeName_ != null && !this.docTypeName_.equals(rootElement.getTagName())) {
            this.log_.warning("DOCTYPE name \"" + this.docTypeName_ + "\" not same as tag name, \"" + rootElement.getTagName() + "\" of root element", this.systemId_, getLineNumber());
        }
        while (isMisc()) {
            readMisc();
        }
        if (this.reader_ != null) {
            this.reader_.close();
        }
        this.handler_.endDocument();
    }

    @Override // com.p000hp.hpl.sparta.ParseSource
    public String toString() {
        return this.systemId_;
    }

    @Override // com.p000hp.hpl.sparta.ParseSource
    public String getSystemId() {
        return this.systemId_;
    }

    @Override // com.p000hp.hpl.sparta.ParseSource
    public int getLineNumber() {
        return this.lineNumber_;
    }

    /* access modifiers changed from: package-private */
    public int getLastCharRead() {
        return this.ch_;
    }

    /* access modifiers changed from: package-private */
    public final String getHistory() {
        return "";
    }

    private int fillBuf() throws IOException {
        if (this.eos_) {
            return -1;
        }
        if (this.endPos_ == this.cbuf_.length) {
            this.endPos_ = 0;
            this.curPos_ = 0;
        }
        int count = this.reader_.read(this.cbuf_, this.endPos_, this.cbuf_.length - this.endPos_);
        if (count <= 0) {
            this.eos_ = DEBUG;
            return -1;
        }
        this.endPos_ += count;
        return count;
    }

    private int fillBuf(int min) throws IOException {
        if (this.eos_) {
            return -1;
        }
        int count = 0;
        if (this.cbuf_.length - this.curPos_ < min) {
            for (int i = 0; this.curPos_ + i < this.endPos_; i++) {
                this.cbuf_[i] = this.cbuf_[this.curPos_ + i];
            }
            count = this.endPos_ - this.curPos_;
            this.endPos_ = count;
            this.curPos_ = 0;
        }
        int res = fillBuf();
        if (res != -1) {
            return count + res;
        }
        if (count == 0) {
            return -1;
        }
        return count;
    }

    private final char readChar() throws ParseException, IOException {
        if (this.curPos_ < this.endPos_ || fillBuf() != -1) {
            if (this.cbuf_[this.curPos_] == '\n') {
                this.lineNumber_++;
            }
            char[] cArr = this.cbuf_;
            int i = this.curPos_;
            this.curPos_ = i + 1;
            return cArr[i];
        }
        throw new ParseException(this, "unexpected end of expression.");
    }

    private final char peekChar() throws ParseException, IOException {
        if (this.curPos_ < this.endPos_ || fillBuf() != -1) {
            return this.cbuf_[this.curPos_];
        }
        throw new ParseException(this, "unexpected end of expression.");
    }

    private final void readChar(char expected) throws ParseException, IOException {
        char ch = readChar();
        if (ch != expected) {
            throw new ParseException(this, ch, expected);
        }
    }

    private final boolean isChar(char expected) throws ParseException, IOException {
        if (this.curPos_ < this.endPos_ || fillBuf() != -1) {
            return this.cbuf_[this.curPos_] == expected ? DEBUG : H_DEBUG;
        }
        throw new ParseException(this, "unexpected end of expression.");
    }

    private final char readChar(char expect0, char expect1) throws ParseException, IOException {
        char ch = readChar();
        if (ch == expect0 || ch == expect1) {
            return ch;
        }
        throw new ParseException(this, ch, new char[]{expect0, expect1});
    }

    private final char readChar(char expect0, char expect1, char expect2, char expect3) throws ParseException, IOException {
        char ch = readChar();
        if (ch == expect0 || ch == expect1 || ch == expect2 || ch == expect3) {
            return ch;
        }
        throw new ParseException(this, ch, new char[]{expect0, expect1, expect2, expect3});
    }

    private final boolean isChar(char expect0, char expect1) throws ParseException, IOException {
        if (this.curPos_ >= this.endPos_ && fillBuf() == -1) {
            return H_DEBUG;
        }
        char ch = this.cbuf_[this.curPos_];
        if (ch == expect0 || ch == expect1) {
            return DEBUG;
        }
        return H_DEBUG;
    }

    private final boolean isChar(char expect0, char expect1, char expect2, char expect3) throws ParseException, IOException {
        if (this.curPos_ >= this.endPos_ && fillBuf() == -1) {
            return H_DEBUG;
        }
        char ch = this.cbuf_[this.curPos_];
        if (ch == expect0 || ch == expect1 || ch == expect2 || ch == expect3) {
            return DEBUG;
        }
        return H_DEBUG;
    }

    private static final boolean isIn(char ch, char[] expected) {
        for (char c : expected) {
            if (ch == c) {
                return DEBUG;
            }
        }
        return H_DEBUG;
    }

    private final void readS() throws ParseException, IOException {
        readChar(' ', '\t', '\r', '\n');
        while (isChar(' ', '\t', '\r', '\n')) {
            readChar();
        }
    }

    private final boolean isS() throws ParseException, IOException {
        return isChar(' ', '\t', '\r', '\n');
    }

    static {
        for (char ch = 0; ch < MAX_COMMON_CHAR; ch = (char) (ch + 1)) {
            IS_NAME_CHAR[ch] = isNameChar(ch);
        }
    }

    private boolean isNameChar() throws ParseException, IOException {
        char ch = peekChar();
        return ch < MAX_COMMON_CHAR ? IS_NAME_CHAR[ch] : isNameChar(ch);
    }

    private static boolean isLetter(char ch) {
        return "abcdefghijklmnopqrstuvwxyz".indexOf(Character.toLowerCase(ch)) != -1 ? DEBUG : H_DEBUG;
    }

    private static boolean isNameChar(char ch) {
        return (Character.isDigit(ch) || isLetter(ch) || isIn(ch, NAME_PUNCT_CHARS) || isExtender(ch)) ? DEBUG : H_DEBUG;
    }

    private static boolean isExtender(char ch) {
        switch (ch) {
            case 183:
            case 720:
            case 721:
            case 903:
            case 1600:
            case 3654:
            case 3782:
            case 12293:
            case 12337:
            case 12338:
            case 12339:
            case 12340:
            case 12341:
            case 12445:
            case 12446:
            case 12540:
            case 12541:
            case 12542:
                return DEBUG;
            default:
                return H_DEBUG;
        }
    }

    private final String readName() throws ParseException, IOException {
        int i;
        StringBuffer result = null;
        int i2 = 0 + 1;
        this.tmpBuf_[0] = readNameStartChar();
        while (true) {
            i = i2;
            if (!isNameChar()) {
                break;
            }
            if (i >= TMP_BUF_SIZE) {
                if (result == null) {
                    result = new StringBuffer(i);
                    result.append(this.tmpBuf_, 0, i);
                } else {
                    result.append(this.tmpBuf_, 0, i);
                }
                i = 0;
            }
            i2 = i + 1;
            this.tmpBuf_[i] = readChar();
        }
        if (result == null) {
            return Sparta.intern(new String(this.tmpBuf_, 0, i));
        }
        result.append(this.tmpBuf_, 0, i);
        return result.toString();
    }

    private char readNameStartChar() throws ParseException, IOException {
        char ch = readChar();
        if (isLetter(ch) || ch == '_' || ch == ':') {
            return ch;
        }
        throw new ParseException(this, ch, "letter, underscore, colon");
    }

    private final String readEntityValue() throws ParseException, IOException {
        char quote = readChar('\'', '\"');
        StringBuffer result = new StringBuffer();
        while (!isChar(quote)) {
            if (isPeReference()) {
                result.append(readPeReference());
            } else if (isReference()) {
                result.append(readReference());
            } else {
                result.append(readChar());
            }
        }
        readChar(quote);
        return result.toString();
    }

    private final boolean isEntityValue() throws ParseException, IOException {
        return isChar('\'', '\"');
    }

    private final void readSystemLiteral() throws ParseException, IOException {
        char quote = readChar();
        while (peekChar() != quote) {
            readChar();
        }
        readChar(quote);
    }

    private final void readPubidLiteral() throws ParseException, IOException {
        readSystemLiteral();
    }

    private boolean isMisc() throws ParseException, IOException {
        return (isComment() || isPi() || isS()) ? DEBUG : H_DEBUG;
    }

    private void readMisc() throws ParseException, IOException {
        if (isComment()) {
            readComment();
        } else if (isPi()) {
            readPi();
        } else if (isS()) {
            readS();
        } else {
            throw new ParseException(this, "expecting comment or processing instruction or space");
        }
    }

    private final void readComment() throws ParseException, IOException {
        readSymbol(COMMENT_BEGIN);
        while (!isSymbol(COMMENT_END)) {
            readChar();
        }
        readSymbol(COMMENT_END);
    }

    private final boolean isComment() throws ParseException, IOException {
        return isSymbol(COMMENT_BEGIN);
    }

    private final void readPi() throws ParseException, IOException {
        readSymbol(PI_BEGIN);
        while (!isSymbol(QU_END)) {
            readChar();
        }
        readSymbol(QU_END);
    }

    private final boolean isPi() throws ParseException, IOException {
        return isSymbol(PI_BEGIN);
    }

    private void readProlog() throws ParseException, EncodingMismatchException, IOException {
        if (isXmlDecl()) {
            readXmlDecl();
        }
        while (isMisc()) {
            readMisc();
        }
        if (isDocTypeDecl()) {
            readDocTypeDecl();
            while (isMisc()) {
                readMisc();
            }
        }
    }

    private boolean isDocTypeDecl() throws ParseException, IOException {
        return isSymbol(DOCTYPE_BEGIN);
    }

    private void readXmlDecl() throws ParseException, EncodingMismatchException, IOException {
        readSymbol(XML_BEGIN);
        readVersionInfo();
        if (isS()) {
            readS();
        }
        if (isEncodingDecl()) {
            String encodingDeclared = readEncodingDecl();
            if (this.encoding_ != null && !encodingDeclared.toLowerCase().equals(this.encoding_)) {
                throw new EncodingMismatchException(this.systemId_, encodingDeclared, this.encoding_);
            }
        }
        while (!isSymbol(QU_END)) {
            readChar();
        }
        readSymbol(QU_END);
    }

    private boolean isXmlDecl() throws ParseException, IOException {
        return isSymbol(XML_BEGIN);
    }

    private boolean isEncodingDecl() throws ParseException, IOException {
        return isSymbol(ENCODING);
    }

    private String readEncodingDecl() throws ParseException, IOException {
        readSymbol(ENCODING);
        readEq();
        char quote = readChar('\'', '\"');
        StringBuffer result = new StringBuffer();
        while (!isChar(quote)) {
            result.append(readChar());
        }
        readChar(quote);
        return result.toString();
    }

    private void readVersionInfo() throws ParseException, IOException {
        readS();
        readSymbol(VERSION);
        readEq();
        char quote = readChar('\'', '\"');
        readVersionNum();
        readChar(quote);
    }

    private final void readEq() throws ParseException, IOException {
        if (isS()) {
            readS();
        }
        readChar('=');
        if (isS()) {
            readS();
        }
    }

    private boolean isVersionNumChar() throws ParseException, IOException {
        char ch = peekChar();
        return (Character.isDigit(ch) || ('a' <= ch && ch <= 'z') || (('Z' <= ch && ch <= 'Z') || isIn(ch, VERSIONNUM_PUNC_CHARS))) ? DEBUG : H_DEBUG;
    }

    private void readVersionNum() throws ParseException, IOException {
        readChar();
        while (isVersionNumChar()) {
            readChar();
        }
    }

    private void readDocTypeDecl() throws ParseException, IOException {
        readSymbol(DOCTYPE_BEGIN);
        readS();
        this.docTypeName_ = readName();
        if (isS()) {
            readS();
            if (!isChar('>') && !isChar('[')) {
                this.isExternalDtd_ = DEBUG;
                readExternalId();
                if (isS()) {
                    readS();
                }
            }
        }
        if (isChar('[')) {
            readChar();
            while (!isChar(']')) {
                if (isDeclSep()) {
                    readDeclSep();
                } else {
                    readMarkupDecl();
                }
            }
            readChar(']');
            if (isS()) {
                readS();
            }
        }
        readChar('>');
    }

    private void readDeclSep() throws ParseException, IOException {
        if (isPeReference()) {
            readPeReference();
        } else {
            readS();
        }
    }

    private boolean isDeclSep() throws ParseException, IOException {
        return (isPeReference() || isS()) ? DEBUG : H_DEBUG;
    }

    private void readMarkupDecl() throws ParseException, IOException {
        if (isPi()) {
            readPi();
        } else if (isComment()) {
            readComment();
        } else if (isEntityDecl()) {
            readEntityDecl();
        } else if (isSymbol(MARKUPDECL_BEGIN)) {
            while (!isChar('>')) {
                if (isChar('\'', '\"')) {
                    char quote = readChar();
                    while (!isChar(quote)) {
                        readChar();
                    }
                    readChar(quote);
                } else {
                    readChar();
                }
            }
            readChar('>');
        } else {
            throw new ParseException(this, "expecting processing instruction, comment, or \"<!\"");
        }
    }

    private char readCharRef() throws ParseException, IOException {
        readSymbol(CHARREF_BEGIN);
        int radix = 10;
        if (isChar('x')) {
            readChar();
            radix = 16;
        }
        int i = 0;
        while (!isChar(';')) {
            int i2 = i + 1;
            this.tmpBuf_[i] = readChar();
            if (i2 >= TMP_BUF_SIZE) {
                this.log_.warning("Tmp buffer overflow on readCharRef", this.systemId_, getLineNumber());
                return ' ';
            }
            i = i2;
        }
        readChar(';');
        String num = new String(this.tmpBuf_, 0, i);
        try {
            return (char) Integer.parseInt(num, radix);
        } catch (NumberFormatException e) {
            this.log_.warning("\"" + num + "\" is not a valid " + (radix == 16 ? "hexadecimal" : "decimal") + " number", this.systemId_, getLineNumber());
            return ' ';
        }
    }

    private final char[] readReference() throws ParseException, IOException {
        if (!isSymbol(CHARREF_BEGIN)) {
            return readEntityRef().toCharArray();
        }
        return new char[]{readCharRef()};
    }

    private final boolean isReference() throws ParseException, IOException {
        return isChar('&');
    }

    private String readEntityRef() throws ParseException, IOException {
        readChar('&');
        String name = readName();
        String result = (String) this.entities_.get(name);
        if (result == null) {
            result = "";
            if (this.isExternalDtd_) {
                this.log_.warning("&" + name + "; not found -- possibly defined in external DTD)", this.systemId_, getLineNumber());
            } else {
                this.log_.warning("No declaration of &" + name + ";", this.systemId_, getLineNumber());
            }
        }
        readChar(';');
        return result;
    }

    private String readPeReference() throws ParseException, IOException {
        readChar('%');
        String name = readName();
        String result = (String) this.pes_.get(name);
        if (result == null) {
            result = "";
            this.log_.warning("No declaration of %" + name + ";", this.systemId_, getLineNumber());
        }
        readChar(';');
        return result;
    }

    private boolean isPeReference() throws ParseException, IOException {
        return isChar('%');
    }

    private void readEntityDecl() throws ParseException, IOException {
        String value;
        String value2;
        readSymbol(ENTITY_BEGIN);
        readS();
        if (isChar('%')) {
            readChar('%');
            readS();
            String name = readName();
            readS();
            if (isEntityValue()) {
                value2 = readEntityValue();
            } else {
                value2 = readExternalId();
            }
            this.pes_.put(name, value2);
        } else {
            String name2 = readName();
            readS();
            if (isEntityValue()) {
                value = readEntityValue();
            } else if (isExternalId()) {
                value = readExternalId();
                if (isS()) {
                    readS();
                }
                if (isSymbol(NDATA)) {
                    readSymbol(NDATA);
                    readS();
                    readName();
                }
            } else {
                throw new ParseException(this, "expecting double-quote, \"PUBLIC\" or \"SYSTEM\" while reading entity declaration");
            }
            this.entities_.put(name2, value);
        }
        if (isS()) {
            readS();
        }
        readChar('>');
    }

    private boolean isEntityDecl() throws ParseException, IOException {
        return isSymbol(ENTITY_BEGIN);
    }

    private String readExternalId() throws ParseException, IOException {
        if (isSymbol(SYSTEM)) {
            readSymbol(SYSTEM);
        } else if (isSymbol(PUBLIC)) {
            readSymbol(PUBLIC);
            readS();
            readPubidLiteral();
        } else {
            throw new ParseException(this, "expecting \"SYSTEM\" or \"PUBLIC\" while reading external ID");
        }
        readS();
        readSystemLiteral();
        return "(WARNING: external ID not read)";
    }

    private boolean isExternalId() throws ParseException, IOException {
        return (isSymbol(SYSTEM) || isSymbol(PUBLIC)) ? DEBUG : H_DEBUG;
    }

    private final void readSymbol(char[] expected) throws ParseException, IOException {
        int n = expected.length;
        if (this.endPos_ - this.curPos_ >= n || fillBuf(n) > 0) {
            this.ch_ = this.cbuf_[this.endPos_ - 1];
            if (this.endPos_ - this.curPos_ < n) {
                throw new ParseException(this, "end of XML file", expected);
            }
            for (int i = 0; i < n; i++) {
                if (this.cbuf_[this.curPos_ + i] != expected[i]) {
                    throw new ParseException(this, new String(this.cbuf_, this.curPos_, n), expected);
                }
            }
            this.curPos_ += n;
            return;
        }
        this.ch_ = -1;
        throw new ParseException(this, "end of XML file", expected);
    }

    private final boolean isSymbol(char[] expected) throws ParseException, IOException {
        int n = expected.length;
        if (this.endPos_ - this.curPos_ >= n || fillBuf(n) > 0) {
            this.ch_ = this.cbuf_[this.endPos_ - 1];
            if (this.endPos_ - this.curPos_ < n) {
                return H_DEBUG;
            }
            for (int i = 0; i < n; i++) {
                if (this.cbuf_[this.curPos_ + i] != expected[i]) {
                    return H_DEBUG;
                }
            }
            return DEBUG;
        }
        this.ch_ = -1;
        return H_DEBUG;
    }

    private String readAttValue() throws ParseException, IOException {
        char quote = readChar('\'', '\"');
        StringBuffer result = new StringBuffer();
        while (!isChar(quote)) {
            if (isReference()) {
                result.append(readReference());
            } else {
                result.append(readChar());
            }
        }
        readChar(quote);
        return result.toString();
    }

    private void readPossibleCharData() throws ParseException, IOException {
        int i = 0;
        while (!isChar('<') && !isChar('&') && !isSymbol(END_CDATA)) {
            this.tmpBuf_[i] = readChar();
            if (this.tmpBuf_[i] == '\r' && peekChar() == '\n') {
                this.tmpBuf_[i] = readChar();
            }
            i++;
            if (i == TMP_BUF_SIZE) {
                this.handler_.characters(this.tmpBuf_, 0, TMP_BUF_SIZE);
                i = 0;
            }
        }
        if (i > 0) {
            this.handler_.characters(this.tmpBuf_, 0, i);
        }
    }

    private void readCdSect() throws ParseException, IOException {
        StringBuffer result = null;
        readSymbol(BEGIN_CDATA);
        int i = 0;
        while (!isSymbol(END_CDATA)) {
            if (i >= TMP_BUF_SIZE) {
                if (result == null) {
                    result = new StringBuffer(i);
                    result.append(this.tmpBuf_, 0, i);
                } else {
                    result.append(this.tmpBuf_, 0, i);
                }
                i = 0;
            }
            this.tmpBuf_[i] = readChar();
            i++;
        }
        readSymbol(END_CDATA);
        if (result != null) {
            result.append(this.tmpBuf_, 0, i);
            char[] cdSect = result.toString().toCharArray();
            this.handler_.characters(cdSect, 0, cdSect.length);
            return;
        }
        this.handler_.characters(this.tmpBuf_, 0, i);
    }

    private boolean isCdSect() throws ParseException, IOException {
        return isSymbol(BEGIN_CDATA);
    }

    private final Element readElement() throws ParseException, IOException {
        Element element = new Element();
        boolean isSTag = readEmptyElementTagOrSTag(element);
        this.handler_.startElement(element);
        if (isSTag) {
            readContent();
            readETag(element);
        }
        this.handler_.endElement(element);
        return element;
    }

    /* access modifiers changed from: package-private */
    public ParseLog getLog() {
        return this.log_;
    }

    private boolean readEmptyElementTagOrSTag(Element element) throws ParseException, IOException {
        readChar('<');
        element.setTagName(readName());
        while (isS()) {
            readS();
            if (!isChar('/', '>')) {
                readAttribute(element);
            }
        }
        if (isS()) {
            readS();
        }
        boolean isSTag = isChar('>');
        if (isSTag) {
            readChar('>');
        } else {
            readSymbol(END_EMPTYTAG);
        }
        return isSTag;
    }

    private void readAttribute(Element element) throws ParseException, IOException {
        String name = readName();
        readEq();
        String value = readAttValue();
        if (element.getAttribute(name) != null) {
            this.log_.warning("Element " + this + " contains attribute " + name + "more than once", this.systemId_, getLineNumber());
        }
        element.setAttribute(name, value);
    }

    private void readETag(Element element) throws ParseException, IOException {
        readSymbol(BEGIN_ETAG);
        String name = readName();
        if (!name.equals(element.getTagName())) {
            this.log_.warning("end tag (" + name + ") does not match begin tag (" + element.getTagName() + ")", this.systemId_, getLineNumber());
        }
        if (isS()) {
            readS();
        }
        readChar('>');
    }

    private boolean isETag() throws ParseException, IOException {
        return isSymbol(BEGIN_ETAG);
    }

    private void readContent() throws ParseException, IOException {
        readPossibleCharData();
        boolean keepGoing = DEBUG;
        while (keepGoing) {
            if (isETag()) {
                keepGoing = H_DEBUG;
            } else if (isReference()) {
                char[] ref = readReference();
                this.handler_.characters(ref, 0, ref.length);
            } else if (isCdSect()) {
                readCdSect();
            } else if (isPi()) {
                readPi();
            } else if (isComment()) {
                readComment();
            } else if (isChar('<')) {
                readElement();
            } else {
                keepGoing = H_DEBUG;
            }
            readPossibleCharData();
        }
    }
}
