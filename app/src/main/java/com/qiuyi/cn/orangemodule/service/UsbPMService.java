package com.qiuyi.cn.orangemodule.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.IBinder;
import android.util.Log;

import com.qiuyi.cn.orangemodule.util.MessageUtil;
import com.qiuyi.cn.orangemodule.util.MyApplication;
import com.qiuyi.cn.orangemodule.util.UsbCommunication;

import java.util.Timer;
import java.util.TimerTask;

/*
* PM2.5 空气检测服务
* */

public class UsbPMService extends Service {

    private static final String TAG = UsbPMService.class.getSimpleName();
    private static final String ACTION = "com.yangjian.testPM.RECEIVER";

    private UsbCommunication communication;
    private UsbDevice usbDevice;

    private Timer timer;

    private byte[] receiveBytes;
    private String initData;

    private String[] frameList;
    private Intent intent = new Intent(ACTION);

    public UsbPMService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate() executed");
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
                    initData = MessageUtil.byte2String(receiveBytes);

                    Log.e(TAG, initData.split("/r/n")[0]);

                    Log.e("pmpmpm", "这里是数据"+initData.split("/r/n")[0]);

                    if (initData.startsWith("EF")) {
                        frameList = MessageUtil.getPMData(initData);

                        intent.putExtra("pm", frameList);

                        sendBroadcast(intent);
                    }


                } else {
                    Log.e(TAG, "No Data!");
                }
            }
        }, 3000, 200);
    }




}
