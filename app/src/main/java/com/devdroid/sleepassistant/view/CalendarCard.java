package com.devdroid.sleepassistant.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.devdroid.sleepassistant.mode.CustomDate;
import com.devdroid.sleepassistant.utils.DateUtil;
import java.util.ArrayList;

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
    private Paint mCircleClickPaint; // 绘制圆形的画笔
    private Paint mCircleHollowPaint; // 绘制圆形的画笔
    private Paint mTextPaint; // 绘制文本的画笔
    private int mViewWidth; // 视图的宽度
    private int mViewHeight; // 视图的高度
    private int mCellSpace; // 单元格间距
    private Row rows[] = new Row[TOTAL_ROW]; // 行数组，每个元素代表一行
    private static CustomDate mShowDate; // 自定义的日期，包括year,month,day
    private OnCellClickListener mCellClickListener; // 单元格点击回调事件
    private int touchSlop;
    private boolean callBackCellSpace;
    private Cell mClickCell;
    private float mDownX;
    private float mDownY;
    //点击的位置
    private int currentCol = -1;
    private int currentROW = -1;
    //已签到的时间
    public ArrayList<CustomDate> signDateList = new ArrayList<CustomDate>();
    private CustomDate mOldDate;
    private CustomDate clickData;

    /**
     * 单元格点击的回调接口
     *
     * @author wuwenjie
     *
     */
    public interface OnCellClickListener {
        void clickDate(CustomDate date); // 回调点击的日期

        void changeDate(CustomDate date); // 回调滑动ViewPager改变的日期
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
        mCirclePaint.setColor(Color.parseColor("#FFF4CE"));          //签到
        mCircleClickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleClickPaint.setStyle(Paint.Style.FILL);
        mCircleClickPaint.setColor(Color.parseColor("#FFCA0A"));      //点击
        mCircleHollowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCircleHollowPaint.setStrokeWidth((float) 1.5);
        mCircleHollowPaint.setStyle(Paint.Style.STROKE);
        mCircleHollowPaint.setColor(Color.parseColor("#FFCA0A"));        //画环
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        initDate();
    }

    private void initDate() {
        mShowDate = new CustomDate();
        fillDate();//
    }

    private void fillDate() {
        int monthDay = DateUtil.getCurrentMonthDay(); // 今天
        int lastMonthDays = DateUtil.getMonthDays(mShowDate.year,mShowDate.month - 1); // 上个月的天数
        int currentMonthDays = DateUtil.getMonthDays(mShowDate.year,mShowDate.month); // 当前月的天数
        int firstDayWeek = DateUtil.getWeekDayFromDate(mShowDate.year,mShowDate.month);
        boolean isCurrentMonth = false;
        if (DateUtil.isCurrentMonth(mShowDate)) {
            isCurrentMonth = true;
        }
        int day = 0;
        for (int j = 0; j < TOTAL_ROW; j++) {
            rows[j] = new Row(j);
            for (int i = 0; i < TOTAL_COL; i++) {
                int position = i + j * TOTAL_COL; // 单元格位置
                // 这个月的
                if (position >= firstDayWeek && position < firstDayWeek + currentMonthDays) {
                    day++;
                    CustomDate date = CustomDate.modifiDayForObject(mShowDate, day);
                    rows[j].cells[i] = new Cell(date, State.CURRENT_MONTH_DAY, i, j);

                    if(signDateList.contains(date)){
                        rows[j].cells[i] = new Cell(date, State.SIGN_DAY, i, j);
                    }

                    // 今天
                    if (isCurrentMonth && day == monthDay ) {
                        rows[j].cells[i] = new Cell(date, State.TODAY, i, j);
                    }

                    if (isCurrentMonth && day > monthDay) { // 如果比这个月的今天要大，表示还没到
                        rows[j].cells[i] = new Cell(
                                date,
                                State.UNREACH_DAY, i, j);
                    }

                    if((currentROW == j&&currentCol == i)||date.equals(clickData)){
                        rows[j].cells[i] = new Cell(date, State.CLICK_DAY, i, j);
                    }

                    // 过去一个月
                } else if (position < firstDayWeek) {
                    rows[j].cells[i] = new Cell(new CustomDate(mShowDate.year,
                            mShowDate.month - 1, lastMonthDays
                            - (firstDayWeek - position - 1)),
                            State.PAST_MONTH_DAY, i, j);
                    // 下个月
                } else if (position >= firstDayWeek + currentMonthDays) {
                    rows[j].cells[i] = new Cell((new CustomDate(mShowDate.year,
                            mShowDate.month + 1, position - firstDayWeek
                            - currentMonthDays + 1)),
                            State.NEXT_MONTH_DAY, i, j);
                }


            }
        }
        mCellClickListener.changeDate(mShowDate);
        currentCol = -1;
        currentROW = -1;
        if(mShowDate.getYear()<DateUtil.getYear()||(mShowDate.getYear()== DateUtil.getYear()&&mShowDate.getMonth()<=DateUtil.getMonth()))    {
            if(mOldDate==null||mShowDate==null||!mOldDate.getYearMonth().equals(mShowDate.getYearMonth())) {
                mOldDate = new CustomDate(mShowDate.getYear(),mShowDate.getMonth(),mShowDate.getDay());
                getSignInfoMonth(mShowDate.getYearMonth());
            }
        } else{
            if(mOldDate==null||mShowDate==null||!mOldDate.getYearMonth().equals(mShowDate.getYearMonth())) {
                mOldDate = new CustomDate(mShowDate.getYear(),mShowDate.getMonth(),mShowDate.getDay());
                signDateList.clear();
                update();
            }
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
        mViewWidth = w;
        mViewHeight = h;
        mCellSpace = Math.min(mViewHeight / TOTAL_ROW, mViewWidth / TOTAL_COL);
        if (!callBackCellSpace) {
            callBackCellSpace = true;
        }
        mTextPaint.setTextSize(mCellSpace / 3);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float disX = event.getX() - mDownX;
                float disY = event.getY() - mDownY;
                if (Math.abs(disX) < touchSlop && Math.abs(disY) < touchSlop) {
                    int col = (int) (mDownX / mCellSpace);
                    int row = (int) (mDownY / mCellSpace);
                    measureClickCell(col, row);
                }
                break;
            default:
                break;
        }

        return true;
    }

    /**
     * 计算点击的单元格
     * @param col
     * @param row
     */
    private void measureClickCell(int col, int row) {
        if (col >= TOTAL_COL || row >= TOTAL_ROW)
            return;
        if (mClickCell != null) {
            rows[mClickCell.j].cells[mClickCell.i] = mClickCell;
        }
        if (rows[row] != null) {
            mClickCell = new Cell(rows[row].cells[col].date,rows[row].cells[col].state, rows[row].cells[col].i,rows[row].cells[col].j);
            CustomDate date = rows[row].cells[col].date;
            date.week = col;
            currentCol = col;
            currentROW = row;
            clickData = date;
            mCellClickListener.clickDate(date);
            // 刷新界面
            update();
        }
    }

    /**
     * 组元素
     *
     * @author wuwenjie
     *
     */
    class Row {
        public int j;

        Row(int j) {
            this.j = j;
        }

        public Cell[] cells = new Cell[TOTAL_COL];

        // 绘制单元格
        public void drawCells(Canvas canvas) {
            for (int i = 0; i < cells.length; i++) {
                if (cells[i] != null) {
                    cells[i].drawSelf(canvas);
                }
            }
        }

    }

    /**
     * 单元格元素
     *
     * @author wuwenjie
     *
     */
    class Cell {
        public CustomDate date;
        public State state;
        public int i;
        public int j;

        public Cell(CustomDate date, State state, int i, int j) {
            super();
            this.date = date;
            this.state = state;
            this.i = i;
            this.j = j;
        }

        public void drawSelf(Canvas canvas) {
            switch (state) {
                case CLICK_DAY:
                    CustomDate preDate = new CustomDate(date.getYear(),date.getMonth(),date.getDay()-1);
                    CustomDate nexDate = new CustomDate(date.getYear(),date.getMonth(),date.getDay()+1);
                    if(i!=0&&signDateList.contains(preDate)&&signDateList.contains(this.date)){
                        canvas.drawLine((float) (mCellSpace * (i + 0.5))-mCellSpace / 3,(float) ((j + 0.5) * mCellSpace),(float)(mCellSpace * (i + 0.5)-mCellSpace / 2),(float) ((j + 0.5) * mCellSpace),mCircleHollowPaint);
                    }
                    if(i!=6&&signDateList.contains(this.date)&&signDateList.contains(nexDate)&&(nexDate.getYear()<DateUtil.getYear()||(nexDate.getYear()== DateUtil.getYear()&&nexDate.getMonth()<DateUtil.getMonth())||(nexDate.getYear()== DateUtil.getYear()&&nexDate.getMonth()==DateUtil.getMonth()&&nexDate.getDay()<=DateUtil.getCurrentMonthDay())) ) {
                        canvas.drawLine((float) (mCellSpace * (i + 0.5))+mCellSpace / 3,(float) ((j + 0.5) * mCellSpace),(float)(mCellSpace * (i + 0.5)+mCellSpace / 2),(float) ((j + 0.5) * mCellSpace),mCircleHollowPaint);
                    }
                    mTextPaint.setColor(Color.parseColor("#FFFFFF"));
                    canvas.drawCircle((float) (mCellSpace * (i + 0.5)), (float) ((j + 0.5) * mCellSpace), mCellSpace / 3 + 1f, mCircleHollowPaint);
                    canvas.drawCircle((float) (mCellSpace * (i + 0.5)),(float) ((j + 0.5) * mCellSpace), mCellSpace / 3,mCircleClickPaint);
                    break;
                case SIGN_DAY:
                    preDate = new CustomDate(date.getYear(),date.getMonth(),date.getDay()-1);
                    nexDate = new CustomDate(date.getYear(),date.getMonth(),date.getDay()+1);
                    if(i!=0&&signDateList.contains(preDate)){
                        canvas.drawLine((float) (mCellSpace * (i + 0.5))-mCellSpace / 3,(float) ((j + 0.5) * mCellSpace),(float)(mCellSpace * (i + 0.5)-mCellSpace / 2),(float) ((j + 0.5) * mCellSpace),mCircleHollowPaint);
                    }
                    if(i!=6&&signDateList.contains(nexDate)&&(nexDate.getYear()<DateUtil.getYear()||(nexDate.getYear()== DateUtil.getYear()&&nexDate.getMonth()<DateUtil.getMonth())||(nexDate.getYear()== DateUtil.getYear()&&nexDate.getMonth()==DateUtil.getMonth()&&nexDate.getDay()<=DateUtil.getCurrentMonthDay())) ) {
                        canvas.drawLine((float) (mCellSpace * (i + 0.5))+mCellSpace / 3,(float) ((j + 0.5) * mCellSpace),(float)(mCellSpace * (i + 0.5)+mCellSpace / 2),(float) ((j + 0.5) * mCellSpace),mCircleHollowPaint);
                    }
                    canvas.drawCircle((float) (mCellSpace * (i + 0.5)), (float) ((j + 0.5) * mCellSpace), mCellSpace / 3 + 1f, mCircleHollowPaint);
                    mTextPaint.setColor(Color.parseColor("#131313"));
                    canvas.drawCircle((float) (mCellSpace * (i + 0.5)),(float) ((j + 0.5) * mCellSpace), mCellSpace / 3,mCirclePaint);
                    break;
                case TODAY: // 今天
                    preDate = new CustomDate(date.getYear(),date.getMonth(),date.getDay()-1);
                    nexDate = new CustomDate(date.getYear(),date.getMonth(),date.getDay()+1);
                    if(i!=0&&signDateList.contains(preDate)){
                        canvas.drawLine((float) (mCellSpace * (i + 0.5))-mCellSpace / 3,(float) ((j + 0.5) * mCellSpace),(float)(mCellSpace * (i + 0.5)-mCellSpace / 2),(float) ((j + 0.5) * mCellSpace),mCircleHollowPaint);
                    }
                    if(i!=6&&signDateList.contains(nexDate)&&(nexDate.getYear()<DateUtil.getYear()||(nexDate.getYear()== DateUtil.getYear()&&nexDate.getMonth()<DateUtil.getMonth())||(nexDate.getYear()== DateUtil.getYear()&&nexDate.getMonth()==DateUtil.getMonth()&&nexDate.getDay()<=DateUtil.getCurrentMonthDay())) ) {
                        canvas.drawLine((float) (mCellSpace * (i + 0.5))+mCellSpace / 3,(float) ((j + 0.5) * mCellSpace),(float)(mCellSpace * (i + 0.5)+mCellSpace / 2),(float) ((j + 0.5) * mCellSpace),mCircleHollowPaint);
                    }
                    mTextPaint.setColor(Color.parseColor("#131313"));
                    canvas.drawCircle((float) (mCellSpace * (i + 0.5)), (float) ((j + 0.5) * mCellSpace), mCellSpace / 3 + 1f, mCircleHollowPaint);
                    if(mClickCell == null||mClickCell.date.equals(this.date)){
                        canvas.drawCircle((float) (mCellSpace * (i + 0.5)),(float) ((j + 0.5) * mCellSpace), mCellSpace / 3,mCirclePaint);
                    }
                    break;
                case CURRENT_MONTH_DAY: // 当前月日期
                    mTextPaint.setColor(Color.BLACK);
                    break;
                case PAST_MONTH_DAY: // 过去一个月
                case NEXT_MONTH_DAY: // 下一个月
                    mTextPaint.setColor(Color.parseColor("#999999"));
                    break;
                case UNREACH_DAY: // 还未到的天
                    mTextPaint.setColor(Color.parseColor("#131313"));
                    break;
                default:
                    break;
            }
            // 绘制文字
            String content = date.day + "";
            canvas.drawText(content,(float) ((i + 0.5) * mCellSpace - mTextPaint.measureText(content) / 2), (float) ((j + 0.7)
                            * mCellSpace - mTextPaint.measureText(content, 0, 1) / 2), mTextPaint);
        }
    }

    /**
     *
     * @author wuwenjie 单元格的状态 当前月日期，过去的月的日期，下个月的日期
     */
    enum State {
        TODAY,CURRENT_MONTH_DAY, PAST_MONTH_DAY, NEXT_MONTH_DAY, UNREACH_DAY, SIGN_DAY,CLICK_DAY;
    }

    // 从左往右划，上一个月
    public void leftSlide() {
        if (mShowDate.month == 1) {
            mShowDate.month = 12;
            mShowDate.year -= 1;
        } else {
            mShowDate.month -= 1;
        }
        update();
    }

    // 从右往左划，下一个月
    public void rightSlide() {
        if (mShowDate.month == 12) {
            mShowDate.month = 1;
            mShowDate.year += 1;
        } else {
            mShowDate.month += 1;
        }
        update();
    }

    public void update() {
        fillDate();
        invalidate();
    }
    private void getSignInfoMonth(String dataString){
        signDateList.clear();
        update();
//        RESTHttpUtils httpUtils = new RESTHttpUtils(getContext(),
//                new RESTHttpUtils.NetWorkUtilCallBack() {
//                    @Override
//                    public <T> void loadDone(T entity) {}
//                    @Override
//                    public void respCode(String code) {}
//                    @Override
//                    public <T> void loadDone(List<T> list) {
//                        if(list!=null&&list.size()>0){
//                            ArrayList<String> signList = (ArrayList<String>) list;
//                            for(String day:signList){
//                                String[] in = day.split("-");
//                                CustomDate da = new CustomDate(Integer.valueOf(in[0]),Integer.valueOf(in[1]),Integer.valueOf(in[2]));
//                                signDateList.add(da);
//                            }
//                            update();
//                        }
//                    }
//                    @Override
//                    public void respErrorMsg(String msg) {
//                    }
//                }, false);
//        String url = String.format(ApiConstant.POST_GROUP_SIGN_MONTH,groupId, SharedPreferencesUtils.getString(getContext(), "token", ""));
//        BaseRequestParams para = new BaseRequestParams();
//        para.add("date",dataString);
//        RESTHttpUtils.PostTask<String> get = httpUtils.new PostTask<String>(para,String.class);
//        get.execute(url);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
