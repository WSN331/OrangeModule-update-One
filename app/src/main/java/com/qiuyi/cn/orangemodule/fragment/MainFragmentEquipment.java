package com.qiuyi.cn.orangemodule.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.pager.DevicePager;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/12/26.
 * 第一个设备模块
 * 这里处理的是第一个模块的逻辑
 */
public class MainFragmentEquipment extends BaseFragment{

    private static final String ACTION = "com.yangjian.testframework.RECEIVER";

    //frameLayout放4块板子
    @BindView(R.id.equipment_content)
    FrameLayout content;
    //框架背景
    @BindView(R.id.frame)
    LinearLayout frame;
    //状态
    @BindView(R.id.tv_state)
    TextView tv_state;
    //电量
    @BindView(R.id.tv_electricity)
    TextView tv_electricity;
    //电压
    @BindView(R.id.tv_voltage)
    TextView tv_voltage;

    //广播接收
    private BroadcastReceiver messageReceiver;
    private IntentFilter intentFilter;

    //初始化设备模块的界面
    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_equipment,null);
        ButterKnife.bind(this,view);
        return view;
    }

    //将模块化功能界面添加到现在的界面中
    @Override
    public void initData() {
        //将各个模块加入到FrameLayout布局中
        DevicePager devicePager = new DevicePager(mActivity);

        content.addView(devicePager.mRootView);
        //这里处理各个各个模块的逻辑
        devicePager.initData();

        //广播接收数据
        initBroadcast();
    }

    //注册广播
    private void initBroadcast() {
        //注册广播
        messageReceiver = new MessageReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);

        //接收数据返回广播
        mActivity.registerReceiver(messageReceiver,intentFilter);
    }


    //接受数据
    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String[] frameData = intent.getStringArrayExtra("frame");

            if (frameData != null && frameData.length>=3) {
                frame.setBackgroundResource(R.drawable.framework2);
                //电量
                String text1 = frameData[1] + "%";;
                //电压
                String text2 = frameData[0] + "mv";
                //状态
                String text3 = frameData[2];
                Log.i("aaaaa","状态："+text3+"电量："+text1+"电压"+text2);

                if(text3.equals("1"))
                {
                    tv_state.setText("框架状态:已连接");
                }
                tv_electricity.setText("框架剩余电量:"+text1);
                tv_voltage.setText("框架电压:"+text2);
            }/*else{
                frame.setBackgroundResource(R.drawable.framework);
                tv_state.setText("框架状态:未连接");
                tv_electricity.setText("框架剩余电量:0%");
                tv_voltage.setText("框架电压:0mv");
            }*/
        }
    }
}
