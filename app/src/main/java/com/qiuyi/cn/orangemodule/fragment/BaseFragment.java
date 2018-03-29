package com.qiuyi.cn.orangemodule.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.activity.FileControllActivity;
import com.qiuyi.cn.orangemodule.adapter.LeftListViewAdapter;
import com.qiuyi.cn.orangemodule.bean.LeftItem;
import com.qiuyi.cn.orangemodule.myview.MyPopWindow;
import com.qiuyi.cn.orangemodule.util.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;


/**
 * Created by Administrator on 2017/8/8.
 *
 * fragment布局的基类
 *
 * fragment的生命周期
 *              重要              重要             重要
 *    粘贴    创建fragment  创建fragment布局   创建fragment所依赖的Activity
 * onAttach()->onCreate()->onCreateView()->onActivityCreated()->onStart()->onResume()
 * onDetach()<-onDestroy<-onDestroyView()                     <-onStop()<-onPause()
 *
 * 这个类相当于在写一个Activity布局，用了碎片的思想，使用Fragment实现不同布局，但是方法类似
 * 都是现需要初始化UI，然后初始化数据Data
 */
public abstract class BaseFragment extends Fragment{

    //使用Context对象,获取activity
    public Activity mActivity;
    //创建自己的布局
    public View mRootView;
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

    //fragment创建
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //这个就是能够实现View重新加载,否则会导致fragment切换中消失
/*        if(mRootView!=null){
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                parent.removeView(mRootView);
            }
            return mRootView;
        }
        this.mRootView = initView();
        return mRootView;*/
        mRootView = inflater.inflate(R.layout.ttitle_layout,null);
        mAddView = mRootView.findViewById(R.id.myView);
        initView(mAddView);
        initMyData();
        return mRootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    //创建子布局
    public void initView(FrameLayout mAddView){

    };

    /**
     * 初始化数据
     */
    private void initMyData() {
        listView = mRootView.findViewById(R.id.drawerLayout_listView);
        myTextView = mRootView.findViewById(R.id.tv_drop);
        drawerLayout = mRootView.findViewById(R.id.drawerLayout);
        myImgView = mRootView.findViewById(R.id.img_more);

        //禁止手势滑动
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        //打开手势滑动
        //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);


        final List<LeftItem> list = new ArrayList<LeftItem>();
        for(int i=0;i< Constant.IMAGES.length;i++){
            LeftItem item = new LeftItem(Constant.IMAGES[i],Constant.TEXTSHOW[i]);
            list.add(item);
        }

        LeftListViewAdapter adapter = new LeftListViewAdapter(mActivity,list);
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
                new MyPopWindow(mActivity, new MyPopWindow.OnItemClickListener() {
                    @Override
                    public void onItemClick(PopupWindow popupWindow, int position) {
                        switch (position){
                            case 1:
                                popupWindow.dismiss();
                                break;
                            case 2:
                                //去到文件管理模块
                                goToFileControll();
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
                }).showAsDropDown(myTextView);
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

    //去到文件管理模块
    private void goToFileControll(){
        Intent intent = new Intent(mActivity, FileControllActivity.class);
        startActivity(intent);
    }


    //数据初始化
    public abstract void initData();
}
