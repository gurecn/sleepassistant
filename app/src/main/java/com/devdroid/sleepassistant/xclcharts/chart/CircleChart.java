/**
 * Copyright 2014  XCL-Charts
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @Project XCL-Charts 
 * @Description Android图表基类库
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version v0.1
 */


package com.devdroid.sleepassistant.xclcharts.chart;

import java.util.List;

import com.devdroid.sleepassistant.xclcharts.common.DrawHelper;
import com.devdroid.sleepassistant.xclcharts.common.MathHelper;
import com.devdroid.sleepassistant.xclcharts.renderer.CirChart;
import com.devdroid.sleepassistant.xclcharts.renderer.XEnum;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Paint.Align;
import android.graphics.RectF;
import android.util.Log;


/**    
 * @ClassName CircleChart
 * @Description 圆形图基类 <br/>
 *        注意：当为半圆时，推荐 宽应当是高的两倍。
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 */

public class CircleChart extends CirChart {

	private static final String TAG = "CircleChart";
	
    private String mDataInfo = "";
    private XEnum.CircleType mDisplayType = XEnum.CircleType.FULL;
    //内环填充颜色
    private Paint mPaintBgCircle=null;
    private Paint mPaintFillCircle = null;
    private Paint mPaintDataInfo = null;
    
    //内部填充
    private boolean mShowInnerFill = true;
    
    //内部背景填充
    private boolean mShowInnerBG = true;
    
    //显示圆形箭头标示
    private boolean mShowCap = false;    

    //数据源
    protected List<PieData> mDataSet;
    
    private float moRadius = 0.9f;
    private float miRadius = 0.8f;
    
   

    public CircleChart() {
    
        initChart();
    }
    
    @Override
	public XEnum.ChartType getType()
	{
		return XEnum.ChartType.CIRCLE;
	}

    private void initChart() {
    	        	
    	if(null != getLabelPaint())
    	{
	        getLabelPaint().setColor(Color.WHITE);
	        getLabelPaint().setTextSize(50);
	        getLabelPaint().setTextAlign(Align.CENTER);
    	}
      
        //设置起始偏移角度
        setInitialAngle(180);
    }


    /**
     * 设置图表的数据源
     *
     * @param piedata 来源数据集
     */
    public void setDataSource(List<PieData> piedata) {
        this.mDataSet = piedata;
    }

    /**
     * 设置附加信息
     *
     * @param info 附加信息
     */
    public void setAttributeInfo(String info) {
        mDataInfo = info;
    }

    /**
     * 设置圆是显示成完整的一个图还是只显示成一个半圆
     *
     * @param display 半圆/完整圆
     */
    public void setCircleType(XEnum.CircleType display) {
        mDisplayType = display;
    }

    /**
     * 开放内部填充的画笔
     *
     * @return 画笔
     */
    public Paint getFillCirclePaint() {
    	
    	if(null == mPaintFillCircle)
    	{
	        mPaintFillCircle = new Paint();
	        mPaintFillCircle.setColor(Color.rgb(77, 83, 97));
	        mPaintFillCircle.setAntiAlias(true);
    	}
        return mPaintFillCircle;
    }

    /**
     * 开放内部背景填充的画笔
     *
     * @return 画笔
     */
    public Paint getBgCirclePaint() {
    	if(null == mPaintBgCircle)
    	{
	        mPaintBgCircle = new Paint();
	        mPaintBgCircle.setColor(Color.rgb(148, 159, 181));
	        mPaintBgCircle.setAntiAlias(true);
    	}
        return mPaintBgCircle;
    }

    /**
     * 开放绘制附加信息的画笔
     *
     * @return 画笔
     */
    public Paint getDataInfoPaint() {
    	
    	 if(null == mPaintDataInfo)
         {
 	        mPaintDataInfo = new Paint();
 	        mPaintDataInfo.setTextSize(22);
 	        mPaintDataInfo.setColor(Color.WHITE);
 	        mPaintDataInfo.setTextAlign(Align.CENTER);
 	        mPaintDataInfo.setAntiAlias(true);
         }
    	 
        return mPaintDataInfo;
    }            

    /**
     * 隐藏内部背景填充
     */
    public void hideInnerFill()
    {
    	mShowInnerFill = false;
    }
    
    /**
     * 隐藏背景
     */
    public void hideInnerBG()
    {
    	mShowInnerBG = false;
    }
    
    /**
     * 隐藏内部背景填充
     */
    public void showInnerFill()
    {
    	mShowInnerFill = true;
    }
    
    /**
     * 显示内部背景填充
     * @return 显示状态
     */
    public boolean isShowInnerFill()
    {
    	return mShowInnerFill;
    }
    
    /**
     * 隐藏背景
     */
    public void showInnerBG()
    {
    	mShowInnerBG = true;
    }
    
    /**
     * 背景显示状态
     * @return 显示状态
     */
    public boolean isShowInnerBG()
    {
    	return mShowInnerBG;
    }
    
    /**
     * 外环
     * @param radius  半径比例
     */
    public void setORadius(float radius)
    {
    	moRadius = radius;
    }
    
    /**
     * 内环
     * @param radius 半径比例
     */
    public void setIRadius(float radius)
    {
    	miRadius = radius;
    }
    
    /**
     * 是否显示圆形箭头标示(仅限360度圆形才有)
     * @return 状态
     */
    public boolean isShowCap()
    {
    	return mShowCap; 
    }
    
    /**
     * 显示圆形箭头标示(仅限360度圆形才有. <br/>
     * 起始色默认为圆背景色，如没设，则默认为内部填充色)
     */
    public void ShowCap()
    {
    	mShowCap = true;
    }
    
    /**
     * 隐藏圆形箭头标示
     */
    public void HideCap()
    {
    	mShowCap = false;
    }
    

    /**
     * 依比例绘制扇区
     *
     * @param paintArc    画笔
     * @param cirX        x坐标
     * @param cirY        y坐标
     * @param radius      半径
     * @param offsetAngle 偏移
     * @param curretAngle 当前值
     * @throws Exception 例外
     */
    protected void drawPercent(Canvas canvas, Paint paintArc,
                               final float cirX,
                               final float cirY,
                               final float radius,
                               final float offsetAngle,
                               final float curretAngle) throws Exception {
        try {
            float arcLeft = sub(cirX , radius);
            float arcTop = sub(cirY , radius);
            float arcRight = add(cirX , radius);
            float arcBottom = add(cirY , radius);
            RectF arcRF0 = new RectF(arcLeft, arcTop, arcRight, arcBottom);
            //在饼图中显示所占比例
            canvas.drawArc(arcRF0, offsetAngle, curretAngle, true, paintArc);
        } catch (Exception e) {
            throw e;
        }
    }
    
    private float getCirY(float cirY,float labelHeight)
    {
    	float txtY = cirY;
    	if("" == mDataInfo )
    	{    		
    		txtY = cirY + labelHeight/3 ;
    	}
    	return txtY;
    }


    /**
     * 绘制图
     */
    protected boolean renderPlot(Canvas canvas){
        try {

            //中心点坐标
            float cirX = plotArea.getCenterX();
            float cirY = plotArea.getCenterY();
            float radius = getRadius();

            //确定去饼图范围
            float arcLeft = sub(cirX , radius);
            float arcTop =  sub(cirY , radius);
            float arcRight = add(cirX , radius);
            float arcBottom = add(cirY , radius);
            RectF arcRF0 = new RectF(arcLeft, arcTop, arcRight, arcBottom);

            //画笔初始化
            Paint paintArc = new Paint();
            paintArc.setAntiAlias(true);

            //用于存放当前百分比的圆心角度
            float currentAngle = 0.0f;

            float infoHeight = DrawHelper.getInstance().getPaintFontHeight(getDataInfoPaint());
            float LabelHeight = DrawHelper.getInstance().getPaintFontHeight(getLabelPaint());
            float textHeight = LabelHeight + infoHeight;

            for (PieData cData : mDataSet) {
                paintArc.setColor(cData.getSliceColor());
                if (XEnum.CircleType.HALF == mDisplayType) {
                    setInitialAngle(180);
                    
                    //半圆， 宽应当是高的两倍
                    float hRadius =  this.getWidth() / 2.f; 
                    float hCirY = this.getBottom() ; 
                                                            
                    if(this.isShowBorder())
                    {
                    	hRadius -= this.getBorderWidth() ;
                    	hCirY  -= this.getBorderWidth() / 2;
                    }                   
                    
                    float oRadius = MathHelper.getInstance().round(mul(hRadius , moRadius),2);
                    float iRadius = MathHelper.getInstance().round( mul(hRadius , miRadius ),2);
                                       
                    if(isShowInnerBG()) //内部背景填充
                    {
                    	drawPercent(canvas, getBgCirclePaint(), cirX, hCirY, hRadius, 180f, 180f);                    	                    	
                    }else{
                    	oRadius = iRadius = hRadius;
                    }
                    
                    if(isShowInnerFill())
                    {
	                    drawPercent(canvas, getFillCirclePaint(), cirX, hCirY, oRadius, 180f, 180f);
                    }
                    
                    
                   // float per = (float) cData.getPercentage();                    
                    //currentAngle = MathHelper.getInstance().round(mul(180f, div(per, 100f)),2);
                    
                    currentAngle = MathHelper.getInstance().getSliceAngle(180f, (float) cData.getPercentage());                    
                    drawPercent(canvas, paintArc, cirX, hCirY, hRadius , 180f,  currentAngle);
                    
                                      
                    if(isShowInnerFill())  //内部填充
                    {
                    	drawPercent(canvas, getFillCirclePaint(), cirX, hCirY ,iRadius, 180f, 180f);
                    }
                    
                    if("" != cData.getLabel()) { //getCirY(hCirY,textHeight)
                    	canvas.drawText(cData.getLabel(), cirX, sub(hCirY,textHeight), getLabelPaint());
                    }
                    if("" != mDataInfo )
                    	canvas.drawText(mDataInfo, cirX, hCirY - infoHeight, getDataInfoPaint());

                } else {
                    currentAngle = MathHelper.getInstance().getSliceAngle(360.f, (float) cData.getPercentage());
                    
                    if(isShowInnerBG())
                    	canvas.drawCircle(cirX, cirY, radius, getBgCirclePaint());
                    	// canvas.drawCircle(cirX, cirY, (float) (Math.round(radius * 0.9f)), mPaintFillCircle);
                    
                    
                    if(isShowInnerFill())
                    {
                    	float fillRadius = MathHelper.getInstance().round(mul(radius , moRadius),2);
                    	canvas.drawCircle(cirX, cirY, fillRadius, getFillCirclePaint());                                     
                    }
        	    	        	    	                    
                    canvas.drawArc(arcRF0, mOffsetAngle, currentAngle, true, paintArc);
                    
     				//////////////////
                    if(isShowCap() && (isShowInnerBG() || isShowInnerFill()) )
                    {
                    	
	                    float cap = MathHelper.getInstance().round(mul(radius , miRadius ),2);
	                    float capRadius = cap +  (radius - cap) /2 ;
	                    
	                    //箭头
	                    if(isShowInnerBG())
	                    {
	                    	paintArc.setColor( getBgCirclePaint().getColor() );
	                    }else{
	                    	paintArc.setColor( getFillCirclePaint().getColor() );
	                    }
	        	    	PointF pointBegin = MathHelper.getInstance().calcArcEndPointXY(cirX,cirY,
	        	    						capRadius,getInitialAngle() ); 	
	        	    	
	        	    	canvas.drawLine(cirX,cirY, pointBegin.x,pointBegin.y, paintArc);        	    	
	        	    	canvas.drawCircle(pointBegin.x,pointBegin.y, (radius - cap) /2, paintArc);
	                    
	                    //箭头                    
	        	    	PointF point = MathHelper.getInstance().calcArcEndPointXY(cirX,cirY,
	        	    					capRadius,add(mOffsetAngle , currentAngle) ); 	                   
	                   
	        	    	paintArc.setColor(cData.getSliceColor());        	    	
	        	    	canvas.drawLine(cirX,cirY, point.x,point.y, paintArc);        	    	
	        	    	canvas.drawCircle(point.x,point.y, (radius - cap) /2, paintArc);
                    }
        	    	////////////////////        	    	
                    
                    if(isShowInnerFill())
                    	canvas.drawCircle(cirX, cirY, 
                    					MathHelper.getInstance().round(mul(radius , miRadius ),2), getFillCirclePaint());
                    
                    if("" != cData.getLabel())
                       canvas.drawText(cData.getLabel(), cirX, getCirY(cirY,LabelHeight), getLabelPaint());

                    if ("" != mDataInfo)
                        canvas.drawText(mDataInfo, cirX, add(cirY , infoHeight), getDataInfoPaint());                                                                               
                }

                break;
            }

        } catch (Exception e) {
        	Log.e(TAG,e.toString());
        }
        return true;
    }

    @Override
	protected boolean postRender(Canvas canvas) throws Exception 
	{
		// 绘制图表
		try {
			super.postRender(canvas);
			
			return renderPlot(canvas);
		} catch (Exception e) {
			throw e;
		}
		
	}
    

}
