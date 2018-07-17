package com.qiuyi.cn.orangemoduleNew.bean;

/**
 * Created by Administrator on 2018/3/18.
 * 还原备份的条目
 */
public class MyItemFile {

    private int icon;

    private String name;
    private String size;

    public MyItemFile(){}

    public MyItemFile(String name,int icon, String size) {
        this.name = name;
        this.size = size;
        this.icon = icon;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
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
