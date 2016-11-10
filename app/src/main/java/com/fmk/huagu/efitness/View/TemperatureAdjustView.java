package com.fmk.huagu.efitness.View;

        import android.content.Context;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Matrix;
        import android.graphics.Paint;
        import android.graphics.Path;
        import android.graphics.Point;
        import android.graphics.RectF;
        import android.graphics.SweepGradient;
        import android.util.AttributeSet;
        import android.view.MotionEvent;
        import android.view.View;

/**
 * Created by fengm on 2016/11/5.
 */

public class TemperatureAdjustView extends View {
    public TemperatureAdjustView(Context context) {
        super(context);
        initView();
    }

    public TemperatureAdjustView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public TemperatureAdjustView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

//    public TemperatureAdjustView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        initView();
//    }

    private int minheight;
    private int width;
    private int left, top, right, bottom;

    private float radius;

    private float startangle;
    private float endangle;

    private Paint paint;
    private Point point;//圆形点


    private Point point61;//减号六边形的中心
    private Point point62;//加号六边形的中心
    private Point point63;//数字六边形的中心

    private boolean isdrag = false;//是否可以拖动
    private double degree = 135f;// 这个角度是分正负的
    private double curdegree = 135f;


    private int adaptive;//一个用于适应大小的整体调整变量

    private final int maxtemperature = 70;//最大温度

    private int curtemperature=0;//当前的温度

    private float xx3, yy3;
    private int big_index = 0;// 哪个变大一点

    private float rotatedegree;

    /**
     * 获取当前的温度
     *
     * @return 返回的温度
     */
    public int getCurtemperature() {
        if (degree >= 135f)
            rotatedegree = (float) degree-135f;
        else if (degree < 0)
            rotatedegree = 180f + (float) degree + 45f;
        else if (degree >= 0 && degree <= 45f)
            rotatedegree = (float) degree+225f;
        curtemperature = (int) (Math.abs(rotatedegree) / (float) (270 / (float) maxtemperature));
        return curtemperature;
    }

    /**
     * 设置当前温度
     *
     * @param curtemperature 温度
     */
    public void setCurtemperature(int curtemperature) {
        this.curtemperature = curtemperature;
        rotatedegree = curtemperature * (270 / (float) maxtemperature);
        if (rotatedegree<45f)
            degree = rotatedegree + 135f;
        else if (rotatedegree>45f && rotatedegree<135f)
            degree = rotatedegree - 45f - 180f;
        else if (rotatedegree > 135f)
            degree = rotatedegree - 135f;
    }

    private void initView() {
        paint = new Paint();
    }

    private void getPaint(int res, Paint.Style ps) {
        paint.reset();
        paint.setColor(res);
        paint.setStyle(ps);
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(30f);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //背景
        getPaint(Color.parseColor("#39344E"), Paint.Style.STROKE);
        RectF recf = new RectF(left, top, right, bottom);
        canvas.drawArc(recf, startangle, 270f, false, paint);

        getCurtemperature();

        //渐变进度
        paint.reset();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(30f);
        SweepGradient sg = new SweepGradient(point.x,point.y,new int[]{Color.BLUE,Color.CYAN,Color.MAGENTA,Color.RED,Color.GRAY},null);
        Matrix matrix = new Matrix();
        matrix.setRotate(45f,point.x,point.y);
        sg.setLocalMatrix(matrix);
        paint.setShader(sg);
        canvas.drawArc(recf,startangle, rotatedegree,false,paint);

        //这里是绘制的减号六边形
        float xx = point.x + radius * (float) Math.cos(startangle * Math.PI / 180);
        float yy = point.y + radius * (float) Math.sin(startangle * Math.PI / 180);
        getPaint(Color.parseColor("#3E3953"), Paint.Style.FILL);
        point61 = new Point((int) xx, (int) yy);
        draw6(canvas, point61, 1);

        //这里是绘制的减号
        getPaint(Color.parseColor("#9C9BA4"), Paint.Style.FILL);
        paint.setStrokeWidth(adaptive / 5);
        canvas.drawLine(xx - adaptive / 2, yy, xx + adaptive / 2, yy, paint);

        //这里是绘制的加号六边形
        float xx1 = point.x + radius * (float) Math.cos(endangle * Math.PI / 180);
        float yy1 = point.y + radius * (float) Math.sin(endangle * Math.PI / 180);
        getPaint(Color.parseColor("#3E3953"), Paint.Style.FILL);
        point62 = new Point((int) xx1, (int) yy1);
        draw6(canvas, point62, 2);

        //这里是绘制的加号
        getPaint(Color.parseColor("#9C9BA4"), Paint.Style.FILL);
        paint.setStrokeWidth(adaptive / 5);
        canvas.drawLine(xx1 - adaptive / 2, yy1, xx1 + adaptive / 2, yy1, paint);
        canvas.drawLine(xx1, yy1 - adaptive / 2, xx1, yy1 + adaptive / 2, paint);

        //这是中间的摄氏度
        getPaint(Color.parseColor("#AAA9AF"), Paint.Style.FILL);
        paint.setTextSize(radius / 7 * 6);
        float offset = (paint.measureText(curtemperature + "") / 3);
        float bottom = paint.getFontMetrics().bottom;
        canvas.drawText(curtemperature + "", point.x - offset - (radius / 9 * 2), point.y + ((bottom - paint.getFontMetrics().top) / 4), paint);
        getPaint(Color.parseColor("#EF661A"), Paint.Style.FILL);
        paint.setTextSize((radius / 3));
        canvas.drawText("℃", point.x + (offset * 2) - (radius / 9 * 2), point.y - bottom / 2, paint);

        //中间下面的时间
        deawTime(canvas, point.x, yy1);

        //这个是可以滑动的滑块
        xx3 = point.x + radius * (float) Math.cos(degree * Math.PI / 180);
        yy3 = point.y + radius * (float) Math.sin(degree * Math.PI / 180);
        getPaint(Color.parseColor("#D58104"), Paint.Style.FILL);
        point63 = new Point((int) xx3, (int) yy3);
        draw6(canvas, point63, 3);
        //滑块上面的文字
        getPaint(Color.parseColor("#FFFFFF"), Paint.Style.FILL);
        paint.setTextSize(adaptive / 3 * 2);
        canvas.drawText(curtemperature + "℃", xx3 - paint.measureText(curtemperature + "℃") / 2, yy3 + (paint.getFontMetrics().bottom - paint.getFontMetrics().top) / 4, paint);

    }

    private void deawTime(Canvas canvas, float xx, float yy) {
        getPaint(Color.parseColor("#AAA9AF"), Paint.Style.STROKE);
        paint.setTextSize(adaptive);
        paint.setStrokeWidth(adaptive / 10);
        float timetextwidth = paint.measureText(showtime);
        float x = xx - (timetextwidth + (float) 4) / 2;
        canvas.drawCircle(x, yy, adaptive / 2, paint);

        canvas.drawLine(x, yy, x, yy - (adaptive / 4), paint);
        canvas.drawLine(x, yy, x + (adaptive / 3), yy, paint);

        getPaint(Color.parseColor("#AAA9AF"), Paint.Style.FILL);
        paint.setTextSize(adaptive);
        float texth = paint.getFontMetrics().bottom - paint.getFontMetrics().top;
        canvas.drawText(showtime, xx - (((timetextwidth + (float) adaptive) / 2) - adaptive), yy + (texth / 4), paint);

    }


    private void draw6(Canvas canvas, Point point, int big) {
        int sidewidth = adaptive;
        if (big == big_index) {
            sidewidth = adaptive + adaptive / 5;
        }
        float startx = point.x - sidewidth;
        float starty = point.y;

        float hh = (float) (Math.sqrt(3) * sidewidth);
        Path path1 = new Path();
        path1.moveTo(startx, starty);
        path1.lineTo((point.x - sidewidth / 2), point.y - (hh / 2));
        path1.lineTo((point.x + sidewidth / 2), point.y - (hh / 2));
        path1.lineTo((point.x + sidewidth), starty);
        path1.lineTo((point.x + sidewidth / 2), point.y + (hh / 2));
        path1.lineTo((point.x - sidewidth / 2), point.y + (hh / 2));
        path1.close();
        canvas.drawPath(path1, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = MeasureSpec.getSize(widthMeasureSpec);
        minheight = width / 3 * 2;

        adaptive = width / 20;

        top = adaptive;
        left = (width - (minheight - top * 2)) / 2;
        right = width - left;
        bottom = minheight - top;

        point = new Point(width / 2, minheight / 2);

        radius = (bottom - top) / 2;

        startangle = (90f + 45f);
        endangle = startangle + 270f;

        setMeasuredDimension(width, minheight);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if (isIn(x, y)) {
                    isdrag = true;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float xx = event.getX();
                float yy = event.getY();
                if (isdrag) {
                    curdegree = (Math.atan2(yy - (float) point.y, xx - (float) point.x) * 180) / Math.PI;
                    if (curdegree > 45f && curdegree < 135f) {
                        isdrag = false;
                        return false;
                    }
                    degree = curdegree;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                isdrag = false;
                big_index = 0;
                if (!isdrag) {
                    invalidate();
                } else {
                    degree = curdegree;
                }
                onTemperaturechangelistener.tmpChange(getCurtemperature());
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean isIn(float x, float y) {
        if (Math.abs(x - point63.x) < adaptive / 2 && Math.abs(y - point63.y) < adaptive / 2) {
            big_index = 3;
            return true;
        }
        if (Math.abs(x - point61.x) < adaptive / 2 && Math.abs(y - point61.y) < adaptive / 2) {
            big_index = 1;
            curdegree -= (270 / 70);
            if (curdegree >= (135f) || curdegree <= 45f) {
                degree = curdegree;
                onTemperaturechangelistener.tmpChange(getCurtemperature());
                invalidate();
            }
            return false;
        }
        if (Math.abs(x - point62.x) < adaptive / 2 && Math.abs(y - point62.y) < adaptive / 2) {
            big_index = 2;
            curdegree += (270 / 70);
            if (curdegree >= 135f || curdegree <= 45f) {
                degree = curdegree;
                onTemperaturechangelistener.tmpChange(getCurtemperature());
                invalidate();
            }
            return false;
        }
        big_index = 0;
        return false;
    }


    private String showtime = "00:00";

    /**
     * 显示的下方的时间
     * @param time  显示的时间  格式  00:00
     */
    public void showTime(String time){
        showtime = time;
    }

    private OnTemperatureChangeListener onTemperaturechangelistener;
    public interface OnTemperatureChangeListener {
        void tmpChange(int curtemperature);
    }

    /**
     * 设置温度变化监听
     * @param onthisviewchangelintener
     */
    public void setOnTemperatureChangeListener(OnTemperatureChangeListener onthisviewchangelintener){
        this.onTemperaturechangelistener = onthisviewchangelintener;
    }

}
