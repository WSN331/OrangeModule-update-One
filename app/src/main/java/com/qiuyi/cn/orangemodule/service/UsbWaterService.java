package com.qiuyi.cn.orangemodule.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.os.IBinder;
import android.util.Log;

import com.qiuyi.cn.orangemodule.util.MessageUtil;
import com.qiuyi.cn.orangemodule.util.MyApplication;
import com.qiuyi.cn.orangemodule.util.UsbCommunication;

import java.util.Timer;
import java.util.TimerTask;

/*
* 甲醛
* */

public class UsbWaterService extends Service {

    private static final String TAG = UsbWaterService.class.getSimpleName();
    private static final String ACTION = "com.yangjian.testWater.RECEIVER";
    private static final String ACTION_STOP3 = "com.yangjian.waterSTOP.RECEIVER";


    private UsbCommunication communication;
    private UsbDevice usbDevice;

    private Timer timer;

    private byte[] receiveBytes;
    private String initData;

    private String dataWater = null;
    private Intent intent = new Intent(ACTION);

    public UsbWaterService() {
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
        filter.addAction(ACTION_STOP3);
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

                    //将bytes[]转成16进制的字符串输出
                    String mydata = MessageUtil.bytesToHex(receiveBytes);

                    Log.e("water", "这里是数据"+mydata.split("0d0a")[0]+"数据长度"+mydata.split("0d0a")[0].length());

                    String nowdata = mydata.split("0d0a")[0];

                    if (nowdata.startsWith("4546303030333031") && nowdata.length()==24) {
                        dataWater = MessageUtil.getWaterData(mydata);

                        intent.putExtra("water", dataWater);
                    }else{
                        intent.putExtra("water",dataWater);
                    }
                    sendBroadcast(intent);
                } else {
                    Log.e(TAG, "No Data!");
                }
            }
        }, 3000, 1000);
    }




}
