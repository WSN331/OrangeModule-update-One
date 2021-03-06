package com.qiuyi.cn.orangemoduleNew.upansaf.db.util;


import com.qiuyi.cn.orangemoduleNew.upansaf.db.bean.AppInfo;
import com.qiuyi.cn.orangemoduleNew.upansaf.db.bean.CollectionFiles;
import com.qiuyi.cn.orangemoduleNew.upansaf.db.bean.SecretFiles;

import java.util.List;

/**
 * Created by ASUS on 2017/7/27 0027.
 */

public class DBUtil {

    //查询所有私密模块文件
    public static List<SecretFiles> getSecretFile(){
        List<SecretFiles> listSecretFiles = SecretFiles.listAll(SecretFiles.class);
        return listSecretFiles;
    }



    //根据文件路径查询文件是否存在
    public static CollectionFiles getCollectFile(String filePath){
        if(filePath!=null){
            List<CollectionFiles> listFiles = CollectionFiles.findWithQuery(CollectionFiles.class,"Select * from CollectionFiles where filePath=?",filePath);
            if(listFiles!=null && listFiles.size()>0){
                return listFiles.get(0);
            }
        }
        return null;
    }



    //获取一个新的appInfo，或者一个根据条件查询出来的AppInfo
    public static AppInfo getAppInfo(String rootPath){
        //1 没有传进来rootPath意味着需要一个新的AppInfo
        if(rootPath==null){
            AppInfo appInfo = new AppInfo();
            return appInfo;
        }else{
            //2 传进来一个rootPath就意味着要去找这个AppInfo
            List<AppInfo> listAppInfo = AppInfo.findWithQuery(AppInfo.class,"Select * from app_info where rootPath = ?",rootPath);

            if(listAppInfo!=null && listAppInfo.size()>0){
                //找到了就返回
                return listAppInfo.get(0);
            }else{
                //没有找到,返回空
                return null;
            }
        }
    }

    //设置Uri
    public static void setUri(AppInfo udiskInfo,String uri) {
        udiskInfo.setRootUriPath(uri);
        udiskInfo.save();
    }

}

/*    *//**
     * 获取缓存uri
     * @return
     *//*
    public static String getUri() {
        return getAppInfo().getRootUriPath();
    }

    *//**
     * 设置缓存uri
     * @param uri
     *//*
    public static void setUri(String uri) {
        AppInfo appInfo = getAppInfo();
        appInfo.setRootUriPath(uri);
        appInfo.save();
    }

    *//**
     * 获取应用消息
     * @return 应用消息
     *//*
    private static AppInfo getAppInfo() {
        Iterator<AppInfo> it = AppInfo.findAll(AppInfo.class);
        if (it.hasNext()) {
            AppInfo appInfo = it.next();
            if (appInfo != null) {
                return appInfo;
            }
        }
        AppInfo appInfo = new AppInfo();
        appInfo.setWrongPass(0);
        return appInfo;
    }*/


/*    *//**
 * 获取输错密码次数
 * @return 次数
 *//*
    public static int getWrongPassCount() {
        return getAppInfo().getWrongPass();
    }

    *//**
 * 设置输错密码次数
 * @param count 次数
 *//*
    public static void setWrongPassCount(int count) {
        AppInfo appInfo = getAppInfo();
        appInfo.setWrongPass(count);
        appInfo.save();
    }

    *//**
 * 初始化输错密码次数
 *//*
    public static void initWrongPassCount() {
        setWrongPassCount(0);
    }*/