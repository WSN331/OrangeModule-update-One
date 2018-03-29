package com.qiuyi.cn.orangemodule.util.FileManager.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.util.Constant;
import com.qiuyi.cn.orangemodule.util.FileManager.ConstantValue;
import com.qiuyi.cn.orangemodule.util.FileManager.FileUtils;
import com.qiuyi.cn.orangemodule.util.FileManager.MyFileManager;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.FileBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.ImageBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.MusicBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.VideoBean;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/28.
 * 查询U盘文件
 */
public class FindUpanMsg_Service extends Service{

    private MyFileManager myFileManager;
    private List<File> allFiles;//所有文件
    private List<File> listMusics;//音乐
    private List<File> listVideos;//视频
    private List<File> listImages;//图片
    private List<File> listFiles;//文件
    private List<File> listFileZars;//压缩包

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        final String floder = intent.getStringExtra("Folder");

        new Thread(new Runnable() {
            @Override
            public void run() {
                divideFiles(floder);
            }
        }).start();

        return super.onStartCommand(intent, flags, startId);
    }


    //文件分类
    public void divideFiles(String floder){

        allFiles = new ArrayList<>();
        listMusics = new ArrayList<>();
        listVideos = new ArrayList<>();
        listImages = new ArrayList<>();
        listFiles = new ArrayList<>();
        listFileZars = new ArrayList<>();

        myFileManager = MyFileManager.getInstance(this);

        Log.e("执行顺序","4");

        allFiles = myFileManager.getUpanFiles(new File(floder));

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

        MainActivity.listUPANAllFiles = allFiles;
        MainActivity.listUPANImages = listImages;
        MainActivity.listUPANVideos = listVideos;
        MainActivity.listUPANFiles = listFiles;
        MainActivity.listUPANMusics = listMusics;
        MainActivity.listUPANFileZars = listFileZars;


        Intent intent = new Intent(Constant.FINDUPAN_MSG);
        intent.putExtra("findOkUpan",true);
        sendBroadcast(intent);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}