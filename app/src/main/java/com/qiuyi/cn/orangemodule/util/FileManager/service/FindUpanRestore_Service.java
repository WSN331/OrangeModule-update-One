package com.qiuyi.cn.orangemodule.util.FileManager.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.util.Constant;
import com.qiuyi.cn.orangemodule.util.FileManager.MyFileHelper;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/4/11.
 * 查找在U盘中的备份文件夹
 */
public class FindUpanRestore_Service extends Service{

    //线程池
    private ExecutorService executorService;
    //文件操作方法
    private MyFileHelper myFileHelper;
    //根文件
    private File currentFile;

    @Override
    public void onCreate() {
        super.onCreate();

        executorService = Executors.newCachedThreadPool();
        //文件的存取
        //myFileHelper = new MyFileHelper(getApplicationContext());
        currentFile = MainActivity.rootUFile;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                MainActivity.uMusic = myFileHelper.getUPCardTFile(currentFile,"音乐");
                MainActivity.uVideo = myFileHelper.getUPCardTFile(currentFile,"视频");
                MainActivity.uImages = myFileHelper.getUPCardTFile(currentFile,"照片");
                MainActivity.uFiles = myFileHelper.getUPCardTFile(currentFile,"文档");
                Intent intent = new Intent(Constant.FINDUPAN_BFMSG);
                intent.putExtra("findOkUpan",true);
                sendBroadcast(intent);
                Log.e("find", "文笔");
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
