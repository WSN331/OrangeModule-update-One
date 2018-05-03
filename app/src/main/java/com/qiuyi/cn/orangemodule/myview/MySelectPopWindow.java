package com.qiuyi.cn.orangemodule.myview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiuyi.cn.orangemodule.R;

/**
 * Created by Administrator on 2018/3/13.
 * popWindwo显示
 */
public class MySelectPopWindow extends PopupWindow implements View.OnClickListener{


    private View view;
    private OnItemClickListener mListener;
    private TextView myNative,myUdisk,myCancel;

    public MySelectPopWindow(Context context, OnItemClickListener mListener){
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.layout_popup_select, null);
        this.mListener = mListener;

        this.setContentView(view);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击

        this.setFocusable(false);
        this.setOutsideTouchable(false);
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));

        myNative = view.findViewById(R.id.tv_native);
        myNative.setOnClickListener(this);
        myUdisk = view.findViewById(R.id.tv_udisk);
        myUdisk.setOnClickListener(this);
        myCancel = view.findViewById(R.id.tv_cancel);
        myCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_native:
                mListener.onItemClick(this, 1);
                break;
            case R.id.tv_udisk:
                mListener.onItemClick(this, 2);
                break;
            case R.id.tv_cancel:
                mListener.onItemClick(this, 3);
                break;
            default:
                break;
        }
    }

    public interface  OnItemClickListener{
        void onItemClick(MySelectPopWindow popupWindow, int position);
    }
}
