package com.qiuyi.cn.orangemodule.util.FileManager.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.util.Constant;
import com.qiuyi.cn.orangemodule.util.FileManager.ConstantValue;
import com.qiuyi.cn.orangemodule.util.FileManager.MyFileManager;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.FileBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.ImageBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.MusicBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.VideoBean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by Administrator on 2018/3/26.
 * Native查找信息的
 */
public class FindFileMsg_Service extends Service{

    //各类文件
    private MyFileManager myFileManager;
    private List<MusicBean> listMusics;//音乐
    private List<VideoBean> listVideos;//视频
    private List<ImageBean> listImages;//图片
    private List<FileBean> listFiles;//文件
    private List<FileBean> listFileZars;//压缩包

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                getFileList();
            }
        }).start();


        return super.onStartCommand(intent, flags, startId);
    }

    //得到所有需要的文件
    private void getFileList() {

        //使用线程并发得到需要的文件
        myFileManager = MyFileManager.getInstance(this);

        listImages = new ArrayList<>();
        listMusics = new ArrayList<>();
        listVideos = new ArrayList<>();
        listFiles = new ArrayList<>();
        listFileZars = new ArrayList<>();

        ExecutorService executorService = Executors.newCachedThreadPool();

        Callable<List<ImageBean>> myImgCallable = new Callable<List<ImageBean>>() {
            @Override
            public List<ImageBean> call() throws Exception {
                return myFileManager.getImages();
            }
        };
        Callable<List<MusicBean>> myMucCallable = new Callable<List<MusicBean>>() {
            @Override
            public List<MusicBean> call() throws Exception {
                return myFileManager.getMusics();
            }
        };
        Callable<List<VideoBean>> myVidCallable = new Callable<List<VideoBean>>() {
            @Override
            public List<VideoBean> call() throws Exception {
                return myFileManager.getVideos();
            }
        };
        Callable<List<FileBean>> myFileCallable = new Callable<List<FileBean>>() {
            @Override
            public List<FileBean> call() throws Exception {
                return myFileManager.getFilesByType(ConstantValue.TYPE_DOC);
            }
        };
        Callable<List<FileBean>> myFileZarCallable = new Callable<List<FileBean>>() {
            @Override
            public List<FileBean> call() throws Exception {
                return myFileManager.getFilesByType(ConstantValue.TYPE_ZIP);
            }
        };

        FutureTask<List<ImageBean>> myImgTask = new FutureTask<List<ImageBean>>(myImgCallable);
        FutureTask<List<MusicBean>> myMucTask = new FutureTask<List<MusicBean>>(myMucCallable);
        FutureTask<List<VideoBean>> myVioTask = new FutureTask<List<VideoBean>>(myVidCallable);
        FutureTask<List<FileBean>> myFileTask = new FutureTask<List<FileBean>>(myFileCallable);
        FutureTask<List<FileBean>> myFileZarTask = new FutureTask<List<FileBean>>(myFileZarCallable);

        executorService.submit(myImgTask);
        executorService.submit(myMucTask);
        executorService.submit(myVioTask);
        executorService.submit(myFileTask);
        executorService.submit(myFileZarTask);

        try {
            listImages = myImgTask.get();
            listMusics = myMucTask.get();
            listVideos = myVioTask.get();
            listFiles = myFileTask.get();
            listFileZars = myFileZarTask.get();

            MainActivity.listImages = listImages;
            MainActivity.listMusics = listMusics;
            MainActivity.listVideos = listVideos;
            MainActivity.listFiles = listFiles;
            MainActivity.listFileZars = listFileZars;

            Intent intent = new Intent(Constant.FINDFILE_MSG);
            intent.putExtra("findOk",true);
            sendBroadcast(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(executorService!=null){
                executorService.shutdown();
            }
        }


    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
