package com.qiuyi.cn.orangemoduleNew.util.FileManager.bean1;

/**
 * Created by Administrator on 2018/3/21.
 * 图片
 */
public class ImageBean extends FileInfo{

    /*
    * 图片的相关属性
    * */
    private String name;//名字
    private String path;//路径
    private Long date;//日期
    private Long size;//大小
    private String type;//类型
    private String location;//地点

    public ImageBean() {
    }

    public ImageBean(String name, String path, Long date, Long size, String type, String location) {
        this.name = name;
        this.path = path;
        this.date = date;
        this.size = size;
        this.type = type;
        this.location = location;
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

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
