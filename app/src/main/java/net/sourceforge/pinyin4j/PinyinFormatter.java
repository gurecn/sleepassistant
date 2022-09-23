package net.sourceforge.pinyin4j;

import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

class PinyinFormatter {
    PinyinFormatter() {
    }

    static String formatHanyuPinyin(String pinyinStr, HanyuPinyinOutputFormat outputFormat) throws BadHanyuPinyinOutputFormatCombination {
        if (HanyuPinyinToneType.WITH_TONE_MARK == outputFormat.getToneType() && (HanyuPinyinVCharType.WITH_V == outputFormat.getVCharType() || HanyuPinyinVCharType.WITH_U_AND_COLON == outputFormat.getVCharType())) {
            throw new BadHanyuPinyinOutputFormatCombination("tone marks cannot be added to v or u:");
        }
        if (HanyuPinyinToneType.WITHOUT_TONE == outputFormat.getToneType()) {
            pinyinStr = pinyinStr.replaceAll("[1-5]", "");
        } else if (HanyuPinyinToneType.WITH_TONE_MARK == outputFormat.getToneType()) {
            pinyinStr = convertToneNumber2ToneMark(pinyinStr.replaceAll("u:", "v"));
        }
        if (HanyuPinyinVCharType.WITH_V == outputFormat.getVCharType()) {
            pinyinStr = pinyinStr.replaceAll("u:", "v");
        } else if (HanyuPinyinVCharType.WITH_U_UNICODE == outputFormat.getVCharType()) {
            pinyinStr = pinyinStr.replaceAll("u:", "ü");
        }
        if (HanyuPinyinCaseType.UPPERCASE == outputFormat.getCaseType()) {
            return pinyinStr.toUpperCase();
        }
        return pinyinStr;
    }

    private static String convertToneNumber2ToneMark(String pinyinStr) {
        String lowerCasePinyinStr = pinyinStr.toLowerCase();
        if (!lowerCasePinyinStr.matches("[a-z]*[1-5]?")) {
            return lowerCasePinyinStr;
        }
        char unmarkedVowel = '$';
        int indexOfUnmarkedVowel = -1;
        if (!lowerCasePinyinStr.matches("[a-z]*[1-5]")) {
            return lowerCasePinyinStr.replaceAll("v", "ü");
        }
        int tuneNumber = Character.getNumericValue(lowerCasePinyinStr.charAt(lowerCasePinyinStr.length() - 1));
        int indexOfA = lowerCasePinyinStr.indexOf(97);
        int indexOfE = lowerCasePinyinStr.indexOf(101);
        int ouIndex = lowerCasePinyinStr.indexOf("ou");
        if (-1 != indexOfA) {
            indexOfUnmarkedVowel = indexOfA;
            unmarkedVowel = 'a';
        } else if (-1 != indexOfE) {
            indexOfUnmarkedVowel = indexOfE;
            unmarkedVowel = 'e';
        } else if (-1 != ouIndex) {
            indexOfUnmarkedVowel = ouIndex;
            unmarkedVowel = "ou".charAt(0);
        } else {
            int i = lowerCasePinyinStr.length() - 1;
            while (true) {
                if (i < 0) {
                    break;
                } else if (String.valueOf(lowerCasePinyinStr.charAt(i)).matches("[aeiouv]")) {
                    indexOfUnmarkedVowel = i;
                    unmarkedVowel = lowerCasePinyinStr.charAt(i);
                    break;
                } else {
                    i--;
                }
            }
        }
        if ('$' == unmarkedVowel || -1 == indexOfUnmarkedVowel) {
            return lowerCasePinyinStr;
        }
        return lowerCasePinyinStr.substring(0, indexOfUnmarkedVowel).replaceAll("v", "ü") + "āáăàaēéĕèeīíĭìiōóŏòoūúŭùuǖǘǚǜü".charAt(("aeiouv".indexOf(unmarkedVowel) * 5) + (tuneNumber - 1)) + lowerCasePinyinStr.substring(indexOfUnmarkedVowel + 1, lowerCasePinyinStr.length() - 1).replaceAll("v", "ü");
    }
}
