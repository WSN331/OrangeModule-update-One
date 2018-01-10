package com.qiuyi.cn.orangemodule.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.qiuyi.cn.orangemodule.fragment.MainFragmentSmartHome;
import com.qiuyi.cn.orangemodule.pager.BasePager;

import java.util.List;

/**
 * Created by Administrator on 2017/12/28.
 *
 * Pager + viewPager +TabLayout的适配器
 *
 */
public class MyPagerAdapter extends PagerAdapter{

    private List<BasePager> listPager;

    public MyPagerAdapter(List<BasePager> listPager){
        this.listPager = listPager;
    }

    //标题的绑定
    @Override
    public CharSequence getPageTitle(int position) {
        return MainFragmentSmartHome.titles[position];
    }

    @Override
    public int getCount() {
        return listPager.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    //初始化界面
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        //得到每一个界面
        BasePager basePager = listPager.get(position);

        //初始化数据
        basePager.initData();

        container.addView(basePager.mRootView);

        return basePager.mRootView;

    }

    //切换移除
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
