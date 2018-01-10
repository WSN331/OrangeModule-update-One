package com.qiuyi.cn.orangemodule.pager;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.oragee.banners.BannerView;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.activity.GoodsActivity;
import com.qiuyi.cn.orangemodule.activity.OtherActivity;
import com.qiuyi.cn.orangemodule.util.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/12/28.
 *
 * 推荐页面
 */
public class RecommendPager extends BasePager implements View.OnClickListener{

    //轮播模块
    @BindView(R.id.bannerView)
    BannerView myBanView;

    //存储设备
    @BindView(R.id.backup_memory)
    FrameLayout memory;
    //外接扬声器
    @BindView(R.id.speaker)
    FrameLayout speaker;
    //温度传感器
    @BindView(R.id.temp_hum_mod)
    FrameLayout template;
    //闪光灯
    @BindView(R.id.led)
    FrameLayout led;
    //激光笔
    @BindView(R.id.laser_pointer)
    FrameLayout pint;
    //usb扩展
    @BindView(R.id.usb)
    LinearLayout usb;
    //扩展槽
    @BindView(R.id.dummy)
    LinearLayout dummy;
    //热键
    @BindView(R.id.hotkey)
    LinearLayout hotkey;
    //滑动笔
    @BindView(R.id.laser_pointer2)
    LinearLayout point2;

    //上新，推荐，抢购，更多
    @BindView(R.id.one)
    LinearLayout one;
    @BindView(R.id.two)
    LinearLayout two;
    @BindView(R.id.three)
    LinearLayout three;
    @BindView(R.id.four)
    LinearLayout four;

    public RecommendPager(Activity mActivity) {
        super(mActivity);
    }

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_recommend,null);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void initData() {

        //这里是轮播模块
        List<View> viewList = new ArrayList<>();
        for(int i=0;i< Constant.slid_images.length;i++){
            ImageView imageView = new ImageView(mActivity);
            imageView.setImageResource(Constant.slid_images[i]);
            viewList.add(imageView);
        }

        myBanView.setViewList(viewList);
        myBanView.startLoop(true);

        //按键监听
        listenerClick();

    }


    //不同模块之间的点击事件
    private void listenerClick() {

        one.setOnClickListener(this);
        two.setOnClickListener(this);
        three.setOnClickListener(this);
        four.setOnClickListener(this);

        memory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity,GoodsActivity.class);
                intent.putExtra("title",Constant.module_names[0]);
                intent.putExtra("price",Constant.module_prices[0]);
                intent.putExtra("image",Constant.module_images[0]);
                mActivity.startActivity(intent);
            }
        });

        speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity,GoodsActivity.class);
                intent.putExtra("title",Constant.module_names[1]);
                intent.putExtra("price",Constant.module_prices[1]);
                intent.putExtra("image",Constant.module_images[1]);
                mActivity.startActivity(intent);
            }
        });

        template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity,GoodsActivity.class);
                intent.putExtra("title",Constant.module_names[3]);
                intent.putExtra("price",Constant.module_prices[3]);
                intent.putExtra("image",Constant.module_images[3]);
                mActivity.startActivity(intent);
            }
        });

        led.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity,GoodsActivity.class);
                intent.putExtra("title",Constant.module_names[4]);
                intent.putExtra("price",Constant.module_prices[4]);
                intent.putExtra("image",Constant.module_images[4]);
                mActivity.startActivity(intent);
            }
        });

        pint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity,GoodsActivity.class);
                intent.putExtra("title",Constant.module_names[2]);
                intent.putExtra("price",Constant.module_prices[2]);
                intent.putExtra("image",Constant.module_images[2]);
                mActivity.startActivity(intent);
            }
        });

        point2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity,GoodsActivity.class);
                intent.putExtra("title",Constant.module_names[2]);
                intent.putExtra("price",Constant.module_prices[2]);
                intent.putExtra("image",Constant.module_images[2]);
                mActivity.startActivity(intent);
            }
        });

        usb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity,GoodsActivity.class);
                intent.putExtra("title",Constant.module_names[5]);
                intent.putExtra("price",Constant.module_prices[5]);
                intent.putExtra("image",Constant.module_images[5]);
                mActivity.startActivity(intent);
            }
        });

        dummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity,GoodsActivity.class);
                intent.putExtra("title",Constant.module_names[6]);
                intent.putExtra("price",Constant.module_prices[6]);
                intent.putExtra("image",Constant.module_images[6]);
                mActivity.startActivity(intent);
            }
        });

        hotkey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity,GoodsActivity.class);
                intent.putExtra("title",Constant.module_names[7]);
                intent.putExtra("price",Constant.module_prices[7]);
                intent.putExtra("image",Constant.module_images[7]);
                mActivity.startActivity(intent);
            }
        });
    }

    //点击事件
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.one:
            case R.id.two:
            case R.id.three:
            case R.id.four:
                Intent intent = new Intent(mActivity, OtherActivity.class);
                mActivity.startActivity(intent);
                break;
            default:break;
        }
    }
}
