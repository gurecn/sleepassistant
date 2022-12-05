/*
 * <summary></summary>
 * <author>Hankcs</author>
 * <email>me@hankcs.com</email>
 * <create-date>2016-08-30 AM10:29</create-date>
 *
 * <copyright file="SimplifiedToHongKongChineseDictionary.java" company="码农场">
 * Copyright (c) 2008-2016, 码农场. All Right Reserved, http://www.hankcs.com/
 * This source is subject to Hankcs. Please contact Hankcs to get more information.
 * </copyright>
 */
package com.devdroid.hanlp.dictionary.ts;

import com.devdroid.hanlp.HanLP;
import com.devdroid.hanlp.collection.AhoCorasick.AhoCorasickDoubleArrayTrie;

import java.util.TreeMap;

import static com.devdroid.hanlp.utility.Predefine.logger;

/**
 * 繁体转香港繁体
 * @author hankcs
 */
public class TraditionalToHongKongChineseDictionary extends BaseChineseDictionary
{
    static AhoCorasickDoubleArrayTrie<String> trie = new AhoCorasickDoubleArrayTrie<String>();
    static
    {
        long start = System.currentTimeMillis();
        String datPath = HanLP.tcDictionaryRoot + "t2hk";
        if (!loadDat(datPath, trie))
        {
            TreeMap<String, String> t2hk = new TreeMap<String, String>();
            if (!load(t2hk, false, HanLP.tcDictionaryRoot + "t2hk.txt"))
            {
                throw new IllegalArgumentException("繁体转香港繁体加载失败");
            }
            trie.build(t2hk);
            saveDat(datPath, trie, t2hk.entrySet());
        }
        logger.info("繁体转香港繁体加载成功，耗时" + (System.currentTimeMillis() - start) + "ms");
    }

    public static String convertToHongKongTraditionalChinese(String traditionalChineseString)
    {
        return segLongest(traditionalChineseString.toCharArray(), trie);
    }

    public static String convertToHongKongTraditionalChinese(char[] traditionalHongKongChineseString)
    {
        return segLongest(traditionalHongKongChineseString, trie);
    }
}