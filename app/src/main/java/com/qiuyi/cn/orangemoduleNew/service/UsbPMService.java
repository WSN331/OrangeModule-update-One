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
* PM2.5 空气检测服务
* */

public class UsbPMService extends Service {

    private static final String TAG = UsbPMService.class.getSimpleName();
    private static final String ACTION = "com.yangjian.testPM.RECEIVER";
    //空气服务停止广播
    private static final String ACTION_STOP2 = "com.yangjian.airSTOP.RECEIVER";

    private UsbCommunication communication;
    private UsbDevice usbDevice;

    private Timer timer;

    private byte[] receiveBytes;
    private String initData;

    private String[] frameList;

    private Intent intent = new Intent(ACTION);

    public UsbPMService() {
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
        filter.addAction(ACTION_STOP2);
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

                    //字节转换成16进制字符串
                    String mydata = MessageUtil.bytesToHex(receiveBytes);

                    Log.e("pppppp","数据:"+mydata.split("0d0a")[0]+"--长度:"+mydata.split("0d0a")[0].length());
                    String nowdata = mydata.split("0d0a")[0];
                    if(nowdata.startsWith("4546303030323033") && nowdata.length() == 28){
                        frameList = MessageUtil.getPMData(nowdata);

                        intent.putExtra("pmvalue", frameList);
                    }else{
                        String[] nullframeList = null;
                        intent.putExtra("pmvalue",nullframeList);
                    }

                    sendBroadcast(intent);



                } else {
                    Log.e(TAG, "No Data!");
                }
            }
        }, 3000, 1000);
    }




}
