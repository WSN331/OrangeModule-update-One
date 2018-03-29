package com.qiuyi.cn.orangemodule.util.FileManager.bean1;

/**
 * Created by Administrator on 2018/3/21.
 * 视频文件
 */
public class VideoBean extends FileInfo{


    private String id;// 视频的id
    private String name; // 视频名称
    private String resolution; //分辨率
    private String path;// 路径
    private long size;// 大小
    private long duration;// 时长
    private long date;//修改日期

    public VideoBean() {
    }

    public VideoBean(String id, String name, String resolution, String path, long size, long duration, long date) {
        this.id = id;
        this.name = name;
        this.resolution = resolution;
        this.path = path;
        this.size = size;
        this.duration = duration;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
