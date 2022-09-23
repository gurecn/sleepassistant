package com.p000hp.hpl.sparta;

import java.util.Hashtable;

/* renamed from: com.hp.hpl.sparta.Sparta */
public class Sparta {
    private static CacheFactory cacheFactory_ = new CacheFactory() {
        /* class com.p000hp.hpl.sparta.Sparta.C00022 */

        @Override // com.p000hp.hpl.sparta.Sparta.CacheFactory
        public Cache create() {
            return new HashtableCache();
        }
    };
    private static Internment internment_ = new Internment() {
        /* class com.p000hp.hpl.sparta.Sparta.C00011 */
        private final Hashtable strings_ = new Hashtable();

        @Override // com.p000hp.hpl.sparta.Sparta.Internment
        public String intern(String s) {
            String ss = (String) this.strings_.get(s);
            if (ss != null) {
                return ss;
            }
            this.strings_.put(s, s);
            return s;
        }
    };

    /* renamed from: com.hp.hpl.sparta.Sparta$Cache */
    public interface Cache {
        Object get(Object obj);

        Object put(Object obj, Object obj2);

        int size();
    }

    /* renamed from: com.hp.hpl.sparta.Sparta$CacheFactory */
    public interface CacheFactory {
        Cache create();
    }

    /* renamed from: com.hp.hpl.sparta.Sparta$Internment */
    public interface Internment {
        String intern(String str);
    }

    public static String intern(String s) {
        return internment_.intern(s);
    }

    public static void setInternment(Internment i) {
        internment_ = i;
    }

    static Cache newCache() {
        return cacheFactory_.create();
    }

    public static void setCacheFactory(CacheFactory f) {
        cacheFactory_ = f;
    }

    /* renamed from: com.hp.hpl.sparta.Sparta$HashtableCache */
    private static class HashtableCache extends Hashtable implements Cache {
        private HashtableCache() {
        }
    }
}
