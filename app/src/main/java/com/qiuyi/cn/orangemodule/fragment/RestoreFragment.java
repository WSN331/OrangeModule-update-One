package com.qiuyi.cn.orangemodule.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.qiuyi.cn.myloadingdialog.LoadingDialog;
import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.adapter.BackUp_fm_adapter;
import com.qiuyi.cn.orangemodule.bean.MyItemFile;
import com.qiuyi.cn.orangemodule.util.Constant;
import com.qiuyi.cn.orangemodule.util.DiskWriteToSD;
import com.qiuyi.cn.orangemodule.util.FileManager.MyFileHelper;
import com.qiuyi.cn.orangemodule.util.FileManager.contacts.ContactBean;
import com.qiuyi.cn.orangemodule.util.FileManager.service.FindUpanConstacts;
import com.qiuyi.cn.orangemodule.util.FileManager.service.FindUpanRestore_Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by Administrator on 2018/3/18.
 * 还原页面
 */
public class RestoreFragment extends Fragment implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener{

    public Activity mActivity;

    private LoadingDialog dialog;

    private ExecutorService executorService;

    //SwipeRefreshLayout
    private SwipeRefreshLayout swipeRefreshLayout;
    //recyclerView
    private RecyclerView rl_restore;

    //还原
    private Button bt_restore;

    //适配器
    private BackUp_fm_adapter myAdapter;
    //LinnerLayoutManager
    private LinearLayoutManager myManager;

    //文件操作方法
    private MyFileHelper myFileHelper;

    //FileItem
    private List<MyItemFile> listFiles;

    private List<File> listUPANMusics = new ArrayList<>();//音乐
    private List<File> listUPANVideos = new ArrayList<>();//视频
    private List<File> listUPANImages = new ArrayList<>();//图片
    private List<File> listUPANFiles = new ArrayList<>();//文件

    private List<Long> sizes;//存储文件大小

    private List<ContactBean> listContact = new ArrayList<>();//联系人
    private File constactFile = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_restore,null);
        rl_restore = view.findViewById(R.id.rl_restore);
        swipeRefreshLayout = view.findViewById(R.id.swip_restore);

        bt_restore = view.findViewById(R.id.bt_restore);

        bt_restore.setOnClickListener(this);

        swipeRefreshLayout.setColorSchemeColors(Color.RED);

        executorService = Executors.newCachedThreadPool();
        dialog = new LoadingDialog.Builder(mActivity)
                .setMessage("还原中...")
                .setCancelable(false)
                .setCancelOutside(false).create();

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //启动服务查找U盘路径，并查找U盘文件
        //myFileHelper = new MyFileHelper(mActivity);
        File file = MainActivity.rootUFile;
        if(file!=null){
            //启动查找U盘文件的服务
            mActivity.startService(new Intent(mActivity, FindUpanRestore_Service.class));
            mActivity.startService(new Intent(mActivity, FindUpanConstacts.class));
        }
        initBroadCast();

        initData();
    }

    //广播注册
    private void initBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.RESTORE);
        //U盘文件信息
        filter.addAction(Constant.FINDUPAN_BFMSG);
        mActivity.registerReceiver(myUpanReceiver,filter);
    }

    private BroadcastReceiver myUpanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                //联系人查询完毕
                case Constant.RESTORE:
                    if(intent.getBooleanExtra("isOK",false)){
                        initData();
                    }
                    break;
                case Constant.FINDUPAN_BFMSG:
                    if(intent.getBooleanExtra("findOkUpan",false)){
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
        try{
            addData();
        }catch (Exception e){
            e.printStackTrace();
        }


        //添加数据
        addFileTypeData();

        myManager = new LinearLayoutManager(mActivity);

        rl_restore.setLayoutManager(myManager);
        myAdapter = new BackUp_fm_adapter(mActivity,listFiles);
        rl_restore.setAdapter(myAdapter);

        swipeRefreshLayout.setRefreshing(false);

        initListener();
    }

    //监听事件
    private void initListener() {

        bt_restore.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        myAdapter.setOnBackUpClickListener(new BackUp_fm_adapter.BackupOnClick() {
            @Override
            public void onBackItemClick(View view, int position) {
                boolean isShowBox = myAdapter.isShowCheckBox();
                if(isShowBox){
                    //正在显示checkbox
                    boolean[] flag = myAdapter.getFlag();
                    flag[position] = !flag[position];

                    myAdapter.setFlag(flag);
                    myAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onBackLongItemClick(View view, int position) {
                myAdapter.setShowCheckBox(true);
                boolean flag[] = myAdapter.getFlag();
                flag[position] = true;
                myAdapter.setFlag(flag);

                myAdapter.notifyDataSetChanged();
            }
        });
    }


    //添加数据
    private void addData() {
        if(MainActivity.uMusic!=null){
            listUPANMusics.clear();
            for(File musicFile:MainActivity.uMusic.listFiles()){
                listUPANMusics.add(musicFile);//音乐
            }
        }
        if(MainActivity.uVideo!=null){
            listUPANVideos.clear();
            for(File videoFile:MainActivity.uVideo.listFiles()){
                listUPANVideos.add(videoFile);//视频
            }
        }

        if(MainActivity.uImages!=null){
            listUPANImages.clear();
            for(File imageFile:MainActivity.uImages.listFiles()){
                listUPANImages.add(imageFile);//图片
            }
        }

        if(MainActivity.uFiles!=null){
            listUPANFiles.clear();
            for(File ufile:MainActivity.uFiles.listFiles()){
                listUPANFiles.add(ufile);//文件
            }
        }

        listContact = MainActivity.listContacts;//联系人

        if(listUPANMusics.size()<=0||listUPANVideos.size()<=0||listUPANImages.size()<=0
                ||listUPANFiles.size()<=0||listContact.size()<0){
            Toast.makeText(mActivity,"数据正在加载中，请刷新重试", Toast.LENGTH_SHORT).show();
        }

        sizes = new ArrayList<>();

        Long sizeImages = Long.valueOf(0);
        if(listUPANImages!=null && listUPANImages.size()>0){
            for(File file:listUPANImages){
                sizeImages += file.length();
            }
        }
        sizes.add(sizeImages);

        Long sizeVideo = Long.valueOf(0);
        if(listUPANVideos!=null && listUPANVideos.size()>0){
            for(File file:listUPANVideos){
                sizeVideo += file.length();
                //sizeVideo += FileUtils.getFileSize(file);
            }
        }
        sizes.add(sizeVideo);

        Long sizeFiles = Long.valueOf(0);
        if(listUPANFiles!=null && listUPANFiles.size()>0){
            for(File file:listUPANFiles){
                sizeFiles += file.length();//FileUtils.getFileSize(file);
            }
        }
        sizes.add(sizeFiles);

        Long sizeMusic = Long.valueOf(0);
        if(listUPANMusics!=null && listUPANMusics.size()>0){
            for(File file:listUPANMusics){
                sizeMusic += file.length();
                //FileUtils.getFileSize(file);
            }
        }
        sizes.add(sizeMusic);
    }

    //添加数据
    private void addFileTypeData() {
        listFiles = new ArrayList<>();

        for(int i=0;i<4;i++){
            MyItemFile itemFile = new MyItemFile(Constant.BACKUPMSG[i],R.drawable.folder,"文件大小："+ Formatter.formatFileSize(mActivity,sizes.get(i)));
            listFiles.add(itemFile);
        }
        for(int i=4;i<5;i++){
            int length = 0;
            if(listContact!=null){
                length = listContact.size();
            }
            MyItemFile itemFile = new MyItemFile(Constant.BACKUPMSG[i],R.drawable.folder,"联系人："+length);
            listFiles.add(itemFile);
        }

    }



    //还原按钮点击
    @Override
    public void onClick(View view) {

        int count = 0;
        final List<Integer> mySelect = new ArrayList<>();
        //选择，显示一下有哪几个选择了
        boolean[] flag = myAdapter.getFlag();
        for(int i = flag.length-1;i>=0;i--){
            if(flag[i]){
                Log.e("select", "选中："+i);
                mySelect.add(i);
                count++;
            }
        }

        if(count>0){
            dialog.show();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    startRestore(mySelect);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            myAdapter.ReFresh();
                        }
                    });
                }
            });
        }else{
            new AlertDialog.Builder(mActivity)
                    .setCancelable(false)
                    .setTitle("还原")
                    .setMessage("没有选择还原项目（默认还原全部）,点击确定同意还原全部，点击取消重新选择需要还原的项目")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            for(int j=0;i<5;j++){
                                mySelect.add(j);
                            }
                            dialog.show();
                            executorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    startRestore(mySelect);
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                            myAdapter.ReFresh();
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }

        myAdapter.setShowCheckBox(false);
        myAdapter.ReFresh();
    }


    //文件还原
    private void startRestore(List<Integer> mySelect) {
        List<Callable<Void>> partions = new ArrayList<>();
        final DiskWriteToSD diskWriteToSD = new DiskWriteToSD(mActivity.getApplicationContext());
        if(diskWriteToSD.isSDCardState()){

            for(Integer myInet:mySelect){
                switch(myInet){
                    case 0:
                        partions.add(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                for(File file : listUPANImages){
                                    diskWriteToSD.writeFileToSD(file,"照片");
                                }
                                return null;
                            }
                        });
                        break;
                    case 1:
                        partions.add(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                for(File file : listUPANVideos){
                                    diskWriteToSD.writeFileToSD(file,"视频");
                                }
                                return null;
                            }
                        });
                        break;
                    case 2:
                        partions.add(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                for(File file : listUPANFiles){
                                    diskWriteToSD.writeFileToSD(file,"文档");
                                }
                                return null;
                            }
                        });
                        break;
                    case 3:
                        partions.add(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                for(File file : listUPANMusics){
                                    diskWriteToSD.writeFileToSD(file,"音乐");
                                }
                                return null;
                            }
                        });
                        break;
                    case 4:
                        partions.add(new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                //获取联系人文件
                                constactFile = MainActivity.constactUP;
                                if(constactFile!=null){
                                    diskWriteToSD.writeFileToSD(constactFile,"联系人");
                                }
                                for(ContactBean myBean:listContact){
                                    if(myBean==null){
                                        continue;
                                    }
                                    diskWriteToSD.init(myBean);
                                }
                                diskWriteToSD.sendToSDCardPhone(listContact, mActivity.getApplicationContext());
                                return null;
                            }
                        });
                        break;
                }
            }

            //开始还原
            if(partions.size()>0){
                ExecutorService executorService = Executors.newCachedThreadPool();
                List<FutureTask<Void>> listFutureTask = new ArrayList<>();
                for(Callable<Void> callable : partions){
                    FutureTask<Void> futureTask = new FutureTask<Void>(callable);
                    listFutureTask.add(futureTask);
                    executorService.submit(futureTask);
                }
                try {
                    for(FutureTask<Void> futureTask : listFutureTask){
                        futureTask.get();
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
                    Toast.makeText(mActivity,"没有外置SD卡",Toast.LENGTH_SHORT);
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);

        myAdapter.setShowCheckBox(false);
        myAdapter.ReFresh();
        //重新初始化数据
        initData();
    }
}
