package com.qiuyi.cn.orangemodule.util;

import android.app.Application;
import android.content.Context;

import com.orm.SugarContext;

/**
 * Created by Administrator on 2018/1/7.
 *
 * 获取全局Context
 */
public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        SugarContext.init(this);

        context = getApplicationContext();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //释放资源
        SugarContext.terminate();
    }

    //返回全局context
    public static Context getContext(){
        return context;
    }
}
