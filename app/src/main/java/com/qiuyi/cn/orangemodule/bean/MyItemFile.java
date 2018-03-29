package com.qiuyi.cn.orangemodule.bean;

/**
 * Created by Administrator on 2018/3/18.
 * 还原备份的条目
 */
public class MyItemFile {
    private String name;
    private String size;

    public MyItemFile(){}

    public MyItemFile(String name, String size) {
        this.name = name;
        this.size = size;
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
