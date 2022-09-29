/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/10/17 19:02</create-date>
 *
 * <copyright file="HanLP.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package com.devdroid.hanlp;

import android.content.Context;

import com.devdroid.hanlp.corpus.dependency.CoNll.CoNLLSentence;
import com.devdroid.hanlp.corpus.io.IIOAdapter;
import com.devdroid.hanlp.dependency.nnparser.NeuralNetworkDependencyParser;
import com.devdroid.hanlp.dictionary.py.Pinyin;
import com.devdroid.hanlp.dictionary.py.PinyinDictionary;
import com.devdroid.hanlp.dictionary.ts.*;
import com.devdroid.hanlp.mining.phrase.IPhraseExtractor;
import com.devdroid.hanlp.mining.phrase.MutualInformationEntropyPhraseExtractor;
import com.devdroid.hanlp.mining.word.NewWordDiscover;
import com.devdroid.hanlp.mining.word.WordInfo;
import com.devdroid.hanlp.model.crf.CRFLexicalAnalyzer;
import com.devdroid.hanlp.model.perceptron.PerceptronLexicalAnalyzer;
import com.devdroid.hanlp.seg.HMM.HMMSegment;
import com.devdroid.hanlp.seg.NShort.NShortSegment;
import com.devdroid.hanlp.seg.Other.DoubleArrayTrieSegment;
import com.devdroid.hanlp.seg.Segment;
import com.devdroid.hanlp.seg.Viterbi.ViterbiSegment;
import com.devdroid.hanlp.seg.common.Term;
import com.devdroid.hanlp.summary.TextRankKeyword;
import com.devdroid.hanlp.summary.TextRankSentence;
import com.devdroid.hanlp.tokenizer.StandardTokenizer;
import com.devdroid.sleepassistant.utils.Logger;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import static com.devdroid.hanlp.utility.Predefine.logger;

/**
 * HanLP: Han Language Processing <br>
 * 汉语言处理包 <br>
 * 常用接口工具类
 *
 * @author hankcs
 */
public class HanLP {
    /**
     * 开发模式
     */
    public static boolean DEBUG = false;
    public static String CustomDictionaryPath[];
    public static String CoreDictionaryPath,CoreDictionaryTransformMatrixDictionaryPath,BiGramDictionaryPath,CoreStopWordDictionaryPath,CoreSynonymDictionaryDictionaryPath,PersonDictionaryPath;
    public static String PersonDictionaryTrPath,PlaceDictionaryPath,PlaceDictionaryTrPath,OrganizationDictionaryPath,OrganizationDictionaryTrPath,tcDictionaryRoot,
        PinyinDictionaryPath,JapanesePersonDictionaryPath,TranslatedPersonDictionaryPath,PartOfSpeechTagDictionary;
    public static String CharTypePath,CharTablePath,WordNatureModelPath,MaxEntModelPath,NNParserModelPath,CRFSegmentModelPath,HMMSegmentModelPath;
    public static String CRFCWSModelPath,CRFPOSModelPath,CRFNERModelPath,PerceptronCWSModelPath,PerceptronPOSModelPath,PerceptronNERModelPath;
    public static boolean ShowTermNature;
    public static boolean Normalization = false;

    public static IIOAdapter IOAdapter;

    /**
     * 加载配置
     */
    public static void loadProperties(Context context, String hanlpPath) {

        // 自动读取配置
        Properties p = new Properties();
        try {
            InputStream inputStream = context.getAssets().open(hanlpPath);
            p.load(inputStream);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            logger.severe("没有找到hanlp.properties，可能会导致找不到data");
            return;
        }
        try {
            CoreDictionaryPath = p.getProperty("CoreDictionaryPath", CoreDictionaryPath);
            CoreDictionaryTransformMatrixDictionaryPath = p.getProperty("CoreDictionaryTransformMatrixDictionaryPath", CoreDictionaryTransformMatrixDictionaryPath);
            BiGramDictionaryPath = p.getProperty("BiGramDictionaryPath", BiGramDictionaryPath);
            CoreStopWordDictionaryPath = p.getProperty("CoreStopWordDictionaryPath", CoreStopWordDictionaryPath);
            CoreSynonymDictionaryDictionaryPath = p.getProperty("CoreSynonymDictionaryDictionaryPath", CoreSynonymDictionaryDictionaryPath);
            PersonDictionaryPath = p.getProperty("PersonDictionaryPath", PersonDictionaryPath);
            PersonDictionaryTrPath = p.getProperty("PersonDictionaryTrPath", PersonDictionaryTrPath);
            CustomDictionaryPath = p.getProperty("CustomDictionaryPath", "hanlp/data/dictionary/custom/CustomDictionary.txt").split(";");
            tcDictionaryRoot = p.getProperty("tcDictionaryRoot", tcDictionaryRoot);
            if (tcDictionaryRoot != null && !tcDictionaryRoot.endsWith("/")) tcDictionaryRoot += '/';
            PinyinDictionaryPath = p.getProperty("PinyinDictionaryPath", PinyinDictionaryPath);
            TranslatedPersonDictionaryPath = p.getProperty("TranslatedPersonDictionaryPath", TranslatedPersonDictionaryPath);
            JapanesePersonDictionaryPath = p.getProperty("JapanesePersonDictionaryPath", JapanesePersonDictionaryPath);
            PlaceDictionaryPath = p.getProperty("PlaceDictionaryPath", PlaceDictionaryPath);
            PlaceDictionaryTrPath = p.getProperty("PlaceDictionaryTrPath", PlaceDictionaryTrPath);
            OrganizationDictionaryPath = p.getProperty("OrganizationDictionaryPath", OrganizationDictionaryPath);
            OrganizationDictionaryTrPath = p.getProperty("OrganizationDictionaryTrPath", OrganizationDictionaryTrPath);
            CharTypePath = p.getProperty("CharTypePath", CharTypePath);
            CharTablePath = p.getProperty("CharTablePath", CharTablePath);
            PartOfSpeechTagDictionary = p.getProperty("PartOfSpeechTagDictionary", PartOfSpeechTagDictionary);
            WordNatureModelPath = p.getProperty("WordNatureModelPath", WordNatureModelPath);
            MaxEntModelPath = p.getProperty("MaxEntModelPath", MaxEntModelPath);
            NNParserModelPath = p.getProperty("NNParserModelPath", NNParserModelPath);
            CRFSegmentModelPath = p.getProperty("CRFSegmentModelPath", CRFSegmentModelPath);
            HMMSegmentModelPath = p.getProperty("HMMSegmentModelPath", HMMSegmentModelPath);
            CRFCWSModelPath = p.getProperty("CRFCWSModelPath", CRFCWSModelPath);
            CRFPOSModelPath = p.getProperty("CRFPOSModelPath", CRFPOSModelPath);
            CRFNERModelPath = p.getProperty("CRFNERModelPath", CRFNERModelPath);
            PerceptronCWSModelPath = p.getProperty("PerceptronCWSModelPath", PerceptronCWSModelPath);
            PerceptronPOSModelPath = p.getProperty("PerceptronPOSModelPath", PerceptronPOSModelPath);
            PerceptronNERModelPath = p.getProperty("PerceptronNERModelPath", PerceptronNERModelPath);
            ShowTermNature = "true".equals(p.getProperty("ShowTermNature", "true"));
            Normalization = "true".equals(p.getProperty("Normalization", "false"));
            IOAdapter = new IIOAdapter() {
                @Override
                public InputStream open(String path) throws IOException {
                    Logger.d("1111111111","IIOAdapter open：" + path);
                    return context.getAssets().open(path);
                }

                @Override
                public OutputStream create(String path) {
                    Logger.d("1111111111","IIOAdapter create：" + path);
                    throw new IllegalAccessError("不支持写入" + path);
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启调试模式(会降低性能)
     */
    public static void enableDebug()
    {
        enableDebug(true);
    }

    /**
     * 开启调试模式(会降低性能)
     *
     * @param enable
     */
    public static void enableDebug(boolean enable)
    {
        DEBUG = enable;
        if (DEBUG)
        {
            logger.setLevel(Level.ALL);
        }
        else
        {
            logger.setLevel(Level.OFF);
        }
    }

    /**
     * 工具类，不需要生成实例
     */
    private HanLP() {
    }

    /**
     * 繁转简
     *
     * @param traditionalChineseString 繁体中文
     * @return 简体中文
     */
    public static String convertToSimplifiedChinese(String traditionalChineseString) {
        return TraditionalChineseDictionary.convertToSimplifiedChinese(traditionalChineseString.toCharArray());
    }

    /**
     * 简转繁
     *
     * @param simplifiedChineseString 简体中文
     * @return 繁体中文
     */
    public static String convertToTraditionalChinese(String simplifiedChineseString) {
        return SimplifiedChineseDictionary.convertToTraditionalChinese(simplifiedChineseString.toCharArray());
    }

    /**
     * 简转繁,是{@link HanLP#convertToTraditionalChinese(String)}的简称
     *
     * @param s 简体中文
     * @return 繁体中文(大陆标准)
     */
    public static String s2t(String s) {
        return HanLP.convertToTraditionalChinese(s);
    }

    /**
     * 繁转简,是{@link HanLP#convertToSimplifiedChinese(String)}的简称
     *
     * @param t 繁体中文(大陆标准)
     * @return 简体中文
     */
    public static String t2s(String t) {
        return HanLP.convertToSimplifiedChinese(t);
    }

    /**
     * 簡體到臺灣正體
     *
     * @param s 簡體
     * @return 臺灣正體
     */
    public static String s2tw(String s) {
        return SimplifiedToTaiwanChineseDictionary.convertToTraditionalTaiwanChinese(s);
    }

    /**
     * 臺灣正體到簡體
     *
     * @param tw 臺灣正體
     * @return 簡體
     */
    public static String tw2s(String tw) {
        return TaiwanToSimplifiedChineseDictionary.convertToSimplifiedChinese(tw);
    }

    /**
     * 簡體到香港繁體
     *
     * @param s 簡體
     * @return 香港繁體
     */
    public static String s2hk(String s) {
        return SimplifiedToHongKongChineseDictionary.convertToTraditionalHongKongChinese(s);
    }

    /**
     * 香港繁體到簡體
     *
     * @param hk 香港繁體
     * @return 簡體
     */
    public static String hk2s(String hk) {
        return HongKongToSimplifiedChineseDictionary.convertToSimplifiedChinese(hk);
    }

    /**
     * 繁體到臺灣正體
     *
     * @param t 繁體
     * @return 臺灣正體
     */
    public static String t2tw(String t) {
        return TraditionalToTaiwanChineseDictionary.convertToTaiwanChinese(t);
    }

    /**
     * 臺灣正體到繁體
     *
     * @param tw 臺灣正體
     * @return 繁體
     */
    public static String tw2t(String tw) {
        return TaiwanToTraditionalChineseDictionary.convertToTraditionalChinese(tw);
    }

    /**
     * 繁體到香港繁體
     *
     * @param t 繁體
     * @return 香港繁體
     */
    public static String t2hk(String t) {
        return TraditionalToHongKongChineseDictionary.convertToHongKongTraditionalChinese(t);
    }

    /**
     * 香港繁體到繁體
     *
     * @param hk 香港繁體
     * @return 繁體
     */
    public static String hk2t(String hk) {
        return HongKongToTraditionalChineseDictionary.convertToTraditionalChinese(hk);
    }

    /**
     * 香港繁體到臺灣正體
     *
     * @param hk 香港繁體
     * @return 臺灣正體
     */
    public static String hk2tw(String hk) {
        return HongKongToTaiwanChineseDictionary.convertToTraditionalTaiwanChinese(hk);
    }

    /**
     * 臺灣正體到香港繁體
     *
     * @param tw 臺灣正體
     * @return 香港繁體
     */
    public static String tw2hk(String tw) {
        return TaiwanToHongKongChineseDictionary.convertToTraditionalHongKongChinese(tw);
    }

    /**
     * 转化为拼音
     *
     * @param text       文本
     * @param separator  分隔符
     * @param remainNone 有些字没有拼音（如标点），是否保留它们的拼音（true用none表示，false用原字符表示）
     * @return 一个字符串，由[拼音][分隔符][拼音]构成
     */
    public static String convertToPinyinString(String text, String separator, boolean remainNone) {
        List<Pinyin> pinyinList = PinyinDictionary.convertToPinyin(text, true);
        int length = pinyinList.size();
        StringBuilder sb = new StringBuilder(length * (5 + separator.length()));
        int i = 1;
        for (Pinyin pinyin : pinyinList)
        {

            if (pinyin == Pinyin.none5 && !remainNone)
            {
                sb.append(text.charAt(i - 1));
            }
            else sb.append(pinyin.getPinyinWithToneMark());
            if (i < length)
            {
                sb.append(separator);
            }
            ++i;
        }
        return sb.toString();
    }

    /**
     * 转化为拼音
     *
     * @param text 待解析的文本
     * @return 一个拼音列表
     */
    public static List<Pinyin> convertToPinyinList(String text) {
        return PinyinDictionary.convertToPinyin(text);
    }

    /**
     * 转化为拼音（首字母）
     *
     * @param text       文本
     * @param separator  分隔符
     * @param remainNone 有些字没有拼音（如标点），是否保留它们（用none表示）
     * @return 一个字符串，由[首字母][分隔符][首字母]构成
     */
    public static String convertToPinyinFirstCharString(String text, String separator, boolean remainNone) {
        List<Pinyin> pinyinList = PinyinDictionary.convertToPinyin(text, remainNone);
        int length = pinyinList.size();
        StringBuilder sb = new StringBuilder(length * (1 + separator.length()));
        int i = 1;
        for (Pinyin pinyin : pinyinList)
        {
            sb.append(pinyin.getFirstChar());
            if (i < length)
            {
                sb.append(separator);
            }
            ++i;
        }
        return sb.toString();
    }

    /**
     * 分词
     *
     * @param text 文本
     * @return 切分后的单词
     */
    public static List<Term> segment(String text) {
        return StandardTokenizer.segment(text.toCharArray());
    }

    /**
     * 创建一个分词器<br>
     * 这是一个工厂方法<br>
     * 与直接new一个分词器相比，使用本方法的好处是，以后HanLP升级了，总能用上最合适的分词器
     *
     * @return 一个分词器
     */
    public static Segment newSegment() {
        return new ViterbiSegment();   // Viterbi分词器是目前效率和效果的最佳平衡
    }

    /**
     * 创建一个分词器，
     * 这是一个工厂方法<br>
     *
     * @param algorithm 分词算法，传入算法的中英文名都可以，可选列表：<br>
     *                  <ul>
     *                  <li>维特比 (viterbi)：效率和效果的最佳平衡</li>
     *                  <li>双数组trie树 (dat)：极速词典分词，千万字符每秒</li>
     *                  <li>条件随机场 (crf)：分词、词性标注与命名实体识别精度都较高，适合要求较高的NLP任务</li>
     *                  <li>感知机 (perceptron)：分词、词性标注与命名实体识别，支持在线学习</li>
     *                  <li>N最短路 (nshort)：命名实体识别稍微好一些，牺牲了速度</li>
     *                  <li>2阶隐马 (hmm2)：训练速度较CRF快</li>
     *                  </ul>
     * @return 一个分词器
     */
    public static Segment newSegment(String algorithm) {
        if (algorithm == null)
        {
            throw new IllegalArgumentException(String.format("非法参数 algorithm == %s", algorithm));
        }
        algorithm = algorithm.toLowerCase();
        if ("viterbi".equals(algorithm) || "维特比".equals(algorithm))
            return new ViterbiSegment();   // Viterbi分词器是目前效率和效果的最佳平衡
        else if ("dat".equals(algorithm) || "双数组trie树".equals(algorithm))
            return new DoubleArrayTrieSegment();
        else if ("nshort".equals(algorithm) || "n最短路".equals(algorithm))
            return new NShortSegment();
        else if ("crf".equals(algorithm) || "条件随机场".equals(algorithm))
            try
            {
                return new CRFLexicalAnalyzer();
            }
            catch (IOException e)
            {
                logger.warning("CRF模型加载失败");
                throw new RuntimeException(e);
            }
        else if ("hmm2".equals(algorithm) || "二阶隐马".equals(algorithm))
            return new HMMSegment();
        else if ("perceptron".equals(algorithm) || "感知机".equals(algorithm))
        {
            try
            {
                return new PerceptronLexicalAnalyzer();
            }
            catch (IOException e)
            {
                logger.warning("感知机模型加载失败");
                throw new RuntimeException(e);
            }
        }
        throw new IllegalArgumentException(String.format("非法参数 algorithm == %s", algorithm));
    }

    /**
     * 依存文法分析
     *
     * @param sentence 待分析的句子
     * @return CoNLL格式的依存关系树
     */
    public static CoNLLSentence parseDependency(String sentence) {
        return NeuralNetworkDependencyParser.compute(sentence);
    }

    /**
     * 提取短语
     *
     * @param text 文本
     * @param size 需要多少个短语
     * @return 一个短语列表，大小 <= size
     */
    public static List<String> extractPhrase(String text, int size) {
        IPhraseExtractor extractor = new MutualInformationEntropyPhraseExtractor();
        return extractor.extractPhrase(text, size);
    }

    /**
     * 提取词语
     *
     * @param text 大文本
     * @param size 需要提取词语的数量
     * @return 一个词语列表
     */
    public static List<WordInfo> extractWords(String text, int size) {
        return extractWords(text, size, false);
    }

    /**
     * 提取词语
     *
     * @param reader 从reader获取文本
     * @param size   需要提取词语的数量
     * @return 一个词语列表
     */
    public static List<WordInfo> extractWords(BufferedReader reader, int size) throws IOException {
        return extractWords(reader, size, false);
    }

    /**
     * 提取词语（新词发现）
     *
     * @param text         大文本
     * @param size         需要提取词语的数量
     * @param newWordsOnly 是否只提取词典中没有的词语
     * @return 一个词语列表
     */
    public static List<WordInfo> extractWords(String text, int size, boolean newWordsOnly) {
        NewWordDiscover discover = new NewWordDiscover(4, 0.0f, .5f, 100f, newWordsOnly);
        return discover.discover(text, size);
    }

    /**
     * 提取词语（新词发现）
     *
     * @param reader       从reader获取文本
     * @param size         需要提取词语的数量
     * @param newWordsOnly 是否只提取词典中没有的词语
     * @return 一个词语列表
     */
    public static List<WordInfo> extractWords(BufferedReader reader, int size, boolean newWordsOnly) throws IOException {
        NewWordDiscover discover = new NewWordDiscover(4, 0.0f, .5f, 100f, newWordsOnly);
        return discover.discover(reader, size);
    }

    /**
     * 提取词语（新词发现）
     *
     * @param reader          从reader获取文本
     * @param size            需要提取词语的数量
     * @param newWordsOnly    是否只提取词典中没有的词语
     * @param max_word_len    词语最长长度
     * @param min_freq        词语最低频率
     * @param min_entropy     词语最低熵
     * @param min_aggregation 词语最低互信息
     * @return 一个词语列表
     */
    public static List<WordInfo> extractWords(BufferedReader reader, int size, boolean newWordsOnly, int max_word_len, float min_freq, float min_entropy, float min_aggregation) throws IOException {
        NewWordDiscover discover = new NewWordDiscover(max_word_len, min_freq, min_entropy, min_aggregation, newWordsOnly);
        return discover.discover(reader, size);
    }

    /**
     * 提取关键词
     *
     * @param document 文档内容
     * @param size     希望提取几个关键词
     * @return 一个列表
     */
    public static List<String> extractKeyword(String document, int size) {
        return TextRankKeyword.getKeywordList(document, size);
    }

    /**
     * 自动摘要
     * 分割目标文档时的默认句子分割符为，,。:：“”？?！!；;
     *
     * @param document 目标文档
     * @param size     需要的关键句的个数
     * @return 关键句列表
     */
    public static List<String> extractSummary(String document, int size) {
        return TextRankSentence.getTopSentenceList(document, size);
    }

    /**
     * 自动摘要
     * 分割目标文档时的默认句子分割符为，,。:：“”？?！!；;
     *
     * @param document   目标文档
     * @param max_length 需要摘要的长度
     * @return 摘要文本
     */
    public static String getSummary(String document, int max_length) {
        // Parameter size in this method refers to the string length of the summary required;
        // The actual length of the summary generated may be short than the required length, but never longer;
        return TextRankSentence.getSummary(document, max_length);
    }

    /**
     * 自动摘要
     *
     * @param document           目标文档
     * @param size               需要的关键句的个数
     * @param sentence_separator 分割目标文档时的句子分割符，正则格式， 如：[。？?！!；;]
     * @return 关键句列表
     */
    public static List<String> extractSummary(String document, int size, String sentence_separator) {
        return TextRankSentence.getTopSentenceList(document, size, sentence_separator);
    }

    /**
     * 自动摘要
     *
     * @param document           目标文档
     * @param max_length         需要摘要的长度
     * @param sentence_separator 分割目标文档时的句子分割符，正则格式， 如：[。？?！!；;]
     * @return 摘要文本
     */
    public static String getSummary(String document, int max_length, String sentence_separator) {
        // Parameter size in this method refers to the string length of the summary required;
        // The actual length of the summary generated may be short than the required length, but never longer;
        return TextRankSentence.getSummary(document, max_length, sentence_separator);
    }

}
