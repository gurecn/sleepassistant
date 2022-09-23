package com.p000hp.hpl.sparta;

/* renamed from: com.hp.hpl.sparta.DefaultLog */
/* compiled from: ParseSource */
class DefaultLog implements ParseLog {
    DefaultLog() {
    }

    @Override // com.p000hp.hpl.sparta.ParseLog
    public void error(String msg, String systemId, int line) {
        System.err.println(systemId + "(" + line + "): " + msg + " (ERROR)");
    }

    @Override // com.p000hp.hpl.sparta.ParseLog
    public void warning(String msg, String systemId, int line) {
        System.out.println(systemId + "(" + line + "): " + msg + " (WARNING)");
    }

    @Override // com.p000hp.hpl.sparta.ParseLog
    public void note(String msg, String systemId, int line) {
        System.out.println(systemId + "(" + line + "): " + msg + " (NOTE)");
    }
}
