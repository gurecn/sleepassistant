package com.hankcs.hanlp.model.crf.crfpp;


import com.hankcs.hanlp.model.perceptron.cli.Args;
import com.hankcs.hanlp.model.perceptron.cli.Argument;

import java.util.List;

/**
 * 对应crf_learn
 *
 * @author zhifac
 */
public class crf_learn
{
    public static class Option
    {
        @Argument(description = "use features that occur no less than INT(default 1)", alias = "f")
        public Integer freq = 1;
        @Argument(description = "set INT for max iterations in LBFGS routine(default 10k)", alias = "m")
        public  Integer maxiter = 10000;
        @Argument(description = "set FLOAT for cost parameter(default 1.0)", alias = "c")
        public  Double cost = 1.0;
        @Argument(description = "set FLOAT for termination criterion(default 0.0001)", alias = "e")
        public  Double eta = 0.0001;
        @Argument(description = "convert text model to binary model", alias = "C")
        public  Boolean convert = false;
        @Argument(description = "convert binary model to text model", alias = "T")
        public  Boolean convert_to_text = false;
        @Argument(description = "build also text model file for debugging", alias = "t")
        public  Boolean textmodel = false;
        @Argument(description = "(CRF|CRF-L1|CRF-L2|MIRA)\", \"select training algorithm", alias = "a")
        public  String algorithm = "CRF-L2";
        @Argument(description = "set INT for number of iterations variable needs to be optimal before considered for shrinking. (default 20)", alias = "H")
        public  Integer shrinking_size = 20;
        @Argument(description = "show this help and exit", alias = "h")
        public  Boolean help = false;
        @Argument(description = "number of threads(default auto detect)")
        public  Integer thread = Runtime.getRuntime().availableProcessors();
    }
}
