package com.qiuyi.cn.orangemodule.util.FileManager;

/**
 * Created by Administrator on 2018/3/22.
 */
public class ConstantValue {

    public static final String QQ_IMAGE_KEY = ".*[Qq][Qq][_][Ii][m][a][g][e][s].*";//QQ
    public static final String WECHART_IMAGE_KEY = ".*[Ww][e][i][Xx][i][n].*";//wechat
    public static final String SCREENSHOT_KEY = ".*[Ss][c][r][e][e][n].*";//截图
    public static final String CAMERA_KEY = ".*[D][C][I][M][/][C][a][m][e][r][a].*";//camera
    public static final String GIF_KEY = ".*\\.[g][i][f]";//GIF

    /*QQ*/
    public static final int KEY_QQ = 0;
    /*wechat*/
    public static final int KEY_WECHAT = 1;
    /*截图*/
    public static final int KEY_SCREENSHOT = 2;
    /*camera*/
    public static final int KEY_CAMERA = 3;
    /*GIF*/
    public static final int KEY_GIF = 4;
    /*other*/
    public static final int KEY_OTHER = -1;

    /**文档类型*/
    public static final int TYPE_DOC = 0;
    /**apk类型*/
    public static final int TYPE_APK = 1;
    /**压缩包类型*/
    public static final int TYPE_ZIP = 2;
    /*MP3类型*/
    public static final int TYPE_MP3 = 3;
    /*MP4类型*/
    public static final int TYPE_MP4 = 4;
    /*图片类型*/
    public static final int TYPE_IMG = 5;

    //文件类型综合
    public static final int[] TYPE_FILE = {TYPE_IMG,TYPE_MP4,TYPE_MP3,TYPE_DOC,TYPE_APK,TYPE_ZIP};
    //文件属于QQ,wechat,本地综合
    public static final int[] FILE_KEY = {KEY_QQ,KEY_WECHAT,KEY_SCREENSHOT,KEY_CAMERA,KEY_GIF,KEY_OTHER};

    public static String timeParse(long duration) {
        String time = "" ;
        long minute = duration / 60000 ;
        long seconds = duration % 60000 ;
        long second = Math.round((float)seconds/1000) ;
        if( minute < 10 ){
            time += "0" ;
        }
        time += minute+":" ;
        if( second < 10 ){
            time += "0" ;
        }
        time += second ;
        return time ;
    }
}
