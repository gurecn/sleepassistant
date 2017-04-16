package com.devdroid.sleepassistant.view.chart;

import android.content.Context;
import android.util.AttributeSet;

import org.xclcharts.view.ChartView;

/**
 * 图表View基类View
 * Created by Gaolei on 2017-04-16.
 */
public class BaseChartView extends ChartView {
    public BaseChartView(Context context) {
        super(context);
    }

    public BaseChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
