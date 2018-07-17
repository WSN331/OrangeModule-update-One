package com.qiuyi.cn.orangemoduleNew.fragment;

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
import android.support.v4.provider.DocumentFile;
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
import android.widget.TextView;
import android.widget.Toast;

import com.qiuyi.cn.myloadingdialog.LoadingDialog;
import com.qiuyi.cn.orangemoduleNew.MainActivity;
import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.activity.BkrtActivity;
import com.qiuyi.cn.orangemoduleNew.activity.SearchActivity;
import com.qiuyi.cn.orangemoduleNew.adapter.BackUp_fm_adapter;
import com.qiuyi.cn.orangemoduleNew.bean.MyItemFile;
import com.qiuyi.cn.orangemoduleNew.util.Constant;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.contacts.PhoneInfo;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.service.FindAllFile_II_Service;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.service.FindContacts;
import com.qiuyi.cn.orangemoduleNew.util.ShareUtil;
import com.qiuyi.cn.orangemoduleNew.util.WriteToUdisk;

import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

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

    private List<File> listMusics;//音乐
    private List<File> listVideos;//视频
    private List<File> listImages;//图片
    private List<File> listFiles;//文件
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
        initBroadCast();


        mActivity.startService(new Intent(mActivity,FindContacts.class));
        mActivity.startService(new Intent(mActivity, FindAllFile_II_Service.class));
        //mActivity.startService(new Intent(mActivity, FindFileMsg_Service.class));
/*        if(MainActivity.constacts==null){
            mActivity.startService(new Intent(mActivity,FindContacts.class));
        }
        if(MainActivity.listFileZars==null){
            mActivity.startService(new Intent(mActivity, FindFileMsg_Service.class));
        }*/


        String text = ShareUtil.getString("Update",null);
        if(text!=null){
            tv_prebackup.setText("上次备份："+text);
        }else{
            tv_prebackup.setText("上次备份："+"无");
        }

        initData();
    }

    private void initBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BACKUP);

        filter.addAction(SearchActivity.SearchActivity_getSDFile);
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
                case SearchActivity.SearchActivity_getSDFile:
                    if(intent.getBooleanExtra("findAllSDFile",false)){
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
            for(File image:listImages){
                sizeImages += image.length();
            }
        }
        sizes.add(sizeImages);

        Long sizeVideo = Long.valueOf(0);
        if(listVideos!=null && listVideos.size()>0){
            for(File video:listVideos){
                sizeVideo += video.length();
            }
        }
        sizes.add(sizeVideo);

        Long sizeFiles = Long.valueOf(0);
        if(listFiles!=null && listFiles.size()>0){
            for(File file:listFiles){
                sizeFiles += file.length();
            }
        }
        sizes.add(sizeFiles);

        Long sizeMusic = Long.valueOf(0);
        if(listMusics!=null && listMusics.size()>0){
            for(File music:listMusics){
                sizeMusic += music.length();
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

        if(!MainActivity.isHaveUpan){
            new AlertDialog.Builder(mActivity)
                    .setTitle("U盘")
                    .setMessage("请插入U盘").show();
            return;
        }

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
                    startWrite(mySelect);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            String upDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
                            ShareUtil.setString("Update",upDate);

                            tv_prebackup.setText("上次备份："+upDate);
                            dialog.dismiss();
                            myAdapter.ReFresh();
                        }
                    });
                }
            });

        }else{
            new AlertDialog.Builder(mActivity)
                    .setCancelable(false)
                    .setTitle("备份")
                    .setMessage("没有选择备份项目（默认备份全部）,点击确定同意备份全部，点击取消重新选择需要备份的项目")
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
                                    startWrite(mySelect);
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            String upDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
                                            ShareUtil.setString("Update",upDate);

                                            tv_prebackup.setText("上次备份："+upDate);
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

    //开始写入U盘
    private void startWrite(List<Integer> mySelect) {
        List<Callable<Boolean>> partions = new ArrayList<>();
        final WriteToUdisk udisk = WriteToUdisk.getInstance(mActivity.getApplicationContext(),mActivity);
        //获取U盘根目录
        final DocumentFile rootFile = udisk.getCurrentFolder();
        if(rootFile!=null){
            for(Integer myInet:mySelect){
                switch (myInet){
                    case 0:
                        partions.add(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                DocumentFile dirFile = udisk.findUFile(rootFile,"照片");
                                for(File imageBean:listImages){
                                    udisk.writeToSDFile(mActivity,imageBean,dirFile);
                                }
                                return true;
                            }
                        });
                        break;
                    case 1:
                        partions.add(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                DocumentFile dirFile = udisk.findUFile(rootFile,"视频");
                                for(File videoBean:listVideos){
                                    udisk.writeToSDFile(mActivity,videoBean,dirFile);
                                }
                                return true;
                            }
                        });
                        break;
                    case 2:
                        partions.add(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                DocumentFile dirFile = udisk.findUFile(rootFile,"文档");
                                for(File fileBean:listFiles){
                                    udisk.writeToSDFile(mActivity, fileBean, dirFile);
                                }
                                return true;
                            }
                        });
                        break;
                    case 3:
                        partions.add(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                DocumentFile dirFile = udisk.findUFile(rootFile,"音乐");
                                for(File musicBean:listMusics){
                                    udisk.writeToSDFile(mActivity,musicBean,dirFile);
                                }
                                return true;
                            }
                        });
                        break;
                    case 4:
                        partions.add(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                DocumentFile dirFile = udisk.findUFile(rootFile,"联系人");
                                udisk.writeStrToSDFile(mActivity,contacts.toString(),dirFile, BkrtActivity.PHONE_FILE);
                                return true;
                            }
                        });
                        break;
                }
            }

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
        }
    }

    //文件的刷新
    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);

        myAdapter.setShowCheckBox(false);
        myAdapter.ReFresh();

        //重新初始化数据
        initData();
    }


}
