package com.qiuyi.cn.orangemoduleNew.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qiuyi.cn.orangemoduleNew.MainActivity;
import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.bean.UserGet;
import com.qiuyi.cn.orangemoduleNew.bean.VerifyGet;
import com.qiuyi.cn.orangemoduleNew.interfaceToutil.MyHttpUtil;
import com.qiuyi.cn.orangemoduleNew.util.CommomDialog;
import com.qiuyi.cn.orangemoduleNew.util.Constant;
import com.qiuyi.cn.orangemoduleNew.util.CustomVideoView;
import com.qiuyi.cn.orangemoduleNew.util.MyApplication;
import com.qiuyi.cn.orangemoduleNew.util.OkHttpUtil;
import com.qiuyi.cn.orangemoduleNew.util.ShareUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by Administrator on 2017/12/26.
 * 登录界面
 */
public class LoginActivity extends Activity{

    //账号
    @BindView(R.id.et_account)
    TextView my_account;
    //验证码
    @BindView(R.id.et_password)
    TextView my_password;
    //获取验证码
    @BindView(R.id.send_verify)
    TextView sendVerify;
    //登录按钮
    @BindView(R.id.bt_login)
    Button login;
    //视频播放对象
    @BindView(R.id.videoView)
    CustomVideoView videoView;

    //手机号
    private String phone;
    //密码
    private String password;

    private OkHttpUtil myhttpUtil = OkHttpUtil.getInstance();
    private String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";

    private boolean runningThree = false;
    private CountDownTimer downTimer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long l) {
            runningThree = true;
            sendVerify.setTextColor(Color.parseColor("#cccccc"));
            sendVerify.setText((l / 1000) + "秒");
        }

        @Override
        public void onFinish() {
            runningThree = false;
            sendVerify.setTextColor(Color.parseColor("#6dc091"));
            sendVerify.setText("重新发送");
        }
    };

    //Activity生命周期回顾
    //1 activity创建
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        initView();
    }

    //2 start
    @Override
    protected void onStart() {
        super.onStart();
    }

    //3 onresume
    @Override
    protected void onResume() {
        super.onResume();
    }

    //初始化界面
    private void initView() {

        //加载视频文件
        videoView.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.video));
        //开始播放
        videoView.start();

        //循环播放,暂时不需要
/*        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();
            }
        });*/

        //登录
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                phone = my_account.getText().toString();
                password = my_password.getText().toString();

                if(checkAP()){
                    //输入格式正确
                    RequestBody formBody = new FormBody.Builder()
                            .add("account",phone)
                            .add("verifyCode",password)
                            .build();
                    myhttpUtil.postAsynHttp(formBody, Constant.URL_LOGIN, new UserGet(), new MyHttpUtil() {
                        @Override
                        public void getResponse(Object response,String myStr) {
                            UserGet userMsg = (UserGet) response;
                            Log.e("myStrMs",myStr);
                            if(userMsg.loginResult == 0){
                                //成功登录
                                ShareUtil.setString(Constant.MYMSG_LOGIN,myStr);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            }
                            if(userMsg.loginResult == 1){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //验证码错误
                                        new CommomDialog(LoginActivity.this, R.style.dialog, "验证码错误", new CommomDialog.OnCloseListener() {
                                            @Override
                                            public void onClick(Dialog dialog, boolean confirm) {
                                                if(confirm){
                                                    dialog.dismiss();
                                                }
                                            }
                                        }).setTitle("提示").show();
                                    }
                                });
                            }
                            if(userMsg.loginResult == 2){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //已经登录
                                        new CommomDialog(LoginActivity.this, R.style.dialog, "用户已登录", new CommomDialog.OnCloseListener() {
                                            @Override
                                            public void onClick(Dialog dialog, boolean confirm) {
                                                if(confirm){
                                                    dialog.dismiss();
                                                }
                                            }
                                        }).setTitle("提示").show();
                                    }
                                });
                            }
                        }
                    });
                }

            }
        });

        //发送验证码
        sendVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!runningThree){
                    //验证码倒计时效果
                    downTimer.start();

                    phone = my_account.getText().toString();
                    if(!phone.isEmpty() && phone.matches(regex)){
                        RequestBody formBody = new FormBody.Builder()
                                .add("phone",phone)
                                .build();
                        myhttpUtil.postAsynHttp(formBody, Constant.URL_VERIFY, new VerifyGet(), new MyHttpUtil() {
                            @Override
                            public void getResponse(Object response,String myStr) {
                                VerifyGet myVerify = (VerifyGet) response;
                                //获取验证码
                                if(myVerify.sendVerifyResult==0){
                                    //成功
                                    Log.e("Verify","获取验证码成功");
                                }else{
                                    //失败
                                    Log.e("Verify","获取验证码失败");
                                }
                            }
                        });
                    }else{
                        Toast.makeText(MyApplication.getContext(),"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    //检查账号验证码
    private boolean checkAP() {
        if(!phone.isEmpty() && !password.isEmpty() && phone.matches(regex)){
            return true;
        }else{
            Toast.makeText(MyApplication.getContext(),"请按正确格式输入相应内容",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // 7 从stop到start的切换状态
    @Override
    protected void onRestart() {
        super.onRestart();
        initView();
    }


    //4 activity onPause暂停与onResume对应
    @Override
    protected void onPause() {
        super.onPause();
    }

    //5 activity停止
    @Override
    protected void onStop() {
        super.onStop();
        videoView.stopPlayback();
    }

    //6 activity销毁
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
