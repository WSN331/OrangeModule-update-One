package com.qiuyi.cn.orangemodule.util.FileManager.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.activity.SearchActivity;
import com.qiuyi.cn.orangemodule.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by Administrator on 2018/4/2.
 * 查找所有文件的第二种方法,包括文件夹
 */
public class FindAllFile_II_Service extends Service{

    private ExecutorService executorService;

    @Override
    public void onCreate() {
        super.onCreate();

        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    //有外部存储卡
                    File file = Environment.getExternalStorageDirectory().getAbsoluteFile();

                    long startTime = System.currentTimeMillis();
                    MainActivity.MY_ALLFILLES_II = getAllFiles(file);
                    long endTime = System.currentTimeMillis();

                    Log.e("文件查找时间", "全局文件查找时间："+(endTime-startTime));

                    MainActivity.MY_ALLFILLES_II.addAll(getAllDirectory(file));

                    Intent intentToSearch = new Intent(SearchActivity.SearchActivity_getSDFile);
                    intentToSearch.putExtra("findAllSDFile",true);
                    sendBroadcast(intentToSearch);
                    Log.e("search","查找完毕");
                }

            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    public List<File> getAllDirectory(File newFile){
        List<File> allDirectoryFiles = new ArrayList<>();
        for(File direcFile:newFile.listFiles()){
            if(direcFile.isDirectory()){
                allDirectoryFiles.add(direcFile);
                allDirectoryFiles.addAll(getAllDirectory(direcFile));
            }
        }
        return allDirectoryFiles;
    }

    //获取所有文件
    private List<File> getAllFiles(File currentFolder) {

        List<File> newFiles = new ArrayList<>();
        List<Callable<List<File>>> partions = new ArrayList<>();

        for(final File file:currentFolder.listFiles()){
            if(file.isDirectory()){
                //newFiles.addAll(getAllFiles(file));
                partions.add(new Callable<List<File>>() {
                    @Override
                    public List<File> call() throws Exception {
                        return getSecondFiles(file);
                    }
                });
            }else{
                newFiles.add(file);
            }
        }

        if(partions.size()>0) {
            //启动并发
            ExecutorService executorPool = Executors.newCachedThreadPool();
            List<FutureTask<List<File>>> listTask = new ArrayList<>();
            for (Callable<List<File>> callable : partions) {
                FutureTask<List<File>> futureTask = new FutureTask<List<File>>(callable);
                listTask.add(futureTask);
                executorPool.submit(futureTask);
            }

            try {
                for (FutureTask<List<File>> task : listTask) {
                    newFiles.addAll(task.get());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(executorPool!=null){
                    executorPool.shutdown();
                }
            }

        }
        return newFiles;
    }


    public List<File> getSecondFiles(File currentFolder){
        List<File> newFiles = new ArrayList<>();
        for(File file: currentFolder.listFiles()){
            if(file.isDirectory()){
                newFiles.addAll(getSecondFiles(file));
            }else{
                newFiles.add(file);
            }
        }
        return newFiles;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
