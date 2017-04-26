package com.devdroid.sleepassistant.utils;

/**
 * Created by Gaolei on 2017/4/26.
 */

public class ByteUtils {
    public static byte[] int2byte(int res) {
        int l = res / 256;
        int m = res % 256;
        byte[] targets = new byte[]{(byte)m, (byte)l};
        return targets;
    }

    public static int byte2int(byte[] res) {
        int b1 = res[0] & 255;
        int b2 = res[1] & 255;
        int t1 = b1 + b2 * 256;
        return t1;
    }
}
