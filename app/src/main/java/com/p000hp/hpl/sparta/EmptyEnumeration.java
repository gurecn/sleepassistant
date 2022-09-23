package com.p000hp.hpl.sparta;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/* renamed from: com.hp.hpl.sparta.EmptyEnumeration */
/* compiled from: Document */
class EmptyEnumeration implements Enumeration {
    EmptyEnumeration() {
    }

    public boolean hasMoreElements() {
        return false;
    }

    @Override // java.util.Enumeration
    public Object nextElement() {
        throw new NoSuchElementException();
    }
}
