package com.p000hp.hpl.sparta;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* access modifiers changed from: package-private */
/* renamed from: com.hp.hpl.sparta.NodeListWithPosition */
/* compiled from: XPathVisitor */
public class NodeListWithPosition {
    private static final Integer EIGHT = new Integer(8);
    private static final Integer FIVE = new Integer(5);
    private static final Integer FOUR = new Integer(4);
    private static final Integer NINE = new Integer(9);
    private static final Integer ONE = new Integer(1);
    private static final Integer SEVEN = new Integer(7);
    private static final Integer SIX = new Integer(6);
    private static final Integer TEN = new Integer(10);
    private static final Integer THREE = new Integer(3);
    private static final Integer TWO = new Integer(2);
    private Hashtable positions_ = new Hashtable();
    private final Vector vector_ = new Vector();

    NodeListWithPosition() {
    }

    /* access modifiers changed from: package-private */
    public Enumeration iterator() {
        return this.vector_.elements();
    }

    /* access modifiers changed from: package-private */
    public void removeAllElements() {
        this.vector_.removeAllElements();
        this.positions_.clear();
    }

    /* access modifiers changed from: package-private */
    public void add(String string) {
        this.vector_.addElement(string);
    }

    private static Integer identity(Node node) {
        return new Integer(System.identityHashCode(node));
    }

    /* access modifiers changed from: package-private */
    public void add(Node node, int position) {
        Integer posn;
        this.vector_.addElement(node);
        switch (position) {
            case 1:
                posn = ONE;
                break;
            case 2:
                posn = TWO;
                break;
            case 3:
                posn = THREE;
                break;
            case 4:
                posn = FOUR;
                break;
            case 5:
                posn = FIVE;
                break;
            case 6:
                posn = SIX;
                break;
            case 7:
                posn = SEVEN;
                break;
            case 8:
                posn = EIGHT;
                break;
            case 9:
                posn = NINE;
                break;
            case 10:
                posn = TEN;
                break;
            default:
                posn = new Integer(position);
                break;
        }
        this.positions_.put(identity(node), posn);
    }

    /* access modifiers changed from: package-private */
    public int position(Node node) {
        return ((Integer) this.positions_.get(identity(node))).intValue();
    }

    public String toString() {
        try {
            StringBuffer y = new StringBuffer("{ ");
            Enumeration i = this.vector_.elements();
            while (i.hasMoreElements()) {
                Object e = i.nextElement();
                if (e instanceof String) {
                    y.append("String(" + e + ") ");
                } else {
                    Node n = (Node) e;
                    y.append("Node(" + n.toXml() + ")[" + this.positions_.get(identity(n)) + "] ");
                }
            }
            y.append("}");
            return y.toString();
        } catch (IOException e2) {
            return e2.toString();
        }
    }
}
