package com.qiuyi.cn.orangemodule.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.preference.DialogPreference;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.adapter.FileAdapter;
import com.qiuyi.cn.orangemodule.util.FileUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/1/5.
 *
 * U盘模块
 */
public class UdiskActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{

    @BindView(R.id.mySwipeRefreshLayout)
    SwipeRefreshLayout mySwipeRefreshLayout;
    @BindView(R.id.myRecyclerView)
    RecyclerView myRecyclerView;

    private File currentFolder; //U盘根目录
    private List<File> usbFileData = new ArrayList<>();//当前展示的U盘文件
    private FileAdapter fileAdapter;//文件的adapter
    private List<File> currentFilePath = new ArrayList<>();//当前文件路径

    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udisk);
        ButterKnife.bind(this);

        initBasic();
        initData();
    }

    private void initBasic() {
        myRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mySwipeRefreshLayout.setColorSchemeColors(Color.RED);
        mySwipeRefreshLayout.setOnRefreshListener(this);
    }

    //初始化数据
    private void initData() {
        //查找U盘路径
        findUdiskPath();

    }

    //查找U盘路径
    private void findUdiskPath() {
        //存储得到的文件路径
        String[] result = null;
        //得到存储管理
        StorageManager storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);

        //利用反射调用storageManager的系统方法
        try {
            //利用反射
            Method method = StorageManager.class.getMethod("getVolumePaths");
            method.setAccessible(true);
            try {
                result = (String[]) method.invoke(storageManager);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < result.length; i++) {
                Log.e("path----> ", result[i] + "");
                if (result[i] != null && result[i].startsWith("/storage") && !result[i].startsWith("/storage/emulated/0")) {
                    currentFolder = new File(result[i]);
                    getAllFiles();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //得到U盘下的所有文件
    private void getAllFiles() {
        //得到所有文件
        File files[] = currentFolder.listFiles();
        usbFileData.clear();

        //装载文件
        if(files !=null){
            //将当前目录文件获取
            for(File f:files){
                usbFileData.add(f);
            }
            //排序文件
            Collections.sort(usbFileData, new Comparator<File>() {
                @Override
                public int compare(File file, File t1) {
                    if(file.isDirectory()){
                        return -1;
                    }else {
                        return 1;
                    }
                }
            });
            //初始化界面
            initView();
        }
    }

    //初始化界面
    private void initView() {


        fileAdapter = new FileAdapter(usbFileData);
        myRecyclerView.setAdapter(fileAdapter);
        //停止刷新并显示
        mySwipeRefreshLayout.setRefreshing(false);
        //设置监听事件
        initListener();
    }

    //监听事件
    private void initListener() {

        //文件点击
        fileAdapter.setOnItemClick(new FileAdapter.OnItemClick() {
            @Override
            public void onItemClick(View view, int position) {
                //得到点击的文件
                File file = usbFileData.get(position);
                if(file.isDirectory()){
                    //将原来的路径添加到List中
                    currentFilePath.add(currentFolder);
                    //重新设置当前的路径
                    currentFolder = file;
                    getAllFiles();
                }else{
                    FileUtil.openFile(file, UdiskActivity.this);
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {
                final File file = usbFileData.get(position);
                builder = new AlertDialog.Builder(UdiskActivity.this)
                        .setTitle("确定要删除吗？")
                        .setPositiveButton("确定",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                file.delete();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(true);
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

    }


    //界面刷新
    @Override
    public void onRefresh() {
        mySwipeRefreshLayout.setRefreshing(true);
        //重新获取当前页
        getAllFiles();
    }

    //后退按钮，后退目录
    @Override
    public void onBackPressed() {
        //这个不是最初的目录
        if(currentFilePath.size()>0){
            //回退到前一个
            int preview = currentFilePath.size()-1;
            currentFolder = currentFilePath.get(preview);
            //删除当前目录
            currentFilePath.remove(preview);
            getAllFiles();
        }else{
            super.onBackPressed();
        }
    }
}
