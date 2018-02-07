package com.qiuyi.cn.orangemodule.interfaceToutil;

/**
 * Created by Administrator on 2018/1/26.
 * 回调接口
 */
public interface MyHttpUtil<T>{

    //得到response是解析好的类，str是未解析的Json字符串
    void getResponse(T response,String str);
}
