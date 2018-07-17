package com.qiuyi.cn.orangemoduleNew.myview;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiuyi.cn.orangemoduleNew.R;


/**
 * Created by Administrator on 2016/10/24.
 */

public class MoreOperatePopWindow extends PopupWindow implements View.OnClickListener{

    private View view;
    private OnItemClickListener mListener;

    private int popupWidth;
    private int popupHeight;

    private TextView tv_share,tv_collection,tv_secret,tv_rename,tv_detail;
    private TextView[] myText = new TextView[5];

    public MoreOperatePopWindow(Activity context, OnItemClickListener mListener){
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.layout_popup_more_operate, null);
        this.mListener = mListener;

        this.setContentView(view);

        this.setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击

        this.setFocusable(false);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));

        //获取自己的长宽高
        view.measure(View.MeasureSpec.UNSPECIFIED,View.MeasureSpec.UNSPECIFIED);
        popupHeight = view.getMeasuredHeight();
        popupWidth = view.getMeasuredWidth();


        tv_share = view.findViewById(R.id.tv_share);
        tv_collection = view.findViewById(R.id.tv_collection);
        tv_secret = view.findViewById(R.id.tv_secret);
        tv_rename = view.findViewById(R.id.tv_rename);
        tv_detail = view.findViewById(R.id.tv_detail);
        myText[0] = tv_share;
        myText[1] = tv_collection;
        myText[2] = tv_secret;
        myText[3] = tv_rename;
        myText[4] = tv_detail;


        view.findViewById(R.id.lay_share).setOnClickListener(this);
        view.findViewById(R.id.lay_collection).setOnClickListener(this);
        view.findViewById(R.id.lay_secret).setOnClickListener(this);
        view.findViewById(R.id.lay_rename).setOnClickListener(this);
        view.findViewById(R.id.lay_detail).setOnClickListener(this);
    }


    /**
     * 设置显示在v上方(以v的左边距为开始位置)
     * @param v
     */
    public void showUp(View v) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //在控件上方显示
        showAtLocation(v, Gravity.NO_GRAVITY, (location[0]) - popupWidth / 2, location[1] - popupHeight);
    }

    /**
     * 设置显示在v上方（以v的中心位置为开始位置）
     * @param v
     */
    public void showUp2(View v) {
        //获取需要在其上方显示的控件的位置信息
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        //在控件上方显示
        showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1] - popupHeight);
    }

    public MoreOperatePopWindow setTitle(int[] locations){
        for(int i=0;i<locations.length;i++){
            if(locations[i]==1){
                //为1不显示
                myText[i].setTextColor(Color.parseColor("#FF0000"));
            }
            if(locations[i]==2){
                //为2表示这个文件已经收藏
                myText[i].setText("取消收藏");
            }
        }
        return this;
    }


    @Override
    public void onClick(View v) {
         switch (v.getId()){
             case R.id.lay_share:
                 mListener.onItemClick(this, 1);
                 break;
             case R.id.lay_collection:
                 mListener.onItemClick(this, 2);
                 break;
             case R.id.lay_secret:
                 mListener.onItemClick(this, 3);
                 break;
             case R.id.lay_rename:
                 mListener.onItemClick(this, 4);
                 break;
             case R.id.lay_detail:
                 mListener.onItemClick(this, 5);
                 break;
         }
    }


    public interface  OnItemClickListener{
        void onItemClick(MoreOperatePopWindow popupWindow, int position);
    }

}
