package com.devdroid.sleepassistant.utils;

import android.os.Build;
import static android.os.Build.VERSION_CODES.LOLLIPOP;

/**
 * @author jiangxuwen
 */
public class Machine {
    private static final int SDK_VERSION_CODE_5_1_1 = 22;

    // SDK 版本判断
    private static final int SDK_VERSION = Build.VERSION.SDK_INT;
    /**
     * SDK >= 21 android版本是否为5.0以上
     */
    public static final boolean HAS_SDK_LOLLIPOP = SDK_VERSION >= LOLLIPOP;
    /**
     * SDK >= 22 android版本是否为5.1(5.1.1)以上
     */
    public static final boolean HAS_SDK_5_1_1 = SDK_VERSION >= SDK_VERSION_CODE_5_1_1;
}
