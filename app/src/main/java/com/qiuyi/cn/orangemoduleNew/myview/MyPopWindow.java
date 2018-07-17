package com.qiuyi.cn.orangemoduleNew.myview;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiuyi.cn.orangemoduleNew.R;

/**
 * Created by Administrator on 2018/3/13.
 * popWindwo显示
 */
public class MyPopWindow extends PopupWindow implements View.OnClickListener{


    private View view;
    private OnItemClickListener mListener;
    private TextView mySim,myFile,myOther;

    public MyPopWindow(Context context, OnItemClickListener mListener){
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.layout_popup_menu, null);
        this.mListener = mListener;

        this.setContentView(view);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击

        this.setFocusable(false);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));

        mySim = view.findViewById(R.id.tv_sim);
        mySim.setOnClickListener(this);
        myFile = view.findViewById(R.id.tv_file);
        myFile.setOnClickListener(this);
        myOther = view.findViewById(R.id.tv_other);
        myOther.setOnClickListener(this);
        view.findViewById(R.id.Img_no).setOnClickListener(this);
    }


    public MyPopWindow setText(String listTitle){
        myFile.setText(listTitle);
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_sim:
                mListener.onItemClick(this, 1);
                break;
            case R.id.tv_file:
                mListener.onItemClick(this, 2);
                break;
            case R.id.tv_other:
                mListener.onItemClick(this, 3);
                break;
            case R.id.Img_no:
                mListener.onItemClick(this, 4);
                break;
        }
    }

    public interface  OnItemClickListener{
        void onItemClick(MyPopWindow popupWindow, int position);
    }



}
