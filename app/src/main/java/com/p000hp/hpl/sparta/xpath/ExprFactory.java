package com.p000hp.hpl.sparta.xpath;

import java.io.IOException;

/* renamed from: com.hp.hpl.sparta.xpath.ExprFactory */
public class ExprFactory {
    static BooleanExpr createExpr(XPath xpath, SimpleStreamTokenizer toks) throws XPathException, IOException {
        int valueN;
        int valueN2;
        switch (toks.ttype) {
            case SimpleStreamTokenizer.TT_WORD /*{ENCODED_INT: -3}*/:
                if (!toks.sval.equals("text")) {
                    throw new XPathException(xpath, "at beginning of expression", toks, "text()");
                } else if (toks.nextToken() != 40) {
                    throw new XPathException(xpath, "after text", toks, "(");
                } else if (toks.nextToken() != 41) {
                    throw new XPathException(xpath, "after text(", toks, ")");
                } else {
                    switch (toks.nextToken()) {
                        case 33:
                            toks.nextToken();
                            if (toks.ttype != 61) {
                                throw new XPathException(xpath, "after !", toks, "=");
                            }
                            toks.nextToken();
                            if (toks.ttype == 34 || toks.ttype == 39) {
                                String tValue = toks.sval;
                                toks.nextToken();
                                return new TextNotEqualsExpr(tValue);
                            }
                            throw new XPathException(xpath, "right hand side of !=", toks, "quoted string");
                        case 61:
                            toks.nextToken();
                            if (toks.ttype == 34 || toks.ttype == 39) {
                                String tValue2 = toks.sval;
                                toks.nextToken();
                                return new TextEqualsExpr(tValue2);
                            }
                            throw new XPathException(xpath, "right hand side of equals", toks, "quoted string");
                        default:
                            return TextExistsExpr.INSTANCE;
                    }
                }
            case SimpleStreamTokenizer.TT_NUMBER /*{ENCODED_INT: -2}*/:
                int position = toks.nval;
                toks.nextToken();
                return new PositionEqualsExpr(position);
            case 64:
                if (toks.nextToken() != -3) {
                    throw new XPathException(xpath, "after @", toks, "name");
                }
                String name = toks.sval;
                switch (toks.nextToken()) {
                    case 33:
                        toks.nextToken();
                        if (toks.ttype != 61) {
                            throw new XPathException(xpath, "after !", toks, "=");
                        }
                        toks.nextToken();
                        if (toks.ttype == 34 || toks.ttype == 39) {
                            String value = toks.sval;
                            toks.nextToken();
                            return new AttrNotEqualsExpr(name, value);
                        }
                        throw new XPathException(xpath, "right hand side of !=", toks, "quoted string");
                    case 60:
                        toks.nextToken();
                        if (toks.ttype == 34 || toks.ttype == 39) {
                            valueN2 = Integer.parseInt(toks.sval);
                        } else if (toks.ttype == -2) {
                            valueN2 = toks.nval;
                        } else {
                            throw new XPathException(xpath, "right hand side of less-than", toks, "quoted string or number");
                        }
                        toks.nextToken();
                        return new AttrLessExpr(name, valueN2);
                    case 61:
                        toks.nextToken();
                        if (toks.ttype == 34 || toks.ttype == 39) {
                            String value2 = toks.sval;
                            toks.nextToken();
                            return new AttrEqualsExpr(name, value2);
                        }
                        throw new XPathException(xpath, "right hand side of equals", toks, "quoted string");
                    case 62:
                        toks.nextToken();
                        if (toks.ttype == 34 || toks.ttype == 39) {
                            valueN = Integer.parseInt(toks.sval);
                        } else if (toks.ttype == -2) {
                            valueN = toks.nval;
                        } else {
                            throw new XPathException(xpath, "right hand side of greater-than", toks, "quoted string or number");
                        }
                        toks.nextToken();
                        return new AttrGreaterExpr(name, valueN);
                    default:
                        return new AttrExistsExpr(name);
                }
            default:
                throw new XPathException(xpath, "at beginning of expression", toks, "@, number, or text()");
        }
    }
}
