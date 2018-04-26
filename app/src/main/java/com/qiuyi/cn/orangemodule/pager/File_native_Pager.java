package com.qiuyi.cn.orangemodule.pager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.os.StatFs;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.activity.AllFileShowActivity;
import com.qiuyi.cn.orangemodule.activity.FileShowActivity;
import com.qiuyi.cn.orangemodule.adapter.MyPagerAdapter;
import com.qiuyi.cn.orangemodule.adapter.NativiAdapter;
import com.qiuyi.cn.orangemodule.bean.FileType;
import com.qiuyi.cn.orangemodule.interfaceToutil.MyScrollerListener;
import com.qiuyi.cn.orangemodule.myview.MyViewGroup;
import com.qiuyi.cn.orangemodule.util.Constant;
import com.qiuyi.cn.orangemodule.util.FileManager.ConstantValue;
import com.qiuyi.cn.orangemodule.util.FileManager.FileUtils;
import com.qiuyi.cn.orangemodule.util.FileManager.MyFileManager;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.FileBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.ImageBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.MusicBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.VideoBean;
import com.qiuyi.cn.orangemodule.util.FileManager.service.FindFileMsg_Service;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/3/14.
 * 文件管理-本地
 */
public class File_native_Pager extends BaseRefreshPager{

    @BindView(R.id.native_recycler)
    RecyclerView myNativeRl;

    private GridLayoutManager myGridManager;
    private NativiAdapter myAdapter;
    private List<FileType> myFileTypes;


    private float rlposY,rlnowY;
    private float grposY,grnowY;
    private int count = 0;
    private float distance = 0.0f;

    //各类文件
    private List<FileBean> listAllFiles;
    private List<MusicBean> listMusics;//音乐
    private List<VideoBean> listVideos;//视频
    private List<ImageBean> listImages;//图片
    private List<FileBean> listFiles;//文件
    private List<FileBean> listFileZars;//压缩包

    public File_native_Pager(Activity mActivity) {
        super(mActivity);
    }

    private void initBroadCast() {
        IntentFilter filter = new IntentFilter(Constant.FINDFILE_MSG);
        mActivity.registerReceiver(fileMsgreceiver,filter);
        IntentFilter filterRf = new IntentFilter(Constant.NATIVEFRESH);
        mActivity.registerReceiver(refreshNative,filterRf);
    }


    private BroadcastReceiver refreshNative = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra("NativeToFresh",false)){
                initData();
            }
        }
    };


    private BroadcastReceiver fileMsgreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getBooleanExtra("findOk",false)){
                listMusics = MainActivity.listMusics;
                listVideos = MainActivity.listVideos;
                listImages = MainActivity.listImages;
                listFiles = MainActivity.listFiles;
                listFileZars = MainActivity.listFileZars;

                Constant.NATIVE_SIZE[0] = listImages.size();
                Constant.NATIVE_SIZE[1] = listVideos.size();
                Constant.NATIVE_SIZE[2] = listFiles.size();
                Constant.NATIVE_SIZE[3] = listMusics.size();
                Constant.NATIVE_SIZE[4] = listFileZars.size();
                Constant.NATIVE_SIZE[5] = (listImages.size()+listMusics.size()+listVideos.size()
                        + listFiles.size()+listFileZars.size());


                //点击
                myAdapter.setOnItemClick(new NativiAdapter.myItemClick() {
                    @Override
                    public void onItemClick(View view, int position) {

                        FileType fileType = myFileTypes.get(position);
                        if(fileType.getShowType()==0){
                            Intent intent = new Intent(mActivity, FileShowActivity.class);
                            if(position==0){
                                intent.putExtra("type",0);
                                intent.putExtra("listFile", (Serializable)listImages);
                            }else if(position == 1){
                                intent.putExtra("type",1);
                                intent.putExtra("listFile", (Serializable)listVideos);
                            }else if(position == 2){
                                intent.putExtra("type",2);
                                intent.putExtra("listFile", (Serializable)listFiles);
                            }else if(position == 3){
                                intent.putExtra("type",3);
                                intent.putExtra("listFile", (Serializable)listMusics);
                            }else if(position == 4){
                                intent.putExtra("type",4);
                                intent.putExtra("listFile", (Serializable)listFileZars);
                            }else{
                                intent = null;
                            }

                            if(intent!=null){
                                mActivity.startActivityForResult(intent,1);
                            }
                        }else if(fileType.getShowType()==3){
                            //这里是进入所有文件
                            Intent intent = new Intent(mActivity,AllFileShowActivity.class);
                            mActivity.startActivity(intent);
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });

                myAdapter.changeList();
                myViewGroup.scrollTo(0,0);
                Log.e("Native","刷新完毕");
            }

        }
    };




    @Override
    public void addView(LinearLayout myFrameLayout) {
        super.addView(myFrameLayout);

        View view = LayoutInflater.from(mActivity).inflate(R.layout.pager_native,null);

        ButterKnife.bind(this,view);

        myFrameLayout.addView(view);
    }


    @Override
    public void initData() {
        super.initData();

        initBroadCast();

        initAddData();

        myGridManager = new GridLayoutManager(mActivity,4);

        myGridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(position < 6 ){
                    return 2;
                }
                if(position == 6){
                    return 4;
                }
                return 1;
            }
        });

        myNativeRl.setLayoutManager(myGridManager);

        myAdapter = new NativiAdapter(mActivity,myFileTypes);
        myNativeRl.setAdapter(myAdapter);


        //启动查找数据的服务
        mActivity.startService(new Intent(mActivity,FindFileMsg_Service.class));


        myNativeRl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {

                switch (ev.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        rlposY = ev.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        rlnowY = ev.getRawY();
                        float dy = rlnowY - rlposY;

                        boolean istop = myNativeRl.canScrollVertically(-1);
                        GridLayoutManager manager = (GridLayoutManager) myNativeRl.getLayoutManager();
                        int l1 = manager.findFirstVisibleItemPosition();
                        int l2 = manager.findFirstCompletelyVisibleItemPosition();

                        Log.e("move", "istop："+!istop+"distance："+distance+"srollY"+myViewGroup.getScrollY()+"posy："+l1+"nowy："+l2);
                        if((!istop && distance>=0 && dy>0)||myViewGroup.getScrollY()<0){

                            distance += dy;
                            count++;

                            if(count==1){
                                distance -= dy;
                            }

                            myViewGroup.scrollTo(0, (int) -distance+10);
                            setLockDisplay(distance,myViewGroup.mRefreshHeight,myViewGroup.mSecretHeight,2);
                        }

                        rlposY = rlnowY;
                        break;
                    case MotionEvent.ACTION_UP:
                        myViewGroup.scrollTo(0, 0);
                        setViewDispaly(distance,myViewGroup.mRefreshHeight,myViewGroup.mSecretHeight,2,null);
                        count=0;
                        distance = 0.0f;
                        //下拉距离，使用回调

                        break;
                    default:
                        break;
                }
                return false;
            }
        });

    }



    //构造数据
    private void initAddData() {
        myFileTypes = new ArrayList<>();

        //前面5个条目
        for(int i=0;i<5;i++){
            FileType type1 = new FileType();
            type1.setShowType(0);
            type1.setImage(Constant.NATIVE_IMAGES[i]);
            type1.setName(Constant.NATIVE_TITLE[i]);
            type1.setSize(Constant.NATIVE_SIZE[i]);

            myFileTypes.add(type1);
        }

        //所有文件条目
        FileType circleType = new FileType();
        circleType.setShowType(3);
        circleType.setName("所有文件");
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        Long memoryTotal = FileUtils.getTotalSize(path);
        Long memoryAvail = FileUtils.getAvailSize(path);
        Long memoryHasUse = memoryTotal - memoryAvail;

        int percentage = (int) (100 * (memoryHasUse*1.0f/memoryTotal));
        circleType.setProgress(percentage);

        circleType.setShowSize(Formatter.formatFileSize(mActivity,memoryHasUse)+"/"+
        Formatter.formatFileSize(mActivity,memoryTotal));
        myFileTypes.add(circleType);

        //常见应用标题
/*        FileType type2 = new FileType();
        type2.setShowType(1);
        myFileTypes.add(type2);

        //常见应用的标签
        for(int i = 0;i<2;i++){
            FileType type3 = new FileType();
            type3.setShowType(2);
            myFileTypes.add(type3);
        }*/
    }





}
/*    //得到所有需要的文件
    private void getFileList() {

        //使用线程并发得到需要的文件
        myFileManager = MyFileManager.getInstance(mActivity);

        listImages = new ArrayList<>();
        listMusics = new ArrayList<>();
        listVideos = new ArrayList<>();
        listFiles = new ArrayList<>();
        listFileZars = new ArrayList<>();

        ExecutorService executorService = Executors.newCachedThreadPool();

        Callable<List<ImageBean>> myImgCallable = new Callable<List<ImageBean>>() {
            @Override
            public List<ImageBean> call() throws Exception {
                return myFileManager.getImages();
            }
        };
        Callable<List<MusicBean>> myMucCallable = new Callable<List<MusicBean>>() {
            @Override
            public List<MusicBean> call() throws Exception {
                return myFileManager.getMusics();
            }
        };
        Callable<List<VideoBean>> myVidCallable = new Callable<List<VideoBean>>() {
            @Override
            public List<VideoBean> call() throws Exception {
                return myFileManager.getVideos();
            }
        };
        Callable<List<FileBean>> myFileCallable = new Callable<List<FileBean>>() {
            @Override
            public List<FileBean> call() throws Exception {
                return myFileManager.getFilesByType(ConstantValue.TYPE_DOC);
            }
        };
        Callable<List<FileBean>> myFileZarCallable = new Callable<List<FileBean>>() {
            @Override
            public List<FileBean> call() throws Exception {
                return myFileManager.getFilesByType(ConstantValue.TYPE_ZIP);
            }
        };

        FutureTask<List<ImageBean>> myImgTask = new FutureTask<List<ImageBean>>(myImgCallable);
        FutureTask<List<MusicBean>> myMucTask = new FutureTask<List<MusicBean>>(myMucCallable);
        FutureTask<List<VideoBean>> myVioTask = new FutureTask<List<VideoBean>>(myVidCallable);
        FutureTask<List<FileBean>> myFileTask = new FutureTask<List<FileBean>>(myFileCallable);
        FutureTask<List<FileBean>> myFileZarTask = new FutureTask<List<FileBean>>(myFileZarCallable);

        executorService.submit(myImgTask);
        executorService.submit(myMucTask);
        executorService.submit(myVioTask);
        executorService.submit(myFileTask);
        executorService.submit(myFileZarTask);

        try {
            listImages = myImgTask.get();
            listMusics = myMucTask.get();
            listVideos = myVioTask.get();
            listFiles = myFileTask.get();
            listFileZars = myFileZarTask.get();

            Constant.NATIVE_SIZE[0] = listImages.size();
            Constant.NATIVE_SIZE[1] = listMusics.size();
            Constant.NATIVE_SIZE[2] = listVideos.size();
            Constant.NATIVE_SIZE[3] = listFiles.size();
            Constant.NATIVE_SIZE[4] = listFileZars.size();
            Constant.NATIVE_SIZE[5] = (listImages.size()+listMusics.size()+listVideos.size()
                    + listFiles.size()+listFileZars.size());


            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    myAdapter.changeList();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(executorService!=null){
                executorService.shutdown();
            }
        }
    }*/