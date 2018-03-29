package com.qiuyi.cn.orangemodule.util.FileManager.bean1;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/14.
 * 手机中存储的文件
 */
public class FileInfo implements Serializable{

    //文件类型
    private int filetype;
    //文件属于QQ，微信还是别的
    private int filename;


    public int getFiletype() {
        return filetype;
    }

    public void setFiletype(int filetype) {
        this.filetype = filetype;
    }

    public int getFilename() {
        return filename;
    }

    public void setFilename(int filename) {
        this.filename = filename;
    }
}
