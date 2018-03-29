package com.qiuyi.cn.orangemodule.interfaceToutil;

/**
 * Created by Administrator on 2018/3/19.
 * 下拉刷新
 */
public interface MyScrollerListener {

    //分别返回的是，下拉距离，刷新栏高度，私密空间高度,最后一个参数判断刷新的哪个界面，path参数用来处理U盘界面
    void setViewDispaly(float distance,int refreshHeight,int secretHeight,int size,String path);

    //时刻变化的拉动距离
    void setLockDisplay(float distance,int refreshHeight,int secretHeight,int size);
}
