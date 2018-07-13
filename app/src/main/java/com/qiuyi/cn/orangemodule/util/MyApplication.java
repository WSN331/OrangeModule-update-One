package com.qiuyi.cn.orangemodule.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.orm.SugarContext;
import com.qiuyi.cn.orangemodule.pager.DevicePager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Administrator on 2018/1/7.
 *
 * 获取全局Context
 * Application生命周期
 *
 */
public class MyApplication extends Application {

    private UsbManager usbManager;  //USB管理器:负责管理USB设备的类
    private static Context context;
    private UsbCommunication communication = null;
    private UsbDevice frameworkDevice;
    private HashMap<String, UsbDevice> deviceList;  //设备列表

    @Override
    public void onCreate() {
        super.onCreate();

        SugarContext.init(this);

        context = getApplicationContext();

        frameworkDevice = findDevice();

        if(frameworkDevice!=null){
            communication = new UsbCommunication(context,frameworkDevice);
        }


        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.e("MyApplication", "I am back to Background");
                String myOrder = "EF0000010";
                if(communication!=null && isInBackgroundState()){
                    communication.sendMessage(myOrder.getBytes());
                }

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });
    }

    //查找设备
    private UsbDevice findDevice() {
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while(deviceIterator.hasNext()){
            UsbDevice device = deviceIterator.next();
            //寻找设备,框架模块
            if(device.getVendorId()==1155 && device.getProductId()==22336){
                return device;
            }
        }
        return null;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //释放资源
        SugarContext.terminate();
    }

    //返回全局context
    public static Context getContext(){
        return context;
    }


    /**
     * 判断当前应用是否处于后台
     * @return true表示处于后台，false表示处于前台
     */
    private boolean isInBackgroundState(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        //通过ActivityManager获取正在运行的任务信息
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH){
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo processInfo:runningProcesses){
                //前台程序
                if(processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                    for(String activeProcess:processInfo.pkgList){
                        if(activeProcess.equals(getApplicationContext().getPackageName())){
                            return false;
                        }
                    }
                }
            }
            return true;
        }else{
            //通过ActivityManager获取正在运行的任务信息
            List<ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(50);
            //获取第一个activity栈的信息
            ActivityManager.RunningTaskInfo runningTaskInfo = runningTasks.get(0);
            //获取栈中的栈顶activity,根据activity的包名判断，是否是当前应用的包名
            ComponentName componentName = runningTaskInfo.topActivity;
            if(componentName.getPackageName().equals(getPackageName())){
                //处于前台
                return false;
            }else{
                //处于后台状态
                return true;
            }
        }
    }

}
