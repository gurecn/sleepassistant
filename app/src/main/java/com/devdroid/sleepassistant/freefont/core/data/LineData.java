package com.devdroid.sleepassistant.freefont.core.data;

import android.graphics.Color;

import com.devdroid.sleepassistant.freefont.core.linedrawer.Gravity;
import com.devdroid.sleepassistant.freefont.core.annotation.Description;
import com.devdroid.sleepassistant.freefont.core.annotation.Img;

/**
 * Created by zhaolei on 2017/10/17.
 */

public class LineData {

    @Description(name = "相对高度")
    public float rh;

    @Description(name = "位置" ,cls = Gravity.class)
    public String gravity;

    @Description(name = "图片名称",cls = Img.class)
    public String bitmap;

    @Description(name = "颜色" ,cls = Color.class)
    public Integer color;
}
