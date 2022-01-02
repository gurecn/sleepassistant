package com.devdroid.sleepassistant.freefont.core.animation;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by zhaolei on 2017/12/5.
 */

public class TAnimationQueen extends BaseAnimation{
    private ArrayList<BaseAnimation> animations = new ArrayList<>();
    @SuppressLint("SoonBlockedPrivateApi")
    public TAnimationQueen(TextView tv) {
        super(tv);
    }

    public void addAnimation(BaseAnimation animation){
        if(!animations.contains(animation)){
            animations.add(animation);
        }
    }

    @Deprecated
    @Override
    public ValueAnimator setDuration(long duration) {
        return this;
    }


    @Override
    public long getDuration() {
        checkDuration();
        return super.getDuration();
    }

    @Override
    public void start() {
        checkDuration();
        super.start();
//        fRunning.setAccessible(true);
//        for (BaseAnimation animation:animations) {
//            try {
//                fRunning.setBoolean(animation,true);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        }
//        fRunning.setAccessible(false);
    }

    private void checkDuration(){
        long duration = 0;
        for (BaseAnimation animation:animations) {
            duration += animation.getDuration();
        }
        if(duration!=super.getDuration()){
            super.setDuration(duration);
        }
    }

    @Override
    public void reverse() {
        checkDuration();
        super.reverse();
    }

    @Override
    public void end() {
        super.end();
//        for (BaseAnimation animation:animations) {
//            fRunning.setAccessible(true);
//            try {
//                fRunning.setBoolean(animation,false);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            fRunning.setAccessible(false);
//        }
    }


    @Override
    public void transformCanvas(int index, RectF rect, Canvas canvas, Paint paint) {
        long currentTime = (long) ((Float)getAnimatedValue()*getDuration());
        long time = 0;
        long duration;
        for (BaseAnimation animation:animations) {
            duration = animation.getDuration();
            if(time<=currentTime&&time+duration>currentTime){
                animation.setCurrentPlayTime(currentTime-time);
                animation.transformCanvas(index,rect,canvas,paint);
                break;
            }
            time+=duration;
        }
    }


}
