package com.p000hp.hpl.sparta;

/* renamed from: com.hp.hpl.sparta.CharCircBuffer */
class CharCircBuffer {
    private final int[] buf_;
    private boolean enabled_ = true;
    private int next_ = 0;
    private int total_ = 0;

    CharCircBuffer(int n) {
        this.buf_ = new int[n];
    }

    /* access modifiers changed from: package-private */
    public void enable() {
        this.enabled_ = true;
    }

    /* access modifiers changed from: package-private */
    public void disable() {
        this.enabled_ = false;
    }

    /* access modifiers changed from: package-private */
    public void addInt(int i) {
        addRaw(65536 + i);
    }

    /* access modifiers changed from: package-private */
    public void addChar(char ch) {
        addRaw(ch);
    }

    private void addRaw(int v) {
        if (this.enabled_) {
            this.buf_[this.next_] = v;
            this.next_ = (this.next_ + 1) % this.buf_.length;
            this.total_++;
        }
    }

    /* access modifiers changed from: package-private */
    public void addString(String s) {
        for (char c : s.toCharArray()) {
            addChar(c);
        }
    }

    public String toString() {
        StringBuffer result = new StringBuffer((this.buf_.length * 11) / 10);
        for (int i = this.total_ < this.buf_.length ? this.buf_.length - this.total_ : 0; i < this.buf_.length; i++) {
            int v = this.buf_[(this.next_ + i) % this.buf_.length];
            if (v < 65536) {
                result.append((char) v);
            } else {
                result.append(Integer.toString(v - 65536));
            }
        }
        return result.toString();
    }
}
