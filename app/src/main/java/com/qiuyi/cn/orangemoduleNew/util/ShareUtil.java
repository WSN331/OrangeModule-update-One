package com.qiuyi.cn.orangemoduleNew.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Administrator on 2018/2/1.
 * SharedPreference的存储方法
 */
public class ShareUtil {

    private static final String SAVE_NAME = "SaveFile";
    private static final Context context = MyApplication.getContext();
    private static SharedPreferences sp;

    public static void setString(String key,String value){
        if(sp==null){
            sp = context.getSharedPreferences(SAVE_NAME,Context.MODE_PRIVATE);
        }
        sp.edit().putString(key,value).commit();
    }

    public static String getString(String key,String value){
        if(sp==null){
            sp = context.getSharedPreferences(SAVE_NAME,Context.MODE_PRIVATE);
        }
        return sp.getString(key,value);
    }

    public static void setBoolean(String key,boolean value){
        if(sp==null){
            sp = context.getSharedPreferences(SAVE_NAME,Context.MODE_PRIVATE);
        }
        sp.edit().putBoolean(key,value).commit();
    }

    public static boolean getBoolean(String key,boolean value){
        if(sp==null){
            sp = context.getSharedPreferences(SAVE_NAME,Context.MODE_PRIVATE);
        }
        return sp.getBoolean(key,value);
    }

    public static void removeMsg(String key){
        if(sp==null){
            sp = context.getSharedPreferences(SAVE_NAME,Context.MODE_PRIVATE);
        }
        sp.edit().remove(key).commit();
    }
}
