package com.qiuyi.cn.orangemodule.util.FileManager.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.util.Constant;
import com.qiuyi.cn.orangemodule.util.FileManager.ConstantValue;
import com.qiuyi.cn.orangemodule.util.FileManager.MyFileManager;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.FileBean;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Created by Administrator on 2018/3/26.
 * 查询所有文件
 */
public class FindAllFile_Service extends Service{


    //文件管理类
    private MyFileManager myFileManager;
    private List<FileBean> myAllFiles;

    private ExecutorService myExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                getAllFile();
            }
        }).start();


        return super.onStartCommand(intent, flags, startId);
    }

    //获得所有文件
    private void getAllFile() {

        myFileManager = MyFileManager.getInstance(this);

        myExecutor = Executors.newCachedThreadPool();

        Callable<List<FileBean>> callback = new Callable<List<FileBean>>() {
            @Override
            public List<FileBean> call() throws Exception {
                return myFileManager.getFiles();
            }
        };

        FutureTask<List<FileBean>> futureTask =  new FutureTask<List<FileBean>>(callback);
        myExecutor.submit(futureTask);

        try {
            myAllFiles = futureTask.get();

            MainActivity.MY_ALLFILES = myAllFiles;

            Intent intent = new Intent(Constant.FINDALL_MSG);
            intent.putExtra("findallmsg",true);
            sendBroadcast(intent);



        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(myExecutor!=null){
                myExecutor.shutdown();
            }
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
