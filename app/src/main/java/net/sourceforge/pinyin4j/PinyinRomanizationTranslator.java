package net.sourceforge.pinyin4j;

import com.p000hp.hpl.sparta.Element;
import com.p000hp.hpl.sparta.ParseException;

class PinyinRomanizationTranslator {
    PinyinRomanizationTranslator() {
    }

    static String convertRomanizationSystem(String sourcePinyinStr, PinyinRomanizationType sourcePinyinSystem, PinyinRomanizationType targetPinyinSystem) {
        String pinyinString = TextHelper.extractPinyinString(sourcePinyinStr);
        String toneNumberStr = TextHelper.extractToneNumber(sourcePinyinStr);
        try {
            Element hanyuNode = PinyinRomanizationResource.getInstance().getPinyinMappingDoc().xpathSelectElement("//" + sourcePinyinSystem.getTagName() + "[text()='" + pinyinString + "']");
            if (hanyuNode == null) {
                return null;
            }
            return hanyuNode.xpathSelectString("../" + targetPinyinSystem.getTagName() + "/text()") + toneNumberStr;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
