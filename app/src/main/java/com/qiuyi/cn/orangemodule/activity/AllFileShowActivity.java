package com.qiuyi.cn.orangemodule.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.provider.DocumentFile;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qiuyi.cn.myloadingdialog.LoadingDialog;
import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.adapter.SDFileAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.FileUtils;
import com.qiuyi.cn.orangemodule.util.FileUtilOpen;
import com.qiuyi.cn.orangemodule.util.WriteToUdisk;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Administrator on 2018/3/27.
 * 展示所有文件
 */
public class AllFileShowActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener{

    private TextView tv_fload,bt_selectAll;
    private RecyclerView rl_fileshow;
    private SwipeRefreshLayout allFile_sl;

    private ImageView bt_search;

    //底部功能框
    private LinearLayout ll_pager_native_bom;
    private TextView tv_delete,tv_copy,tv_cancel;

    //手机根路径
    private String rootPath;
    private String currentPath;

    //获取文件
    private List<File> fileList;
    private SDFileAdapter sdfileAdapter;
    private GridLayoutManager myGridManager;

    //判断是否全选
    private boolean isSelectAll = true;

    //复制到U盘
    private LoadingDialog dialog;
    private WriteToUdisk udiskUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allfileshow);

        dialog = new LoadingDialog.Builder(this)
                .setMessage("复制中")
                .setCancelable(false)
                .setCancelOutside(false)
                .create();
        udiskUtil =  WriteToUdisk.getInstance(this.getApplicationContext(),this);

        initView();

        initData();
    }

    //初始化数据
    private void initData() {

        if(FileUtils.isSDCardAvailable()){
            //SD卡存在
            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            Intent intent = getIntent();
            String searchPath = null;
            searchPath = intent.getStringExtra("filepath");
            if(searchPath!=null){
                //是从查询页面过来的
                currentPath = searchPath;
            }else{
                currentPath = rootPath;
            }
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

        /*if(sdfileAdapter == null){*/
            sdfileAdapter = new SDFileAdapter(this,fileList);
            rl_fileshow.setLayoutManager(myGridManager);
            rl_fileshow.setAdapter(sdfileAdapter);

            sdfileAdapter.setOnItemClick(new SDFileAdapter.SD_OnItemClick() {
                @Override
                public void onItemClick(View view, int position) {
                    File file = fileList.get(position);

                    boolean isShowBox = sdfileAdapter.isShowCheckBox();

                    //处于可以选择的状态
                    if(isShowBox){

                        //正在显示checkbox
                        boolean[] flag = sdfileAdapter.getFlag();

                        flag[position] = !flag[position];

                        sdfileAdapter.setFlag(flag);
                        sdfileAdapter.notifyDataSetChanged();
                    }else{
                        if(file.isDirectory()){
                            currentPath += "/"+file.getName();
                            tv_fload.setText(currentPath);
                            readFileList(file);
                        }else{
                            FileUtilOpen.openFileByPath(getApplicationContext(),file.getPath());
                        }
                    }

                }
                @Override
                public void onItemLongClick(View view, int position) {

                    //全选按钮显示
                    bt_selectAll.setVisibility(View.VISIBLE);
                    //底部导航栏按钮显示
                    ll_pager_native_bom.setVisibility(View.VISIBLE);

                    //全选按钮
                    bt_selectAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(isSelectAll){
                                sdfileAdapter.selectAll();
                                bt_selectAll.setText("取消全选");
                                isSelectAll = false;
                            }else{
                                sdfileAdapter.noSelect();
                                bt_selectAll.setText("全选");
                                isSelectAll = true;
                            }
                            sdfileAdapter.notifyDataSetChanged();
                        }
                    });

                    //取消按钮
                    tv_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bt_selectAll.setVisibility(View.GONE);
                            ll_pager_native_bom.setVisibility(View.GONE);

                            sdfileAdapter.setShowCheckBox(false);
                            sdfileAdapter.ReFresh();
                        }
                    });

                    //删除按钮
                    tv_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //选择，显示一下有哪几个选择了
                            boolean[] flag = sdfileAdapter.getFlag();
                            for(int i = flag.length-1;i>=0;i--){
                                if(flag[i]){
                                    Log.e("select", "选中："+i);
                                    fileList.remove(i);
                                }
                            }
                            sdfileAdapter.ReFresh();
                        }
                    });

                    //U盘存在
                    if(MainActivity.isHaveUpan){
                        //复制到U盘按钮
                        tv_copy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        boolean[] flag = sdfileAdapter.getFlag();
                                        for(int i = flag.length-1;i>=0;i--){
                                            if(flag[i]){
                                                Log.e("select", "选中："+i);
                                                writeToUDick(fileList.get(i),udiskUtil.getCurrentFolder());
                                            }
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                //头部状态栏显示
                                                bt_selectAll.setVisibility(View.GONE);
                                                //底部状态栏显示
                                                ll_pager_native_bom.setVisibility(View.GONE);

                                                sdfileAdapter.setShowCheckBox(false);
                                                sdfileAdapter.ReFresh();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        });
                    }

                    sdfileAdapter.setShowCheckBox(true);
                    boolean flag[] = sdfileAdapter.getFlag();
                    flag[position] = true;

                    sdfileAdapter.setFlag(flag);
                    sdfileAdapter.notifyDataSetChanged();
                }
            });

            tv_fload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!currentPath.equals(rootPath)) {
                        if(sdfileAdapter.isShowCheckBox()){
                            bt_selectAll.setVisibility(View.GONE);
                            ll_pager_native_bom.setVisibility(View.GONE);

                            sdfileAdapter.setShowCheckBox(false);
                            sdfileAdapter.ReFresh();
                        }
                        currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
                        tv_fload.setText(currentPath);
                        readFileList(new File(currentPath));
                    }
                }
            });

/*        }else{
            sdfileAdapter.notifyDataSetChanged();
        }*/

    }

    //将选中的文件写入U盘
    private void writeToUDick(File file,DocumentFile documentFile) {
        if(file.isDirectory()){
            DocumentFile dirFile = documentFile.createDirectory(file.getName());
            for(File newFile:file.listFiles()){
                writeToUDick(newFile,dirFile);
            }
        }else{
            udiskUtil.writeToSDFile(this,file,documentFile);
        }
    }


    //初始化界面
    private void initView() {

        bt_search = findViewById(R.id.img_search);

        //目录
        tv_fload = findViewById(R.id.tv_floader);
        //全选按钮
        bt_selectAll = findViewById(R.id.bt_selectAll);
        //展示列表
        rl_fileshow = findViewById(R.id.allFile_rl);
        allFile_sl = findViewById(R.id.allFile_sl);
        //底部导航栏
        ll_pager_native_bom = findViewById(R.id.ll_pager_native_bom);
        tv_delete = findViewById(R.id.tv_delete);
        tv_copy = findViewById(R.id.tv_copy);
        tv_cancel = findViewById(R.id.tv_cancel);


        allFile_sl.setColorSchemeColors(Color.RED);
        allFile_sl.setOnRefreshListener(this);

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //去到搜索界面,来自U盘
                Intent intent = new Intent(new Intent(AllFileShowActivity.this,SearchActivity.class));
                intent.putExtra("main",true);
                startActivity(intent);
            }
        });

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

    @Override
    public void onRefresh() {
        allFile_sl.setRefreshing(true);
        readFileList(new File(currentPath));

        bt_selectAll.setVisibility(View.GONE);
        ll_pager_native_bom.setVisibility(View.GONE);

        sdfileAdapter.setShowCheckBox(false);
        sdfileAdapter.ReFresh();

        allFile_sl.setRefreshing(false);
    }
}
