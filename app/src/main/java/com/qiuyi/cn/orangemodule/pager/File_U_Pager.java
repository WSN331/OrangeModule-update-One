package com.qiuyi.cn.orangemodule.pager;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.storage.StorageManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.upan.UAllFileShowActivity;
import com.qiuyi.cn.orangemodule.activity.UFileShowActivity;
import com.qiuyi.cn.orangemodule.adapter.NativiAdapter;
import com.qiuyi.cn.orangemodule.bean.FileType;
import com.qiuyi.cn.orangemodule.upansaf.ui.FileActivity;
import com.qiuyi.cn.orangemodule.upanupdate.AllUdiskFileShowActivity;
import com.qiuyi.cn.orangemodule.util.Constant;
import com.qiuyi.cn.orangemodule.util.FileManager.service.FindUpanMsg_Service;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/3/14.
 * 文件管理-u盘
 */
public class File_U_Pager extends BaseRefreshPager{

    //USB相关权限
    private static final String[] ACTION_USB = {
            //USB插入权限
            UsbManager.ACTION_USB_DEVICE_ATTACHED,
            //USB拔出权限
            UsbManager.ACTION_USB_DEVICE_DETACHED
    };

    @BindView(R.id.upan_recycler)
    RecyclerView myUpanRl;

    private GridLayoutManager myGridManager;
    private NativiAdapter myAdapter;
    private List<FileType> myFileTypes;

    private float rlposY,rlnowY;
    private float grposY,grnowY;
    private int count = 0;
    private float distance = 0.0f;

    private List<File> listMusics;//音乐
    private List<File> listVideos;//视频
    private List<File> listImages;//图片
    private List<File> listFiles;//文件
    private List<File> listFileZars;//压缩包


    public File_U_Pager(Activity mActivity) {
        super(mActivity);
    }

    //初始化广播监听
    private void initBroadCast() {
        //数据刷新
        IntentFilter filter = new IntentFilter(Constant.FINDUPAN_MSG);
        mActivity.registerReceiver(upanFileMsgreceiver,filter);
        //界面重新刷新
        IntentFilter filterRefresh = new IntentFilter(Constant.UPANREFRESH);
        mActivity.registerReceiver(refreshReceiver,filterRefresh);
    }

    //初始化USB插入监听
    private void initPermission() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB[0]);
        mActivity.registerReceiver(usbBroadCastReceiver,filter);
    }

    //刷新界面
    private BroadcastReceiver refreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra("UpanToFresh",false)){
                try {
                    Thread.sleep(2000);
                    //设备插入
                    initData();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    //插入再次读取
    private BroadcastReceiver usbBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action){
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    try {
                        Thread.sleep(2000);
                        //设备插入
                        initData();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    private BroadcastReceiver upanFileMsgreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getBooleanExtra("findOkUpan",false)){
                listMusics = MainActivity.listUPANMusics;
                listVideos = MainActivity.listUPANVideos;
                listImages = MainActivity.listUPANImages;
                listFiles = MainActivity.listUPANFiles;
                listFileZars = MainActivity.listUPANFileZars;

                Constant.UPAN_SIZE[0] = listImages.size();
                Constant.UPAN_SIZE[1] = listVideos.size();
                Constant.UPAN_SIZE[2] = listFiles.size();
                Constant.UPAN_SIZE[3] = listMusics.size();
                Constant.UPAN_SIZE[4] = listFileZars.size();

                //点击
                myAdapter.setOnItemClick(new NativiAdapter.myItemClick() {
                    @Override
                    public void onItemClick(View view, int position) {

                        FileType fileType = myFileTypes.get(position);
                        if(fileType.getShowType()==0){
                            Intent intent = new Intent(mActivity, UFileShowActivity.class);
                            if(position==0){
                                intent.putExtra("type",0);
                            }else if(position == 1){
                                intent.putExtra("type",1);
                            }else if(position == 2){
                                intent.putExtra("type",2);
                            }else if(position == 3){
                                intent.putExtra("type",3);
                            }else if(position == 4){
                                intent.putExtra("type",4);
                            }else{
                                intent = null;
                            }
                            if(intent!=null){
                                mActivity.startActivity(intent);
                            }
                        }else if(fileType.getShowType()==3){
                            //这里是进入所有文件
                            Intent intent = new Intent(mActivity,AllUdiskFileShowActivity.class);
                            mActivity.startActivity(intent);
                        }
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {

                    }
                });

                myAdapter.changeListFile();
                myViewGroup.scrollTo(0,0);
                Log.e("UPAN","刷新完成");
            }

        }
    };


    @Override
    public void addView(LinearLayout myFrameLayout) {
        super.addView(myFrameLayout);

        View view = LayoutInflater.from(mActivity).inflate(R.layout.pager_upan,null);

        ButterKnife.bind(this,view);

        myFrameLayout.addView(view);
    }


    @Override
    public void initData() {
        super.initData();


        initBroadCast();
        initPermission();

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
        myUpanRl.setLayoutManager(myGridManager);

        myAdapter = new NativiAdapter(mActivity,myFileTypes);
        myUpanRl.setAdapter(myAdapter);


        myUpanRl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {

                switch (ev.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        rlposY = ev.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        rlnowY = ev.getRawY();
                        float dy = rlnowY - rlposY;

                        boolean istop = myUpanRl.canScrollVertically(-1);
                        GridLayoutManager manager = (GridLayoutManager) myUpanRl.getLayoutManager();
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
                            setLockDisplay(distance,myViewGroup.mRefreshHeight,myViewGroup.mSecretHeight,3);
                        }

                        rlposY = rlnowY;
                        break;
                    case MotionEvent.ACTION_UP:
                        myViewGroup.scrollTo(0, 0);
                        if(currentFolder==null){
                            setViewDispaly(distance,myViewGroup.mRefreshHeight,myViewGroup.mSecretHeight,3,null);
                        }else{
                            setViewDispaly(distance,myViewGroup.mRefreshHeight,myViewGroup.mSecretHeight,3,currentFolder.toString());
                        }
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

        for(int i=0;i<5;i++){
            FileType type1 = new FileType();
            type1.setShowType(0);
            type1.setImage(Constant.UPAN_IMAGES[i]);
            type1.setName(Constant.UPAN_TITLE[i]);
            type1.setSize(Constant.UPAN_SIZE[i]);
            myFileTypes.add(type1);
        }

        //查找存储设备
        findStorage();

        //所有文件条目
        FileType circleType = new FileType();
        circleType.setShowType(3);
        circleType.setName("所有文件");

        Long total = Constant.UPAN_MEMORYSIZE;
        Long canused = Constant.UPAN_AVAILSIZE;
        Long hasUsed = total - canused;
        int percentage = 0;
        if(total!=0){
            percentage = (int) (100 * (hasUsed*1.0f/total));
            if(hasUsed>0 && percentage ==0){
                percentage = 1;
            }
        }
        circleType.setProgress(percentage);

        circleType.setShowSize(Formatter.formatFileSize(mActivity,hasUsed)+"/"+
                Formatter.formatFileSize(mActivity,total));

        myFileTypes.add(circleType);
    }


    private File currentFolder = null; //U盘根目录
    //查找U盘，并开启服务如果查找到了
    private void findStorage() {
        //存储得到的文件路径
        String[] result = null;
        //得到存储管理
        StorageManager storageManager = (StorageManager) mActivity.getSystemService(Context.STORAGE_SERVICE);
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

                    Constant.UPAN_AVAILSIZE = currentFolder.getUsableSpace();
                    Constant.UPAN_MEMORYSIZE = currentFolder.getTotalSpace();
                    //启动查找U盘文件的服务
                    Intent intent = new Intent(mActivity, FindUpanMsg_Service.class);
                    intent.putExtra("Folder",result[i]);
                    mActivity.startService(intent);


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(currentFolder == null){
            Toast.makeText(mActivity,"请插入U盘",Toast.LENGTH_SHORT);
        }
    }

}
