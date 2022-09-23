package com.p000hp.hpl.sparta.xpath;

import java.io.IOException;
import java.io.Reader;

/* renamed from: com.hp.hpl.sparta.xpath.SimpleStreamTokenizer */
public class SimpleStreamTokenizer {
    private static final int QUOTE = -6;
    public static final int TT_EOF = -1;
    public static final int TT_NUMBER = -2;
    public static final int TT_WORD = -3;
    private static final int WHITESPACE = -5;
    private final StringBuffer buf_ = new StringBuffer();
    private final int[] charType_ = new int[256];
    private char inQuote_ = 0;
    private int nextType_;
    public int nval = Integer.MIN_VALUE;
    private boolean pushedBack_ = false;
    private final Reader reader_;
    public String sval = "";
    public int ttype = Integer.MIN_VALUE;

    public String toString() {
        switch (this.ttype) {
            case TT_WORD /*{ENCODED_INT: -3}*/:
            case 34:
                return "\"" + this.sval + "\"";
            case TT_NUMBER /*{ENCODED_INT: -2}*/:
                return Integer.toString(this.nval);
            case TT_EOF /*{ENCODED_INT: -1}*/:
                return "(EOF)";
            case 39:
                return "'" + this.sval + "'";
            default:
                return "'" + ((char) this.ttype) + "'";
        }
    }

    public SimpleStreamTokenizer(Reader reader) throws IOException {
        this.reader_ = reader;
        for (int i = 0; i < this.charType_.length; i = (char) (i + 1)) {
            if ((65 <= i && i <= 90) || ((97 <= i && i <= 122) || i == 45)) {
                this.charType_[i] = -3;
            } else if (48 <= i && i <= 57) {
                this.charType_[i] = -2;
            } else if (i < 0 || i > 32) {
                this.charType_[i] = i;
            } else {
                this.charType_[i] = WHITESPACE;
            }
        }
        nextToken();
    }

    /* JADX DEBUG: Multi-variable search result rejected for r2v0, resolved type: char */
    /* JADX DEBUG: Multi-variable search result rejected for r0v0, resolved type: int[] */
    /* JADX WARN: Multi-variable type inference failed */
    public void ordinaryChar(char ch) {
        this.charType_[ch] = ch;
    }

    public void wordChars(char from, char to) {
        for (char ch = from; ch <= to; ch = (char) (ch + 1)) {
            this.charType_[ch] = -3;
        }
    }

    public int nextToken() throws IOException {
        int ch;
        int currentType;
        boolean whitespace;
        boolean transition;
        int i;
        if (this.pushedBack_) {
            this.pushedBack_ = false;
            return this.ttype;
        }
        this.ttype = this.nextType_;
        do {
            boolean transition2 = false;
            do {
                ch = this.reader_.read();
                if (ch != -1) {
                    currentType = this.charType_[ch];
                } else if (this.inQuote_ != 0) {
                    throw new IOException("Unterminated quote");
                } else {
                    currentType = -1;
                }
                if (this.inQuote_ == 0 && currentType == WHITESPACE) {
                    whitespace = true;
                } else {
                    whitespace = false;
                }
                if (transition2 || whitespace) {
                    transition2 = true;
                    continue;
                } else {
                    transition2 = false;
                    continue;
                }
            } while (whitespace);
            if (currentType == 39 || currentType == 34) {
                if (this.inQuote_ == 0) {
                    this.inQuote_ = (char) currentType;
                } else if (this.inQuote_ == currentType) {
                    this.inQuote_ = 0;
                }
            }
            if (this.inQuote_ != 0) {
                currentType = this.inQuote_;
            }
            if (transition2 || !((this.ttype < -1 || this.ttype == 39 || this.ttype == 34) && this.ttype == currentType)) {
                transition = true;
            } else {
                transition = false;
            }
            if (transition) {
                switch (this.ttype) {
                    case TT_WORD /*{ENCODED_INT: -3}*/:
                        this.sval = this.buf_.toString();
                        this.buf_.setLength(0);
                        break;
                    case TT_NUMBER /*{ENCODED_INT: -2}*/:
                        this.nval = Integer.parseInt(this.buf_.toString());
                        this.buf_.setLength(0);
                        break;
                    case 34:
                    case 39:
                        this.sval = this.buf_.toString().substring(1, this.buf_.length() - 1);
                        this.buf_.setLength(0);
                        break;
                }
                if (currentType != WHITESPACE) {
                    if (currentType == QUOTE) {
                        i = ch;
                    } else {
                        i = currentType;
                    }
                    this.nextType_ = i;
                }
            }
            switch (currentType) {
                case TT_WORD /*{ENCODED_INT: -3}*/:
                case TT_NUMBER /*{ENCODED_INT: -2}*/:
                case 34:
                case 39:
                    this.buf_.append((char) ch);
                    continue;
            }
        } while (!transition);
        return this.ttype;
    }

    public void pushBack() {
        this.pushedBack_ = true;
    }
}
