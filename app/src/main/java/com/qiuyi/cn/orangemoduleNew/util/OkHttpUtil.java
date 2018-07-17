package com.qiuyi.cn.orangemoduleNew.util;

import android.util.Log;

import com.google.gson.Gson;
import com.qiuyi.cn.orangemoduleNew.interfaceToutil.MyHttpUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/1/24.
 * okhttp3请求
 */
public class OkHttpUtil{
    private OkHttpClient mOkHttpClient;
    //静态内部类
    private OkHttpUtil(){
        //初始化OkHttpClient
        initOkHttpClient();
    }
    private static class OkHttpHolder{
        private final static OkHttpUtil instance = new OkHttpUtil();
    }
    public static OkHttpUtil getInstance(){
        return OkHttpHolder.instance;
    }

    //初始化OkHttpClient
    private void initOkHttpClient() {
        //获取存放缓存文件目录
        File sdcache = MyApplication.getContext().getExternalCacheDir();

        int cacheSize = 10 * 1024* 1024;
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)//连接超时（秒）
                .writeTimeout(20,TimeUnit.SECONDS)//写入超时（秒）
                .readTimeout(20,TimeUnit.SECONDS)//读取超时（秒）
                .cache(new Cache(sdcache.getAbsoluteFile(),cacheSize));//设置缓存
        mOkHttpClient = builder.build();
    }


    /**
     * post异步请求
     * @param formBody 请求体（表单）
     * @param urlAddress 请求链接
     */
    public <T> void postAsynHttp(RequestBody formBody, String urlAddress, final T myObject, final MyHttpUtil myHttp){
        final Request request = new Request.Builder()
                .url(urlAddress)
                .post(formBody)
                .build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("request","请求失败"+e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("request","请求成功");
                String str = response.body().string();

                //解析数据
                processData(str,myObject,myHttp);
            }
        });
    }


    /**
     * 解析数据
     * @param str 需要解析的字符串
     * @param myObject 将字符串解析成对应的类
     * @param myHttp 回调接口
     * @param <T> 传进来的参数类型
     */
    private <T> void processData(String str,T myObject,MyHttpUtil myHttp) {
        Gson gson = new Gson();
        str = str.substring(1,str.length()-1).replace("\\","");

        Log.e("myStrMsg",str);
        try {
            myHttp.getResponse(gson.fromJson(str,myObject.getClass()),str);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
