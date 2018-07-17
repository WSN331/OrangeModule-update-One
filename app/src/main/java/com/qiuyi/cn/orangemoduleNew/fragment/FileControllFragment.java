package com.qiuyi.cn.orangemoduleNew.fragment;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.qiuyi.cn.orangemoduleNew.MainActivity;
import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.adapter.MyPagerAdapter;
import com.qiuyi.cn.orangemoduleNew.pager.BasePager;
import com.qiuyi.cn.orangemoduleNew.pager.File_U_Pager;
import com.qiuyi.cn.orangemoduleNew.pager.File_native_Pager;
import com.qiuyi.cn.orangemoduleNew.pager.File_recently_Pager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/30.
 * 文件管理主界面
 */
public class FileControllFragment extends BaseFragment{


    private TabLayout myTabLayout;
    private ViewPager myViewPager;

    private static final String[] titles = {"最近","本地","U盘"};
    private BasePager myRecentPager,myNativePager,myUPager;
    private List<BasePager> listPager;
    private MyPagerAdapter myAdapter;

    private int[] init;

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.tab_viewpager_layout,null);

        myTabLayout = view.findViewById(R.id.tab_layout);
        myViewPager = view.findViewById(R.id.view_pager);

        return view;
    }

    @Override
    public void initData() {
        //初始化ViewPager
        initPager();

        init = new int[3];

        myAdapter = new MyPagerAdapter(listPager,titles);

        myViewPager.setAdapter(myAdapter);
        myTabLayout.setupWithViewPager(myViewPager);

        myViewPager.setOffscreenPageLimit(3);

        if(MainActivity.isFromUdisk){
            myViewPager.setCurrentItem(2);
            listPager.get(2).initData();

            init[2] = 1;

            MainActivity.isFromUdisk = false;
        }else{
            myViewPager.setCurrentItem(0);
            listPager.get(0).initData();

            init[0] = 1;
        }

        myViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if(init[position]!=1){
                    listPager.get(position).initData();
                    init[position] = 1;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    //初始化ViewPager
    private void initPager() {
        listPager = new ArrayList<>();
        if(myRecentPager == null){
            myRecentPager = new File_recently_Pager(mActivity);
            listPager.add(myRecentPager);
        }
        if(myNativePager == null){
            myNativePager = new File_native_Pager(mActivity);
            listPager.add(myNativePager);
        }
        if(myUPager == null && MainActivity.isHaveUpan){
            myUPager = new File_U_Pager(mActivity);
            listPager.add(myUPager);
        }
    }


}
