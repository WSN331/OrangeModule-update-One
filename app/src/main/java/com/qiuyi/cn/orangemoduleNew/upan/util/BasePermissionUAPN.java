package com.qiuyi.cn.orangemoduleNew.upan.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;


import com.qiuyi.cn.orangemoduleNew.upan.BasePermissionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限申请模块
 */
public class BasePermissionUAPN extends Activity{

    public Activity myActivity = this;
    private BasePermissionListener myPermissionlistener;

    /**
     * 权限申请
     * @param permissions 待申请的结果权限
     * @param listener 申请结果监听事件
     */
    public void requestToPermission(String[] permissions,BasePermissionListener listener){
        this.myPermissionlistener = listener;

        //待授权集合
        List<String> unPermissionList = new ArrayList<>();

        //遍历传递过来的权限集合
        for(String permission : permissions){
            if(ActivityCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED){
                //未授权的加入到待授权集合中
                unPermissionList.add(permission);
            }
        }

        //将待授权集合去授权
        if(!unPermissionList.isEmpty()){
            //未全部授权去授权
            ActivityCompat.requestPermissions(this,unPermissionList.toArray(new String[unPermissionList.size()]),1);//第二个参数是请求码
        }else{
            //授权成功
            listener.onGranted();
        }
    }

    /**
     * 权限申请结果
     * @param requestCode 请求码
     * @param permissions 所有权限集合
     * @param grantResults 授权结果集合
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case 1:
                //遍历授权结果
                if(grantResults.length > 0){
                    //被用户拒绝的权限
                    List<String> deniedPermissions = new ArrayList<>();
                    //用户通过的权限
                    List<String> grantedPermissions = new ArrayList<>();

                    for(int i=0;i<grantResults.length;i++){
                        //获取授权结果
                        int grantResult = grantResults[i];

                        //遍历授权结果
                        if(grantResult!=PackageManager.PERMISSION_GRANTED){
                            //用户拒绝的授权
                            deniedPermissions.add(permissions[i]);
                        }else{
                            //用户同意的授权
                            grantedPermissions.add(permissions[i]);
                        }
                    }

                    //判断是否授权成功
                    if(deniedPermissions.isEmpty()){
                        //全部授权成功
                        myPermissionlistener.onGranted();
                    }else{
                        //未全部授权成功
                        myPermissionlistener.onGrantedSuccess(grantedPermissions);//回调授权成功的接口
                        myPermissionlistener.onDenied(deniedPermissions);//回调授权失败的接口
                    }
                }
                break;
            default:
                break;
        }
    }
}
