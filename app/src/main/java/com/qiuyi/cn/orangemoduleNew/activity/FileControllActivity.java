package com.qiuyi.cn.orangemoduleNew.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;

import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.adapter.MyPagerAdapter;
import com.qiuyi.cn.orangemoduleNew.pager.BasePager;
import com.qiuyi.cn.orangemoduleNew.pager.File_U_Pager;
import com.qiuyi.cn.orangemoduleNew.pager.File_native_Pager;
import com.qiuyi.cn.orangemoduleNew.pager.File_recently_Pager;

import java.util.ArrayList;
import java.util.List;

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
