package com.devdroid.sleepassistant.utils;


import com.devdroid.sleepassistant.pinyinhelper.Pinyin;

/**
 * @author chenguandong
 * @date 2019/10/23
 * 2020/07/01 重构 by jinzequn
 */
public class PinyinUtils {

    /**
     * 单汉字转拼音
     *
     * @param ch 汉字字符
     * @return 拼音
     */
    public static String getPinyin(char ch) {
        return Pinyin.toPinyin(ch).toLowerCase();
    }

    /**
     * 汉字转拼音
     *
     * @param ccs 汉字字符串(Chinese characters)
     * @return 拼音
     */
    public static String getPinyin(String ccs) {
        return getPinyin(ccs, " ");
    }

    /**
     * 汉字转拼音
     *
     * @param ccs   汉字字符串(Chinese characters)
     * @param split 汉字拼音之间的分隔符
     * @return 拼音
     */
    public static String getPinyin(String ccs, String split) {
        if (ccs == null || ccs.length() == 0) return null;
        return Pinyin.toPinyin(ccs, split).toLowerCase();
    }

    /**
     * 获取第一个汉字首字母
     *
     * @param ccs 汉字字符串(Chinese characters)
     * @return 拼音
     */
    public static String getPinyinFirstLetter(CharSequence ccs) {
        if (ccs == null || ccs.length() == 0) return null;
        return getPinyin(ccs.charAt(0)).substring(0, 1);
    }

    /**
     * 获取所有汉字的首字母
     *
     * @param ccs 汉字字符串(Chinese characters)
     * @return 所有汉字的首字母
     */
    public static String getPinyinAllFirstLetters(CharSequence ccs) {
        return getPinyinAllFirstLetters(ccs, null);
    }

    /**
     * 获取所有汉字的首字母
     *
     * @param ccs   汉字字符串(Chinese characters)
     * @param split 首字母之间的分隔符
     * @return 所有汉字的首字母
     */
    public static String getPinyinAllFirstLetters(CharSequence ccs, CharSequence split) {
        if (ccs == null || ccs.length() == 0) {
            return null;
        }
        int len = ccs.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(getPinyin(ccs.charAt(i)), 0, 1);
            if (split != null) {
                sb.append(split);
            }
        }
        return sb.toString();
    }
}
