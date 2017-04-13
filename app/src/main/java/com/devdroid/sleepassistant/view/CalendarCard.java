package com.devdroid.sleepassistant.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.mode.SleepDataMode;
import com.devdroid.sleepassistant.utils.DateUtil;

import java.util.List;

/**
 * 自定义日历卡
 * Created with IntelliJ IDEA.
 * User: Gaolei
 * Date: 2015/12/17
 * Email: pdsfgl@live.com
 */
public class CalendarCard extends View {

    private static final int TOTAL_COL = 7; // 7列
    private static final int TOTAL_ROW = 6; // 6行
    private Paint mCirclePaint; // 绘制圆形的画笔
    private Paint mCircleHollowPaint; // 绘制圆形的画笔
    private Paint mTextPaint; // 绘制文本的画笔
    private int mCellSpace; // 单元格间距
    private Row rows[] = new Row[TOTAL_ROW]; // 行数组，每个元素代表一行
    private static SleepDataMode mShowDate; // 自定义的日期，包括year,month,day
    private OnCellClickListener mCellClickListener; // 单元格点击回调事件
    private int touchSlop;
    private boolean callBackCellSpace;
    private List<SleepDataMode> mSleepDataModes;
    private boolean mWaitForTouchUp;  //按下判断
    private int mResponseTimes;  //长按时间判断，<1,未触发长按
    private SleepDataMode mCurrentSleepDateMode;
    private LongPressTimer mLongPressTimer;

    /**
     * 单元格点击的回调接口
     *
     * @author wuwenjie
     *
     */
    public interface OnCellClickListener {
        void clickDate(SleepDataMode date); // 回调点击的日期
        void longClickDate(SleepDataMode date); // 回调长按的日期
        void changeDate(SleepDataMode date); // 回调滑动ViewPager改变的日期
    }

    public CalendarCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CalendarCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CalendarCard(Context context) {
        super(context);
        init(context);
    }

    public CalendarCard(Context context, OnCellClickListener listener) {
        super(context);
        this.mCellClickListener = listener;
        init(context);
    }

    private void init(Context context) {
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCircleHollowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleHollowPaint.setStrokeWidth((float) 1.5);
        mCircleHollowPaint.setStyle(Paint.Style.STROKE);
        mCircleHollowPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_calendar_card_click));       //画环
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        initDate();
        mLongPressTimer = new LongPressTimer();
    }

    private void initDate() {
        mShowDate = new SleepDataMode();
        fillDate(false);
    }

    private void fillDate(boolean isClick) {
        int monthDay = DateUtil.getCurrentMonthDay(); // 今天
        int lastMonthDays = DateUtil.getMonthDays(mShowDate.getYear(),mShowDate.getMonth() - 1); // 上个月的天数
        int currentMonthDays = DateUtil.getMonthDays(mShowDate.getYear(),mShowDate.getMonth()); // 当前月的天数
        int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.getYear(),mShowDate.getMonth());
        boolean isCurrentMonth = false;
        if (DateUtil.isCurrentMonth(mShowDate)) {
            isCurrentMonth = true;
        }
        if(!isClick){
            mCellClickListener.changeDate(mShowDate);
            mSleepDataModes = LauncherModel.getInstance().getSnssdkTextDao().querySleepDataInfo(mShowDate.getYear(), mShowDate.getMonth());
        }
        int day = 0;
        for (int j = 0; j < TOTAL_ROW; j++) {
            rows[j] = new Row(j);
            for (int i = 0; i < TOTAL_COL; i++) {
                int position = i + j * TOTAL_COL; // 单元格位置
                if (position >= firstDayWeek && position < firstDayWeek + currentMonthDays) {// 这个月的
                    day++;
                    SleepDataMode date = SleepDataMode.modifiDayForObject(mShowDate, day);
                    rows[j].cells[i] = new Cell(date, State.CURRENT_MONTH_DAY, i, j);
                    if (isCurrentMonth && day == monthDay ) {// 今天
                        rows[j].cells[i] = new Cell(date, State.TODAY, i, j);
                    }
                    if (isCurrentMonth && day > monthDay) { // 如果比这个月的今天要大，表示还没到
                        rows[j].cells[i] = new Cell(date, State.UNREACH_DAY, i, j);
                    }
                    if(mSleepDataModes.contains(date)){
                        for(SleepDataMode sleepDataMode : mSleepDataModes){
                            if(sleepDataMode.equals(date)){
                                rows[j].cells[i] = new Cell(sleepDataMode, transformState(sleepDataMode), i, j);
                            }
                        }
                    }
                } else if (position < firstDayWeek) {// 过去一个月
                    SleepDataMode sleepDataMode = new SleepDataMode(mShowDate.getYear(), mShowDate.getMonth() - 1, lastMonthDays - (firstDayWeek - position - 1), mShowDate.getHour(), mShowDate.getMinute());
                    rows[j].cells[i] = new Cell(sleepDataMode, State.PAST_MONTH_DAY, i, j);
                } else if (position >= firstDayWeek + currentMonthDays) {// 下个月
                    SleepDataMode sleepDataMode = new SleepDataMode(mShowDate.getYear(), mShowDate.getMonth() + 1, position - firstDayWeek - currentMonthDays + 1, mShowDate.getHour(), mShowDate.getMinute());
                    rows[j].cells[i] = new Cell(sleepDataMode, State.NEXT_MONTH_DAY, i, j);
                }
            }
        }
    }

    /**
     * 时间转化成状态
     */
    private State transformState(SleepDataMode sleepDataMode) {
        int minutes = sleepDataMode.getHour() * 60 + sleepDataMode.getMinute();
        if(minutes >= 1170 && minutes <= 1350){//19:30--22:30
            return State.GREAT;
        }else if(minutes > 1350 && minutes <= 1420){//22:30--23:40
            return State.WARN;
        } else {
            return State.BAD;//其他时间
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < TOTAL_ROW; i++) {
            if (rows[i] != null) {
                rows[i].drawCells(canvas);
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCellSpace = Math.min(h / TOTAL_ROW, w / TOTAL_COL);
        if (!callBackCellSpace) {
            callBackCellSpace = true;
        }
        mTextPaint.setTextSize(mCellSpace / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mWaitForTouchUp = true;
                float disX = event.getX() - event.getX();
                float disY = event.getY() - event.getY();
                if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
                    int col = (int) (event.getX() / mCellSpace);
                    int row = (int) (event.getY() / mCellSpace);
                    measureClickCell(col, row);
                }
                if(mCurrentSleepDateMode != null) {
                    mLongPressTimer.startTimer();
                }
                break;
            case MotionEvent.ACTION_UP:
                mWaitForTouchUp = false;
                if(mCurrentSleepDateMode != null && mResponseTimes < 1){
                    mCellClickListener.clickDate(mCurrentSleepDateMode);
                }
                mLongPressTimer.removeTimer();
                break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
                mWaitForTouchUp = false;
                mCurrentSleepDateMode = null;
                mLongPressTimer.removeTimer();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 计算点击的单元格
     */
    private void measureClickCell(int col, int row) {
        if (col >= TOTAL_COL || row >= TOTAL_ROW) return;
        if (rows[row] != null) {
            mCurrentSleepDateMode = rows[row].cells[col].date;
        } else {
            mCurrentSleepDateMode = null;
        }
    }

    /**
     * 组元素
     */
    class Row {
        int j;
        Row(int j) {
            this.j = j;
        }
        Cell[] cells = new Cell[TOTAL_COL];
        // 绘制单元格
        void drawCells(Canvas canvas) {
            for (Cell cell : cells) {
                if (cell != null) {
                    cell.drawSelf(canvas);
                }
            }
        }

    }

    /**
     * 单元格元素
     */
    class Cell {
        private SleepDataMode date;
        State state;
        private int col;
        private int row;

        Cell(SleepDataMode date, State state, int col, int row) {
            super();
            this.date = date;
            this.state = state;
            this.col = col;
            this.row = row;
        }

        void drawSelf(Canvas canvas) {
            SleepDataMode preDate = new SleepDataMode(date.getYear(),date.getMonth(),date.getDay()-1, date.getHour(), date.getMinute());
            SleepDataMode nexDate = new SleepDataMode(date.getYear(),date.getMonth(),date.getDay()+1, date.getHour(), date.getMinute());
            switch (state) {
                case GREAT:
                    drawConnectline(canvas, preDate, nexDate);
                    canvas.drawCircle((float) (mCellSpace * (col + 0.5)), (float) ((row + 0.5) * mCellSpace), mCellSpace / 3 + 1f, mCircleHollowPaint);
                    mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_calendar_card_future_text));
                    mCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.color_calendar_card_gerat));
                    canvas.drawCircle((float) (mCellSpace * (col + 0.5)),(float) ((row + 0.5) * mCellSpace), mCellSpace / 3,mCirclePaint);
                    break;
                case WARN:
                    drawConnectline(canvas, preDate, nexDate);
                    canvas.drawCircle((float) (mCellSpace * (col + 0.5)), (float) ((row + 0.5) * mCellSpace), mCellSpace / 3 + 1f, mCircleHollowPaint);
                    mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_calendar_card_future_text));
                    mCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.color_calendar_card_warn));
                    canvas.drawCircle((float) (mCellSpace * (col + 0.5)),(float) ((row + 0.5) * mCellSpace), mCellSpace / 3,mCirclePaint);
                    break;
                case BAD:
                    drawConnectline(canvas, preDate, nexDate);
                    canvas.drawCircle((float) (mCellSpace * (col + 0.5)), (float) ((row + 0.5) * mCellSpace), mCellSpace / 3 + 1f, mCircleHollowPaint);
                    mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_calendar_card_future_text));
                    mCirclePaint.setColor(ContextCompat.getColor(getContext(), R.color.color_calendar_card_bad));
                    canvas.drawCircle((float) (mCellSpace * (col + 0.5)),(float) ((row + 0.5) * mCellSpace), mCellSpace / 3,mCirclePaint);
                    break;
                case TODAY: // 今天
                    drawConnectline(canvas, preDate, nexDate);
                    mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_calendar_card_yellow));
                    canvas.drawCircle((float) (mCellSpace * (col + 0.5)), (float) ((row + 0.5) * mCellSpace), mCellSpace / 3 + 1f, mCircleHollowPaint);
                    break;
                case CURRENT_MONTH_DAY: // 当前月日期
                    mTextPaint.setColor(Color.BLACK);
                    break;
                case PAST_MONTH_DAY: // 过去一个月
                case NEXT_MONTH_DAY: // 下一个月
                    mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_calendar_card_next_text));
                    break;
                case UNREACH_DAY: // 还未到的天
                    mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_calendar_card_next_text));
                    break;
                default:
                    break;
            }
            // 绘制文字
            String content = date.getDay() + "";
            canvas.drawText(content,(float) ((col + 0.5) * mCellSpace - mTextPaint.measureText(content) / 2), (float) ((row + 0.7)
                    * mCellSpace - mTextPaint.measureText(content, 0, 1) / 2), mTextPaint);
        }

        /**
         * 画连接线
         */
        private void drawConnectline(Canvas canvas, SleepDataMode preDate, SleepDataMode nexDate) {
            if(col!=0&&mSleepDataModes.contains(preDate)){
                canvas.drawLine((float) (mCellSpace * (col + 0.5))-mCellSpace / 3,(float) ((row + 0.5) * mCellSpace),(float)(mCellSpace * (col + 0.5)-mCellSpace / 2),(float) ((row + 0.5) * mCellSpace),mCircleHollowPaint);
            }
            if(col!=6&&mSleepDataModes.contains(nexDate)) {
                canvas.drawLine((float) (mCellSpace * (col + 0.5))+mCellSpace / 3,(float) ((row + 0.5) * mCellSpace),(float)(mCellSpace * (col + 0.5)+mCellSpace / 2),(float) ((row + 0.5) * mCellSpace),mCircleHollowPaint);
            }
        }
    }

    /**
     * @author wuwenjie 单元格的状态 当前月日期，过去的月的日期，下个月的日期
     */
    enum State {
        TODAY,CURRENT_MONTH_DAY, PAST_MONTH_DAY, NEXT_MONTH_DAY, UNREACH_DAY, GREAT, WARN, BAD
    }

    // 从左往右划，上一个月
    public void leftSlide() {
        if (mShowDate.getMonth() == 1) {
            mShowDate.setMonth(12);
            mShowDate.setYear(mShowDate.getYear() - 1);
        } else {
            mShowDate.setMonth(mShowDate.getMonth() - 1);
        }
        update(false);
        mCurrentSleepDateMode = null;
    }

    // 从右往左划，下一个月
    public void rightSlide() {
        if (mShowDate.getMonth() == 12) {
            mShowDate.setMonth(1);
            mShowDate.setYear(mShowDate.getYear() + 1);
        } else {
            mShowDate.setMonth(mShowDate.getMonth() + 1);
        }
        update(false);
        mCurrentSleepDateMode = null;
    }

    public void update(boolean isClick) {
        fillDate(isClick);
        invalidate();
    }




    /**
     * 长按定时器
     */
    public class LongPressTimer extends Handler implements Runnable {
        /**
         * When user presses a key for a long time, the timeout interval to
         * generate first {@link #LONG_PRESS_KEYNUM1} key events. 长按时间一
         */
        static final int LONG_PRESS_TIMEOUT1 = 200;
        static final int LONG_PRESS_KEYNUM1 = 1;
        static final int LONG_PRESS_KEYNUM2 = 2;


        LongPressTimer() {
        }

        void startTimer() {
            mResponseTimes = 0;
            postAtTime(this, SystemClock.uptimeMillis() + LONG_PRESS_TIMEOUT1);
        }

        boolean removeTimer() {
            mResponseTimes = 0;
            removeCallbacks(this);
            return true;
        }

        public void run() {
            if (mWaitForTouchUp) {
                long timeout;
                if (mResponseTimes <= LONG_PRESS_KEYNUM1) {
                    timeout = LONG_PRESS_TIMEOUT1;
                    postAtTime(this, SystemClock.uptimeMillis() + timeout);
                } else if (mResponseTimes == LONG_PRESS_KEYNUM2) {
                    if(mCurrentSleepDateMode != null) {
                        mCellClickListener.longClickDate(mCurrentSleepDateMode);
                    }
                    this.removeTimer();
                }
            mResponseTimes++;
            }
        }
    }
}
