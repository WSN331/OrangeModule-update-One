package com.qiuyi.cn.orangemodule.upan;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.adapter.SDFileAdapter;
import com.qiuyi.cn.orangemodule.upan.myView.MyDialog;
import com.qiuyi.cn.orangemodule.upan.util.BasePermissionUAPN;
import com.qiuyi.cn.orangemodule.util.FileManager.FileUtils;
import com.qiuyi.cn.orangemodule.util.FileUtilOpen;
import com.qiuyi.cn.orangemodule.util.MyApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/3/27.
 * 展示U盘中所有文件
 */
public class UAllFileShowActivity extends BasePermissionUAPN {

    //需要申请的权限
    //基本权限
    private static final String[] ACTION_PERMISSION = {
            //存储权限
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            //读取权限
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };
    //USB相关权限
    private static final String[] ACTION_USB = {
            //USB插入权限
            UsbManager.ACTION_USB_DEVICE_ATTACHED,
            //USB拔出权限
            UsbManager.ACTION_USB_DEVICE_DETACHED
    };

    //USB管理类
    private UsbManager usbManager;
    private HashMap<String,UsbDevice> deviceList;
    private List<UsbDevice> usbDeviceList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upanmain);
        ButterKnife.bind(this);

        //获取基础权限
        getBasePermission();
        //获取USB权限
        getPermission();
    }

    //获取通过USB连接的设备
    private void getUSBConnectDevice() {
        usbDeviceList.clear();
        usbManager = (UsbManager) getSystemService(USB_SERVICE);
        deviceList = usbManager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        while (deviceIterator.hasNext()){
            UsbDevice device = deviceIterator.next();
            usbDeviceList.add(device);
        }
        Toast.makeText(MyApplication.getContext(),"usb设备数量为："+usbDeviceList.size(),Toast.LENGTH_SHORT).show();
        for(final UsbDevice myDevice:usbDeviceList){

            new MyDialog(this, R.style.dialog, new MyDialog.OnItemClickListener() {
                @Override
                public void onClick(Dialog dialog, int position) {
                    if(position==1){
                        //read
                        Intent intent = new Intent(getApplicationContext(),readUSBActivity.class);
                        intent.putExtra("device",myDevice);
                        startActivity(intent);
                        //dialog.dismiss();
                    }
                    if(position==2){
                        //write
                        Intent intent = new Intent(getApplicationContext(),writeUSBActivity.class);
                        //intent.putExtra("device",myDevice);
                        startActivity(intent);
                        //dialog.dismiss();
                    }
                }
            }).show();

        }
    }


    //获取基础权限
    private void getBasePermission(){
        //调用父类方法
        requestToPermission(ACTION_PERMISSION, new BasePermissionListener() {
            @Override
            public void onGranted() {
                //所有授权成功
                Log.e("USB","基础权限授权成功");
                //获取通过USB连接的设备
                getUSBConnectDevice();
            }
            @Override
            public void onGrantedSuccess(List<String> grantedPermission) {
                //获取成功的授权
                Log.e("USB","部分基础权限授权成功");
            }
            @Override
            public void onDenied(List<String> deniedPermission) {
                //获取失败的授权
                Log.e("USB","部分基础权限授权失败");
            }
        });
    }

    //权限注册获取
    private void getPermission() {
        IntentFilter usbDeviceFilter = new IntentFilter();
        //usb权限
        usbDeviceFilter.addAction(ACTION_USB[0]);//插入
        usbDeviceFilter.addAction(ACTION_USB[1]);//拔出
        registerReceiver(usbBroadCastReceiver,usbDeviceFilter);
    }

    //注册广播
    private BroadcastReceiver usbBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    //接收到插入设备的广播
                    Log.e("USB","设备已插入");
                    //得到基础权限
                    getBasePermission();
                    break;
                case UsbManager.ACTION_USB_ACCESSORY_DETACHED:
                    //接收到拔出设备的广播
                    Log.e("USB","设备已拔出");
                    //得到基础权限
                    getBasePermission();
                    break;

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(usbBroadCastReceiver!=null){
            unregisterReceiver(usbBroadCastReceiver);
            usbBroadCastReceiver = null;
        }
    }

}
