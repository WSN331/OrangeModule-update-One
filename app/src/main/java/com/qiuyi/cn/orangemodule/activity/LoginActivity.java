package com.qiuyi.cn.orangemodule.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.util.CustomVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/12/26.
 */
public class LoginActivity extends Activity{

    @BindView(R.id.bt_login)
    Button login;
    //视频播放对象
    @BindView(R.id.videoView)
    CustomVideoView videoView;

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
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
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
