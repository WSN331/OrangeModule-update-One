package com.qiuyi.cn.orangemodule.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.qiuyi.cn.orangemodule.R;

import com.qiuyi.cn.orangemodule.adapter.RecentlyAdapter;
import com.qiuyi.cn.orangemodule.bean.FileInfo;
import com.qiuyi.cn.orangemodule.util.FileManager.adapter.FileAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.adapter.ImageAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.adapter.MusicAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.adapter.VideoAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.FileBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.ImageBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.MusicBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.VideoBean;
import com.qiuyi.cn.orangemodule.util.FileUtilOpen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/16.
 * 文件展示模块
 */
public class FileShowActivity extends Activity{

    private RecyclerView myFileShow;
    private RecentlyAdapter myFileAdapter;
    private GridLayoutManager myGridManager;

    private List<MusicBean> listMusics;//音乐
    private List<VideoBean> listVideos;//视频
    private List<ImageBean> listImages;//图片
    private List<FileBean> listFiles;//文件
    private List<FileBean> listFileZars;//压缩包

    private FileAdapter fileAdapter;
    private ImageAdapter imgAdapter;
    private MusicAdapter mucAdapter;
    private VideoAdapter videoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileshow);

        initView();

        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //设置文件

        myGridManager = new GridLayoutManager(this,4);
        myFileShow.setLayoutManager(myGridManager);



        //获取数据，刷新界面
        whatTypeToget();


    }

    /*
    * 判断得到的数据类型
    * */
    private void whatTypeToget() {
        Intent intent = getIntent();
        Serializable info = intent.getSerializableExtra("listFile");

        switch (intent.getIntExtra("type",0)){
            case 0:
                listImages = (List<ImageBean>) info;
                imgAdapter = new ImageAdapter(this,listImages,myGridManager);
                myFileShow.setAdapter(imgAdapter);

                imgAdapter.setOnImageItemClick(new ImageAdapter.ImageItemClick() {
                    @Override
                    public void openImage(View view, int position, List<ImageBean> allImageBean) {
                        ImageBean imageBean = allImageBean.get(position);
                        if(imageBean.getFiletype()==1){
                            FileUtilOpen.openFileByPath(getApplicationContext(),imageBean.getPath());
                        }
                    }
                });

                break;
            case 1:
                listVideos = (List<VideoBean>) info;
                videoAdapter = new VideoAdapter(this,listVideos,myGridManager);
                myFileShow.setAdapter(videoAdapter);

                videoAdapter.setOnVideoItemClick(new VideoAdapter.VideoItemClick() {
                    @Override
                    public void openVideo(View view, int position, List<VideoBean> allVideoBean) {
                        VideoBean videoBean = allVideoBean.get(position);
                        if(videoBean.getFiletype()==2){
                            FileUtilOpen.openFileByPath(getApplicationContext(),videoBean.getPath());
                        }
                    }
                });
                break;
            case 2:
                listFiles = (List<FileBean>) info;
                fileAdapter = new FileAdapter(this,listFiles,myGridManager);
                myFileShow.setAdapter(fileAdapter);

                fileAdapter.setOnFileItemClick(new FileAdapter.FileItemClick() {
                    @Override
                    public void openFile(View view, int position, List<FileBean> allFileBean) {
                        FileBean fileBean = allFileBean.get(position);
                        if(fileBean.getFiletype()==2){
                            FileUtilOpen.openFileByPath(getApplicationContext(),fileBean.getPath());
                        }
                    }
                });
                break;
            case 3:
                listMusics = (List<MusicBean>) info;
                mucAdapter = new MusicAdapter(this,listMusics,myGridManager);
                myFileShow.setAdapter(mucAdapter);

                mucAdapter.setOnMusicItemClick(new MusicAdapter.MusicItemClick() {
                    @Override
                    public void openMusic(View view, int position, List<MusicBean> allMusicBean) {
                        MusicBean musicBean = allMusicBean.get(position);
                        if(musicBean.getFiletype()==2){
                            FileUtilOpen.openFileByPath(getApplicationContext(),musicBean.getPath());
                        }
                    }
                });
                break;
            case 4:
                listFileZars = (List<FileBean>) info;
                fileAdapter = new FileAdapter(this,listFileZars,myGridManager);
                myFileShow.setAdapter(fileAdapter);

                fileAdapter.setOnFileItemClick(new FileAdapter.FileItemClick() {
                    @Override
                    public void openFile(View view, int position, List<FileBean> allFileBean) {
                        FileBean fileBean = allFileBean.get(position);
                        if(fileBean.getFiletype()==2){
                            FileUtilOpen.openFileByPath(getApplicationContext(),fileBean.getPath());
                        }
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        myFileShow = findViewById(R.id.fileshow_rl);
    }

}
