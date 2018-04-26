package com.qiuyi.cn.orangemodule.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.provider.DocumentFile;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.qiuyi.cn.myloadingdialog.LoadingDialog;
import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.activity.BkrtActivity;
import com.qiuyi.cn.orangemodule.activity.FileShowActivity;
import com.qiuyi.cn.orangemodule.adapter.BackUp_fm_adapter;
import com.qiuyi.cn.orangemodule.bean.FileType;
import com.qiuyi.cn.orangemodule.bean.LogOut;
import com.qiuyi.cn.orangemodule.bean.MyItemFile;
import com.qiuyi.cn.orangemodule.util.Constant;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.FileBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.ImageBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.MusicBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.VideoBean;
import com.qiuyi.cn.orangemodule.util.FileManager.contacts.PhoneInfo;
import com.qiuyi.cn.orangemodule.util.FileManager.permission.ConstantPermission;
import com.qiuyi.cn.orangemodule.util.FileManager.permission.GetPermission;
import com.qiuyi.cn.orangemodule.util.FileManager.permission.PermissionUtil;
import com.qiuyi.cn.orangemodule.util.FileManager.service.FindContacts;
import com.qiuyi.cn.orangemodule.util.WriteToUdisk;

import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/3/18.
 * 备份页面
 */
public class BackUpFragment extends Fragment implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{

    public Activity mActivity;

    private LoadingDialog dialog;

    private ExecutorService executorService;

    //SwipeRefreshLayout
    private SwipeRefreshLayout swipeRefreshLayout;
    //recyclerView
    private RecyclerView rl_backup;
    //备份
    private Button bt_backup;
    //上次备份时间
    private TextView tv_prebackup;
    //适配器
    private BackUp_fm_adapter myAdapter;
    //LinnerLayoutManager
    private LinearLayoutManager myManager;

    //手机联系人获取
    private PhoneInfo phoneInfo;

    private List<MusicBean> listMusics;//音乐
    private List<VideoBean> listVideos;//视频
    private List<ImageBean> listImages;//图片
    private List<FileBean> listFiles;//文件
    private List<Long> sizes;//存储文件大小

    private JSONObject contacts = null;//联系人

    //FileItem
    private List<MyItemFile> listSelect;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActivity = getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_backup,null);
        rl_backup = view.findViewById(R.id.rl_backup);
        swipeRefreshLayout = view.findViewById(R.id.swip_backup);

        bt_backup = view.findViewById(R.id.bt_backup);
        tv_prebackup = view.findViewById(R.id.tv_prebackup);

        swipeRefreshLayout.setColorSchemeColors(Color.RED);

        dialog = new LoadingDialog.Builder(mActivity)
                .setMessage("备份中...")
                .setCancelable(false)
                .setCancelOutside(false)
                .create();

        executorService = Executors.newCachedThreadPool();

        return view;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(MainActivity.constacts==null){
            mActivity.startService(new Intent(mActivity,FindContacts.class));
        }

        initBroadCast();
        initData();
    }

    private void initBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BACKUP);
        filter.addAction(Constant.FINDFILE_MSG);
        mActivity.registerReceiver(myContactsReceiver,filter);
    }

    private BroadcastReceiver myContactsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                //联系人查询完毕
                case Constant.BACKUP:
                    if(intent.getBooleanExtra("isOK",false)){
                        initData();
                    }
                    break;
                case Constant.FINDFILE_MSG:
                    if(intent.getBooleanExtra("findOk",false)){
                        initData();
                    }
                    break;
                default:
                    break;
            }

        }
    };

    /**
     * 数据初始化
     */
    private void initData() {
        //初始化数据文件
        addData();

        //添加数据
        addFileTypeData();

        myManager = new LinearLayoutManager(mActivity);

        rl_backup.setLayoutManager(myManager);
        myAdapter = new BackUp_fm_adapter(mActivity,listSelect);
        rl_backup.setAdapter(myAdapter);

        swipeRefreshLayout.setRefreshing(false);

        initListener();
    }

    //添加数据
    private void addData() {

        sizes = new ArrayList<>();

        listMusics = MainActivity.listMusics;
        listImages = MainActivity.listImages;
        listFiles = MainActivity.listFiles;
        listVideos = MainActivity.listVideos;
        contacts = MainActivity.constacts;

        if(listMusics.size()<=0||listImages.size()<=0||listFiles.size()<=0
                ||listVideos.size()<=0||contacts==null){
            Toast.makeText(mActivity,"数据正在加载中", Toast.LENGTH_SHORT).show();
        }

        Long sizeImages = Long.valueOf(0);
        if(listImages!=null && listImages.size()>0){
            for(ImageBean image:listImages){
                sizeImages += image.getSize();
            }
        }
        sizes.add(sizeImages);

        Long sizeVideo = Long.valueOf(0);
        if(listVideos!=null && listVideos.size()>0){
            for(VideoBean video:listVideos){
                sizeVideo += video.getSize();
            }
        }
        sizes.add(sizeVideo);

        Long sizeFiles = Long.valueOf(0);
        if(listFiles!=null && listFiles.size()>0){
            for(FileBean file:listFiles){
                sizeFiles += file.getSize();
            }
        }
        sizes.add(sizeFiles);

        Long sizeMusic = Long.valueOf(0);
        if(listMusics!=null && listMusics.size()>0){
            for(MusicBean music:listMusics){
                sizeMusic += music.getSize();
            }
        }
        sizes.add(sizeMusic);
    }

    //监听事件
    private void initListener() {

        bt_backup.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        myAdapter.setOnBackUpClickListener(new BackUp_fm_adapter.BackupOnClick() {
            @Override
            public void onBackItemClick(View view, int position) {

                MyItemFile fileType = listSelect.get(position);

                Intent intent = new Intent(mActivity, FileShowActivity.class);
                if (position == 0) {
                    intent.putExtra("type", 0);
                    intent.putExtra("listFile", (Serializable) listImages);
                } else if (position == 1) {
                    intent.putExtra("type", 1);
                    intent.putExtra("listFile", (Serializable) listVideos);
                } else if (position == 2) {
                    intent.putExtra("type",2);
                    intent.putExtra("listFile", (Serializable) listFiles);
                } else if (position == 3) {
                    intent.putExtra("type", 3);
                    intent.putExtra("listFile", (Serializable) listMusics);
                } else {
                    intent = null;
                }
                if (intent != null) {
                    mActivity.startActivity(intent);
                }
            }

            @Override
            public void onBackLongItemClick(View view, int position) {
/*                myAdapter.setShowCheckBox(true);
                myAdapter.notifyDataSetChanged();*/
            }
        });
    }

    //添加数据
    private void addFileTypeData() {

        listSelect = new ArrayList<>();

        for(int i=0;i<4;i++){
            MyItemFile itemFile = new MyItemFile(Constant.BACKUPMSG[i],R.drawable.folder,"文件大小："+Formatter.formatFileSize(mActivity,sizes.get(i)));
            listSelect.add(itemFile);
        }

        for(int i=4;i<5;i++){
            int length;
            if(contacts==null){
                length = 0;
            }else {
                length = contacts.length();
            }

            MyItemFile itemFile = new MyItemFile(Constant.BACKUPMSG[i],R.drawable.folder,"联系人："+length);
            listSelect.add(itemFile);
        }

    }

    //备份按钮的点击
    @Override
    public void onClick(View view) {

        dialog.show();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                startWrite();
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }
        });


    }

    //开始写入U盘
    private void startWrite() {
        List<Callable<Boolean>> partions = new ArrayList<>();

        final WriteToUdisk udisk = WriteToUdisk.getInstance(mActivity.getApplicationContext(),mActivity);
        //获取U盘根目录
        final DocumentFile rootFile = udisk.getCurrentFolder();
        if(rootFile!=null){

            partions.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
/*                    DocumentFile dirFile = udisk.findUFile(rootFile,"照片");
                    Map<DocumentFile,File> searchList = new HashMap<DocumentFile, File>();
                    //首先找到所有要写的文件
                    for(ImageBean imageBean:listImages){
                        File file = new File(imageBean.getPath());
                        DocumentFile findFile = udisk.findUPFile(dirFile,file);
                        searchList.put(findFile,file);
                    }
                    Iterator<DocumentFile> listDocument = searchList.keySet().iterator();
                    while(listDocument.hasNext()){
                        DocumentFile docFile = listDocument.next();
                        File file = searchList.get(docFile);
                        udisk.writeToSDFile(mActivity,file,docFile);
                    }*/
                    DocumentFile dirFile = udisk.findUFile(rootFile,"照片");
                    for(ImageBean imageBean:listImages){
                        File file = new File(imageBean.getPath());
                        udisk.writeToSDFile(mActivity,file,dirFile);
                    }
                    return true;
                }
            });

            partions.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    DocumentFile dirFile = udisk.findUFile(rootFile,"视频");
                    for(VideoBean videoBean:listVideos){
                        File file = new File(videoBean.getPath());
                        udisk.writeToSDFile(mActivity,file,dirFile);
                    }
                    return true;
                }
            });
            partions.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    DocumentFile dirFile = udisk.findUFile(rootFile,"文档");
                    for(FileBean fileBean:listFiles){
                        File file = new File(fileBean.getPath());
                        udisk.writeToSDFile(mActivity, file, dirFile);
                    }
                    return true;
                }
            });
            partions.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
/*                    DocumentFile dirFile = udisk.findUFile(rootFile,"音乐");
                    Map<DocumentFile,File> searchList = new HashMap<DocumentFile, File>();
                    //首先找到所有要写的文件
                    for(MusicBean musicBean:listMusics){
                        File file = new File(musicBean.getPath());
                        DocumentFile findFile = udisk.findUPFile(dirFile,file);
                        searchList.put(findFile,file);
                    }
                    Iterator<DocumentFile> listDocument = searchList.keySet().iterator();
                    while(listDocument.hasNext()){
                        DocumentFile docFile = listDocument.next();
                        File file = searchList.get(docFile);
                        udisk.writeToSDFile(mActivity,file,docFile);
                    }*/
                    DocumentFile dirFile = udisk.findUFile(rootFile,"音乐");
                    for(MusicBean musicBean:listMusics){
                        File file = new File(musicBean.getPath());
                        udisk.writeToSDFile(mActivity,file,dirFile);
                    }
                    return true;
                }
            });
            partions.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    DocumentFile dirFile = udisk.findUFile(rootFile,"联系人");
                    udisk.writeStrToSDFile(mActivity,contacts.toString(),dirFile, BkrtActivity.PHONE_FILE);
                    return true;
                }
            });

            if(partions.size()>0){
                ExecutorService executorService = Executors.newCachedThreadPool();
                List<FutureTask<Boolean>> listFutureTask = new ArrayList<>();
                for(Callable<Boolean> callable : partions){
                    FutureTask<Boolean> futureTask = new FutureTask<Boolean>(callable);
                    listFutureTask.add(futureTask);
                    executorService.submit(futureTask);
                }

                try {
                    for(FutureTask<Boolean> futureTask : listFutureTask){
                        boolean flag = futureTask.get();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(executorService!=null){
                        executorService.shutdown();
                    }
                }
            }
        }else{
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mActivity,"请插入U盘",Toast.LENGTH_SHORT);
                }
            });
        }
    }

    //文件的刷新
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);

        //重新初始化数据
        initData();
    }


}
