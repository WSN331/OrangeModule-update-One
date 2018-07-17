package com.qiuyi.cn.orangemoduleNew.util.FileManager.permission;

import android.app.Activity;

/**
 * Created by Administrator on 2018/4/1.
 * 获取基本权限
 */
public interface GetPermission {

    //得到基础权限
    void getPermission(Activity activity, String[] permissions);

}
