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
 * @version 1.0
 */

package com.devdroid.sleepassistant.xclcharts.renderer;

import com.devdroid.sleepassistant.xclcharts.chart.PieData;
import com.devdroid.sleepassistant.xclcharts.common.MathHelper;
import com.devdroid.sleepassistant.xclcharts.renderer.info.PlotArcLabelInfo;
import com.devdroid.sleepassistant.xclcharts.renderer.plot.LabelBrokenLine;
import com.devdroid.sleepassistant.xclcharts.renderer.plot.LabelBrokenLineRender;
import com.devdroid.sleepassistant.xclcharts.renderer.plot.PlotLabel;
import com.devdroid.sleepassistant.xclcharts.renderer.plot.PlotLabelRender;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.PointF;
import android.util.Log;

/**
 * @ClassName CirChart
 * @Description 圆形类图表，如饼图，刻度盘...类的图表的基类
 * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 *  
 */

public class CirChart extends EventChart{
	
	private static final String TAG = "CirChart";
	
	//半径
	private float mRadius=0.0f;		
	
	//标签注释显示位置 [隐藏,Default,Inside,Ouside,Line]
	private XEnum.SliceLabelStyle mLabelStyle  = XEnum.SliceLabelStyle.INSIDE;	
	
	//开放标签画笔让用户设置
	private Paint mPaintLabel = null;
	//初始偏移角度
	protected float mOffsetAngle = 0.0f;//180;	
	protected float mInitOffsetAngle = 0.0f;
	
	//折线标签基类
	private LabelBrokenLineRender mLabelLine = null;
	
	//同步标签颜色
	private boolean mIsLabelLineSyncColor = false;
	private boolean mIsLabelPointSyncColor = false;
	private boolean mIsLabelSyncColor = false;
	
	//用于设置标签特性
	private PlotLabelRender mPlotLabel = null;
		
		
	public CirChart()
	{				
		//初始化图例
		if(null != plotLegend)
		{
			plotLegend.show();
			plotLegend.setType(XEnum.LegendType.ROW);
			plotLegend.setHorizontalAlign(XEnum.HorizontalAlign.CENTER);
			plotLegend.setVerticalAlign(XEnum.VerticalAlign.BOTTOM);
			plotLegend.showBox();
			plotLegend.hideBackground();
		}
				
	}
	
	@Override
	protected void calcPlotRange()
	{
		super.calcPlotRange();		
		
		this.mRadius = Math.min( div(this.plotArea.getWidth() ,2f) , 
				 				 div(this.plotArea.getHeight(),2f) );	
	}
	
	
	/**
	 * 返回半径
	 * @return 半径
	 */
	public float getRadius()
	{
		return mRadius;
	}
	
	/**
	 * 设置饼图(pie chart)起始偏移角度
	 * @param Angle 偏移角度
	 */
	public void setInitialAngle(float Angle)
	{
		mInitOffsetAngle = mOffsetAngle = Angle;
	}
	
	/**
	 * 返回图的起始偏移角度
	 * @return 偏移角度
	 */
	public float getInitialAngle()
	{
		return mInitOffsetAngle;
	}
	
	/**
	 * 返回图的当前偏移角度
	 * @return 偏移角度
	 */
	public float getOffsetAngle()
	{
		return mOffsetAngle;
	}
	

	/**
	 * 设置标签显示在扇区的哪个位置(里面，外面，隐藏)
	 * @param style 显示位置
	 */
	public void setLabelStyle(XEnum.SliceLabelStyle style)
	{
		mLabelStyle = style;
		//INNER,OUTSIDE,HIDE
		switch(style)
		{
		case INSIDE :
			getLabelPaint().setTextAlign(Align.CENTER);
			break;
		case OUTSIDE :
			break;
		case HIDE :
			break;
		case BROKENLINE:
			break;
		default:			
		}				
	}
	
	/**
	 * 返回标签风格设置
	 * @return	标签风格
	 */
	public XEnum.SliceLabelStyle getLabelStyle()
	{
		return mLabelStyle;
	}
	
	/**
	 * 开放标签画笔
	 * @return 画笔
	 */
	public Paint getLabelPaint()
	{
		if(null == mPaintLabel)
		{
			mPaintLabel = new Paint(Paint.ANTI_ALIAS_FLAG);
			mPaintLabel.setColor(Color.BLACK);
			mPaintLabel.setAntiAlias(true);
			mPaintLabel.setTextAlign(Align.CENTER);	
			mPaintLabel.setTextSize(50);
		}
		return mPaintLabel;
	}
	
	/**
	 * 开放折线标签绘制类(当标签为Line类型时有效)
	 * @return 折线标签绘制类
	 */
	public LabelBrokenLine getLabelBrokenLine()
	{
		if(null == mLabelLine)mLabelLine = new LabelBrokenLineRender();
		return mLabelLine;
	}
	
	protected PointF renderLabelInside(Canvas canvas,String text,float itemAngle,
									 float cirX,float cirY,float radius,float calcAngle,
									 boolean showLabel)
	{
		//显示在扇形的中心
		float calcRadius = MathHelper.getInstance().sub(radius , radius/2f);
		
		//计算百分比标签
		PointF point = MathHelper.getInstance().calcArcEndPointXY(
										cirX, cirY, calcRadius, calcAngle); 						 
		//标识
		if(showLabel)
		{
			//DrawHelper.getInstance().drawRotateText(text, point.x, point.y, itemAngle, 
			//												canvas, getLabelPaint());
			getPlotLabel().drawLabel(canvas, getLabelPaint(), text, point.x, point.y, itemAngle);
		}
		
		return (new PointF(point.x, point.y));
	}
	
	protected PointF renderLabelOutside(Canvas canvas,String text,float itemAngle,
							float cirX,float cirY,float radius,float calcAngle,
							boolean showLabel)
	{
		//显示在扇形的外部
		float calcRadius = MathHelper.getInstance().add(radius  , radius/10f);
		//计算百分比标签
		PointF point = MathHelper.getInstance().calcArcEndPointXY(
										cirX, cirY, calcRadius, calcAngle); 	
			 
		//标识
		if(showLabel)
		{
			//DrawHelper.getInstance().drawRotateText(text, point.x, point.y, itemAngle, 
			//													canvas, getLabelPaint());
			getPlotLabel().drawLabel(canvas, getLabelPaint(), text, point.x, point.y, itemAngle);
		}
		return (new PointF(point.x, point.y));
	}
	
	//折线标签
	protected PointF renderLabelLine(Canvas canvas,PieData cData,
									float cirX,float cirY,float radius,float calcAngle,
									boolean showLabel)
	{		
		if(null == mLabelLine)mLabelLine = new LabelBrokenLineRender();		
		
		if(mIsLabelLineSyncColor)
			mLabelLine.getLabelLinePaint().setColor(cData.getSliceColor());
		if(mIsLabelPointSyncColor)
			mLabelLine.getPointPaint().setColor(cData.getSliceColor());
		
		return ( mLabelLine.renderLabelLine(cData.getLabel(),cData.getItemLabelRotateAngle(),
									cirX,cirY,radius,calcAngle,
									canvas,getLabelPaint(),showLabel,mPlotLabel) );
	}
	
	/**
	 * 设置标签颜色与当地扇区颜色同步
	 */
	public void syncLabelLineColor()
	{
		mIsLabelLineSyncColor = true;
	}
	
	/**
	 * 设置折线标签点颜色与当地扇区颜色同步
	 */
	public void syncLabelPointColor()
	{
		mIsLabelPointSyncColor = true;
	}
	
	/**
	 * 设置折线标签颜色与当地扇区颜色同步
	 */
	public void syncLabelColor()
	{
		mIsLabelSyncColor = true;
	}
	
	/**
	 * 用于设置标签显示属性
	 * @return 标签属性类
	 */
	public PlotLabel getPlotLabel()
	{
		if(null == mPlotLabel)
		{
			mPlotLabel = new PlotLabelRender();
			mPlotLabel.setLabelBoxStyle(XEnum.LabelBoxStyle.TEXT);
		}
		return mPlotLabel;
	}
	
			
	/**
	 * 绘制标签
	 * @param canvas	画布
	 * @param cData 	PieData类
	 * @param info		信息类
	 * @param savePosition	是否保存位置
	 * @param showLabel	是否显示标签
	 * @return 是否成功
	 */
	protected boolean renderLabel(Canvas canvas, PieData cData,
									PlotArcLabelInfo info,
									boolean savePosition, 
									boolean showLabel)
	{				
		if(XEnum.SliceLabelStyle.HIDE == mLabelStyle) return true;
		
		if(null == cData)return false;
		String text = cData.getLabel();
		if(""==text||text.length()==0)return true;
				
		float cirX = info.getX();
		float cirY = info.getY();
		float radius = info.getRadius();		
		double offsetAngle = info.getOffsetAngle();
				
		float calcAngle = (float) MathHelper.getInstance().add(offsetAngle , info.getCurrentAngle()/2);
		if(Float.compare(calcAngle,0.0f) == 0 
				|| Float.compare(calcAngle,0.0f) == -1 )
		{
			Log.e(TAG,"计算出来的圆心角等于0.");
			return false;
		}
		
		PointF position = null;
		
		//标签颜色与当地扇区颜色同步
		if(mIsLabelSyncColor) this.getLabelPaint().setColor(cData.getSliceColor());
	
		int color = getLabelPaint().getColor();
				
		//有定制需求
		XEnum.SliceLabelStyle labelStyle = mLabelStyle;
		if( cData.getCustLabelStyleStatus() )
		{
			labelStyle = cData.getLabelStyle();
			if( XEnum.SliceLabelStyle.INSIDE == labelStyle) 
						getLabelPaint().setTextAlign(Align.CENTER);		
			
			getLabelPaint().setColor(cData.getCustLabelColor());
		}
		
		if(XEnum.SliceLabelStyle.INSIDE  == labelStyle)
		{			 			
			//显示在扇形的内部
			position = renderLabelInside(canvas,text,cData.getItemLabelRotateAngle(),
												cirX,cirY,radius,calcAngle,showLabel);								
		}else if(XEnum.SliceLabelStyle.OUTSIDE == labelStyle){
			//显示在扇形的外部
			position = renderLabelOutside(canvas,text, cData.getItemLabelRotateAngle(),
												cirX,cirY,radius,calcAngle,showLabel);													
		}else if(XEnum.SliceLabelStyle.BROKENLINE == labelStyle){				
			//显示在扇形的外部
			//1/4处为起始点
			position = renderLabelLine(canvas,cData,cirX,cirY,radius,calcAngle,showLabel);
		}else{
			Log.e(TAG,"未知的标签处理类型.");
			return false;
		}						
		getLabelPaint().setColor(color);
		
		if(savePosition)
				info.setLabelPointF(position); //保存标签坐标位置
		return true;
	}
				
	@Override
	protected boolean postRender(Canvas canvas) throws Exception 
	{	
		try {						
			super.postRender(canvas);
			
			//计算主图表区范围
			 calcPlotRange();
			//画Plot Area背景			
			 plotArea.render(canvas);			 
			//绘制标题
			renderTitle(canvas);					
		} catch (Exception e) {
			throw e;
		}
		return true;
	}
	
	@Override
	public boolean render(Canvas canvas) throws Exception {
		// TODO Auto-generated method stubcalcPlotRange
		try {
				if (null == canvas)
						return false;
				
				if(getPanModeStatus())
				{											
					canvas.save();
					//设置原点位置					
					switch(this.getPlotPanMode())
					{
					case HORIZONTAL:
						canvas.translate(mTranslateXY[0],0);		
						break;
					case VERTICAL:
						canvas.translate(0,mTranslateXY[1]);		
						break;
					default:
						canvas.translate(mTranslateXY[0],mTranslateXY[1]);
						break;
					}
					
					//绘制图表
					super.render(canvas);
						
					//还原								
					canvas.restore();			
				}else{
					//绘制图表
					super.render(canvas);
				}
						
				return true;				
		} catch (Exception e) {
			throw e;
		}
	}
	
	

}
