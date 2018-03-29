package com.qiuyi.cn.orangemodule.upan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.upan.adapter.UpanFileAdapter;
import com.qiuyi.cn.orangemodule.util.FileUtilOpen;
import com.qiuyi.cn.orangemodule.util.MyApplication;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/1/5.
 *
 * U盘模块
 */
public class readUSBActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{

    //允许app访问usb
    private static final String ACTION_USB_PERMISSION = "com.android.qiuyi.USB_PERMISSION";
    private PendingIntent pendingIntent;

    @BindView(R.id.mySwipeRefreshLayout)
    SwipeRefreshLayout mySwipeRefreshLayout;
    @BindView(R.id.myRecyclerView)
    RecyclerView myRecyclerView;

    private File currentFolder; //U盘根目录
    private List<File> usbFileData = new ArrayList<>();//当前展示的U盘文件
    private UpanFileAdapter fileAdapter;//文件的adapter
    private List<File> currentFilePath = new ArrayList<>();//当前文件路径

    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udisk);
        ButterKnife.bind(this);

        initBasic();
        initData();
    }

    private void initBasic() {
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mySwipeRefreshLayout.setColorSchemeColors(Color.RED);
        mySwipeRefreshLayout.setOnRefreshListener(this);
    }

    //初始化数据
    private void initData() {
        //获取USB权限
        getUsbPermission();
    }

    //获得USB权限
    private void getUsbPermission() {
        getPermission();
        UsbManager usbManager = (UsbManager) getSystemService(USB_SERVICE);
        UsbDevice usbDevice = getIntent().getParcelableExtra("device");
        //有权限再去找U盘路径
        if(usbManager.hasPermission(usbDevice)){
            Log.e("USB","已经授权");
            //查找U盘路径
            findUdiskPath();
        }else{
            Log.e("USB","申请USB授权");
            //没有权限
            pendingIntent = PendingIntent.getBroadcast(MyApplication.getContext(),0,new Intent(ACTION_USB_PERMISSION),0);
            usbManager.requestPermission(usbDevice,pendingIntent);
        }
    }


    //获取USB监听权限
    private void getPermission() {
        IntentFilter usbDeviceFilter = new IntentFilter();
        usbDeviceFilter.addAction(ACTION_USB_PERMISSION);
        registerReceiver(usbBroadCastReceiver,usbDeviceFilter);
    }

    //注册广播
    private BroadcastReceiver usbBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case ACTION_USB_PERMISSION:
                    Log.e("USB","接收到请求权限广播");
                    //获取传进来的UsbDevice
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,false)){
                        Log.e("USB","用户已授权");
                        findUdiskPath();
                    }else{
                        Log.e("USB","用户未授权");
                    }
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

    //查找U盘路径
    private void findUdiskPath() {
        //存储得到的文件路径
        String[] result = null;
        //得到存储管理
        StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);

        //利用反射调用storageManager的系统方法
        try {
            //利用反射
            Method method = StorageManager.class.getMethod("getVolumePaths");
            method.setAccessible(true);
            try {
                result = (String[]) method.invoke(storageManager);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < result.length; i++) {
                Log.e("path----> ", result[i] + "");
                if (result[i] != null && result[i].startsWith("/storage") && !result[i].startsWith("/storage/emulated/0")) {
                    currentFolder = new File(result[i]);
                    getAllFiles();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //得到U盘下的所有文件
    private void getAllFiles() {
        //得到所有文件
        File files[] = currentFolder.listFiles();
        usbFileData.clear();

        //装载文件
        if(files !=null){
            //将当前目录文件获取
            for(File f:files){
                usbFileData.add(f);
            }
            //排序文件
            Collections.sort(usbFileData, new Comparator<File>() {
                @Override
                public int compare(File file, File t1) {
                    if(file.isDirectory()){
                        return -1;
                    }else {
                        return 1;
                    }
                }
            });
            //初始化界面
            initView();
        }
    }

    //初始化界面
    private void initView() {


        fileAdapter = new UpanFileAdapter(usbFileData);
        myRecyclerView.setAdapter(fileAdapter);
        //停止刷新并显示
        mySwipeRefreshLayout.setRefreshing(false);
        //设置监听事件
        initListener();
    }

    //监听事件
    private void initListener() {

        //文件点击
        fileAdapter.setOnItemClick(new UpanFileAdapter.OnItemClick() {
            @Override
            public void onItemClick(View view, int position) {
                //得到点击的文件
                File file = usbFileData.get(position);
                if(file.isDirectory()){
                    //将原来的路径添加到List中
                    currentFilePath.add(currentFolder);
                    //重新设置当前的路径
                    currentFolder = file;
                    getAllFiles();
                }else{
                    FileUtilOpen.openFileByPath(readUSBActivity.this,file.getPath());
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {
                final File file = usbFileData.get(position);
                builder = new AlertDialog.Builder(readUSBActivity.this)
                        .setTitle("确定要删除吗？")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                file.delete();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(true);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }


    //界面刷新
    @Override
    public void onRefresh() {
        mySwipeRefreshLayout.setRefreshing(true);
        //重新获取当前页
        getAllFiles();
    }

    //后退按钮，后退目录
    @Override
    public void onBackPressed() {
        //这个不是最初的目录
        if(currentFilePath.size()>0){
            //回退到前一个
            int preview = currentFilePath.size()-1;
            currentFolder = currentFilePath.get(preview);
            //删除当前目录
            currentFilePath.remove(preview);
            getAllFiles();
        }else{
            super.onBackPressed();
        }
    }
}
