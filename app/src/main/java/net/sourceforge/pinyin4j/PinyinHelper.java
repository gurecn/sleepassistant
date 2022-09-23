package net.sourceforge.pinyin4j;

import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import net.sourceforge.pinyin4j.multipinyin.Trie;

public class PinyinHelper {
    private static final String[] ARR_EMPTY = new String[0];
    private static final String EMPTY = "";

    public static String[] toHanyuPinyinStringArray(char ch) {
        return getUnformattedHanyuPinyinStringArray(ch);
    }

    public static String[] toHanyuPinyinStringArray(char ch, HanyuPinyinOutputFormat outputFormat) throws BadHanyuPinyinOutputFormatCombination {
        return getFormattedHanyuPinyinStringArray(ch, outputFormat);
    }

    private static String[] getFormattedHanyuPinyinStringArray(char ch, HanyuPinyinOutputFormat outputFormat) throws BadHanyuPinyinOutputFormatCombination {
        String[] pinyinStrArray = getUnformattedHanyuPinyinStringArray(ch);
        if (pinyinStrArray == null) {
            return ARR_EMPTY;
        }
        for (int i = 0; i < pinyinStrArray.length; i++) {
            pinyinStrArray[i] = PinyinFormatter.formatHanyuPinyin(pinyinStrArray[i], outputFormat);
        }
        return pinyinStrArray;
    }

    private static String[] getUnformattedHanyuPinyinStringArray(char ch) {
        return ChineseToPinyinResource.getInstance().getHanyuPinyinStringArray(ch);
    }

    public static String[] toTongyongPinyinStringArray(char ch) {
        return convertToTargetPinyinStringArray(ch, PinyinRomanizationType.TONGYONG_PINYIN);
    }

    public static String[] toWadeGilesPinyinStringArray(char ch) {
        return convertToTargetPinyinStringArray(ch, PinyinRomanizationType.WADEGILES_PINYIN);
    }

    public static String[] toMPS2PinyinStringArray(char ch) {
        return convertToTargetPinyinStringArray(ch, PinyinRomanizationType.MPS2_PINYIN);
    }

    public static String[] toYalePinyinStringArray(char ch) {
        return convertToTargetPinyinStringArray(ch, PinyinRomanizationType.YALE_PINYIN);
    }

    private static String[] convertToTargetPinyinStringArray(char ch, PinyinRomanizationType targetPinyinSystem) {
        String[] hanyuPinyinStringArray = getUnformattedHanyuPinyinStringArray(ch);
        if (hanyuPinyinStringArray == null) {
            return ARR_EMPTY;
        }
        String[] targetPinyinStringArray = new String[hanyuPinyinStringArray.length];
        for (int i = 0; i < hanyuPinyinStringArray.length; i++) {
            targetPinyinStringArray[i] = PinyinRomanizationTranslator.convertRomanizationSystem(hanyuPinyinStringArray[i], PinyinRomanizationType.HANYU_PINYIN, targetPinyinSystem);
        }
        return targetPinyinStringArray;
    }

    public static String[] toGwoyeuRomatzyhStringArray(char ch) {
        return convertToGwoyeuRomatzyhStringArray(ch);
    }

    private static String[] convertToGwoyeuRomatzyhStringArray(char ch) {
        String[] hanyuPinyinStringArray = getUnformattedHanyuPinyinStringArray(ch);
        if (hanyuPinyinStringArray == null) {
            return ARR_EMPTY;
        }
        String[] targetPinyinStringArray = new String[hanyuPinyinStringArray.length];
        for (int i = 0; i < hanyuPinyinStringArray.length; i++) {
            targetPinyinStringArray[i] = GwoyeuRomatzyhTranslator.convertHanyuPinyinToGwoyeuRomatzyh(hanyuPinyinStringArray[i]);
        }
        return targetPinyinStringArray;
    }

    /**
     * Get a string which all Chinese characters are replaced by corresponding
     * main (first) Hanyu Pinyin representation.
     * <p>
     * <p>
     * <b>Special Note</b>: If the return contains "none0", that means that
     * Chinese character is in Unicode CJK talbe, however, it has not
     * pronounciation in Chinese. <b> This interface will be removed in next
     * release. </b>
     *
     * @param str          A given string contains Chinese characters
     * @param outputFormat Describes the desired format of returned Hanyu Pinyin string
     * @param separate     The string is appended after a Chinese character (excluding
     *                     the last Chinese character at the end of sentence). <b>Note!
     *                     Separate will not appear after a non-Chinese character</b>
     * @param retain       Retain the characters that cannot be converted into pinyin characters
     * @return a String identical to the original one but all recognizable
     * Chinese characters are converted into main (first) Hanyu Pinyin
     * representation
     */
    static public String toHanYuPinyinString(String str, HanyuPinyinOutputFormat outputFormat,
        String separate, boolean retain) throws BadHanyuPinyinOutputFormatCombination {
        ChineseToPinyinResource resource = ChineseToPinyinResource.getInstance();
        StringBuilder resultPinyinStrBuf = new StringBuilder();

        char[] chars = str.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            String result = null;//匹配到的最长的结果
            char ch = chars[i];
            Trie currentTrie = resource.getUnicodeToHanyuPinyinTable();
            int success = i;
            int current = i;
            do {
                String hexStr = Integer.toHexString((int) ch).toUpperCase();
                currentTrie = currentTrie.get(hexStr);
                if (currentTrie != null) {
                    if (currentTrie.getPinyin() != null) {
                        result = currentTrie.getPinyin();
                        success = current;
                    }
                    currentTrie = currentTrie.getNextTire();
                }
                current++;
                if (current < chars.length)
                    ch = chars[current];
                else
                    break;
            }
            while (currentTrie != null);

            if (result == null) {//如果在前缀树中没有匹配到，那么它就不能转换为拼音，直接输出或者去掉
                if (retain) resultPinyinStrBuf.append(chars[i]);
            } else {
                String[] pinyinStrArray = resource.parsePinyinString(result);
                if (pinyinStrArray != null) {
                    for (int j = 0; j < pinyinStrArray.length; j++) {
                        resultPinyinStrBuf.append(PinyinFormatter.formatHanyuPinyin(pinyinStrArray[j], outputFormat));
                        if (current < chars.length || (j < pinyinStrArray.length - 1 && i != success)) {//不是最后一个,(也不是拼音的最后一个,并且不是最后匹配成功的)
                            resultPinyinStrBuf.append(separate);
                        }
                        if (i == success)
                            break;
                    }
                }
            }
            i = success;
        }

        return resultPinyinStrBuf.toString();
    }

    private PinyinHelper() {
    }
}
