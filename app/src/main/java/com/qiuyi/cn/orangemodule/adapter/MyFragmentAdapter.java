package com.qiuyi.cn.orangemodule.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2017/12/26.
 *
 * fragment + viewpager +TabLayout的适配器
 */
public class MyFragmentAdapter extends FragmentPagerAdapter{

    List<Fragment> fragmentList;

    public MyFragmentAdapter(FragmentManager supportFragmentManager, List<Fragment> fragmentList) {
        super(supportFragmentManager);

        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    //title的显示绑定
    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }
}
