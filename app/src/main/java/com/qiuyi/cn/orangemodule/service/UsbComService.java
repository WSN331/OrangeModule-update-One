package com.qiuyi.cn.orangemodule.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.qiuyi.cn.orangemodule.util.MyApplication;
import com.qiuyi.cn.orangemodule.util.UsbCommunication;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2018/1/6.
 *
 * 检测USB模块插入
 *
 * startService: onCreate->onStartCommand->onStart->onDestroy
 * bindService: onCreate->onBind->onUnbind->onDestroy
 */
public class UsbComService extends Service{

    private static final String TAG = UsbComService.class.getSimpleName();
    //接收的广播
    private static final String ACTION_USB_PERMISSION = "com.yangjian.temperaturemodule.USB_PERMISSION";
    //发送广播

    //U盘广播
    private static final String ACTION_USB_PAN = "com.yangjian.testUSBpan.RECEIVER";
    //框架广播
    private static final String ACTION_FRAMEWORK = "com.yangjian.testframework.RECEIVER";
    //框架服务停止广播
    private static final String ACTION_STOP1 = "com.yangjian.frameworkSTOP.RECEIVER";
    //空气服务停止广播
    private static final String ACTION_STOP2 = "com.yangjian.airSTOP.RECEIVER";
    //水质服务停止广播
    private static final String ACTION_STOP3 = "com.yangjian.waterSTOP.RECEIVER";
    //甲醛服务停止广播
    private static final String ACTION_STOP4 = "com.yangjian.jqSTOP.RECEIVER";

    //线程运行
    private boolean serviceRunning = false;

    //设备列表
    private HashMap<String,UsbDevice> devicelist;
    //usb管理类
    private UsbManager usbManager;
    //Context
    private Context context;
    //存储已经插入的device
    private List<UsbDevice> usbDeviceList = new ArrayList<>();

    //初始化权限
    private PendingIntent pendingIntent;

    //服务创建的时候执行
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate() executed------start");

        serviceRunning = true;
        //只运行一次，不停的更新数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                //统计运行次数
                int n = 0;
                while (serviceRunning){
                    n++;
/*                    synchronized (usbDeviceList){*/
                        if(context!=null){
                            usbDetection();
                        }
                        if(usbDeviceCallback!=null && n>=3){

                            usbDeviceCallback.dataChanged(usbDeviceList);
                            serviceRunning = false;
                        }
/*                    }*/
                    try{
                        Thread.sleep(2000);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    //执行服务
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //检测是否有usb设备插入
        usbDetection();
        return super.onStartCommand(intent, flags, startId);
    }

    //usb插入检测
    private void usbDetection() {
        //初始化设备列表并返回
        initUsbDevice(context);
    }

    //申请权限的广播
    public BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, intent.getAction());

            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {

                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                Log.e("granted", granted + "");

                if(granted){
                    initUsbDevice(context);
                }else{
                    Log.e("asas","没有权限");
                }
            }
        }
    };


    //初始化设备列表并返回
    private void initUsbDevice(Context context) {
        /*
        * 0表示U盘
        * 1表示框架
        * */
        int[] s = new int[5];
        for(int i=0;i<s.length;i++){
            s[i] = 0;
        }

        usbDeviceList.clear();

        devicelist = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = devicelist.values().iterator();


        //遍历所有
        while (deviceIterator.hasNext()){
            UsbDevice device = deviceIterator.next();
            //检测到U盘
            if (device.getVendorId()==2316 && device.getProductId()==4096){
                s[0] = 1;
                Intent intent = new Intent(ACTION_USB_PAN);
                intent.putExtra("isConn","1");
                sendBroadcast(intent);
                continue;
            }
            Log.e("device",device.getVendorId() +"-"+device.getProductId());

            //检测是否有权限
            if(usbManager.hasPermission(device)){
                Log.e(TAG, "已经获得权限");
                //这里是需要寻找的设备
                usbDeviceList.add(device);

                //框架
                if(device.getVendorId()==1155 && device.getProductId()==22336){
                    s[1] = 1;
                }
                //甲醛
                if(device.getVendorId()==887 && device.getProductId()==30004){
                    s[4] = 1;
                }
            }else{
                Log.e(TAG, "正在获取权限...");

                pendingIntent = PendingIntent.getBroadcast(context,0,new Intent(ACTION_USB_PERMISSION),0);
                context.registerReceiver(broadcastReceiver, new IntentFilter(ACTION_USB_PERMISSION));

                usbManager.requestPermission(device, pendingIntent);
            }
        }

        //U盘
        if(s[0]==0){
            Intent intent = new Intent(ACTION_USB_PAN);
            intent.putExtra("isConn","0");
            sendBroadcast(intent);
        }
        //框架
        if(s[1]==0){
            Intent stopIntent = new Intent(ACTION_STOP1);
            sendBroadcast(stopIntent);
        }
        //空气
/*        if(s[2]==0){
            Intent stopIntent = new Intent(ACTION_STOP2);
            sendBroadcast(stopIntent);
        }
        //水质
        if(s[3]==0){
            Intent stopIntent = new Intent(ACTION_STOP3);
            sendBroadcast(stopIntent);
        }*/
        //甲醛
        if(s[4]==0){
            Intent stopIntent = new Intent(ACTION_STOP4);
            sendBroadcast(stopIntent);
        }

/*        if(usbDeviceCallback!=null){
            usbDeviceCallback.dataChanged(usbDeviceList);
        }*/
    }


    //这里是activity控制Service执行某些任务
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBind();
    }

    public class MyBind extends Binder{
        //得到服务
        public UsbComService getService(){
            return UsbComService.this;
        }
        //传递数据
        public void setData(Context context){
            UsbComService.this.context = context;
            usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            //检测是否有usb设备插入
            usbDetection();
        }
    }


    //回调监听部分
    private usbDeviceCallback usbDeviceCallback = null;
    public void setOnDeviceCallback(usbDeviceCallback usbDeviceCallback){
        this.usbDeviceCallback = usbDeviceCallback;
    }
    //回调监听，将service内部变化传递到外部
    public interface usbDeviceCallback{
        //返回更改的数据
        void dataChanged(List<UsbDevice> deviceList);
    }


    //解除绑定
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    //销毁时调用
    @Override
    public void onDestroy() {
        serviceRunning = false;
        super.onDestroy();
    }


}
