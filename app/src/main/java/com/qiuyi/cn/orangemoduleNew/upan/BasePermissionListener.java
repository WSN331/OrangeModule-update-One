package com.qiuyi.cn.orangemoduleNew.upan;

import java.util.List;

/**
 * 基础授权模块接口
 */
public interface BasePermissionListener {
    //授权成功
    void onGranted();
    //授权成功
    void onGrantedSuccess(List<String> grantedPermission);
    //授权失败
    void onDenied(List<String> deniedPermission);
}
