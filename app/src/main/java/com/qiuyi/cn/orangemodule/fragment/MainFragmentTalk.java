package com.qiuyi.cn.orangemodule.fragment;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.qiuyi.cn.orangemodule.R;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/3/13.
 * 论坛
 */
public class MainFragmentTalk extends BaseFragment{

    @Override
    public void initView(FrameLayout mAddView) {
        View view = View.inflate(mActivity, R.layout.fragment_mytalk,null);
        ButterKnife.bind(this,view);
        mAddView.addView(view);
    }


    @Override
    public void initData() {

    }
}
