package com.qiuyi.cn.orangemodule.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiuyi.cn.myloadingdialog.LoadingDialog;
import com.qiuyi.cn.orangemodule.MainActivity;
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
import com.qiuyi.cn.orangemodule.util.WriteToUdisk;

import java.io.File;
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

    //全选按钮
    private TextView bt_selectAll;
    //底部导航模块
    private LinearLayout ll_pager_native_bom;
    //删除，拷贝到U盘，取消
    private TextView tv_delete,tv_copy,tv_cancel;

    private boolean isSelectAll = true;


    private List<MusicBean> listMusics;//音乐
    private List<VideoBean> listVideos;//视频
    private List<ImageBean> listImages;//图片
    private List<FileBean> listFiles;//文件
    private List<FileBean> listFileZars;//压缩包

    private FileAdapter fileAdapter;
    private ImageAdapter imgAdapter;
    private MusicAdapter mucAdapter;
    private VideoAdapter videoAdapter;

    private LoadingDialog dialog;
    private WriteToUdisk udiskUtil;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileshow);

        dialog = new LoadingDialog.Builder(this)
                .setMessage("复制中")
                .setCancelable(false)
                .setCancelOutside(false)
                .create();
        udiskUtil = WriteToUdisk.getInstance(this.getApplicationContext(),this);

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
        switch (intent.getIntExtra("type",0)){
            case 0:
                listImages = MainActivity.listImages;
                imgAdapter = new ImageAdapter(this,listImages,myGridManager);
                myFileShow.setAdapter(imgAdapter);

                imgAdapter.setOnImageItemClick(new ImageAdapter.ImageItemClick() {
                    @Override
                    public void openImage(View view, int position, List<ImageBean> allImageBean) {
                        ImageBean imageBean = allImageBean.get(position);

                        boolean isShowBox = imgAdapter.isShowCheckBox();

                        if(isShowBox){

                            if(imageBean.getFiletype()!=3&& imageBean.getFiletype()!=0){
                                //正在显示checkbox
                                boolean[] flag = imgAdapter.getFlag();

                                flag[position] = !flag[position];

                                //如果当前position是选中状态，那么就从这个点去找开始和结束点
                                isToSelectImgAll(position,allImageBean,flag);

                                imgAdapter.setFlag(flag);
                                imgAdapter.notifyDataSetChanged();
                            }
                        }else{
                            if(imageBean.getFiletype()==1){
                                FileUtilOpen.openFileByPath(getApplicationContext(),imageBean.getPath());
                            }
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position, final List<ImageBean> allFileBean) {

                        ImageBean imgBean = allFileBean.get(position);

                        //按得不是间隔处
                        if(imgBean.getFiletype()!=3){
                            //头部状态栏显示
                            bt_selectAll.setVisibility(View.VISIBLE);
                            //底部状态栏显示
                            ll_pager_native_bom.setVisibility(View.VISIBLE);

                            bt_selectAll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(isSelectAll){
                                        imgAdapter.selectAll();
                                        isSelectAll = false;
                                        bt_selectAll.setText("取消全选");
                                    }else{
                                        imgAdapter.noSelect();
                                        isSelectAll = true;
                                        bt_selectAll.setText("全选");
                                    }
                                    imgAdapter.notifyDataSetChanged();
                                }
                            });
                            tv_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    //头部状态栏显示
                                    bt_selectAll.setVisibility(View.GONE);
                                    //底部状态栏显示
                                    ll_pager_native_bom.setVisibility(View.GONE);

                                    imgAdapter.setShowCheckBox(false);
                                    imgAdapter.ReFresh();

                                }
                            });

                            tv_delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //选择，显示一下有哪几个选择了
                                    boolean[] flag = imgAdapter.getFlag();
                                    for(int i = flag.length-1;i>=0;i--){
                                        if(flag[i]){
                                            Log.e("select", "选中："+i);
                                            allFileBean.remove(i);
                                        }
                                    }
                                    imgAdapter.ReFresh();
                                }
                            });

                            //有U盘存在
                            if(MainActivity.isHaveUpan){
                                tv_copy.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //选择，显示一下有哪几个选择了
                                        dialog.show();

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                boolean[] flag = imgAdapter.getFlag();
                                                for(int i = flag.length-1;i>=0;i--){
                                                    if(flag[i]){
                                                        Log.e("select", "选中："+i);
                                                        if(allFileBean.get(i).getFiletype()!=0 && allFileBean.get(i).getFiletype()!=3)
                                                        {
                                                            File file = new File(allFileBean.get(i).getPath());
                                                            udiskUtil.writeToSDFile(getApplicationContext(),file,udiskUtil.getCurrentFolder());
                                                        }
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

                                                        imgAdapter.setShowCheckBox(false);
                                                        imgAdapter.ReFresh();
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                });
                            }


                            imgAdapter.setShowCheckBox(true);
                            boolean[] flag = imgAdapter.getFlag();

                            if(imgBean.getFiletype()==0){
                                selectAll(allFileBean,position,flag);
                            }else{
                                flag[position] = true;
                                isToSelectImgAll(position,allFileBean,flag);
                            }

                            imgAdapter.setFlag(flag);

                            imgAdapter.notifyDataSetChanged();
                        }
                    }
                });

                break;
            case 1:
                listVideos = MainActivity.listVideos;
                videoAdapter = new VideoAdapter(this,listVideos,myGridManager);
                myFileShow.setAdapter(videoAdapter);

                videoAdapter.setOnVideoItemClick(new VideoAdapter.VideoItemClick() {
                    @Override
                    public void openVideo(View view, int position, List<VideoBean> allVideoBean) {
                        VideoBean videoBean = allVideoBean.get(position);

                        boolean isShowBox = videoAdapter.isShowCheckBox();

                        if(isShowBox){

                            if(videoBean.getFiletype()!=3&& videoBean.getFiletype()!=0){
                                //正在显示checkbox
                                boolean[] flag = videoAdapter.getFlag();

                                flag[position] = !flag[position];

                                //如果当前position是选中状态，那么就从这个点去找开始和结束点
                                isToSelectVideoAll(position,allVideoBean,flag);

                                videoAdapter.setFlag(flag);
                                videoAdapter.notifyDataSetChanged();
                            }
                        }else{
                            if(videoBean.getFiletype()==2){
                                FileUtilOpen.openFileByPath(getApplicationContext(),videoBean.getPath());
                            }
                        }

                    }

                    @Override
                    public void onLongClick(View view, int position, final List<VideoBean> allVideoBean) {
                        VideoBean videoBean = allVideoBean.get(position);

                        //按得不是间隔处
                        if(videoBean.getFiletype()!=3){
                            //头部状态栏显示
                            bt_selectAll.setVisibility(View.VISIBLE);
                            //底部状态栏显示
                            ll_pager_native_bom.setVisibility(View.VISIBLE);

                            bt_selectAll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(isSelectAll){
                                        videoAdapter.selectAll();
                                        isSelectAll = false;
                                        bt_selectAll.setText("取消全选");
                                    }else{
                                        videoAdapter.noSelect();
                                        isSelectAll = true;
                                        bt_selectAll.setText("全选");
                                    }
                                    videoAdapter.notifyDataSetChanged();
                                }
                            });
                            tv_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    //头部状态栏显示
                                    bt_selectAll.setVisibility(View.GONE);
                                    //底部状态栏显示
                                    ll_pager_native_bom.setVisibility(View.GONE);

                                    videoAdapter.setShowCheckBox(false);
                                    videoAdapter.ReFresh();

                                }
                            });

                            tv_delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //选择，显示一下有哪几个选择了
                                    boolean[] flag = videoAdapter.getFlag();
                                    for(int i = flag.length-1;i>=0;i--){
                                        if(flag[i]){
                                            Log.e("select", "选中："+i);
                                            allVideoBean.remove(i);
                                        }
                                    }
                                    videoAdapter.ReFresh();
                                }
                            });

                            //有U盘存在
                            if(MainActivity.isHaveUpan){
                                tv_copy.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //选择，显示一下有哪几个选择了
                                        dialog.show();

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                boolean[] flag = videoAdapter.getFlag();
                                                for(int i = flag.length-1;i>=0;i--){
                                                    if(flag[i]){
                                                        Log.e("select", "选中："+i);
                                                        if(allVideoBean.get(i).getFiletype()!=0 && allVideoBean.get(i).getFiletype()!=3)
                                                        {
                                                            File file = new File(allVideoBean.get(i).getPath());
                                                            udiskUtil.writeToSDFile(getApplicationContext(),file,udiskUtil.getCurrentFolder());
                                                        }
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

                                                        videoAdapter.setShowCheckBox(false);
                                                        videoAdapter.ReFresh();
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                });
                            }


                            videoAdapter.setShowCheckBox(true);
                            boolean[] flag = videoAdapter.getFlag();

                            if(videoBean.getFiletype()==0){
                                selectVideoAll(allVideoBean,position,flag);
                            }else{
                                flag[position] = true;
                                isToSelectVideoAll(position,allVideoBean,flag);
                            }

                            videoAdapter.setFlag(flag);

                            videoAdapter.notifyDataSetChanged();
                        }
                    }
                });
                break;
            case 2:
                listFiles = MainActivity.listFiles;
                fileAdapter = new FileAdapter(this,listFiles,myGridManager);
                myFileShow.setAdapter(fileAdapter);

                fileAdapter.setOnFileItemClick(new FileAdapter.FileItemClick() {
                    @Override
                    public void openFile(View view, int position, List<FileBean> allFileBean) {
                        FileBean fileBean = allFileBean.get(position);

                        boolean isShowBox = fileAdapter.isShowCheckBox();

                        if(isShowBox){

                            if(fileBean.getFiletype()!=3&& fileBean.getFiletype()!=0){
                                //正在显示checkbox
                                boolean[] flag = fileAdapter.getFlag();

                                flag[position] = !flag[position];

                                //如果当前position是选中状态，那么就从这个点去找开始和结束点
                                isToSelectFileAll(position,allFileBean,flag);

                                fileAdapter.setFlag(flag);
                                fileAdapter.notifyDataSetChanged();
                            }
                        }else{
                            if(fileBean.getFiletype()==2){
                                FileUtilOpen.openFileByPath(getApplicationContext(),fileBean.getPath());
                            }
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position, final List<FileBean> allFileBean) {
                        FileBean fileBean = allFileBean.get(position);

                        //按得不是间隔处
                        if(fileBean.getFiletype()!=3){
                            //头部状态栏显示
                            bt_selectAll.setVisibility(View.VISIBLE);
                            //底部状态栏显示
                            ll_pager_native_bom.setVisibility(View.VISIBLE);

                            bt_selectAll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(isSelectAll){
                                        fileAdapter.selectAll();
                                        isSelectAll = false;
                                        bt_selectAll.setText("取消全选");
                                    }else{
                                        fileAdapter.noSelect();
                                        isSelectAll = true;
                                        bt_selectAll.setText("全选");
                                    }
                                    fileAdapter.notifyDataSetChanged();
                                }
                            });
                            tv_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    //头部状态栏显示
                                    bt_selectAll.setVisibility(View.GONE);
                                    //底部状态栏显示
                                    ll_pager_native_bom.setVisibility(View.GONE);

                                    fileAdapter.setShowCheckBox(false);
                                    fileAdapter.ReFresh();

                                }
                            });

                            tv_delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //选择，显示一下有哪几个选择了
                                    boolean[] flag = fileAdapter.getFlag();
                                    for(int i = flag.length-1;i>=0;i--){
                                        if(flag[i]){
                                            Log.e("select", "选中："+i);
                                            allFileBean.remove(i);
                                        }
                                    }
                                    fileAdapter.ReFresh();
                                }
                            });

                            //有U盘存在
                            if(MainActivity.isHaveUpan){
                                tv_copy.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //选择，显示一下有哪几个选择了
                                        dialog.show();

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                boolean[] flag = fileAdapter.getFlag();
                                                for(int i = flag.length-1;i>=0;i--){
                                                    if(flag[i]){
                                                        Log.e("select", "选中："+i);
                                                        if(allFileBean.get(i).getFiletype()!=0 && allFileBean.get(i).getFiletype()!=3)
                                                        {
                                                            File file = new File(allFileBean.get(i).getPath());
                                                            udiskUtil.writeToSDFile(getApplicationContext(),file,udiskUtil.getCurrentFolder());
                                                        }
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

                                                        fileAdapter.setShowCheckBox(false);
                                                        fileAdapter.ReFresh();
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                });
                            }


                            fileAdapter.setShowCheckBox(true);
                            boolean[] flag = fileAdapter.getFlag();

                            if(fileBean.getFiletype()==0){
                                selectFileAll(allFileBean,position,flag);
                            }else{
                                flag[position] = true;
                                isToSelectFileAll(position,allFileBean,flag);
                            }

                            fileAdapter.setFlag(flag);

                            fileAdapter.notifyDataSetChanged();
                        }
                    }
                });
                break;
            case 3:
                listMusics = MainActivity.listMusics;
                mucAdapter = new MusicAdapter(this,listMusics,myGridManager);
                myFileShow.setAdapter(mucAdapter);

                mucAdapter.setOnMusicItemClick(new MusicAdapter.MusicItemClick() {
                    @Override
                    public void openMusic(View view, int position, List<MusicBean> allMusicBean) {
                        MusicBean musicBean = allMusicBean.get(position);

                        boolean isShowBox = mucAdapter.isShowCheckBox();

                        if(isShowBox){

                            if(musicBean.getFiletype()!=3&& musicBean.getFiletype()!=0){
                                //正在显示checkbox
                                boolean[] flag = mucAdapter.getFlag();

                                flag[position] = !flag[position];

                                //如果当前position是选中状态，那么就从这个点去找开始和结束点
                                isToSelectMusicAll(position,allMusicBean,flag);

                                mucAdapter.setFlag(flag);
                                mucAdapter.notifyDataSetChanged();
                            }
                        }else{
                            if(musicBean.getFiletype()==2){
                                FileUtilOpen.openFileByPath(getApplicationContext(),musicBean.getPath());
                            }
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position, final List<MusicBean> allMusicBean) {
                        MusicBean musicBean = allMusicBean.get(position);

                        //按得不是间隔处
                        if(musicBean.getFiletype()!=3){
                            //头部状态栏显示
                            bt_selectAll.setVisibility(View.VISIBLE);
                            //底部状态栏显示
                            ll_pager_native_bom.setVisibility(View.VISIBLE);

                            bt_selectAll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(isSelectAll){
                                        mucAdapter.selectAll();
                                        isSelectAll = false;
                                        bt_selectAll.setText("取消全选");
                                    }else{
                                        mucAdapter.noSelect();
                                        isSelectAll = true;
                                        bt_selectAll.setText("全选");
                                    }
                                    mucAdapter.notifyDataSetChanged();
                                }
                            });
                            tv_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    //头部状态栏显示
                                    bt_selectAll.setVisibility(View.GONE);
                                    //底部状态栏显示
                                    ll_pager_native_bom.setVisibility(View.GONE);

                                    mucAdapter.setShowCheckBox(false);
                                    mucAdapter.ReFresh();

                                }
                            });

                            tv_delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //选择，显示一下有哪几个选择了
                                    boolean[] flag = mucAdapter.getFlag();
                                    for(int i = flag.length-1;i>=0;i--){
                                        if(flag[i]){
                                            Log.e("select", "选中："+i);
                                            allMusicBean.remove(i);
                                        }
                                    }
                                    mucAdapter.ReFresh();
                                }
                            });

                            //有U盘存在
                            if(MainActivity.isHaveUpan){
                                tv_copy.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //选择，显示一下有哪几个选择了
                                        dialog.show();

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                boolean[] flag = mucAdapter.getFlag();
                                                for(int i = flag.length-1;i>=0;i--){
                                                    if(flag[i]){
                                                        Log.e("select", "选中："+i);
                                                        if(allMusicBean.get(i).getFiletype()!=0 && allMusicBean.get(i).getFiletype()!=3)
                                                        {
                                                            File file = new File(allMusicBean.get(i).getPath());
                                                            udiskUtil.writeToSDFile(getApplicationContext(),file,udiskUtil.getCurrentFolder());
                                                        }
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

                                                        mucAdapter.setShowCheckBox(false);
                                                        mucAdapter.ReFresh();
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                });
                            }


                            mucAdapter.setShowCheckBox(true);
                            boolean[] flag = mucAdapter.getFlag();

                            if(musicBean.getFiletype()==0){
                                selectMusicAll(allMusicBean,position,flag);
                            }else{
                                flag[position] = true;
                                isToSelectMusicAll(position,allMusicBean,flag);
                            }

                            mucAdapter.setFlag(flag);

                            mucAdapter.notifyDataSetChanged();
                        }
                    }
                });
                break;
            case 4:
                listFileZars = MainActivity.listFileZars;
                fileAdapter = new FileAdapter(this,listFileZars,myGridManager);
                myFileShow.setAdapter(fileAdapter);

                fileAdapter.setOnFileItemClick(new FileAdapter.FileItemClick() {
                    @Override
                    public void openFile(View view, int position, List<FileBean> allFileBean) {
                        FileBean fileBean = allFileBean.get(position);

                        boolean isShowBox = fileAdapter.isShowCheckBox();

                        if(isShowBox){

                            if(fileBean.getFiletype()!=3&& fileBean.getFiletype()!=0){
                                //正在显示checkbox
                                boolean[] flag = fileAdapter.getFlag();

                                flag[position] = !flag[position];

                                //如果当前position是选中状态，那么就从这个点去找开始和结束点
                                isToSelectFileAll(position,allFileBean,flag);


                                fileAdapter.setFlag(flag);
                                fileAdapter.notifyDataSetChanged();
                            }
                        }else{
                            if(fileBean.getFiletype()==2){
                                FileUtilOpen.openFileByPath(getApplicationContext(),fileBean.getPath());
                            }
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position, final List<FileBean> allFileBean) {
                        FileBean fileBean = allFileBean.get(position);

                        //按得不是间隔处
                        if(fileBean.getFiletype()!=3){
                            //头部状态栏显示
                            bt_selectAll.setVisibility(View.VISIBLE);
                            //底部状态栏显示
                            ll_pager_native_bom.setVisibility(View.VISIBLE);

                            bt_selectAll.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(isSelectAll){
                                        fileAdapter.selectAll();
                                        isSelectAll = false;
                                        bt_selectAll.setText("取消全选");
                                    }else{
                                        fileAdapter.noSelect();
                                        isSelectAll = true;
                                        bt_selectAll.setText("全选");
                                    }
                                    fileAdapter.notifyDataSetChanged();
                                }
                            });
                            tv_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    //头部状态栏显示
                                    bt_selectAll.setVisibility(View.GONE);
                                    //底部状态栏显示
                                    ll_pager_native_bom.setVisibility(View.GONE);

                                    fileAdapter.setShowCheckBox(false);
                                    fileAdapter.ReFresh();

                                }
                            });

                            tv_delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //选择，显示一下有哪几个选择了
                                    boolean[] flag = fileAdapter.getFlag();
                                    for(int i = flag.length-1;i>=0;i--){
                                        if(flag[i]){
                                            Log.e("select", "选中："+i);
                                            allFileBean.remove(i);
                                        }
                                    }
                                    fileAdapter.ReFresh();
                                }
                            });

                            //有U盘存在
                            if(MainActivity.isHaveUpan){
                                tv_copy.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //选择，显示一下有哪几个选择了
                                        dialog.show();

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                boolean[] flag = fileAdapter.getFlag();
                                                for(int i = flag.length-1;i>=0;i--){
                                                    if(flag[i]){
                                                        Log.e("select", "选中："+i);
                                                        if(allFileBean.get(i).getFiletype()!=0 && allFileBean.get(i).getFiletype()!=3)
                                                        {
                                                            File file = new File(allFileBean.get(i).getPath());
                                                            udiskUtil.writeToSDFile(getApplicationContext(),file,udiskUtil.getCurrentFolder());
                                                        }
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

                                                        fileAdapter.setShowCheckBox(false);
                                                        fileAdapter.ReFresh();
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                });
                            }


                            fileAdapter.setShowCheckBox(true);
                            boolean[] flag = fileAdapter.getFlag();

                            if(fileBean.getFiletype()==0){
                                selectFileAll(allFileBean,position,flag);
                            }else{
                                flag[position] = true;
                                isToSelectFileAll(position,allFileBean,flag);
                            }

                            fileAdapter.setFlag(flag);

                            fileAdapter.notifyDataSetChanged();
                        }
                    }
                });
                break;
            default:
                break;
        }
    }


    //Music是否全部选中
    private void isToSelectImgAll(int position, List<ImageBean> allImageBean, boolean[] flag) {
        int rooti = position-1;
        int rootj = position+1;
        while(allImageBean.get(rooti).getFiletype()!=0){
            rooti--;
        }
        while (allImageBean.get(rootj).getFiletype()!=3){
            rootj++;
        }

        flag[rooti] = true;
        flag[rootj] = true;

        for (int i=rooti+1;i<rootj;i++){
            if(!flag[i]){
                flag[rooti] = false;
                flag[rootj] = false;
                break;
            }
        }
    }

    //Video是否全部选中
    private void isToSelectVideoAll(int position, List<VideoBean> allVideoBean, boolean[] flag) {
        int rooti = position-1;
        int rootj = position+1;
        while(allVideoBean.get(rooti).getFiletype()!=0){
            rooti--;
        }
        while (allVideoBean.get(rootj).getFiletype()!=3){
            rootj++;
        }

        flag[rooti] = true;
        flag[rootj] = true;

        for (int i=rooti+1;i<rootj;i++){
            if(!flag[i]){
                flag[rooti] = false;
                flag[rootj] = false;
                break;
            }
        }
    }

    //File是否全部选中
    private void isToSelectFileAll(int position, List<FileBean> allFileBean, boolean[] flag) {
        int rooti = position-1;
        int rootj = position+1;
        while(allFileBean.get(rooti).getFiletype()!=0){
            rooti--;
        }
        while (allFileBean.get(rootj).getFiletype()!=3){
            rootj++;
        }

        flag[rooti] = true;
        flag[rootj] = true;

        for (int i=rooti+1;i<rootj;i++){
            if(!flag[i]){
                flag[rooti] = false;
                flag[rootj] = false;
                break;
            }
        }
    }

    //Music是否全部选中
    private void isToSelectMusicAll(int position, List<MusicBean> allMusicBean, boolean[] flag) {
        int rooti = position-1;
        int rootj = position+1;
        while(allMusicBean.get(rooti).getFiletype()!=0){
            rooti--;
        }
        while (allMusicBean.get(rootj).getFiletype()!=3){
            rootj++;
        }

        flag[rooti] = true;
        flag[rootj] = true;

        for (int i=rooti+1;i<rootj;i++){
            if(!flag[i]){
                flag[rooti] = false;
                flag[rootj] = false;
                break;
            }
        }
    }


    //选中的是title标题栏，下面需要全部选中
    private void selectAll(List<ImageBean> allFileBean,int position,boolean[] flag){
        for(int i=position;i<allFileBean.size();i++){
            ImageBean allFile = allFileBean.get(i);
            if(allFile.getFiletype()==3){
                flag[i] = !flag[i];
                break;
            }
            flag[i] = !flag[i];
        }
    }

    //选中的是title标题栏，下面需要全部选中
    private void selectVideoAll(List<VideoBean> allFileBean,int position,boolean[] flag){
        for(int i=position;i<allFileBean.size();i++){
            VideoBean allFile = allFileBean.get(i);
            if(allFile.getFiletype()==3){
                flag[i] = !flag[i];
                break;
            }
            flag[i] = !flag[i];
        }
    }

    //选中的是title标题栏，下面需要全部选中
    private void selectMusicAll(List<MusicBean> allFileBean,int position,boolean[] flag){
        for(int i=position;i<allFileBean.size();i++){
            MusicBean allFile = allFileBean.get(i);
            if(allFile.getFiletype()==3){
                flag[i] = !flag[i];
                break;
            }
            flag[i] = !flag[i];
        }
    }

    //选中的是title标题栏，下面需要全部选中
    private void selectFileAll(List<FileBean> allFileBean,int position,boolean[] flag){
        for(int i=position;i<allFileBean.size();i++){
            FileBean allFile = allFileBean.get(i);
            if(allFile.getFiletype()==3){
                flag[i] = !flag[i];
                break;
            }
            flag[i] = !flag[i];
        }
    }

    /**
     * 初始化界面
     */
    private void initView() {
        myFileShow = findViewById(R.id.fileshow_rl);

        bt_selectAll = findViewById(R.id.bt_selectAll);
        ll_pager_native_bom = findViewById(R.id.ll_pager_native_bom);
        tv_delete = findViewById(R.id.tv_delete);
        tv_copy = findViewById(R.id.tv_copy);
        tv_cancel = findViewById(R.id.tv_cancel);

    }

}
