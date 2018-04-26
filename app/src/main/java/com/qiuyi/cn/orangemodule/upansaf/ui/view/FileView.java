package com.qiuyi.cn.orangemodule.upansaf.ui.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;

/**
 * Created by ASUS on 2017/7/19 0019.
 */

public interface FileView {

    /**
     * 设置标题
     * @param preText 前标题
     * @param nowText 现标题
     */
    void setTitle(String preText, String nowText);

    /**
     * 给列表设置适配器
     * @param adapter 适配器
     */
    void setAdapter(DocumentFileAdapter adapter);

    /**
     * 获取适配器
     * @return 适配器
     */
    DocumentFileAdapter getAdapter();

    /**
     * 获取recyclerView
     * @return
     */
    RecyclerView getRecycler();

    /**
     * 设置刷新条
     * @param b 设置控制
     */
    void setRefreshing(boolean b);

    /**
     * 显示密码框
     */
    void showPasswordView();

    /**
     * u盘插入的操作
     * @param intent
     */
    void onUDiskInsert(Intent intent);

    /**
     * u盘移除的操作
     * @param intent
     */
    void onUDiskRemove(Intent intent);

    /**
     * 设置工具栏类型
     * @param type 工具栏类型
     */
    void setToolBarType(int type);

    /**
     * 获取全局context
     */
    Context getContext();

    /**
     * 获取Activity
     */
    Activity getActivity();
}
