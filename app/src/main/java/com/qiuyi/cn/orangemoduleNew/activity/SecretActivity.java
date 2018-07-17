package com.qiuyi.cn.orangemoduleNew.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.myview.Password;

/**
 * Created by Administrator on 2018/3/19.
 * 私密模块
 */
public class SecretActivity extends Activity implements Password.OnFinishListener{

    private Password password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret);

        initView();

    }

    //初始化View
    private void initView() {
        password = findViewById(R.id.password_view);
        password.setOnFinishListener(this);
    }

    @Override
    public void setOnPasswordFinished() {
        if(password.getPasswordLength() == password.getOriginText().length()){
            Log.e("mima", "密码"+password.getOriginText());
            if(password.getOriginText().equals("123456")){
                Intent intent = new Intent(this,SecretContentActivity.class);
                startActivity(intent);
                finish();
            }else{

                new AlertDialog.Builder(this)
                        .setTitle("登录")
                        .setMessage("密码错误")
                        .setCancelable(false)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .create().show();
            }
        }
    }
}
