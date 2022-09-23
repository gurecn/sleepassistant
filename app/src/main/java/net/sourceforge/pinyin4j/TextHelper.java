package net.sourceforge.pinyin4j;

class TextHelper {
    TextHelper() {
    }

    static String extractToneNumber(String hanyuPinyinWithToneNumber) {
        return hanyuPinyinWithToneNumber.substring(hanyuPinyinWithToneNumber.length() - 1);
    }

    static String extractPinyinString(String hanyuPinyinWithToneNumber) {
        return hanyuPinyinWithToneNumber.substring(0, hanyuPinyinWithToneNumber.length() - 1);
    }
}
