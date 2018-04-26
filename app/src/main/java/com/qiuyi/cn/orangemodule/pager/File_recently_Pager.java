package com.qiuyi.cn.orangemodule.pager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.v4.provider.DocumentFile;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.Resource;
import com.qiuyi.cn.myloadingdialog.LoadingDialog;
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
import com.qiuyi.cn.orangemodule.util.WriteToUdisk;

import java.io.File;
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

    //全选模块
    private RelativeLayout rl_selectAll;
    //全选按钮
    private TextView bt_selectAll;
    //底部导航模块
    private LinearLayout ll_pager_recently_bom;
    //删除，拷贝到U盘，取消
    private TextView tv_delete,tv_copy,tv_cancel;


    private boolean isSelectAll = true;

    private List<FileBean> listFileBean;

    private float rlposY,rlnowY;
    private float grposY,grnowY;
    private int count = 0;
    private float distance = 0.0f;


    private LoadingDialog dialog = new LoadingDialog.Builder(mActivity)
            .setMessage("复制中")
            .setCancelable(false)
            .setCancelOutside(false)
            .create();
    private WriteToUdisk udiskUtil = WriteToUdisk.getInstance(mActivity.getApplicationContext(),mActivity);


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

        rl_selectAll = view.findViewById(R.id.rl_selectAll);
        bt_selectAll = view.findViewById(R.id.bt_selectAll);
        ll_pager_recently_bom = view.findViewById(R.id.ll_pager_recently_bom);
        tv_delete = view.findViewById(R.id.tv_delete);
        tv_copy = view.findViewById(R.id.tv_copy);
        tv_cancel = view.findViewById(R.id.tv_cancel);


        listFileBean = new ArrayList<>();

        myFrameLayout.addView(view);
    }


    @Override
    public void initData() {
        super.initData();

        initBroadcast();

        addData();
    }



    //选中的是title标题栏，下面需要全部选中
    private void selectAll(List<FileBean> allFileBean,int position,boolean[] flag){
        for(int i=position;i<allFileBean.size();i++){
            FileBean allFile = allFileBean.get(i);
            if(allFile.getFiletype()==3){
                flag[i] = !flag[i];
                break;
            }
            flag[i] = !flag[i];
        }
    }

    //获取数据
    private void addData() {

        myGridManager = new GridLayoutManager(mActivity,4);

        myRecyclerView.setLayoutManager(myGridManager);

        myadapter = new RecentlyAdapter(mActivity, listFileBean,myGridManager);

        myRecyclerView.setAdapter(myadapter);

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



        myadapter.setOnFileItemClick(new RecentlyAdapter.FileItemClick() {
            @Override
            public void openFile(View view, int position, List<FileBean> allFileBean) {
                FileBean fileBean = allFileBean.get(position);

                boolean isShowBox = myadapter.isShowCheckBox();

                if(isShowBox){
                    if(fileBean.getFiletype()!=3&& fileBean.getFiletype()!=0){
                        //正在显示checkbox
                        boolean[] flag = myadapter.getFlag();

                        flag[position] = !flag[position];

                        //如果当前position是选中状态，那么就从这个点去找开始和结束点
                        //这里是，当一个类别中的文件全部选择，那么头文件也变为选中状态
                        isToSelectAll(position,allFileBean,flag);

                        myadapter.setFlag(flag);
                        myadapter.notifyDataSetChanged();
                    }
                }else{
                    if(fileBean.getFiletype()!=0 && fileBean.getFiletype()!=3){
                        FileUtilOpen.openFileByPath(mActivity,fileBean.getPath());
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position, final List<FileBean> allFileBean) {
                FileBean fileBean = allFileBean.get(position);
                if(fileBean.getFiletype()!=3){

                    //头部状态栏显示
                    rl_selectAll.setVisibility(View.VISIBLE);
                    //底部状态栏显示
                    ll_pager_recently_bom.setVisibility(View.VISIBLE);

                    bt_selectAll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(isSelectAll){
                                myadapter.selectAll();
                                isSelectAll = false;
                                bt_selectAll.setText("取消全选");
                            }else{
                                myadapter.noSelect();
                                isSelectAll = true;
                                bt_selectAll.setText("全选");
                            }
                            myadapter.notifyDataSetChanged();
                        }
                    });
                    tv_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //头部状态栏显示
                            rl_selectAll.setVisibility(View.GONE);
                            //底部状态栏显示
                            ll_pager_recently_bom.setVisibility(View.GONE);

                            myadapter.setShowCheckBox(false);
                            myadapter.ReFresh();

                        }
                    });

                    tv_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //选择，显示一下有哪几个选择了
                            boolean[] flag = myadapter.getFlag();
                            for(int i = flag.length-1;i>=0;i--){
                                if(flag[i]){
                                    Log.e("select", "选中："+i);
                                    allFileBean.remove(i);
                                }
                            }
                            myadapter.ReFresh();
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
                                        boolean[] flag = myadapter.getFlag();
                                        for(int i = flag.length-1;i>=0;i--){
                                            if(flag[i]){
                                                Log.e("select", "选中："+i);
                                                if(allFileBean.get(i).getFiletype()!=0 && allFileBean.get(i).getFiletype()!=3)
                                                {
                                                    File file = new File(allFileBean.get(i).getPath());
                                                    udiskUtil.writeToSDFile(mActivity.getApplicationContext(),file,udiskUtil.getCurrentFolder());
                                                }
                                            }
                                        }
                                        mActivity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                //头部状态栏显示
                                                rl_selectAll.setVisibility(View.GONE);
                                                //底部状态栏显示
                                                ll_pager_recently_bom.setVisibility(View.GONE);

                                                myadapter.setShowCheckBox(false);
                                                myadapter.ReFresh();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        });
                    }


                    myadapter.setShowCheckBox(true);
                    boolean[] flag = myadapter.getFlag();

                    if(fileBean.getFiletype()==0){
                        selectAll(allFileBean,position,flag);
                    }else{
                        flag[position] = true;
                        isToSelectAll(position,allFileBean,flag);
                    }

                    myadapter.setFlag(flag);

                    myadapter.notifyDataSetChanged();
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

                        if((!istop && distance>=0 && dy>0) || myViewGroup.getScrollY()<0){

                            distance += dy;
                            count++;

                            if(count==1){
                                distance -= dy;
                            }

                            myViewGroup.scrollTo(0, ((int) -distance+10)/2);
                            setLockDisplay(distance/2,myViewGroup.mRefreshHeight,myViewGroup.mSecretHeight,1);
                        }

                        rlposY = rlnowY;
                        break;
                    case MotionEvent.ACTION_UP:
                        myViewGroup.scrollTo(0, 0);
                        setViewDispaly(distance/2,myViewGroup.mRefreshHeight,myViewGroup.mSecretHeight,1,null);
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

    //单个->全部
    private void isToSelectAll(int position, List<FileBean> allFileBean, boolean[] flag) {
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
}
/*                        GridLayoutManager manager = (GridLayoutManager) myRecyclerView.getLayoutManager();
                        int l1 = manager.findFirstVisibleItemPosition();
                        int l2 = manager.findFirstCompletelyVisibleItemPosition();*/

/*                        Log.e("move", "istop："+!istop+"distance："+distance+"srollY"+myViewGroup.getScrollY()+"posy："+l1+"nowy："+l2);*/

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
