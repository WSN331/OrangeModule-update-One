package com.qiuyi.cn.orangemodule.pager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.Resource;
import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.adapter.MyPagerAdapter;
import com.qiuyi.cn.orangemodule.adapter.RecentlyAdapter;
import com.qiuyi.cn.orangemodule.bean.FileInfo;
import com.qiuyi.cn.orangemodule.myview.MyViewPager;
import com.qiuyi.cn.orangemodule.util.Constant;
import com.qiuyi.cn.orangemodule.util.FileManager.ConstantValue;
import com.qiuyi.cn.orangemodule.util.FileManager.MyFileManager;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.FileBean;
import com.qiuyi.cn.orangemodule.util.FileManager.service.FindAllFile_Service;
import com.qiuyi.cn.orangemodule.util.FileUtilOpen;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.schedulers.Schedulers;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


/**
 * Created by Administrator on 2018/3/14.
 * 文件管理-最近
 */
public class File_recently_Pager extends BaseRefreshPager{

    private RecyclerView myRecyclerView;
    private RecentlyAdapter myadapter;
    private GridLayoutManager myGridManager;

    private List<FileBean> listFileBean;

    private float rlposY,rlnowY;
    private float grposY,grnowY;
    private int count = 0;
    private float distance = 0.0f;


    public File_recently_Pager(Activity mActivity) {
        super(mActivity);
    }

    private void initBroadcast() {
        IntentFilter filter = new IntentFilter(Constant.FINDALL_MSG);
        mActivity.registerReceiver(myAllFilsReceiver,filter);
    }

    private BroadcastReceiver myAllFilsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra("findallmsg",false)){
                //这里是刷新的逻辑
                listFileBean = MainActivity.MY_ALLFILES;
                myadapter.changeList(listFileBean);
                myViewGroup.scrollTo(0,0);
                Log.e("Recently","刷新完成");
            }
        }
    };


    @Override
    public void addView(LinearLayout myFrameLayout) {
        super.addView(myFrameLayout);
        View view = View.inflate(mActivity,R.layout.pager_recently,null);
        myRecyclerView = view.findViewById(R.id.FileControll_RecyclerView);

        listFileBean = new ArrayList<>();

        final Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                listFileBean = MainActivity.MY_ALLFILES;
                if(listFileBean.size()>0 && listFileBean!=null){
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            myadapter.changeList(listFileBean);
                            timer.cancel();
                            Log.e("TAGGG","结束");
                        }
                    });
                }
            }
        },1000,1000);

        myFrameLayout.addView(view);
    }


    @Override
    public void initData() {
        super.initData();

        initBroadcast();

        addData();
    }




    //获取数据
    private void addData() {

        myGridManager = new GridLayoutManager(mActivity,4);

        myRecyclerView.setLayoutManager(myGridManager);

        myadapter = new RecentlyAdapter(mActivity, listFileBean,myGridManager);

        myRecyclerView.setAdapter(myadapter);


        myadapter.setOnFileItemClick(new RecentlyAdapter.FileItemClick() {
            @Override
            public void openFile(View view, int position, List<FileBean> allFileBean) {
                FileBean fileBean = allFileBean.get(position);
                if(fileBean.getFiletype()!=0 && fileBean.getFiletype()!=3){
                    FileUtilOpen.openFileByPath(mActivity,fileBean.getPath());
                }
            }
        });



        myRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {

                switch (ev.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        rlposY = ev.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        rlnowY = ev.getRawY();
                        float dy = rlnowY - rlposY;

                        boolean istop = myRecyclerView.canScrollVertically(-1);
                        GridLayoutManager manager = (GridLayoutManager) myRecyclerView.getLayoutManager();
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
                            setLockDisplay(distance,myViewGroup.mRefreshHeight,myViewGroup.mSecretHeight,1);
                        }

                        rlposY = rlnowY;
                        break;
                    case MotionEvent.ACTION_UP:
                        myViewGroup.scrollTo(0, 0);
                        setViewDispaly(distance,myViewGroup.mRefreshHeight,myViewGroup.mSecretHeight,1,null);
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


}


/*    *//**
 * FileInfoList文件的设置
 *//*
    private void setFileInfoData() {

        listFiles = new ArrayList<>();

        //标题栏
        FileInfo infoTitle = new FileInfo();
        infoTitle.setType(0);
*//*        infoTitle.setTitle("qq接收的图片");
        infoTitle.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));*//*
        listFiles.add(infoTitle);

        //native_img
        for(int i=0;i<3;i++){
            FileInfo infoImg = new FileInfo();
            infoImg.setType(1);
            listFiles.add(infoImg);
        }

        FileInfo infoFile = new FileInfo();
        infoFile.setType(2);
        listFiles.add(infoFile);

        //间隔
        FileInfo infoLine = new FileInfo();
        infoLine.setType(3);
        listFiles.add(infoLine);
    }*/
