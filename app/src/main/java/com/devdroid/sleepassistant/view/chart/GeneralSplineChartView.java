package com.devdroid.sleepassistant.view.chart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.PointD;
import org.xclcharts.chart.SplineChart;
import org.xclcharts.chart.SplineData;
import org.xclcharts.common.DensityUtil;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.PointPosition;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.info.AnchorDataPoint;
import org.xclcharts.renderer.plot.PlotGrid;
import org.xclcharts.view.ChartView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
/**
 * 普通曲线图
 * Created by Gaolei on 2017/4/14.
 */

public class GeneralSplineChartView  extends ChartView {
    private String TAG = "SplineChart01View";
    private SplineChart chart = new SplineChart();
    //分类轴标签集合
    private LinkedList<String> labels = new LinkedList<String>();
    private LinkedList<SplineData> chartData = new LinkedList<SplineData>();
    Paint pToolTip = new Paint(Paint.ANTI_ALIAS_FLAG);


    public GeneralSplineChartView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
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

    private void initView()
    {
        chartLabels();
        chartDataSet();
        chartRender();

        //綁定手势滑动事件
//        this.bindTouch(this,chart);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //图所占范围大小
        chart.setChartRange(w,h);
    }


    private void chartRender()
    {
        try {

            //设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
            int [] ltrb = getBarLnDefaultSpadding();
            chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);

            //平移时收缩下
            //float margin = DensityUtil.dip2px(getContext(), 20);
            //chart.setXTickMarksOffsetMargin(margin);

            //显示边框
            chart.showRoundBorder();

            //数据源
            chart.setCategories(labels);
            chart.setDataSource(chartData);

            //坐标系
            //数据轴最大值 Y
            chart.getDataAxis().setAxisMax(25);
            //chart.getDataAxis().setAxisMin(0);
            //数据轴刻度间隔
            chart.getDataAxis().setAxisSteps(5);

            //标签轴最大值 X
            chart.setCategoryAxisMax(7);
            //标签轴最小值
            chart.setCategoryAxisMin(1);
            chart.getCategoryAxis().setAxisSteps(1);

            //设置图的背景色
            //chart.setBackgroupColor(true,Color.BLACK);
            //设置绘图区的背景色
            //chart.getPlotArea().setBackgroupColor(true, Color.WHITE);

            //背景网格
            PlotGrid plot = chart.getPlotGrid();
            plot.showHorizontalLines();
            plot.showVerticalLines();
            plot.getHorizontalLinePaint().setStrokeWidth(3);
            plot.getHorizontalLinePaint().setColor(Color.rgb(127, 204, 204));
            plot.setHorizontalLineStyle(XEnum.LineStyle.DOT);


            //把轴线设成和横向网络线一样和大小和颜色,演示下定制性，这块问得人较多
            //chart.getDataAxis().getAxisPaint().setStrokeWidth(
            //		plot.getHorizontalLinePaint().getStrokeWidth());
            //chart.getCategoryAxis().getAxisPaint().setStrokeWidth(
            //		plot.getHorizontalLinePaint().getStrokeWidth());

            chart.getDataAxis().getAxisPaint().setColor(Color.rgb(127, 204, 204));
            chart.getCategoryAxis().getAxisPaint().setColor(Color.rgb(127, 204, 204));

            chart.getDataAxis().getTickMarksPaint().setColor(Color.rgb(127, 204, 204));
            chart.getCategoryAxis().getTickMarksPaint().setColor(Color.rgb(127, 204, 204));

            //居中
            chart.getDataAxis().setHorizontalTickAlign(Align.CENTER);
            chart.getDataAxis().getTickLabelPaint().setTextAlign(Align.CENTER);

            //居中显示轴
            //plot.hideHorizontalLines();
            //plot.hideVerticalLines();
            //chart.setDataAxisLocation(XEnum.AxisLocation.VERTICAL_CENTER);
            //chart.setCategoryAxisLocation(XEnum.AxisLocation.HORIZONTAL_CENTER);


            //定义交叉点标签显示格式,特别备注,因曲线图的特殊性，所以返回格式为:  x值,y值
            //请自行分析定制
            chart.setDotLabelFormatter(new IFormatterTextCallBack(){

                @Override
                public String textFormatter(String value) {
                    // TODO Auto-generated method stub
                    String label = "("+value+")";
                    return (label);
                }

            });
            //激活点击监听
            chart.ActiveListenItemClick();
            //为了让触发更灵敏，可以扩大5px的点击监听范围
            chart.extPointClickRange(5);
            chart.showClikedFocus();

            //显示十字交叉线
            chart.showDyLine();
            chart.getDyLine().setDyLineStyle(XEnum.DyLineStyle.Vertical);
            //扩大实际绘制宽度
            //chart.getPlotArea().extWidth(500.f);

            //封闭轴
            chart.setAxesClosed(true);

            //将线显示为直线，而不是平滑的
            chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEELINE);

            //不使用精确计算，忽略Java计算误差,提高性能
            chart.disableHighPrecision();

            //仅能横向移动
            chart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);

            //批注
            List<AnchorDataPoint> mAnchorSet = new ArrayList<AnchorDataPoint>();

            AnchorDataPoint an1 = new AnchorDataPoint(2,0,XEnum.AnchorStyle.CAPROUNDRECT);
            an1.setAlpha(200);
            an1.setBgColor(Color.RED);
            an1.setAreaStyle(XEnum.DataAreaStyle.FILL);

            AnchorDataPoint an2 = new AnchorDataPoint(1,1,XEnum.AnchorStyle.CIRCLE);
            an2.setBgColor(Color.GRAY);

            AnchorDataPoint an3 = new AnchorDataPoint(0,2,XEnum.AnchorStyle.RECT);
            an3.setBgColor(Color.BLUE);

            mAnchorSet.add(an1);
            mAnchorSet.add(an2);
            mAnchorSet.add(an3);
            chart.setAnchorDataPoint(mAnchorSet);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e(TAG, e.toString());
        }
    }
    private void chartDataSet() {
        //线1的数据集
        List<PointD> linePoint1 = new ArrayList<PointD>();
        linePoint1.add(new PointD(1d, 8d));
        linePoint1.add(new PointD(2d, 8d));
        linePoint1.add(new PointD(3d, 12d));
        linePoint1.add(new PointD(4d, 2d));
        linePoint1.add(new PointD(5d, 25d));
        linePoint1.add(new PointD(6d, 21d));
        linePoint1.add(new PointD(7d, 0d));
        SplineData dataSeries1 = new SplineData("入睡时间曲线",linePoint1, Color.rgb(54, 141, 238) );
        //把线弄细点
        dataSeries1.getLinePaint().setStrokeWidth(2);
        chartData.add(dataSeries1);
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
        //labels.add("5:52:33");
        //labels.add("5:52:35");
        //labels.add("5:52:37");
        //labels.add("5:52:39");
        //labels.add("5:52:41");
        //labels.add("5:52:43");
        //labels.add("5:52:45");
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
        // TODO Auto-generated method stub

        super.onTouchEvent(event);

        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            triggerClick(event.getX(),event.getY());
        }
        return true;
    }


    //触发监听
    private void triggerClick(float x,float y)
    {
        //交叉线
        if(chart.getDyLineVisible())chart.getDyLine().setCurrentXY(x,y);
        if(!chart.getListenItemClickStatus())
        {
            if(chart.getDyLineVisible()&&chart.getDyLine().isInvalidate())this.invalidate();
        }else{
            PointPosition record = chart.getPositionRecord(x,y);
            if( null == record) return;

            if(record.getDataID() >= chartData.size()) return;
            SplineData lData = chartData.get(record.getDataID());
            List<PointD> linePoint =  lData.getLineDataSet();
            int pos = record.getDataChildID();
            int i = 0;
            Iterator it = linePoint.iterator();
            while(it.hasNext())
            {
                PointD  entry=(PointD)it.next();

                if(pos == i)
                {
                    Double xValue = entry.x;
                    Double yValue = entry.y;

                    float r = record.getRadius();
                    chart.showFocusPointF(record.getPosition(),r * 2);
                    chart.getFocusPaint().setStyle(Style.STROKE);
                    chart.getFocusPaint().setStrokeWidth(3);
                    if(record.getDataID() >= 2)
                    {
                        chart.getFocusPaint().setColor(Color.BLUE);
                    }else{
                        chart.getFocusPaint().setColor(Color.RED);
                    }

                    //在点击处显示tooltip
                    pToolTip.setColor(Color.RED);
                    chart.getToolTip().setCurrentXY(x,y);
                    chart.getToolTip().addToolTip(" Key:"+lData.getLineKey(),pToolTip);
                    chart.getToolTip().addToolTip(" Label:"+lData.getLabel(),pToolTip);
                    chart.getToolTip().addToolTip(" Current Value:" +Double.toString(xValue)+","+Double.toString(yValue),pToolTip);
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
    protected int[] getBarLnDefaultSpadding()
    {
        int [] ltrb = new int[4];
        ltrb[0] = DensityUtil.dip2px(getContext(), 10); //left
        ltrb[1] = DensityUtil.dip2px(getContext(), 20); //top
        ltrb[2] = DensityUtil.dip2px(getContext(), 10); //right
        ltrb[3] = DensityUtil.dip2px(getContext(), 15); //bottom
        return ltrb;
    }

}
