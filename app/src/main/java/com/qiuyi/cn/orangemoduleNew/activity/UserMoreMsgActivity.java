package com.qiuyi.cn.orangemoduleNew.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.qiuyi.cn.orangemoduleNew.MainActivity;
import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.bean.LogOut;
import com.qiuyi.cn.orangemoduleNew.bean.UserGet;
import com.qiuyi.cn.orangemoduleNew.interfaceToutil.MyHttpUtil;
import com.qiuyi.cn.orangemoduleNew.util.CommomUtil;
import com.qiuyi.cn.orangemoduleNew.util.Constant;
import com.qiuyi.cn.orangemoduleNew.util.OkHttpUtil;
import com.qiuyi.cn.orangemoduleNew.util.ShareUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2018/2/1.
 * 个人详情页
 */
public class UserMoreMsgActivity extends Activity{

    @BindView(R.id.UserMore_bt_out)
    Button bt_out;

    //得到登录的用户信息
    private UserGet myUserInfo = null;
    private OkHttpUtil okHttpUtil = OkHttpUtil.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usermoremsg);
        ButterKnife.bind(this);

        myUserInfo = CommomUtil.getUserInfo();


        bt_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestBody formbody = new FormBody.Builder()
                        .add("userId",myUserInfo.user.id+"")
                        .add("accessToken",myUserInfo.user.accessToken)
                        .build();
                okHttpUtil.postAsynHttp(formbody, Constant.URL_LOGINOUT, new LogOut(), new MyHttpUtil() {
                    @Override
                    public void getResponse(Object response, String str) {
                        LogOut out = (LogOut) response;
                        if(out.logoutResult==0){
                            //注销成功
                            ShareUtil.removeMsg(Constant.MYMSG_LOGIN);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }else{
                            //注销失败
                        }
                    }
                });

            }
        });
    }


}
