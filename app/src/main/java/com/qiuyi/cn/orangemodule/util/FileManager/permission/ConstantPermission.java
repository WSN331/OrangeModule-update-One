package com.qiuyi.cn.orangemodule.util.FileManager.permission;

import android.Manifest;

/**
 * Created by Administrator on 2018/4/1.
 * 权限列表
 */
public class ConstantPermission {

    public static final int PERMISSION_GET = 0;

    //基本权限
    public static final String[] ACTION_PERMISSION = {
            //文件存储权限
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            //文件读取权限
            Manifest.permission.READ_EXTERNAL_STORAGE,
            //联系人读取权限
            Manifest.permission.READ_CONTACTS,
            //联系人存储权限
            Manifest.permission.WRITE_CONTACTS
    };

}
