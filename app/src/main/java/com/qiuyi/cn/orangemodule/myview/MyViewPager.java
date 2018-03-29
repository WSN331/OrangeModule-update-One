package com.qiuyi.cn.orangemodule.myview;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017/12/27.
 *
 * //禁用滑动效果的ViewPager
 */
public class MyViewPager extends ViewPager{

    public MyViewPager(Context context) {
        super(context);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //滑动效果禁用
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //true表示拦截，false表示不拦截,传给子控件，新闻页ViewPager，不拦截标签页
        return false;
    }
}
