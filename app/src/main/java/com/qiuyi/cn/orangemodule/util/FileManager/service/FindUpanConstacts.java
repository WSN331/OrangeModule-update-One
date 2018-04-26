package com.qiuyi.cn.orangemodule.util.FileManager.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.util.Constant;
import com.qiuyi.cn.orangemodule.util.FileManager.MyFileHelper;
import com.qiuyi.cn.orangemodule.util.FileManager.contacts.PhoneInfo;

import org.json.JSONException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/4/2.
 * 获取U盘中的联系人文件
 */
public class FindUpanConstacts extends Service{

    //线程池
    private ExecutorService executorService;
    //文件操作方法
    private MyFileHelper myFileHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        executorService = Executors.newCachedThreadPool();
        //文件的存取
        myFileHelper = new MyFileHelper(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                MainActivity.listContacts = myFileHelper.getPhoneFile();
                Intent intent = new Intent(Constant.RESTORE);
                intent.putExtra("isOK",true);
                sendBroadcast(intent);
            }
        });


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
