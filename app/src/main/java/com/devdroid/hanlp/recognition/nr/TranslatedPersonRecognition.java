/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/11/12 16:47</create-date>
 *
 * <copyright file="TranslatedPersonRecognition.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package com.devdroid.hanlp.recognition.nr;

import com.devdroid.hanlp.HanLP;
import com.devdroid.hanlp.corpus.tag.Nature;
import com.devdroid.hanlp.dictionary.CoreDictionary;
import com.devdroid.hanlp.dictionary.nr.TranslatedPersonDictionary;
import com.devdroid.hanlp.seg.common.Vertex;
import com.devdroid.hanlp.seg.common.WordNet;
import com.devdroid.hanlp.utility.Predefine;

import java.util.List;
import java.util.ListIterator;

import static com.devdroid.hanlp.dictionary.nr.NRConstant.WORD_ID;

/**
 * 音译人名识别
 * @author hankcs
 */
public class TranslatedPersonRecognition
{
    /**
     * 执行识别
     * @param segResult 粗分结果
     * @param wordNetOptimum 粗分结果对应的词图
     * @param wordNetAll 全词图
     */
    public static void recognition(List<Vertex> segResult, WordNet wordNetOptimum, WordNet wordNetAll)
    {
        StringBuilder sbName = new StringBuilder();
        int appendTimes = 0;
        ListIterator<Vertex> listIterator = segResult.listIterator();
        listIterator.next();
        int line = 1;
        int activeLine = 1;
        while (listIterator.hasNext())
        {
            Vertex vertex = listIterator.next();
            if (appendTimes > 0)
            {
                if (vertex.guessNature() == Nature.nrf || TranslatedPersonDictionary.containsKey(vertex.realWord))
                {
                    sbName.append(vertex.realWord);
                    ++appendTimes;
                }
                else
                {
                    // 识别结束
                    if (appendTimes > 1)
                    {
                        if (HanLP.Config.DEBUG)
                        {
                            System.out.println("音译人名识别出：" + sbName.toString());
                        }
                        wordNetOptimum.insert(activeLine, new Vertex(Predefine.TAG_PEOPLE, sbName.toString(), new CoreDictionary.Attribute(Nature.nrf), WORD_ID), wordNetAll);
                    }
                    sbName.setLength(0);
                    appendTimes = 0;
                }
            }
            else
            {
                // nrf触发识别
                if (vertex.guessNature() == Nature.nrf
//                        || TranslatedPersonDictionary.containsKey(vertex.realWord)
                        )
                {
                    sbName.append(vertex.realWord);
                    ++appendTimes;
                    activeLine = line;
                }
            }

            line += vertex.realWord.length();
        }
    }
}
