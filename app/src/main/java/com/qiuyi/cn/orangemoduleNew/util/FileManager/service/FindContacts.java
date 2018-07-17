package com.qiuyi.cn.orangemoduleNew.util.FileManager.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.qiuyi.cn.orangemoduleNew.MainActivity;
import com.qiuyi.cn.orangemoduleNew.util.Constant;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.contacts.PhoneInfo;

import org.json.JSONException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/4/2.
 * 查找联系人
 */
public class FindContacts extends Service{


    //线程池
    private ExecutorService executorService;
    //手机联系人获取
    private PhoneInfo phoneInfo;

    @Override
    public void onCreate() {
        super.onCreate();

        executorService = Executors.newCachedThreadPool();
        phoneInfo = new PhoneInfo(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    MainActivity.constacts = phoneInfo.getContactInfo();
                    Intent intent = new Intent(Constant.BACKUP);
                    intent.putExtra("isOK",true);
                    sendBroadcast(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
