package com.qiuyi.cn.orangemodule.pager;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.activity.SecretActivity;
import com.qiuyi.cn.orangemodule.interfaceToutil.MyScrollerListener;
import com.qiuyi.cn.orangemodule.myview.MyViewGroup;
import com.qiuyi.cn.orangemodule.util.Constant;
import com.qiuyi.cn.orangemodule.util.FileManager.service.FindAllFile_Service;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/3/15.
 * 带有刷新和进入隐私模块的BasePager
 */
public class BaseRefreshPager extends BasePager implements MyScrollerListener {

/*    @BindView(R.id.myViewGroup)
    MyViewGroup myViewGroup;

    //锁的位置
    @BindView(R.id.lock)
    ImageView myLock;

    //刷新显示
    @BindView(R.id.tv_dropRefresh)
    TextView myDropRefresh;

    //myFrameLayout显示
    @BindView(R.id.myFrameLayout)
    FrameLayout myFrameLayout;*/

    public MyViewGroup myViewGroup;
    private ImageView myLock;
    private TextView myDropRefresh;
    private LinearLayout myFrameLayout;

    public BaseRefreshPager(Activity mActivity){
        super(mActivity);
    }


    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_baserefresh_layout, null);
        //ButterKnife.bind(mActivity,view);
        myViewGroup = view.findViewById(R.id.myViewGroup);
        myLock = view.findViewById(R.id.lock);
        myDropRefresh = view.findViewById(R.id.tv_dropRefresh);
        myFrameLayout = view.findViewById(R.id.myFrameLayout);

        addView(myFrameLayout);

        return view;
    }


    public void addView(LinearLayout myFrameLayout){
        myFrameLayout.removeAllViews();
    }

    //初始化数据
    @Override
    public void initData(){

    }

    @Override
    public void setViewDispaly(float distance, int refreshHeight, int secretHeight,int size,String path) {
        //松开时的滑动变化
        if(0<distance && distance<=refreshHeight){
            //显示下拉刷新
            myViewGroup.scrollTo(0,0);
        }
        if(refreshHeight<distance && distance<=(refreshHeight+(secretHeight/5.0f))){
            //显示松开刷新
            myViewGroup.scrollTo(0,-refreshHeight);
            myDropRefresh.setText("○正在刷新○");

            if(size == 1){
                //最近界面刷新
                mActivity.startService(new Intent(mActivity, FindAllFile_Service.class));
            }
            if(size == 2){
                //本地界面刷新
                Intent intent = new Intent(Constant.NATIVEFRESH);
                intent.putExtra("NativeToFresh",true);
                mActivity.sendBroadcast(intent);
            }
            if(size == 3){
                //U盘界面刷新
                if(path==null){
                    Toast.makeText(mActivity,"U盘没有插入",Toast.LENGTH_SHORT).show();
                    myViewGroup.scrollTo(0,0);
                }else{
                    Intent intent = new Intent(Constant.UPANREFRESH);
                    intent.putExtra("UpanToFresh",true);
                    mActivity.sendBroadcast(intent);
                }
            }
/*            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    myViewGroup.scrollTo(0,0);
                }
            }).start();*/

        }
        if((refreshHeight+(secretHeight/5.0f))<distance && distance<=(secretHeight+refreshHeight)){
            //显示松开进入私密模块
            myViewGroup.scrollTo(0,0);

            Intent intent = new Intent(mActivity, SecretActivity.class);
            mActivity.startActivity(intent);

        }
    }


    @Override
    public void setLockDisplay(float distance, int refreshHeight, int secretHeight,int size) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) myLock.getLayoutParams();

        //正在滑动时的UI变化
        if(0<distance && distance<=refreshHeight){
            myDropRefresh.setText("↓↓下拉刷新");
            //显示下拉刷新
        }
        if(refreshHeight<distance && distance<=(refreshHeight+(secretHeight/5.0f))){
            //显示松开刷新
            myDropRefresh.setText("↑↑松开刷新");
        }
        if((refreshHeight+(secretHeight/5.0f))<distance && distance<=(secretHeight+refreshHeight)){
            //显示松开进入私密模块
            //SecretView显示一个小锁
            myDropRefresh.setText("↑进入私密模块↑");
        }

        float realDistance = distance - refreshHeight;
        if(realDistance>0){
            params.topMargin = (int) (secretHeight -realDistance + 150);
            myLock.setLayoutParams(params);
        }
    }



}
