package com.qiuyi.cn.orangemoduleNew.util;

import android.hardware.usb.UsbManager;

import com.qiuyi.cn.orangemoduleNew.R;

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

    //侧边栏
    public static final int[] IMAGES = {R.drawable.panda,R.drawable.shopping,R.drawable.talk,R.drawable.help,R.drawable.setting};
    public static final String[] TEXTSHOW = {"我的头像","我的商城","论坛","帮助和反馈","设置"};

    //文件管理-native
    public static final int[] NATIVE_IMAGES ={R.drawable.native_img,R.drawable.native_audio,
    R.drawable.native_doc,R.drawable.native_music,R.drawable.native_zar};

    //U盘-upan
    public static final int[] UPAN_IMAGES = {R.drawable.upan_img,R.drawable.upan_audio,
    R.drawable.upan_doc,R.drawable.upan_music,R.drawable.upan_zar};

    public static final String[] NATIVE_TITLE={"图片","视频","文档","音乐","压缩包","所有文件"};
    public static final String[] UPAN_TITLE={"图片","视频","文档","音乐","压缩包","所有文件"};

    public static final int[] NATIVE_SIZE = new int[6];
    public static final int[] UPAN_SIZE = new int[6];

    //FindFileMsg
    public static final String FINDFILE_MSG = "com.qiuyi.cn.findfileMsg";
    //FindAllMsg
    public static final String FINDALL_MSG = "com.qiuyi.cn.findAllfiles";
    //FindUpanMsg
    public static final String FINDUPAN_MSG = "com.qiuyi.cn.findUPANfiles";
    //FindUpanBF
    public static final String FINDUPAN_BFMSG = "com.qiuyi.cn.findUPANfilesBF";

    //UpanRefresh
    public static final String UPANREFRESH = "com.qiuyi.cn.refreshUPAN";
    public static final String NATIVEFRESH = "com.qiuyi.cn.refreshNATIVE";

    //备份还原
    public static final String BACKUP = "com.qiuyi.cm.backupFile";
    public static final String RESTORE = "com.qiuyi.cm.restoreFile";

    //用来存储U盘信息
    public static Long UPAN_MEMORYSIZE = Long.valueOf(0);//所有空间
    public static Long UPAN_AVAILSIZE = Long.valueOf(0);//已经使用的空间


    //USB相关权限
    public static final String[] ACTION_USB = {
            //USB插入权限
            UsbManager.ACTION_USB_DEVICE_ATTACHED,
            //USB拔出权限
            UsbManager.ACTION_USB_DEVICE_DETACHED
    };


    //手机本地备份
    public static final String[] BACKUPMSG = {"照片","视频","文档","音乐","联系人","应用"};

    //选中的图案
    private static final int[] SELECTED_IMAGES = {R.drawable.equipment2,R.drawable.talk4,R.drawable.smart2};
    //未选中的图案
    private static final int[] UNSELECTED_IMAGES = {R.drawable.equipment1,R.drawable.talk3,R.drawable.smart1};
}
