package com.qiuyi.cn.orangemodule.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.adapter.RecentlyAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.FileUtils;
import com.qiuyi.cn.orangemodule.util.FileManager.adapter.FileAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.adapter.ImageAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.adapter.MusicAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.adapter.UFileAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.adapter.VideoAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.FileBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.ImageBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.MusicBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.VideoBean;
import com.qiuyi.cn.orangemodule.util.FileUtilOpen;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/16.
 * U盘文件展示模块
 */
public class UFileShowActivity extends Activity{

    private RecyclerView myFileShow;
    private GridLayoutManager myGridManager;

    private List<File> listFiles;//文件

    private UFileAdapter ufileAdapter;


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

        listFiles = (List<File>) intent.getSerializableExtra("listUFile");

        ufileAdapter = new UFileAdapter(this,listFiles,myGridManager);
        myFileShow.setAdapter(ufileAdapter);

        //点击事件
        ufileAdapter.setOnFileItemClick(new UFileAdapter.FileItemClick() {
            @Override
            public void openFile(View view, int position) {
                FileUtilOpen.openFileByPath(getApplicationContext(),listFiles.get(position).getPath());
            }
        });
    }

    /**
     * 初始化界面
     */
    private void initView() {
        myFileShow = findViewById(R.id.fileshow_rl);
    }

}
