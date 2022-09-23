package net.sourceforge.pinyin4j;

class PinyinRomanizationType {
    static final PinyinRomanizationType GWOYEU_ROMATZYH = new PinyinRomanizationType("Gwoyeu");
    static final PinyinRomanizationType HANYU_PINYIN = new PinyinRomanizationType("Hanyu");
    static final PinyinRomanizationType MPS2_PINYIN = new PinyinRomanizationType("MPSII");
    static final PinyinRomanizationType TONGYONG_PINYIN = new PinyinRomanizationType("Tongyong");
    static final PinyinRomanizationType WADEGILES_PINYIN = new PinyinRomanizationType("Wade");
    static final PinyinRomanizationType YALE_PINYIN = new PinyinRomanizationType("Yale");
    protected String tagName;

    protected PinyinRomanizationType(String tagName2) {
        setTagName(tagName2);
    }

    /* access modifiers changed from: package-private */
    public String getTagName() {
        return this.tagName;
    }

    /* access modifiers changed from: protected */
    public void setTagName(String tagName2) {
        this.tagName = tagName2;
    }
}
