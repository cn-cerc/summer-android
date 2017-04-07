package cn.cerc.summer.android.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by admin on 2017/4/7.
 */

//柱状图绘制

public class StatscsView extends View {



    public StatscsView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub

        init(context, null);
    }

    public StatscsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        init(context, attrs);
    }

    //	坐标轴 轴线 画笔：
    private Paint axisLinePaint;
    //	坐标轴水平内部 虚线画笔
    private Paint hLinePaint;
    //	绘制文本的画笔
    private Paint titlePaint;
    //	矩形画笔 柱状图的样式信息
    private Paint recPaint;
    private void init(Context context, AttributeSet attrs)
    {

        axisLinePaint = new Paint();
        hLinePaint = new Paint();
        titlePaint = new Paint();
        recPaint = new Paint();

        axisLinePaint.setColor(Color.DKGRAY);
        hLinePaint.setColor(Color.LTGRAY);
        titlePaint.setColor(Color.BLACK);

    }




    //7 条
    private int[] thisYear;

    //7 条
    private int[] lastYear;


    /**
     * 跟新自身的数据 需要View子类重绘。
     *
     * 主线程 刷新控件的时候调用：
     * this.invalidate();  失效的意思。
     * this.postInvalidate();  可以子线程 更新视图的方法调用。
     *
     * */
    //updata this year data
    public void updateThisData(int[] thisData)
    {
        thisYear = thisData;
//		this.invalidate(); //失效的意思。
        this.postInvalidate();  //可以子线程 更新视图的方法调用。
    }

    //updata last year data
    public void updateLastData(int[] lastData)
    {
        lastYear = lastData;
//		this.invalidate(); //失效的意思。
        this.postInvalidate();  //可以子线程 更新视图的方法调用。
    }


    private String[] yTitlesStrings =
            new String[]{"2000","2100","2200","2300","2400","2500"};

    private String[] xTitles =
            new String[]{"2001","2002","2003","2004","2005"};

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        // 1 绘制坐标线：
        canvas.drawLine(100, 10, 100, 320, axisLinePaint);

        canvas.drawLine(100, 320, width-10 , 320, axisLinePaint);

        // 2 绘制坐标内部的水平线

        int leftHeight = 300;// 左侧外周的 需要划分的高度：

        int hPerHeight = leftHeight/4;

        hLinePaint.setTextAlign(Paint.Align.CENTER);
        for(int i=0;i<4;i++)
        {
            canvas.drawLine(100, 20+i*hPerHeight, width-10, 20+i*hPerHeight, hLinePaint);
        }


        // 3 绘制 Y 周坐标

        Paint.FontMetrics metrics =titlePaint.getFontMetrics();
        int descent = (int)metrics.descent;
        titlePaint.setTextAlign(Paint.Align.RIGHT);
        for(int i=0;i<yTitlesStrings.length;i++)
        {
            canvas.drawText(yTitlesStrings[i], 80, 20+i*hPerHeight+descent, titlePaint);
        }

        // 4  绘制 X 周 做坐标

        int xAxisLength = width-110;
        int columCount = xTitles.length+1;
        int step = xAxisLength/columCount;

        for(int i=0;i<columCount-1;i++)
        {
            canvas.drawText(xTitles[i], 100+step*(i+1), 360 , titlePaint);
        }

        // 5 绘制矩形

        if(thisYear != null && thisYear.length >0)
        {
            int thisCount = thisYear.length;


            for(int i=0;i<thisCount;i++)
            {
                int value = thisYear[i];

                int num = 8 - value / 10000 ;


                recPaint.setColor(0xFF1078CF);

                Rect rect = new Rect();

                rect.left  = 100 + step * (i+1)  - 10;
                rect.right = 100 + step * (i+1)  + 10;

//				当前的相对高度：
                int rh = (leftHeight * num) / 8 ;

                rect.top = rh + 20;
                rect.bottom = 320 ;

                canvas.drawRect(rect, recPaint);

            }
        }


        if(lastYear != null && lastYear.length >0)
        {
            int thisCount = lastYear.length;


            for(int i=0;i<thisCount;i++)
            {
                int value = lastYear[i];

                int num = 8 - value / 10000 ;


                recPaint.setColor(0xFFAA1122);

                Rect rect = new Rect();

                rect.left  = 100 + step * (i+1)  - 10;
                rect.right = 100 + step * (i+1)  + 10;

//				当前的相对高度：
                int rh = (leftHeight * num) / 8 ;

                rect.top = rh + 20;
                rect.bottom = 320 ;

                canvas.drawRect(rect, recPaint);

            }
        }




    }


}
