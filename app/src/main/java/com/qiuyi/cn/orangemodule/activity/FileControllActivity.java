package com.qiuyi.cn.orangemodule.activity;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TextView;

import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.adapter.LeftListViewAdapter;
import com.qiuyi.cn.orangemodule.adapter.MyPagerAdapter;
import com.qiuyi.cn.orangemodule.bean.LeftItem;
import com.qiuyi.cn.orangemodule.myview.MyPopWindow;
import com.qiuyi.cn.orangemodule.myview.MyViewPager;
import com.qiuyi.cn.orangemodule.pager.BasePager;
import com.qiuyi.cn.orangemodule.pager.BaseRefreshPager;
import com.qiuyi.cn.orangemodule.pager.File_U_Pager;
import com.qiuyi.cn.orangemodule.pager.File_native_Pager;
import com.qiuyi.cn.orangemodule.pager.File_recently_Pager;
import com.qiuyi.cn.orangemodule.util.Constant;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.FileBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.ImageBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.MusicBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.VideoBean;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/3/13.
 * 文件管理模块
 */
public class FileControllActivity extends BaseActivity{

 /*   @BindView(R.id.tab_layout)
    TabLayout myTabLayout;

    @BindView(R.id.view_pager)
    ViewPager myViewPager;*/

    private TabLayout myTabLayout;
    private ViewPager myViewPager;

    private static final String[] titles = {"最近","本地","U盘"};
    private BasePager myRecentPager,myNativePager,myUPager;
    private List<BasePager> listPager;
    private MyPagerAdapter myAdapter;




    /* 继承标题等添加TabLayout+ViewPager*/
    @Override
    public void addPagerView(FrameLayout mAddView) {
        View view = View.inflate(this,R.layout.tab_viewpager_layout,null);
        //ButterKnife.bind(this,view);
        myTabLayout = view.findViewById(R.id.tab_layout);
        myViewPager = view.findViewById(R.id.view_pager);

        mAddView.addView(view);
    }

    @Override
    public void initData() {

        //初始化ViewPager
        initPager();

        myAdapter = new MyPagerAdapter(listPager,titles);

        myViewPager.setAdapter(myAdapter);
        myTabLayout.setupWithViewPager(myViewPager);

    }



    //初始化ViewPager
    private void initPager() {
        listPager = new ArrayList<>();
        if(myRecentPager == null){
            myRecentPager = new File_recently_Pager(this);
            listPager.add(myRecentPager);
        }
        if(myNativePager == null){
            myNativePager = new File_native_Pager(this);
            listPager.add(myNativePager);
        }
        if(myUPager == null){
            myUPager = new File_U_Pager(this);
            listPager.add(myUPager);
        }
    }

}
