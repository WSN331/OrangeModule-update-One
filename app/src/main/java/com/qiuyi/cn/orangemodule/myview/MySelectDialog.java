package com.qiuyi.cn.orangemodule.myview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qiuyi.cn.orangemodule.R;

import org.w3c.dom.Text;


public class MySelectDialog extends Dialog implements View.OnClickListener{


    private Context mContext;
    private OnCloseListener listener;
    private TextView tv_native,tv_udisk,tv_cancel;

    public MySelectDialog(Context context, int themeResId, OnCloseListener listener) {
        //这里就可以直接定义样式
        super(context, themeResId);
        this.mContext = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_popup_select);
        setCanceledOnTouchOutside(false);

        initView();
    }

    private void initView(){
        tv_native = findViewById(R.id.tv_native);
        tv_udisk = findViewById(R.id.tv_udisk);
        tv_cancel = findViewById(R.id.tv_cancel);

        tv_native.setOnClickListener(this);
        tv_udisk.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_native:
                if(listener != null){
                    listener.onClick(this,1);
                }
                this.dismiss();
                break;
            case R.id.tv_udisk:
                if(listener !=null){
                    listener.onClick(this,2);
                }
                this.dismiss();
                break;
            case R.id.tv_cancel:
                this.dismiss();
                break;
        }
    }

    public interface OnCloseListener{
        void onClick(Dialog dialog, Integer flag);
    }
}
