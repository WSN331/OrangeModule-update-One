package com.qiuyi.cn.orangemodule.upan.myView;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.qiuyi.cn.orangemodule.R;


/**
 * Created by Administrator on 2018/2/3.
 * mydialog
 */
public class MyDialog extends Dialog implements View.OnClickListener{

    private Activity mContext;
    private LinearLayout mView;
    private OnItemClickListener mListener;
    private int[] myDrawable = new int[]{R.drawable.six};
    private Bitmap[] myBiyMap = new Bitmap[5];



    public interface OnItemClickListener{
        void onClick(Dialog dialog, int position);
    }

    public MyDialog(Activity context) {
        super(context);
        this.mContext = context;
    }

    public MyDialog(Activity context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    protected MyDialog(Activity context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    public MyDialog(Activity context, int themeResId, OnItemClickListener mListener) {
        super(context, themeResId);
        this.mContext = context;
        this.mListener = mListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upan_select_pop);
        setCanceledOnTouchOutside(false);
        initBitmap();

        initView();
        getWindow().setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画

        //全屏处理
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        WindowManager wm = mContext.getWindowManager();

        lp.width = wm.getDefaultDisplay().getWidth(); //设置宽度
        getWindow().setAttributes(lp);
    }

    //初始化Bitmap
    private void initBitmap() {
/*        for(int i = 0; i < myBiyMap.length;i++){
            Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),myDrawable[i]);
            Bitmap newImg = BlurUtil.doBlur(bmp,1,5);
            myBiyMap[i] = newImg;
        }*/
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(),myDrawable[-0]);
        Bitmap newImg = BlurUtil.doBlur(bmp,3,5);
    }


    private void initView() {
        mView = findViewById(R.id.myBackground);
        findViewById(R.id.select_read).setOnClickListener(this);
        findViewById(R.id.select_write).setOnClickListener(this);
        initBackground();
    }

    //设置毛玻璃背景
    private void initBackground() {
/*        mView.setBackground(new BitmapDrawable(myBiyMap[0]));

        Timer time =new Timer();
        time.schedule(new TimerTask() {
            @Override
            public void run() {
                final Bitmap newImg = myBiyMap[new Random().nextInt(5)];

                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mView.setBackground(new BitmapDrawable(newImg));
                    }
                });
            }
        },3000,3000);*/
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.select_read:
                mListener.onClick(this,1);
                break;
            case R.id.select_write:
                mListener.onClick(this,2);
                break;
            default:
                break;
        }
    }
}
