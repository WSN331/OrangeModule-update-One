package com.qiuyi.cn.orangemodule.upansaf.db.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qiuyi.cn.orangemodule.R;

/**
 * Created by Administrator on 2018/4/8.
 * 获取权限的提示界面
 */
public class PermissionDialog extends Dialog implements View.OnClickListener{

    private Context mContext;

    private TextView submitTxt;
    private TextView cancelTxt;
    private TextView titleTxt;

    private OnDialogClick myClick;

    public interface OnDialogClick{
        void onClick(Dialog dialog, boolean confirm);
    }


    public PermissionDialog(Context context,OnDialogClick mListener) {
        super(context);
        this.mContext = context;
        this.myClick = mListener;
    }

    public PermissionDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected PermissionDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saf_dialog_permission);
        setCanceledOnTouchOutside(false);

        initView();
    }

/*    public PermissionDialog setTitle(String title){
        titleTxt.setText(title);
        return this;
    }*/

    //初始化界面
    private void initView() {
        submitTxt = (TextView) findViewById(R.id.submit);
        cancelTxt = (TextView) findViewById(R.id.cancel);
        titleTxt = (TextView) findViewById(R.id.title);
        submitTxt.setOnClickListener(this);
        cancelTxt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                if(myClick!=null){
                    myClick.onClick(this,true);
                }
                this.dismiss();
                break;
            case R.id.cancel:
                myClick.onClick(this,false);
                this.dismiss();
                break;
        }
    }
}
