package net.sourceforge.pinyin4j.format;

public class HanyuPinyinVCharType {
    public static final HanyuPinyinVCharType WITH_U_AND_COLON = new HanyuPinyinVCharType("WITH_U_AND_COLON");
    public static final HanyuPinyinVCharType WITH_U_UNICODE = new HanyuPinyinVCharType("WITH_U_UNICODE");
    public static final HanyuPinyinVCharType WITH_V = new HanyuPinyinVCharType("WITH_V");
    protected String name;

    public String getName() {
        return this.name;
    }

    /* access modifiers changed from: protected */
    public void setName(String name2) {
        this.name = name2;
    }

    protected HanyuPinyinVCharType(String name2) {
        setName(name2);
    }
}
