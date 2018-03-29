package com.qiuyi.cn.orangemodule.util.FileManager.bean1;

/**
 * Created by Administrator on 2018/3/21.
 * 文档信息
 * 可以是文档,apk,压缩包
 */
public class FileBean extends FileInfo{

    private String name;//文件名
    private String path;//文件路径
    private Long size;//文件大小
    private Long time;//文件创建时间
    private int iconId;//文件图片

    public FileBean() {
    }

    public FileBean(String name, String path, Long size, Long time, int iconId) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.time = time;
        this.iconId = iconId;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
}
