package com.qiuyi.cn.orangemodule.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.adapter.SDFileAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.FileUtils;
import com.qiuyi.cn.orangemodule.util.FileUtilOpen;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2018/3/27.
 * 展示所有文件
 */
public class AllFileShowActivity extends Activity{

    private TextView tv_fload;
    private RecyclerView rl_fileshow;



    //手机根路径
    private String rootPath;
    private String currentPath;

    //获取文件
    private List<File> fileList;
    private SDFileAdapter sdfileAdapter;
    private GridLayoutManager myGridManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allfileshow);

        initView();

        initData();
    }

    //初始化数据
    private void initData() {

        if(FileUtils.isSDCardAvailable()){
            //SD卡存在
            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            currentPath = rootPath;

            tv_fload.setText(currentPath);
            File currentFolder = new File(currentPath);
            readFileList(currentFolder);
        }

    }

    //读取文件
    private void readFileList(final File currentFolder){
        fileList.clear();

        for(File file:currentFolder.listFiles()){
            fileList.add(file);
        }

        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                if (f1.isFile()) {
                    if (f2.isFile()) {
                        return 0;
                    } else {
                        return 1;
                    }
                } else {
                    if (f2.isFile()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        });

        if(sdfileAdapter == null){
            sdfileAdapter = new SDFileAdapter(this,fileList);
            rl_fileshow.setLayoutManager(myGridManager);
            rl_fileshow.setAdapter(sdfileAdapter);

            sdfileAdapter.setOnItemClick(new SDFileAdapter.SD_OnItemClick() {
                @Override
                public void onItemClick(View view, int position) {
                    File file = fileList.get(position);
                    if(file.isDirectory()){
                        currentPath += "/"+file.getName();
                        tv_fload.setText(currentPath);
                        readFileList(file);
                    }else{
                        FileUtilOpen.openFileByPath(getApplicationContext(),file.getPath());
                    }
                }
                @Override
                public void onItemLongClick(View view, int position) {

                }
            });

            tv_fload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!currentPath.equals(rootPath)) {
                        currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
                        tv_fload.setText(currentPath);
                        readFileList(new File(currentPath));
                    }
                }
            });

        }else{
            sdfileAdapter.notifyDataSetChanged();
        }

    }


    //初始化界面
    private void initView() {

        tv_fload = findViewById(R.id.tv_floader);
        rl_fileshow = findViewById(R.id.allFile_rl);

        fileList = new ArrayList<>();
        myGridManager = new GridLayoutManager(this,1);
    }



    @Override
    public void onBackPressed() {
        if (!currentPath.equals(rootPath)) {
            currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
            tv_fload.setText(currentPath);
            readFileList(new File(currentPath));
        }else{
            super.onBackPressed();
        }
    }
}
