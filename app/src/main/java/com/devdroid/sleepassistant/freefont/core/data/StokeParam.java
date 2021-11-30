package com.devdroid.sleepassistant.freefont.core.data;

import android.graphics.Paint;

import com.devdroid.sleepassistant.freefont.core.annotation.Description;

public class StokeParam{

    @Description(name = "描边宽度")
    public float width;

    @Description(name = "边角锐度" , cls = Paint.Join.class)
    public String join;

}