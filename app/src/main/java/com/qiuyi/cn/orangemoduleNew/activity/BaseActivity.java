package com.qiuyi.cn.orangemoduleNew.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.adapter.LeftListViewAdapter;
import com.qiuyi.cn.orangemoduleNew.bean.LeftItem;
import com.qiuyi.cn.orangemoduleNew.util.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/14.
 * 基础活动类
 */
public abstract class BaseActivity extends BasePermission{
    //添加其他布局
    public FrameLayout mAddView;
    //侧边栏
    public ListView listView;
    //下拉菜单
    public TextView myTextView;
    //侧滑栏
    private DrawerLayout drawerLayout;
    //按钮
    private ImageView myImgView;
    //备份还原
    private RadioButton rb_reduction;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filecontroll);

        initView();

        addPagerView(mAddView);

        //得到基础权限
        //getBasePermission();

        initData();
    }

    //添加PagerView
    public void addPagerView(FrameLayout mAddView) {
    }

    //数据的添加
    public abstract void initData();

    //初始化View
    private void initView() {
        //空白部分
        mAddView = findViewById(R.id.myView);
        //侧滑栏的listView
        listView = findViewById(R.id.drawerLayout_listView);
        //标题栏
        myTextView = findViewById(R.id.tv_drop);
        //侧滑栏
        drawerLayout = findViewById(R.id.drawerLayout);
        //侧滑栏按钮
        myImgView = findViewById(R.id.img_more);
        //备份还原
        rb_reduction = findViewById(R.id.rb_reduction);


        //禁止手势滑动
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        //打开手势滑动
        //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);


        final List<LeftItem> list = new ArrayList<LeftItem>();
        for(int i = 0; i< Constant.IMAGES.length; i++){
            LeftItem item = new LeftItem(Constant.IMAGES[i],Constant.TEXTSHOW[i]);
            list.add(item);
        }

        LeftListViewAdapter adapter = new LeftListViewAdapter(this,list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //隐藏侧滑栏
                showDrawerLayout();
            }
        });

        myImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);//侧滑打开
            }
        });

        myTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

/*                new MyPopWindow(getApplicationContext(), new MyPopWindow.OnItemClickListener() {
                    @Override
                    public void onItemClick(PopupWindow popupWindow, int position) {
                        switch (position){
                            case 1:
                                popupWindow.dismiss();
                                break;
                            case 2:
                                //去到主模块
                                goToMainControll();
                                popupWindow.dismiss();
                                break;
                            case 3:
                                popupWindow.dismiss();
                                break;
                            case 4:
                                popupWindow.dismiss();
                                break;
                        }
                    }
                }).setText("首页").showAsDropDown(myTextView);*/
            }
        });


        //备份还原
        rb_reduction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(myActivity,BkrtActivity.class);
                startActivity(intent);
            }
        });

    }

    //侧滑栏显示隐藏控制
    private void showDrawerLayout() {
        if(!drawerLayout.isDrawerOpen(Gravity.LEFT)){
            drawerLayout.openDrawer(Gravity.LEFT);
        }else{
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

/*    //去到主模块
    private void goToMainControll(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }*/


}
