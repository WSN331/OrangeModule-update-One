package com.qiuyi.cn.orangemodule.fragment;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.activity.ShoppingCarActivity;
import com.qiuyi.cn.orangemodule.adapter.MyFragmentAdapter;
import com.qiuyi.cn.orangemodule.adapter.MyPagerAdapter;
import com.qiuyi.cn.orangemodule.myview.MyViewPager;
import com.qiuyi.cn.orangemodule.pager.BasePager;
import com.qiuyi.cn.orangemodule.pager.BluetoothPager;
import com.qiuyi.cn.orangemodule.pager.FramePager;
import com.qiuyi.cn.orangemodule.pager.RecommendPager;
import com.qiuyi.cn.orangemodule.pager.UsbPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/12/26.
 *
 * 商城部分
 */
public class MainFragmentSmartHome extends BaseFragment{

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;

    @BindView(R.id.view_pager)
    ViewPager myViewPager;

    //购物车
    @BindView(R.id.card)
    ImageView card;

    public static final String[] titles = {"推荐", "框架", "USB", "蓝牙"};
    private BasePager recommendPager, framePager, usbPager, bluetoothPager;
    private List<BasePager> listPager= new ArrayList<>();
    private MyPagerAdapter myAdapter;

    @Override
    public void initView(FrameLayout addView) {
        View view = View.inflate(mActivity, R.layout.fragment_smarthome,null);
        ButterKnife.bind(this,view);
        addView.addView(view);
    }

    @Override
    public void initData() {
        //初始化Pager
        initPager();

        myAdapter = new MyPagerAdapter(listPager,titles);
        myViewPager.setAdapter(myAdapter);
        tabLayout.setupWithViewPager(myViewPager);

        listenerClick();
    }

    //装配ViewPager容器
    private void initPager() {
        //推荐页面
        if(recommendPager==null){
            recommendPager = new RecommendPager(mActivity);
            listPager.add(recommendPager);
        }
        //框架页面
        if(framePager==null){
            framePager = new FramePager(mActivity);
            listPager.add(framePager);
        }
        //USB页面
        if(usbPager==null){
            usbPager = new UsbPager(mActivity);
            listPager.add(usbPager);
        }
        //蓝牙页面
        if(bluetoothPager==null){
            bluetoothPager = new BluetoothPager(mActivity);
            listPager.add(bluetoothPager);
        }
    }


    //点击事件监听
    private void listenerClick() {
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, ShoppingCarActivity.class);
                mActivity.startActivity(intent);
            }
        });
    }
}
