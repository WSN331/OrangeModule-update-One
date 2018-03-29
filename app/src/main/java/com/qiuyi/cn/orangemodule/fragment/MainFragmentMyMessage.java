package com.qiuyi.cn.orangemodule.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.activity.LoginActivity;
import com.qiuyi.cn.orangemodule.activity.UserMoreMsgActivity;
import com.qiuyi.cn.orangemodule.bean.UserGet;
import com.qiuyi.cn.orangemodule.util.CommomUtil;
import com.qiuyi.cn.orangemodule.util.Constant;
import com.qiuyi.cn.orangemodule.util.ShareUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/12/26.
 * 我的信息页面
 */
public class MainFragmentMyMessage extends BaseFragment{

    @BindView(R.id.login)
    LinearLayout myLogin;
    @BindView(R.id.MyMsg_user_avatar)
    de.hdodenhof.circleimageview.CircleImageView headImg;
    @BindView(R.id.MyMsg_tv_myName)
    TextView myName;

    //得到当前登录的用户
    private UserGet nowUser = null;

    @Override
    public void initView(FrameLayout addView) {
        View view = View.inflate(mActivity, R.layout.fragment_mymessage,null);
        ButterKnife.bind(this,view);
        addView.addView(view);
    }

    @Override
    public void initData() {

        nowUser = CommomUtil.getUserInfo();
        if(nowUser!=null){
            myName.setText(nowUser.user.account);
        }
        myLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nowUser!=null){
                    startActivity(new Intent(mActivity,UserMoreMsgActivity.class));
                }else{
                    startActivity(new Intent(mActivity, LoginActivity.class));
                }
            }
        });

    }


}
