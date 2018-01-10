package com.qiuyi.cn.orangemodule;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;

import com.qiuyi.cn.orangemodule.adapter.MyFragmentAdapter;
import com.qiuyi.cn.orangemodule.bean.TabBottom;
import com.qiuyi.cn.orangemodule.fragment.MainFragmentEquipment;
import com.qiuyi.cn.orangemodule.fragment.MainFragmentMyMessage;
import com.qiuyi.cn.orangemodule.fragment.MainFragmentSmartHome;
import com.qiuyi.cn.orangemodule.myview.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity{

    //选中的图案
    private static final int[] SELECTED_IMAGES = {R.drawable.equipment2,R.drawable.smart2,R.drawable.mymsg2};
    //未选中的图案
    private static final int[] UNSELECTED_IMAGES = {R.drawable.equipment1,R.drawable.smart1,R.drawable.mymsg1};

    //fragment的三个部分存放处
    @BindView(R.id.view_pager)
    MyViewPager mViewPager;
    //导航栏
    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    //fragment存放
    private List<Fragment> fragmentList;
    //fragmentViewpager适配器
    private MyFragmentAdapter myFragmentAdapter;

    //三个放进去的图片对象
    private List<TabBottom> bottomList;
    //Tablayout底部的三个容器
    private List<TabLayout.Tab> tabList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        //初始化ViewPager
        initViewPager();
        //初始化底部栏
        initTabLayout();

    }


    //底部栏TabLayout初始化
    private void initTabLayout() {
        bottomList = new ArrayList<>();
        tabList = new ArrayList<>();

        //初始化图片和底部容器
        for(int i=0;i<SELECTED_IMAGES.length;i++){
            TabBottom tabean = new TabBottom(UNSELECTED_IMAGES[i],SELECTED_IMAGES[i]);
            bottomList.add(tabean);

            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            tabList.add(tab);
        }

        //每个TabBottom的点击事件
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //若点击的是第一个按钮
                if(tab == mTabLayout.getTabAt(0)){
                    tabList.get(0).setIcon(bottomList.get(0).getSelectedImage());
                    tabList.get(1).setIcon(bottomList.get(1).getUnselectedImage());
                    tabList.get(2).setIcon(bottomList.get(2).getUnselectedImage());
                    //显示第一个fragment
                    mViewPager.setCurrentItem(0);
                }
                if(tab == mTabLayout.getTabAt(1)){
                    tabList.get(1).setIcon(bottomList.get(1).getSelectedImage());
                    tabList.get(0).setIcon(bottomList.get(0).getUnselectedImage());
                    tabList.get(2).setIcon(bottomList.get(2).getUnselectedImage());
                    //显示第一个fragment
                    mViewPager.setCurrentItem(1);
                }
                if(tab == mTabLayout.getTabAt(2)){
                    tabList.get(2).setIcon(bottomList.get(2).getSelectedImage());
                    tabList.get(1).setIcon(bottomList.get(1).getUnselectedImage());
                    tabList.get(0).setIcon(bottomList.get(0).getUnselectedImage());
                    //显示第一个fragment
                    mViewPager.setCurrentItem(2);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        //第一个页面进去就初始化
        tabList.get(0).setIcon(bottomList.get(0).getSelectedImage());
        tabList.get(1).setIcon(bottomList.get(1).getUnselectedImage());
        tabList.get(2).setIcon(bottomList.get(2).getUnselectedImage());
        //显示第一个fragment
        mViewPager.setCurrentItem(0);
    }


    //ViewPager初始化
    private void initViewPager() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new MainFragmentEquipment());
        fragmentList.add(new MainFragmentSmartHome());
        fragmentList.add(new MainFragmentMyMessage());

        myFragmentAdapter = new MyFragmentAdapter(getSupportFragmentManager(),fragmentList);

        mViewPager.setAdapter(myFragmentAdapter);
        //TabLayout绑定
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
