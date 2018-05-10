package com.qiuyi.cn.orangemodule.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.adapter.SDFileAdapter;
import com.qiuyi.cn.orangemodule.util.DiskWriteToSD;
import com.qiuyi.cn.orangemodule.util.FileUtilOpen;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2018/4/2.
 * 收藏夹
 */
public class CollectionActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{

    private SDFileAdapter myAdapter;
    private DiskWriteToSD diskWriteToSD;//复制到本地
    private List<File> collectionFile;

    private SwipeRefreshLayout mySwipLayout;
    private RecyclerView myrlShow;
    private GridLayoutManager myManager;

    private File rootFile;
    private File currentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        diskWriteToSD = new DiskWriteToSD(this);

        initView();

        initData();

    }

    private void initView() {
        mySwipLayout = findViewById(R.id.sf_layout);
        myrlShow = findViewById(R.id.rl_show);
        myManager = new GridLayoutManager(this,1);
        collectionFile = new ArrayList<>();

        mySwipLayout.setColorSchemeColors(Color.RED);
        mySwipLayout.setOnRefreshListener(this);
    }

    //初始化数据
    private void initData() {
        //找到收藏文件夹
        File myCollectionfile = diskWriteToSD.getSDCardFile(AllFileShowActivity.CollectionDirectory_Name);
        rootFile = myCollectionfile;
        currentFile = myCollectionfile;

        readFileList(currentFile);
    }

    //列表展示
    public void readFileList(File currentFolder){
        collectionFile.clear();
        for(File myFile:currentFolder.listFiles()){
            collectionFile.add(myFile);
        }

        //按照文件和文件夹排序
        Collections.sort(collectionFile, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                if (f1.isFile()) {
                    if (f2.isFile()) {
                        //相同种类的文件，按照文件名大小写排序
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    } else {
                        return 1;
                    }
                } else {
                    if (f2.isFile()) {
                        return -1;
                    } else {
                        //相同种类的文件，按照文件名大小写排序
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    }
                }
            }
        });

        myAdapter = new SDFileAdapter(this,collectionFile);
        myrlShow.setLayoutManager(myManager);
        myrlShow.setAdapter(myAdapter);

        myAdapter.setOnItemClick(new SDFileAdapter.SD_OnItemClick() {
            @Override
            public void onItemClick(View view, int position) {
                File file = collectionFile.get(position);
                if(file.isDirectory()){
                    currentFile = file;
                    readFileList(file);
                }else{
                    FileUtilOpen.openFileByPath(getApplicationContext(),file.getPath());
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void changeCount(int count) {

            }
        });


    }

    @Override
    public void onRefresh() {
        mySwipLayout.setRefreshing(true);

        readFileList(currentFile);

        mySwipLayout.setRefreshing(false);
    }
}
