package com.qiuyi.cn.orangemoduleNew.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.os.IBinder;
import android.util.Log;

import com.qiuyi.cn.orangemoduleNew.util.MessageUtil;
import com.qiuyi.cn.orangemoduleNew.util.MyApplication;
import com.qiuyi.cn.orangemoduleNew.util.UsbCommunication;

import java.util.Timer;
import java.util.TimerTask;

/*
* 甲醛
* */

public class UsbJQService extends Service {

    private static final String TAG = UsbJQService.class.getSimpleName();
    private static final String ACTION = "com.yangjian.testJQ.RECEIVER";
    //甲醛服务停止广播
    private static final String ACTION_STOP4 = "com.yangjian.jqSTOP.RECEIVER";

    private UsbCommunication communication;
    private UsbDevice usbDevice;

    private Timer timer;

    private byte[] receiveBytes;
    private String initData;

    private String dateJQ;
    private Intent intent = new Intent(ACTION);

    public UsbJQService() {
    }

    private BroadcastReceiver Stopframe = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            timer.cancel();
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate() executed");

        //注册接收广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_STOP4);
        registerReceiver(Stopframe,filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 这里执行后台任务
        usbDevice = intent.getParcelableExtra("usbDevice");

        communication = new UsbCommunication(MyApplication.getContext(),usbDevice);

        usbTask();
        Log.e(TAG, "onStartCommand() executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void usbTask() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                receiveBytes = communication.receiveMessage();

                if (receiveBytes != null) {
                    //这里执行与Activity的交互操作

                    //先转成字符串
                    initData = MessageUtil.byte2String(receiveBytes);
                    //将bytes[]字符串转成16进制的字符串输出
                    String mydata = MessageUtil.bytesToHexString(initData);

                    Log.e("jqjqjq", "这里是数据"+mydata+"数据长度"+initData.split("\r\n")[0].length());
                    if (mydata.startsWith("4546303030343031") && initData.split("\r\n")[0].length()==10) {
                        dateJQ = MessageUtil.getJQData(mydata);
                        intent.putExtra("jq", dateJQ);

                    }else{
                        intent.putExtra("jq","123");
                    }
                    sendBroadcast(intent);
                } else {
                    Log.e(TAG, "No Data!");
                }
            }
        }, 3000, 1000);
    }




}
