package com.qiuyi.cn.orangemodule.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.activity.LoginActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/12/26.
 * 我的信息页面
 */
public class MainFragmentMyMessage extends BaseFragment{

    @BindView(R.id.login)
    LinearLayout myLogin;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_mymessage,null);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void initData() {
        myLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mActivity, LoginActivity.class));
            }
        });
    }
}
