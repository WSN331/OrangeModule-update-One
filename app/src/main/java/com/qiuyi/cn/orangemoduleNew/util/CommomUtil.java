package com.qiuyi.cn.orangemoduleNew.util;

import com.google.gson.Gson;
import com.qiuyi.cn.orangemoduleNew.bean.UserGet;

/**
 * Created by Administrator on 2018/2/1.
 * 普通工具类
 */
public class CommomUtil {

    private static String loginUser = null;
    private static UserGet nowUser = null;

    //得到当前登录的用户
    public static UserGet getUserInfo(){
        loginUser = ShareUtil.getString(Constant.MYMSG_LOGIN,"");
        if(loginUser!=null){
            Gson gson = new Gson();
            nowUser = gson.fromJson(loginUser,UserGet.class);
            if(nowUser!=null){
                return nowUser;
            }
        }
        return null;
    }
}
