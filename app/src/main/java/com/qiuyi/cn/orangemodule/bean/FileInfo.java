package com.qiuyi.cn.orangemodule.bean;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by Administrator on 2018/3/14.
 * 手机中存储的文件
 */
public class FileInfo implements Serializable{

    //文件属于哪个app(QQ,wechat......)
    private String title;
    //文件类型
    private int type;
    //文件路径
    private String path;
    //文件的详细日期
    private String date;
    //文件名称
    private String name;
    //文件大小
    private String size;
    //图片标记(QQ,wechat,手机)
    private int pic;

    public FileInfo(){};

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
