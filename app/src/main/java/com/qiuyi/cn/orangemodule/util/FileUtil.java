package com.qiuyi.cn.orangemodule.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.activity.UdiskActivity;

import java.io.File;

/**
 * Created by Administrator on 2018/1/5.
 */
public class FileUtil {

    public static final Object[][] FILE_ICON = {
            {R.drawable.doc, "doc", "docx"},
            {R.drawable.jpg, "jpg", "png", "jpeg", "bmp", "gif"},
            {R.drawable.pdf, "pdf"},
            {R.drawable.mp3, "mp3", "wav"},
            {R.drawable.mp4, "mp4", "3gp", "avi"},
            {R.drawable.zip, "zip", "rar"},
            {R.drawable.ppt, "ppt", "pptx"}
    };

    //文件类型列表
    public static final String[][] MIME_MapTable = {
            //{后缀名，    MIME类型}
            {".mp4",    "video/mp4"},
            {".mp3",    "audio/x-mpeg"},
            {".pdf",    "application/pdf"},
            {".ppt",    "application/vnd.ms-powerpoint"},
            {".pptx",    "application/vnd.ms-powerpoint"},
            {".doc",    "application/msword"},
            {".docx",    "application/msword"},
            {".png",    "image/png"},
            {".zip",    "application/zip"},
            {".jpg",    "image/jpeg"},
            {".jpeg",    "image/jpeg"}
    };

    //得到相应文件的图片
    public static int getFileIcon(String end){
        for(Object[] type:FILE_ICON){
            for(int i=1;i<type.length;i++){
                if (type[i].equals(end)){
                    return (int) type[0];
                }
            }
        }
        return R.drawable.unknown;
    }

    //打开文件
    public static void openFile(File file, Context context) {
        Intent intent = new Intent();
        //添加新的任务
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        //得到文件类型
        String type = getMIMEType(file);
        //调用相应的程序
        intent.setDataAndType(Uri.fromFile(file),type);
        context.startActivity(intent);
    }


    //得到文件的类型
    private static String getMIMEType(File file) {
        String type = "*/*";
        String name = file.getName();

        //获取文件后缀名
        String end = name.substring(name.lastIndexOf(".")).toLowerCase();
        if(end==""){
            return type;
        }

        //寻找对应的MIME类型
        for(String[] t:MIME_MapTable){
            if(t[0].equals(end)){
                type = t[1];
            }
        }

        //返回空类型
        return type;
    }
}
