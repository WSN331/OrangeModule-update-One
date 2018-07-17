package com.qiuyi.cn.orangemoduleNew.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.activity.LoginActivity;
import com.qiuyi.cn.orangemoduleNew.activity.UserMoreMsgActivity;
import com.qiuyi.cn.orangemoduleNew.bean.UserGet;
import com.qiuyi.cn.orangemoduleNew.util.CommomUtil;

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
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_mymessage,null);
        ButterKnife.bind(this,view);
        return view;
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
