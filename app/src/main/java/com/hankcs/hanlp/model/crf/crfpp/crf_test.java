package com.hankcs.hanlp.model.crf.crfpp;


import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.model.perceptron.cli.Args;
import com.hankcs.hanlp.model.perceptron.cli.Argument;

import java.io.*;
import java.util.List;

/**
 * 对应crf_test
 *
 * @author zhifac
 */
public class crf_test
{
    private static class Option
    {
        @Argument(description = "set FILE for model file", alias = "m", required = true)
        String model;
        @Argument(description = "output n-best results", alias = "n")
        Integer nbest = 0;
        @Argument(description = "set INT for verbose level", alias = "v")
        Integer verbose = 0;
        @Argument(description = "set cost factor", alias = "c")
        Double cost_factor = 1.0;
        @Argument(description = "output file path", alias = "o")
        String output;
        @Argument(description = "show this help and exit", alias = "h")
        Boolean help = false;
    }
}
