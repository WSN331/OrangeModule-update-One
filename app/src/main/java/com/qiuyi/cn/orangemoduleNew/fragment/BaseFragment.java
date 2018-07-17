package com.qiuyi.cn.orangemoduleNew.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



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
        if(mRootView!=null){
            ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (parent != null) {
                parent.removeView(mRootView);
            }
            return mRootView;
        }
        this.mRootView = initView();

        return mRootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    //创建子布局
    public abstract View initView();

    //数据初始化
    public abstract void initData();
}
