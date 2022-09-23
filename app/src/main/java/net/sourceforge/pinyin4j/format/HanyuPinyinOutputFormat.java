package net.sourceforge.pinyin4j.format;

public final class HanyuPinyinOutputFormat {
    private HanyuPinyinCaseType caseType;
    private HanyuPinyinToneType toneType;
    private HanyuPinyinVCharType vCharType;

    public HanyuPinyinOutputFormat() {
        restoreDefault();
    }

    public void restoreDefault() {
        this.vCharType = HanyuPinyinVCharType.WITH_U_AND_COLON;
        this.caseType = HanyuPinyinCaseType.LOWERCASE;
        this.toneType = HanyuPinyinToneType.WITH_TONE_NUMBER;
    }

    public HanyuPinyinCaseType getCaseType() {
        return this.caseType;
    }

    public void setCaseType(HanyuPinyinCaseType caseType2) {
        this.caseType = caseType2;
    }

    public HanyuPinyinToneType getToneType() {
        return this.toneType;
    }

    public void setToneType(HanyuPinyinToneType toneType2) {
        this.toneType = toneType2;
    }

    public HanyuPinyinVCharType getVCharType() {
        return this.vCharType;
    }

    public void setVCharType(HanyuPinyinVCharType charType) {
        this.vCharType = charType;
    }
}
