package net.sourceforge.pinyin4j.format;

public class HanyuPinyinToneType {
    public static final HanyuPinyinToneType WITHOUT_TONE = new HanyuPinyinToneType("WITHOUT_TONE");
    public static final HanyuPinyinToneType WITH_TONE_MARK = new HanyuPinyinToneType("WITH_TONE_MARK");
    public static final HanyuPinyinToneType WITH_TONE_NUMBER = new HanyuPinyinToneType("WITH_TONE_NUMBER");
    protected String name;

    public String getName() {
        return this.name;
    }

    /* access modifiers changed from: protected */
    public void setName(String name2) {
        this.name = name2;
    }

    protected HanyuPinyinToneType(String name2) {
        setName(name2);
    }
}
