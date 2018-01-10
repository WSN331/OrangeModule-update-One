package com.qiuyi.cn.orangemodule.util;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by Administrator on 2018/1/9.
 *
 * 保存数据到sdcard文件中
 */
public class FileOutToWrite {

    public static void write(String content){
        //获取外部存储状态
/*        if(Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED)){*/
            //获取sdcard根目录
            File sdCardDir = Environment.getExternalStorageDirectory();
            File saveFile = new File(sdCardDir,"JQ.txt");
            //写数据
            try {
                FileOutputStream fos = new FileOutputStream(saveFile,true);
                fos.write(content.getBytes());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
/*        }*/
    }

    public static void read(){
        //获取外部存储状态
        if(Environment.getExternalStorageDirectory().equals(Environment.MEDIA_MOUNTED)){
            //获取sdcard根目录
            File sdCardDir = Environment.getExternalStorageDirectory();
            File saveFile = new File(sdCardDir,"JQ.txt");
            //读数据
            try {
                FileInputStream fis = new FileInputStream(saveFile);
                int len = 0;
                byte[] buf = new byte[1024];
                StringBuilder sb = new StringBuilder();
                while ((len=fis.read(buf))!=-1){
                    sb.append(new String(buf,0,len));
                }
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
