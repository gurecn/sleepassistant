package com.devdroid.sleepassistant.view.chart;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.util.Log;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.TheApplication;
import com.devdroid.sleepassistant.mode.SleepDataMode;

import com.devdroid.sleepassistant.xclcharts.chart.CustomLineData;
import com.devdroid.sleepassistant.xclcharts.chart.PointD;
import com.devdroid.sleepassistant.xclcharts.chart.SplineChart;
import com.devdroid.sleepassistant.xclcharts.chart.SplineData;
import com.devdroid.sleepassistant.xclcharts.common.DensityUtil;
import com.devdroid.sleepassistant.xclcharts.common.IFormatterTextCallBack;
import com.devdroid.sleepassistant.xclcharts.renderer.XEnum;
import com.devdroid.sleepassistant.xclcharts.renderer.plot.PlotGrid;
import com.devdroid.sleepassistant.xclcharts.view.ChartView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
/**
 * github曲线图
 * Created by Gaolei on 2017/4/13.
 */

public class SplineChartView extends ChartView{
    private SplineChart chart = new SplineChart();
    private LinkedList<String> labels = new LinkedList<>();//分类轴标签集合
    private LinkedList<SplineData> chartData = new LinkedList<>();
    private List<CustomLineData> mCustomLineDataset = new LinkedList<>();
    public SplineChartView(Context context) {
        super(context);
        initView();
    }

    public SplineChartView(Context context, AttributeSet attrs){
        super(context, attrs);
        initView();
    }

    public SplineChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        chartLabels();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //图所占范围大小
        chart.setChartRange(w,h);
    }


    private void chartRender() {
        try {

            //设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
            int [] ltrb = getBarLnDefaultSpadding();
            chart.setPadding(ltrb[0] + DensityUtil.dip2px(this.getContext(), 10), ltrb[1], ltrb[2]+DensityUtil.dip2px(this.getContext(), 20), ltrb[3]);
            chart.getAxisTitle().setLeftTitle("入睡时间(h)");
            chart.getAxisTitle().getLeftTitlePaint().setColor(Color.BLACK);


            //显示边框
            chart.showRoundBorder();

            //数据源
            chart.setCategories(labels);
            chart.setDataSource(chartData);
            chart.setCustomLines(mCustomLineDataset);

            //坐标系
            //数据轴最大值
            chart.getDataAxis().setAxisMax(29);
            chart.getDataAxis().setAxisMin(6);
            //数据轴刻度间隔
            chart.getDataAxis().setAxisSteps(1);

            //背景网格
            PlotGrid plot = chart.getPlotGrid();
            plot.hideHorizontalLines();
            plot.hideVerticalLines();
            chart.getDataAxis().getAxisPaint().setColor(Color.rgb(127, 204, 204));
            chart.getCategoryAxis().getAxisPaint().setColor(Color.rgb(127, 204, 204));
            chart.getDataAxis().getTickMarksPaint().setColor(Color.rgb(127, 204, 204));
            chart.getCategoryAxis().getTickMarksPaint().setColor(Color.rgb(127, 204, 204));
            int tickLabelColor = ContextCompat.getColor(getContext(), R.color.tick_label_color);
            chart.getDataAxis().getTickLabelPaint().setColor(tickLabelColor);
            chart.getDataAxis().setHorizontalTickAlign(Paint.Align.LEFT);

            //定义数据轴标签显示格式
            chart.getDataAxis().setLabelFormatter(new IFormatterTextCallBack(){

                @Override
                public String textFormatter(String value) {
                    Double tmp = Double.parseDouble(value);
                    DecimalFormat df=new DecimalFormat("#0");
                    String label = df.format(tmp).toString();
                    return (label);
                }
            });
            //不使用精确计算，忽略Java计算误差,提高性能
            chart.disableHighPrecision();
            chart.disablePanMode();
            chart.hideBorder();
            chart.getPlotLegend().hide();
            chart.getCategoryAxis().setLabelLineFeed(XEnum.LabelLineFeed.ODD_EVEN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void chartDataSet(List<SleepDataMode> sleepDataModes) {
        //线1的数据集
        List<PointD> linePoint1 = new ArrayList<>();
        int dataCount = sleepDataModes.size();
        for(int i = 0;i < dataCount;i++){
            SleepDataMode sleepDataMode = sleepDataModes.get(i);
            int hour = sleepDataMode.getHour();
            float time = hour + sleepDataMode.getMinute() / 60f;
            linePoint1.add(new PointD((double) i + 1, (double) time));
            int skip = dataCount / 30 + 1;
            if(dataCount <= 30 || i % skip == 0){
                labels.add(sleepDataMode.getDay() + "");
            } else {
                labels.add("");
            }
        }
        int splineColor = ContextCompat.getColor(getContext(), R.color.spline_color);
        SplineData dataSeries1 = new SplineData("入睡曲线",linePoint1, splineColor);
        //标签轴最大值
        chart.setCategoryAxisMax(sleepDataModes.size());
        //标签轴最小值
        chart.setCategoryAxisMin(1);
        //把线弄细点
        dataSeries1.getLinePaint().setStrokeWidth(3);
        dataSeries1.setLineStyle(XEnum.LineStyle.DASH);
        dataSeries1.setLabelVisible(false);
        dataSeries1.setDotStyle(XEnum.DotStyle.HIDE);
        chartData.add(dataSeries1);
        chartDesireLines();
        chartRender();
    }

    private void chartLabels() {
//        labels.add("2018");
//        labels.add("2019");
//        labels.add("2020");
//        labels.add("2021");
//        labels.add("2022");
//        labels.add("2023");
    }

    private void chartDesireLines() {
//        CustomLineData s = new CustomLineData("入睡曲线",15d,Color.rgb(54, 141, 238),3);
//        s.hideLine();
//        s.getLineLabelPaint().setColor(Color.rgb(54, 141, 238));
//        s.getLineLabelPaint().setTextSize(27);
//        s.setLineStyle(XEnum.LineStyle.DASH);
//        s.setLabelOffset(5);
//        mCustomLineDataset.add(s);
    }


    @Override
    public void render(Canvas canvas) {
        try{
            chart.render(canvas);
        } catch (Exception e){
        }
    }
    //Demo中bar chart所使用的默认偏移值。
    //偏移出来的空间用于显示tick,axistitle....
    protected int[] getBarLnDefaultSpadding() {
        int [] ltrb = new int[4];
        ltrb[0] = DensityUtil.dip2px(getContext(), 30); //left
        ltrb[1] = DensityUtil.dip2px(getContext(), 30); //top
        ltrb[2] = DensityUtil.dip2px(getContext(), 0); //right
        ltrb[3] = DensityUtil.dip2px(getContext(), 50); //bottom
        return ltrb;
    }
}
