/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/9/15 19:38</create-date>
 *
 * <copyright file="StopwordDictionary.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package com.devdroid.hanlp.dictionary.stopword;

import com.devdroid.hanlp.collection.MDAG.MDAGSet;
import com.devdroid.hanlp.seg.common.Term;

import java.io.*;
import java.util.Collection;

/**
 * @author hankcs
 */
public class StopWordDictionary extends MDAGSet implements Filter
{
    public StopWordDictionary(File file) throws IOException
    {
        super(file);
    }

    public StopWordDictionary(Collection<String> strCollection)
    {
        super(strCollection);
    }

    public StopWordDictionary()
    {
    }

    public StopWordDictionary(String stopWordDictionaryPath) throws IOException
    {
        super(stopWordDictionaryPath);
    }

    @Override
    public boolean shouldInclude(Term term)
    {
        return contains(term.word);
    }
}
