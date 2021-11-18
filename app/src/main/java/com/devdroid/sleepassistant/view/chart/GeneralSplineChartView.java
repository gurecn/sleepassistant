package com.devdroid.sleepassistant.view.chart;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.TheApplication;
import com.devdroid.sleepassistant.xclcharts.chart.PointD;
import com.devdroid.sleepassistant.xclcharts.chart.SplineChart;
import com.devdroid.sleepassistant.xclcharts.chart.SplineData;
import com.devdroid.sleepassistant.xclcharts.common.DensityUtil;
import com.devdroid.sleepassistant.xclcharts.common.IFormatterTextCallBack;
import com.devdroid.sleepassistant.xclcharts.event.click.PointPosition;
import com.devdroid.sleepassistant.xclcharts.renderer.XEnum;
import com.devdroid.sleepassistant.xclcharts.renderer.info.AnchorDataPoint;
import com.devdroid.sleepassistant.xclcharts.renderer.plot.PlotGrid;
import com.devdroid.sleepassistant.xclcharts.view.ChartView;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.devdroid.sleepassistant.mode.SleepDataMode;

/**
 * 七日普通曲线图
 * Created by Gaolei on 2017/4/14.
 */

public class GeneralSplineChartView  extends BaseChartView {
    private String TAG = "SplineChart01View";
    private SplineChart chart = new SplineChart();
    private LinkedList<String> labels = new LinkedList<>();//分类轴标签集合
    private LinkedList<SplineData> chartData = new LinkedList<>();
    Paint pToolTip = new Paint(Paint.ANTI_ALIAS_FLAG);

    public GeneralSplineChartView(Context context) {
        super(context);
        initView();
    }

    public GeneralSplineChartView(Context context, AttributeSet attrs){
        super(context, attrs);
        initView();
    }

    public GeneralSplineChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        chartLabels();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        chart.setChartRange(w,h);//图所占范围大小
    }

    private void chartRender(boolean isWeek) {
        try {
            //设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
            int [] ltrb = getBarLnDefaultSpadding();
            chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);
            //显示边框
            chart.showRoundBorder();
            //数据源
            chart.setCategories(labels);
            chart.setDataSource(chartData);
            //坐标系
            //数据轴最大值 Y
            if(isWeek){
                chart.getDataAxis().setAxisMin(20);
                chart.getDataAxis().setAxisMax(25);
                chart.getCategoryAxis().setAxisSteps(1);
            } else {
                chart.getDataAxis().setAxisMin(6);
                chart.getDataAxis().setAxisMax(29);
                chart.getCategoryAxis().setAxisSteps(5);
            }
            //数据轴刻度间隔
            chart.getDataAxis().setAxisSteps(1);
            //标签轴最大值 X
            chart.setCategoryAxisMax(labels.size());
            //标签轴最小值
            chart.setCategoryAxisMin(1);
            //背景网格
            PlotGrid plot = chart.getPlotGrid();
            plot.showHorizontalLines();
            plot.showVerticalLines();
            plot.getHorizontalLinePaint().setStrokeWidth(1);
            plot.getHorizontalLinePaint().setColor(Color.rgb(127, 204, 204));
            plot.setHorizontalLineStyle(XEnum.LineStyle.DOT);
            chart.getDataAxis().getAxisPaint().setColor(Color.rgb(127, 204, 204));
            chart.getCategoryAxis().getAxisPaint().setColor(Color.rgb(127, 204, 204));
            chart.getDataAxis().getTickMarksPaint().setColor(Color.rgb(127, 204, 204));
            chart.getCategoryAxis().getTickMarksPaint().setColor(Color.rgb(127, 204, 204));
            //居中
            chart.getDataAxis().setHorizontalTickAlign(Align.LEFT);
            chart.getDataAxis().getTickLabelPaint().setTextAlign(Align.CENTER);
            int tickLabelColor = ContextCompat.getColor(getContext(), R.color.tick_label_color);
            chart.getDataAxis().getTickLabelPaint().setColor(tickLabelColor);
            //封闭轴
            chart.setAxesClosed(false);
            //将线显示为直线，而不是平滑的
            chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEELINE);
            //不使用精确计算，忽略Java计算误差,提高性能
            chart.disableHighPrecision();
            //仅能横向移动
            chart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }
    public void chartDataSet(List<SleepDataMode> sleepDataModes, boolean isWeek) {
        //线的数据集
        chartData.clear();
        labels.clear();
        List<PointD> linePoint1 = new ArrayList<>();
        int dataCount = sleepDataModes.size();
        for(int i = 0;i < dataCount;i++){
            SleepDataMode sleepDataMode = sleepDataModes.get(i);
            int skip = dataCount / 30 + 1;
            if(isWeek) {
                labels.add(sleepDataMode.getWeek() + "");
            } else if(dataCount <= 30 || i % skip == 0){
                labels.add(sleepDataMode.getDay() + "");
            } else {
                labels.add("");
            }
            int hour = sleepDataMode.getHour();
            if(hour == -1)continue;
            float time = hour + sleepDataMode.getMinute() / 60f;
            linePoint1.add(new PointD((double)(i + 1), (double) time));
        }
        int splineColor = ContextCompat.getColor(getContext(), R.color.spline_color);
        SplineData dataSeries1 = new SplineData("入睡时间曲线",linePoint1, splineColor);
        dataSeries1.getLinePaint().setStrokeWidth(2);//把线弄细点
        chartData.add(dataSeries1);
        chartRender(isWeek);
        invalidate();
    }

    private void chartLabels()
    {
        labels.add("1");
        labels.add("2");
        labels.add("3");
        labels.add("4");
        labels.add("5");
        labels.add("6");
        labels.add("7");
    }

    @Override
    public void render(Canvas canvas) {
        try{
            chart.render(canvas);
        } catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if(event.getAction() == MotionEvent.ACTION_UP) {
            triggerClick(event.getX(),event.getY());
        }
        return true;
    }


    //触发监听
    private void triggerClick(float x,float y) {
        //交叉线
        if(chart.getDyLineVisible())chart.getDyLine().setCurrentXY(x,y);
        if(!chart.getListenItemClickStatus()) {
            if(chart.getDyLineVisible()&&chart.getDyLine().isInvalidate())this.invalidate();
        }else{
            PointPosition record = chart.getPositionRecord(x,y);
            if( null == record) return;
            if(record.getDataID() >= chartData.size()) return;
            SplineData lData = chartData.get(record.getDataID());
            List<PointD> linePoint =  lData.getLineDataSet();
            int pos = record.getDataChildID();
            int i = 0;
            for (PointD entry : linePoint) {
                if (pos == i) {
                    Double xValue = entry.x;
                    Double yValue = entry.y;
                    float r = record.getRadius();
                    chart.showFocusPointF(record.getPosition(), r * 2);
                    chart.getFocusPaint().setStyle(Style.STROKE);
                    chart.getFocusPaint().setStrokeWidth(3);
                    if (record.getDataID() >= 2) {
                        chart.getFocusPaint().setColor(Color.BLUE);
                    } else {
                        chart.getFocusPaint().setColor(Color.RED);
                    }
                    //在点击处显示tooltip
                    pToolTip.setColor(Color.RED);
                    chart.getToolTip().setCurrentXY(x, y);
                    chart.getToolTip().addToolTip(" Key:" + lData.getLineKey(), pToolTip);
                    chart.getToolTip().addToolTip(" Label:" + lData.getLabel(), pToolTip);
                    chart.getToolTip().addToolTip(" Current Value:" + Double.toString(xValue) + "," + Double.toString(yValue), pToolTip);
                    chart.getToolTip().getBackgroundPaint().setAlpha(100);
                    this.invalidate();
                    break;
                }
                i++;
            }//end while
        }
    }
    //Demo中bar chart所使用的默认偏移值。
    //偏移出来的空间用于显示tick,axistitle....
    protected int[] getBarLnDefaultSpadding() {
        int [] ltrb = new int[4];
        ltrb[0] = DensityUtil.dip2px(getContext(), 20); //left
        ltrb[1] = DensityUtil.dip2px(getContext(), 20); //top
        ltrb[2] = DensityUtil.dip2px(getContext(), 10); //right
        ltrb[3] = DensityUtil.dip2px(getContext(), 15); //bottom
        return ltrb;
    }
}
