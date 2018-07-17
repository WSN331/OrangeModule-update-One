package com.qiuyi.cn.orangemoduleNew.fragment;

import android.view.View;

import com.qiuyi.cn.orangemoduleNew.R;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/3/13.
 * 论坛
 */
public class MainFragmentTalk extends BaseFragment{


    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_mytalk,null);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void initData() {

    }
}
