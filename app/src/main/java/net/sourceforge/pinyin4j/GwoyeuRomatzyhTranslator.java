package net.sourceforge.pinyin4j;

import com.p000hp.hpl.sparta.Element;
import com.p000hp.hpl.sparta.ParseException;

class GwoyeuRomatzyhTranslator {
    private static String[] tones = {"_I", "_II", "_III", "_IV", "_V"};

    GwoyeuRomatzyhTranslator() {
    }

    static String convertHanyuPinyinToGwoyeuRomatzyh(String hanyuPinyinStr) {
        String pinyinString = TextHelper.extractPinyinString(hanyuPinyinStr);
        String toneNumberStr = TextHelper.extractToneNumber(hanyuPinyinStr);
        try {
            Element hanyuNode = GwoyeuRomatzyhResource.getInstance().getPinyinToGwoyeuMappingDoc().xpathSelectElement("//" + PinyinRomanizationType.HANYU_PINYIN.getTagName() + "[text()='" + pinyinString + "']");
            if (hanyuNode != null) {
                return hanyuNode.xpathSelectString("../" + PinyinRomanizationType.GWOYEU_ROMATZYH.getTagName() + tones[Integer.parseInt(toneNumberStr) - 1] + "/text()");
            }
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
