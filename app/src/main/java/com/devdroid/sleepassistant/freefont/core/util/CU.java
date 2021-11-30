package com.devdroid.sleepassistant.freefont.core.util;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import java.util.regex.Pattern;

/**
 * Created by zhaolei on 2017/10/21.
 */

public class CU {

    public static int toInt(String color){
        if(color.startsWith("#")){
            return Color.parseColor(color);
        }else if(!isNumeric(color) || color.length() == 6 || color.length() == 8){
            return Color.parseColor("#"+color);
        } else {
            return Integer.valueOf(color);
        }
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[-0-9]*");
        return pattern.matcher(str).matches();
    }

    public static int[] toInt(String[] colors){
        int[] result = new int[colors.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = toInt(colors[i]);
        }
        return result;
    }

    public static String toString(int color){
       return Integer.toHexString(color);
    }

    public static void filterDrawable(Drawable drawable,int color){
        if(drawable==null) return;
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
    }


}
