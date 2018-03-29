package com.qiuyi.cn.orangemodule.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.qiuyi.cn.orangemodule.R;

/**
 * Created by Administrator on 2018/1/23.
 * 自定义水平\垂直电池控件
 */
public class BatteryView extends View {

    //电量
    private int mPower = 100;
    //方向
    private int orientation;
    //颜色
    private int mColor;
    //长宽
    private int width;
    private int height;


    public BatteryView(Context context) {
        super(context);
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //找到attrs.xml文件
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Battery);
        orientation = typedArray.getInt(R.styleable.Battery_batteryOrientation, 0);
        mColor = typedArray.getColor(R.styleable.Battery_batteryColor, 0xFFFFFFFF);
        mPower = typedArray.getInt(R.styleable.Battery_batteryPower, 100);

        width = getMeasuredWidth();
        height = getMeasuredHeight();

        /**
         * recycle() :官方的解释是：回收TypedArray，以便后面重用。在调用这个函数后，你就不能再使用这个TypedArray。
         * 在TypedArray后调用recycle主要是为了缓存。当recycle被调用后，这就说明这个对象从现在可以被重用了。
         *TypedArray 内部持有部分数组，它们缓存在Resources类中的静态字段中，这样就不用每次使用前都需要分配内存。
         */
        typedArray.recycle();
    }

    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //对View上的内容进行测量后得到的View内容占据的宽度
        width = getMeasuredWidth();
        //对View上的内容进行测量后得到的View内容占据的高度
        height = getMeasuredHeight();
    }

    //绘画
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //判断电池方向    horizontal:0    vertical:1
        if (orientation == 0) {
            //水平
            drawHorizontalBattery(canvas);
        } else if (orientation == 1) {
            //竖直
            drawVerticalBattery(canvas);
        }
    }


    /**
     * 绘制水平电池
     *
     * @param canvas
     */
    private void drawHorizontalBattery(Canvas canvas) {

        Paint paint = new Paint();
        paint.setColor(mColor);

        //描边,绘制外边框
        paint.setStyle(Paint.Style.STROKE);
        float strokeWidth = width / 20.f;
        float stokeWidth_2 = strokeWidth / 2;
        paint.setStrokeWidth(strokeWidth);


        //矩形,左上右下
        RectF r1 = new RectF(stokeWidth_2, stokeWidth_2, width - stokeWidth_2 - strokeWidth, height - stokeWidth_2);
        //设置外边框颜色
        paint.setColor(Color.WHITE);
        canvas.drawRect(r1, paint);

        //画电池内矩形电量
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);
        float offset = (width-strokeWidth*2) * mPower/100.f;
        RectF r2 = new RectF(strokeWidth,strokeWidth,offset,height-strokeWidth);
        //根据电池电量决定电池内矩形电量颜色
        if (mPower < 30) {
            paint.setColor(Color.RED);
        }
        if (mPower >= 30 && mPower < 50) {
            paint.setColor(Color.BLUE);
        }
        if (mPower >= 50) {
            paint.setColor(Color.GREEN);
        }
        canvas.drawRect(r2, paint);
        //画电池头
        RectF r3 = new RectF(width - strokeWidth, height * 0.25f, width, height * 0.75f);
        //设置电池头颜色为黑色
        paint.setColor(Color.WHITE);
        canvas.drawRect(r3, paint);
    }

    /**
     * 绘制垂直电池
     *
     * @param canvas
     */
    private void drawVerticalBattery(Canvas canvas) {

        Paint paint = new Paint();
        paint.setColor(mColor);

        //描边，绘制外边框
        paint.setStyle(Paint.Style.STROKE);
        float strokeWidth = height / 50.0f;
        float strokeWidth2 = strokeWidth / 2;
        paint.setStrokeWidth(strokeWidth);

        //矩形，左上右下
        int headHeight = (int) (strokeWidth + 20.f);
        RectF rect = new RectF(strokeWidth2, headHeight + strokeWidth2, width - strokeWidth2, height - strokeWidth2);
        //设置外边框颜色
        paint.setColor(Color.WHITE);
        canvas.drawRect(rect, paint);

        //画电池内矩形电量
        paint.setStrokeWidth(0);
        paint.setStyle(Paint.Style.FILL);
        float topOffset = (height - headHeight - strokeWidth) * (100 - mPower) / 100.0f;
        RectF rect2 = new RectF(strokeWidth, headHeight + strokeWidth + topOffset, width - strokeWidth, height - strokeWidth);
        //根据电池电量决定电池内矩形电量颜色
        if (mPower < 30) {
            paint.setColor(Color.RED);
        }
        if (mPower >= 30 && mPower < 50) {
            paint.setColor(Color.BLUE);
        }
        if (mPower >= 50) {
            paint.setColor(Color.GREEN);
        }
        canvas.drawRect(rect2, paint);

        //画电池头
        RectF headRect = new RectF(width / 4.0f, 0, width * 0.75f, headHeight);

        paint.setColor(Color.WHITE);
        canvas.drawRect(headRect, paint);
    }

    /**
     * 设置电池电量
     *
     * @param power
     */
    public void setPower(int power) {
        this.mPower = power;
        if (mPower < 0) {
            mPower = 100;
        }
        invalidate();//刷新VIEW
    }

    /**
     * 设置电池颜色
     *
     * @param color
     */
    public void setColor(int color) {
        this.mColor = color;
        invalidate();
    }

    /**
     * 获取电池电量
     *
     * @return
     */
    public int getPower() {
        return mPower;
    }

}



