package com.qiuyi.cn.orangemoduleNew.util.FileManager.service;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.qiuyi.cn.orangemoduleNew.MainActivity;
import com.qiuyi.cn.orangemoduleNew.activity.SearchActivity;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.ConstantValue;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.FileUtils;

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

    private List<File> allFiles;//所有文件
    private List<File> listMusics;//音乐
    private List<File> listVideos;//视频
    private List<File> listImages;//图片
    private List<File> listFiles;//文件
    private List<File> listFileZars;//压缩包


    @Override
    public void onCreate() {
        super.onCreate();

        executorService = Executors.newCachedThreadPool();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        allFiles = new ArrayList<>();
        listMusics = new ArrayList<>();
        listVideos = new ArrayList<>();
        listImages = new ArrayList<>();
        listFiles = new ArrayList<>();
        listFileZars = new ArrayList<>();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    //有外部存储卡
                    File file = Environment.getExternalStorageDirectory().getAbsoluteFile();

                    long startTime = System.currentTimeMillis();
                    allFiles = getAllFiles(file);

                    divide(allFiles);

                    MainActivity.MY_ALLFILLES_II = allFiles;

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



    //将所有文件分类
    private void divide(List<File> allFiles) {

        for(File itemFile:allFiles){
            if (FileUtils.getFileType(itemFile.getPath()) == ConstantValue.TYPE_IMG){
                listImages.add(itemFile);
            }else if(FileUtils.getFileType(itemFile.getPath()) == ConstantValue.TYPE_MP4){
                listVideos.add(itemFile);
            }else if(FileUtils.getFileType(itemFile.getPath()) == ConstantValue.TYPE_DOC){
                listFiles.add(itemFile);
            }else if(FileUtils.getFileType(itemFile.getPath()) == ConstantValue.TYPE_MP3){
                listMusics.add(itemFile);
            }else if(FileUtils.getFileType(itemFile.getPath()) == ConstantValue.TYPE_ZIP){
                listFileZars.add(itemFile);
            }
        }

        MainActivity.listImages = listImages;
        MainActivity.listVideos = listVideos;
        MainActivity.listFiles = listFiles;
        MainActivity.listMusics = listMusics;
        MainActivity.listFileZars = listFileZars;
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
