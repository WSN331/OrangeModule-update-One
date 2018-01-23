package com.qiuyi.cn.orangemodule.util;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Yang on 2017/7/11.
 * Function：Usb通信类
 */

public class UsbCommunication {

    private static final String TAG = UsbCommunication.class.getSimpleName();
    //接收的广播
    private static final String ACTION_USB_PERMISSION = "com.yangjian.temperaturemodule.USB_PERMISSION";

    private HashMap<String, UsbDevice> deviceList;  //设备列表
    private UsbManager usbManager;  //USB管理器:负责管理USB设备的类
    private UsbDevice usbDevice;   //找到的USB设备
    private UsbInterface usbInterface;  //代表USB设备的一个接口
    private UsbDeviceConnection deviceConnection;  //USB连接的一个类。用此连接可以向USB设备发送和接收数据，这里我们使用这个类下面的块传输方式
    private UsbEndpoint usbEpIn;  //代表一个接口的某个节点的类:读数据节点
    private UsbEndpoint usbEpOut;  //代表一个接口的某个节点的类:写数据节点

    //构造方法
    public UsbCommunication(Context context,UsbDevice usbDevice) {
        this.usbDevice = usbDevice;

        //获取设备接口
        findInterface(context,usbDevice);
    }

    //获取设备接口
    private void findInterface(Context context,UsbDevice usbDevice) {

        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

        if (usbDevice == null) {
            Log.e(TAG, "没有找到设备");
            return;
        }

        for (int i = 0; i < usbDevice.getInterfaceCount(); i++) {
            //一个设备上面一般只有一个接口，有两个端点，分别接受和发送数据
            UsbInterface uInterface = usbDevice.getInterface(i);
            usbInterface = uInterface;
            Log.e(TAG, usbInterface.toString());
        }

        getEndpoint(usbInterface);

        if (usbInterface != null) {
            UsbDeviceConnection connection = null;
            //判断是否有权限
            if (usbManager.hasPermission(usbDevice)) {
                Log.e(TAG, "已经获得权限");
                //打开数据交互连接
                connection = usbManager.openDevice(usbDevice);

                Log.e("dangqian", connection == null ? "true" : "false");

                if (connection == null) {
                    Log.e(TAG, "设备连接为空");
                    return;
                }
                if (connection != null && connection.claimInterface(usbInterface, true)) {
                    //初始化连接
                    deviceConnection = connection;

                    Log.e("chushihua", deviceConnection == null ? "true" : "false");
                } else {
                    connection.close();
                }

            }
        } else {
            Log.e(TAG, "没有找到接口");
        }
    }

    //获取端点
    private void getEndpoint (UsbInterface usbInterface) {
        for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
            UsbEndpoint ep = usbInterface.getEndpoint(i);
            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                    usbEpOut = ep;
                    Log.e(TAG, "获取发送数据的端点");
                } else {
                    usbEpIn = ep;
                    Log.e(TAG, "获取接受数据的端点");
                }
            }
        }
    }

    public UsbDeviceConnection getDeviceConnection() {
        return deviceConnection;
    }


    //方式一：接受数据
    public byte[] receiveMessage() {
        byte[] bytes = new byte[512];
        int result = deviceConnection.bulkTransfer(usbEpIn, bytes, bytes.length, 3000);

        byte[] mybytes = new byte[result+10];
        int newresult = deviceConnection.bulkTransfer(usbEpIn,mybytes,mybytes.length,3000);
        //获取到的数据
        Log.i("bytesshuju",bytes+"");
        return mybytes;
    }





    //发送数据
/*    public int sendMessage(byte[] bytes) {
        int result = deviceConnection.bulkTransfer(usbEpOut, bytes, bytes.length, 3000);
        Log.e(TAG, result + "----------------------");
        return result;
    }*/


    //方式二：接受数据
/*    public byte[] receiveData() {
        byte[] bytes = null;
        int max = usbEpIn.getMaxPacketSize();
        ByteBuffer byteBuffer = ByteBuffer.allocate(max);
        UsbRequest usbRequest = new UsbRequest();
        usbRequest.initialize(deviceConnection, usbEpIn);
        usbRequest.queue(byteBuffer, max);
        if (deviceConnection.requestWait() == usbRequest) {
            bytes = byteBuffer.array();
        }
        return bytes;
    }*/




    /*    //初始化设备
    private void initUsbDevice(Context context) {

        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();

        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();
            //找到指定的设备
            if (device.getVendorId() == 2316 && device.getProductId() == 4096) {
                usbDevice = device;
                Log.e(TAG, "找到设备");
            }
        }
        findInterface();
    }*/
}
