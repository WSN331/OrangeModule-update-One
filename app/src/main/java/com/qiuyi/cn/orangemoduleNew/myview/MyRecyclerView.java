package com.qiuyi.cn.orangemoduleNew.myview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2018/3/19.
 */
public class MyRecyclerView extends RecyclerView{

    public Context context;

    public MyRecyclerView(Context context) {
        super(context);
        this.context = context;
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


/*    private float posY,nowY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean istop = canScrollVertically(-1);
        posY = ev.getY();


        switch(ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //父容器禁止拦截
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                nowY = ev.getY();
                float distance = nowY - posY;

                if(!istop && distance>0) {
                    //交给父容器拦截
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }*/
}
