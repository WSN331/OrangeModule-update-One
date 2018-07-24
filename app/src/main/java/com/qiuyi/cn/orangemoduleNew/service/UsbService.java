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
* 框架检测模块
*
* */

public class UsbService extends Service {

    private static final String TAG = UsbService.class.getSimpleName();
    private static final String ACTION = "com.yangjian.testframework.RECEIVER";
    private static final String ACTION_STOP = "com.yangjian.frameworkSTOP.RECEIVER";

    private UsbCommunication communication;
    private UsbDevice usbDevice;

    private Timer timer;
    private byte[] receiveBytes;
    private String initData;
    private String[] frameList;
    private Intent intent = new Intent(ACTION);

    public UsbService() {
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
        filter.addAction(ACTION_STOP);
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

                Log.e("framework",receiveBytes+"");
                if (receiveBytes != null) {
                    //这里执行与Activity的交互操作
                    initData = MessageUtil.byte2String(receiveBytes);

                    //Log.e("framework", "字节："+receiveBytes.toString()+"");
                   //Log.e("framework", "字节->16进制字符串: "+MessageUtil.bytesToHex(receiveBytes).split("\r\n")[0]+" 长度:"+MessageUtil.bytesToHex(receiveBytes).split("\r\n")[0].length());
                    Log.e("framework", "字节->字符串"+initData.split("\r\n")[0]+" 长度:"+initData.split("\r\n")[0].length());

                    if (initData.startsWith("EF") && initData.split("\r\n")[0].length()==21) {
                        frameList = null;

                        frameList = MessageUtil.formatFrameData(initData);

                        intent.putExtra("frame", frameList);
                    }else{

                    }
                    sendBroadcast(intent);
                } else {
                    Log.e(TAG, "No Data!");
                }
            }
        }, 3000, 1000);
    }





}
