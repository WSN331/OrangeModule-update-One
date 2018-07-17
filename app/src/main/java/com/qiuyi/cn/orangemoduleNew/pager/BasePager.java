package com.qiuyi.cn.orangemoduleNew.pager;

import android.app.Activity;
import android.view.View;

/**
 * Created by Administrator on 2017/12/27.
 * 一些Pager的基础页面
 */
public abstract class BasePager {
    public Activity mActivity;
    public View mRootView;

    public BasePager(Activity mActivity){
        this.mActivity = mActivity;
        this.mRootView = initView();
    }

    //初始化界面
    public abstract View initView();

    //初始化数据
    public void initData(){};
}
