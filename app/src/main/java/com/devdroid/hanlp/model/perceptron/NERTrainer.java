/*
 * <author>Hankcs</author>
 * <email>me@hankcs.com</email>
 * <create-date>2017-10-28 11:39</create-date>
 *
 * <copyright file="NERTrainer.java" company="码农场">
 * Copyright (c) 2017, 码农场. All Right Reserved, http://www.hankcs.com/
 * This source is subject to Hankcs. Please contact Hankcs to get more information.
 * </copyright>
 */
package com.devdroid.hanlp.model.perceptron;

import com.devdroid.hanlp.model.perceptron.feature.FeatureMap;
import com.devdroid.hanlp.model.perceptron.instance.Instance;
import com.devdroid.hanlp.model.perceptron.instance.NERInstance;
import com.devdroid.hanlp.model.perceptron.tagset.NERTagSet;
import com.devdroid.hanlp.model.perceptron.tagset.TagSet;
import com.devdroid.hanlp.corpus.document.sentence.Sentence;

/**
 * @author hankcs
 */
public class NERTrainer extends PerceptronTrainer
{
    /**
     * 支持任意自定义NER类型，例如：<br>
     * tagSet.nerLabels.clear();<br>
     * tagSet.nerLabels.add("nr");<br>
     * tagSet.nerLabels.add("ns");<br>
     * tagSet.nerLabels.add("nt");<br>
     */
    public NERTagSet tagSet;

    public NERTrainer(NERTagSet tagSet)
    {
        this.tagSet = tagSet;
    }

    public NERTrainer()
    {
        tagSet = new NERTagSet();
        tagSet.nerLabels.add("nr");
        tagSet.nerLabels.add("ns");
        tagSet.nerLabels.add("nt");
    }

    /**
     * 重载此方法以支持任意自定义NER类型，例如：<br>
     * NERTagSet tagSet = new NERTagSet();<br>
     * tagSet.nerLabels.add("nr");<br>
     * tagSet.nerLabels.add("ns");<br>
     * tagSet.nerLabels.add("nt");<br>
     * return tagSet;<br>
     * @return
     */
    @Override
    protected TagSet createTagSet()
    {
        return tagSet;
    }

    @Override
    protected Instance createInstance(Sentence sentence, FeatureMap featureMap)
    {
        return NERInstance.create(sentence, featureMap);
    }
}
