package com.qiuyi.cn.orangemoduleNew.util.FileManager.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by Administrator on 2018/3/31.
 * 权限申请基本类
 */
public class PermissionUtil implements GetPermission {

    public PermissionUtil(){};

    private static class SinglePermissionUtil{
        private static final PermissionUtil instance = new PermissionUtil();
    }

    public static final PermissionUtil getInstance(){
        return SinglePermissionUtil.instance;
    }

    @Override
    public void getPermission(Activity activity, String[] permissions) {
        //检查版本,版本大于6.0需要手动申请
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            for(String permission:permissions){
                //检查权限是否获取到了
                if(ActivityCompat.checkSelfPermission(activity,permission) != PackageManager.PERMISSION_GRANTED){

                    //如果App的权限申请曾经被用户拒绝过，就需要在这里和用户做出解释
                    if(ActivityCompat.shouldShowRequestPermissionRationale(activity,permission)){
                        Toast.makeText(activity, "please give me the permission", Toast.LENGTH_SHORT).show();
                    }else{
                     //进行权限申请
                        ActivityCompat.requestPermissions(activity,new String[]{permission}, ConstantPermission.PERMISSION_GET);
                    }
                }

            }
        }
    }


}
