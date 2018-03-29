package com.qiuyi.cn.orangemodule.myview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2018/3/15.
 * 本地界面的隐私模块
 * 显示模块
 * 下拉刷新模块
 */
public class MyViewGroup extends ViewGroup{


    //隐私模块
    private View mSecret;
    //下拉刷新
    private View mRefresh;
    //正常显示模块
    private LinearLayout mShowBlock;

    //滑动模块的RecyclerView
    private View view;
    //是否可以下拉
    private boolean istop = false;

    public boolean istop() {
        return istop;
    }

    public void setIstop(boolean istop) {
        this.istop = istop;
    }

    //屏幕高度
    public int mScreenHeight;
    //屏幕宽度
    public int mScreenWidth;
    //隐私模块高度
    public int mSecretHeight;
    //下拉刷新模块高度
    public int mRefreshHeight;

/*    private MyScrollerListener myScrollerListener;

    private FixHeaderListener myHeaderListener;

    public void setFixHeaderListener(FixHeaderListener myHeaderListener){
        this.myHeaderListener = myHeaderListener;
    }

    public void setMyScrollerListener(MyScrollerListener myScrollerListener){
        this.myScrollerListener = myScrollerListener;
    }*/

    public MyViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 测量ChildView的宽度和高度，然后根据ChildView的计算结果，设置ViewGroup自己的宽高
     *
     * @param widthMeasureSpec 水平空间规格说明
     * @param heightMeasureSpec 竖直空间规格说明
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureChildren(widthMeasureSpec,heightMeasureSpec);

        //屏幕宽度
        mScreenWidth = MeasureSpec.getSize(widthMeasureSpec);

        //屏幕高度
        mScreenHeight = MeasureSpec.getSize(heightMeasureSpec);

    }

    @Override
    protected void onLayout(boolean change, int l, int t, int r, int b) {

        //隐私模块
        mSecret = getChildAt(0);
        mSecretHeight = mSecret.getMeasuredHeight();

        //刷新模块
        mRefresh = getChildAt(1);
        mRefreshHeight = mRefresh.getMeasuredHeight();

        //显示模块
        mShowBlock = (LinearLayout) getChildAt(2);
        mShowBlock.getChildAt(0);


        view = mShowBlock.getChildAt(0);


        //隐私模块
        mSecret.layout(0,-(mRefreshHeight+mSecretHeight),mScreenWidth,0);
        //刷新模块
        mRefresh.layout(0,-mRefreshHeight,mScreenWidth,0);
        //显示模块
        mShowBlock.layout(0,1,mScreenWidth,mScreenHeight);
    }



/*    private float mPosY,mCurPosY,mPosX,mCurPosX;
    private int count = 1;

    //滑动监听
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mPosY = event.getY();
                mPosX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                mCurPosY = event.getY();
                mCurPosX = event.getX();

                float distanceX = mCurPosX - mPosX;
                float distanceY = mCurPosY - mPosY;

                Log.e("viewgroup", "distance"+distanceY);
                if(Math.abs(distanceY)>Math.abs(distanceX)){
                    //上下滑动
                    if(distanceY>0){
                        //下滑动
                        scrollTo(0, (int) (mPosY-mCurPosY));
                        myScrollerListener.setLockDisplay(mCurPosY-mPosY,mRefreshHeight,mSecretHeight);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                float distance = mCurPosY-mPosY;
                //下拉距离，使用回调
                myScrollerListener.setViewDispaly(distance,mRefreshHeight,mSecretHeight);
                break;
            default:
                break;
        }
        return true;
    }*/


/*    //拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN){
            return false;
        }else{
            return true;
        }
    }

    public interface MyScrollerListener{
        //分别返回的是，下拉距离，刷新栏高度，私密空间高度
        void setViewDispaly(float distance,int refreshHeight,int secretHeight);

        //时刻变化的拉动距离
        void setLockDisplay(float distance,int refreshHeight,int secretHeight);
    }

    public interface FixHeaderListener{
        boolean isTop();
    }*/


}
