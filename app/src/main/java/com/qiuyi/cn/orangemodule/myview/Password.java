package com.qiuyi.cn.orangemodule.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.EditText;

import com.qiuyi.cn.orangemodule.R;

/**
 * Created by Administrator on 2018/3/20.
 */
public class Password extends EditText{

    /*
    * 方框
    * */
    private int borderColor;
    private float borderWidth;
    private float borderRadius;

    /*
    * 密码样式
    * */
    private Integer passwordLength;
    private int passwordColor;
    private float passwordWidth;
    private float passwordRadius;

    private Paint passwordPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private OnFinishListener onFinishListener;

    //默认上面的margin
    private int defaultContentMargin = 6;
    //默认的笔宽
    private int defaultSplitLineWidth = 3; //px

    //输入的文本长度
    private int textLength;
    //原始文本
    private String originText;

    public Password(Context context) {
        super(context);
    }

    public Password(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.MyEditTextPassword);

        try {
            /*
            * ta.getDimension()
            * ta.getDimensionPixelOffset()
            * ta.getDimensionPixelSize()
            * 我的模拟器是480*800的，屏幕密度是1.5，
            * 从打印结果就可以推知，getDimension()、getDimensionPixelSize()和getDimenPixelOffset()的结果值
            * 都是将资源文件中定义的dip值乘以屏幕密度,即171*1.5=256.5，
            * 只是getDimension()返回的是float，
            * 其余两个返回的是int, 其中getDimensionPixelSize()返回的是实际数值的四舍五入,
            * 而getDimensionPixelOffset返回的是实际数值去掉后面的小数点;
            * 再跟踪代码查看这三个函数的具体实现，可以了解得更具体。
            * 总然而之，这三个函数返回的都是dip值乘以屏幕密度,如果你在资源文件中定义的长度单位不是dip，
            * 而是px的话，程序会直接抛出异常。
            * */
            passwordLength = ta.getInteger(R.styleable.MyEditTextPassword_passwordLength,6);

            passwordColor = ta.getColor(R.styleable.MyEditTextPassword_passwordColor,0xf00);
            passwordWidth = ta.getDimensionPixelSize(R.styleable.MyEditTextPassword_passwordWidth,12);//px
            passwordRadius = ta.getDimensionPixelSize(R.styleable.MyEditTextPassword_passwordRadius,6);

            borderColor = ta.getColor(R.styleable.MyEditTextPassword_borderColor,0x000000);
            borderWidth = ta.getDimensionPixelSize(R.styleable.MyEditTextPassword_borderWidth,8);//px
            borderRadius = ta.getDimensionPixelSize(R.styleable.MyEditTextPassword_borderRadius,6);

        } catch (Exception e) {
            e.printStackTrace();
        }
        ta.recycle();

        borderPaint.setStyle(Paint.Style.FILL);
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderWidth);

        passwordPaint.setColor(passwordColor);
        passwordPaint.setStrokeWidth(passwordWidth);

    }

    public Password(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        //画边框
        RectF r1 = new RectF(0,0,width,height);
        borderPaint.setColor(borderColor);
        canvas.drawRoundRect(r1,borderRadius,borderRadius,borderPaint);

        //画密码内容区域，遮挡密码
        RectF rectContent = new RectF(r1.left+defaultContentMargin,r1.top+defaultContentMargin,r1.right-defaultContentMargin,r1.bottom-defaultContentMargin);
        borderPaint.setColor(Color.WHITE);
        canvas.drawRoundRect(rectContent,borderRadius,borderRadius,borderPaint);

        //画分割线
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(defaultSplitLineWidth);
        for(int i=1;i<passwordLength;i++){
            float x = width * i/passwordLength;
            canvas.drawLine(x,0,x,height,borderPaint);
        }

        //画密码
        float px = height/2;
        float py = height/2;
        float halfWidth = width / passwordLength /2;
        for(int i=0;i<textLength;i++){
            px = i*width/passwordLength+halfWidth;
            canvas.drawCircle(px,py,passwordWidth,passwordPaint);
        }
    }

    //输入
    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        textLength = text.length();
        //UI层刷新
        invalidate();
        onInputFinished(text);
    }


    private void onInputFinished(CharSequence text) {
        if(text!=null){
            //得到原始明文密码
            originText = text.toString();
            if(onFinishListener!=null){
                onFinishListener.setOnPasswordFinished();
            }
        }
    }

    public void setOnFinishListener(OnFinishListener onFinishListener){
        this.onFinishListener = onFinishListener;
    }


    public String getOriginText() {
        return originText;
    }

    public void setOriginText(String originText) {
        this.originText = originText;
    }

    public int getTextLength() {
        return textLength;
    }

    public void setTextLength(int textLength) {
        this.textLength = textLength;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public float getBorderRadius() {
        return borderRadius;
    }

    public void setBorderRadius(float borderRadius) {
        this.borderRadius = borderRadius;
    }

    public int getPasswordLength() {
        return passwordLength;
    }

    public void setPasswordLength(int passwordLength) {
        this.passwordLength = passwordLength;
    }

    public int getPasswordColor() {
        return passwordColor;
    }

    public void setPasswordColor(int passwordColor) {
        this.passwordColor = passwordColor;
    }

    public float getPasswordWidth() {
        return passwordWidth;
    }

    public void setPasswordWidth(float passwordWidth) {
        this.passwordWidth = passwordWidth;
    }

    public interface OnFinishListener{
        void setOnPasswordFinished();
    }

}
