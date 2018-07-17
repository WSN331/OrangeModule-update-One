package com.qiuyi.cn.orangemoduleNew.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.qiuyi.cn.orangemoduleNew.R;

/**
 * Created by Administrator on 2018/3/27.
 */
public class CircleBar extends View {


    //宽高
    private int width;
    private int height;

    //外圆
    private Paint myOutCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    //文字
    private Paint myTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    //圆环颜色
    private int outColor;
    //文字大小
    private int textSize;
    //起始进度
    private int progress = 90;


    public void setProgress(int progress) {
        if(progress>100){
            progress = 100;
        }
        if(progress<0){
            progress = 0;
        }
        this.progress = progress;
        postInvalidate();
    }


    public CircleBar(Context context) {
        super(context);
    }

    public CircleBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.CircleBar);
        try {
            textSize = ta.getInteger(R.styleable.CircleBar_textSize,30);
            outColor = ta.getColor(R.styleable.CircleBar_outColor,0xf00);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ta.recycle();
    }

    public CircleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getMeasuredWidth();
        height = getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawOutCircle(myOutCirclePaint,canvas);
        drawInText(myTextPaint,canvas);
    }

    //画外圆
    private void drawOutCircle(Paint myOutCirclePaint,Canvas canvas) {
        float paintWidth = width/10.0f;
        myOutCirclePaint.setStyle(Paint.Style.STROKE);
        myOutCirclePaint.setStrokeWidth(paintWidth);
        myOutCirclePaint.setColor(Color.parseColor("#cccccc"));

        canvas.drawCircle(width/2,height/2,(width-2*paintWidth)/2,myOutCirclePaint);


        myOutCirclePaint.setColor(outColor);
        RectF oval = new RectF(paintWidth,paintWidth,width-paintWidth,height-paintWidth);
        float swipeProgess = progress / 100f * 360;

        canvas.drawArc(oval,-90,swipeProgess,false,myOutCirclePaint);
    }

    //画内部文字
    private void drawInText(Paint myTextPaint, Canvas canvas) {

        myTextPaint.setColor(Color.BLACK);
        myTextPaint.setTextSize(textSize);

        canvas.drawText(progress+"%",width/2-myTextPaint.measureText(progress+"%")/2,
                height/2-(myTextPaint.descent()+myTextPaint.ascent())/2,myTextPaint);
    }


}
