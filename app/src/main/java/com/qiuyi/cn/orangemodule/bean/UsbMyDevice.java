package com.qiuyi.cn.orangemodule.bean;

import android.hardware.usb.UsbDevice;

/**
 * Created by Administrator on 2018/1/7.
 */
public class UsbMyDevice {

    private UsbDevice usbDevice;
    private static UsbMyDevice myDevice;

    private UsbMyDevice(UsbDevice usbDevice) {
        this.usbDevice = usbDevice;
    }

    //静态内部类
    public static UsbMyDevice getInstance(UsbDevice usbDevice){
        if(myDevice==null){
            synchronized (UsbDevice.class){
                if(myDevice==null){
                    myDevice = new UsbMyDevice(usbDevice);
                }
            }
        }
        return myDevice;
    }


    public UsbDevice getMyDevice() {
        return usbDevice;
    }

    public void setMyDevice(UsbDevice usbDevice) {
        this.usbDevice = usbDevice;
    }
}
