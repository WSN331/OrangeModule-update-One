package com.qiuyi.cn.orangemodule.util;

import com.qiuyi.cn.orangemodule.R;

/**
 * Created by Administrator on 2018/1/2.
 * 各类常量
 */
public class Constant {

    //private static final String IP = "192.168.2.178";
    private static final String IP = "192.168.2.199";
    private static final String PORT = "8080";

    //获取验证码
    public static final String URL_VERIFY = "http://"+IP+":"+PORT+"/user/sendVerify";
    //登录
    public static final String URL_LOGIN = "http://"+IP+":"+PORT+"/user/login";
    //注销
    public static final String URL_LOGINOUT = "http://"+IP+":"+PORT+"/user/logout";


    //存储用户登录后的用户信息
    public static final String MYMSG_LOGIN = "myMsgLogin";


    //商品图片
    public static final int[] module_images =
            {R.drawable.backup_memory, R.drawable.speaker, R.drawable.laser_pointer,
             R.drawable.temp_hum_mod, R.drawable.led, R.drawable.usb,
             R.drawable.dummy, R.drawable.hotkey};
    //商品名称
    public static final String[] module_names =
            {"存储管理", "扬声器", "激光笔", "温湿度传感器", "闪光灯", "USB扩展", "扩展槽", "热键"};
    //价格
    public static final String[] module_prices =
            {"¥200", "¥150", "¥80", "¥100", "¥200", "¥200", "¥25", "¥80"};
    //商品类型
    public static final String[] module_type={"热销框架","USB插件","蓝牙通讯"};
    //显示类型
    public static final int[] module_myflag = {0,1,2};

    //滑动图片
    public static final int[] slid_images = {R.drawable.paq1, R.drawable.paq2, R.drawable.paq3,
            R.drawable.paq4, R.drawable.paq5};
}
