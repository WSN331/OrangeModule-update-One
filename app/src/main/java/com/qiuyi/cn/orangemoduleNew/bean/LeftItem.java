package com.qiuyi.cn.orangemoduleNew.bean;

/**
 * Created by Administrator on 2018/3/13.
 */
public class LeftItem {

    private int imageView;
    private String itemText;

    public LeftItem(int imageView, String itemText) {
        this.imageView = imageView;
        this.itemText = itemText;
    }

    public int getImageView() {
        return imageView;
    }

    public void setImageView(int imageView) {
        this.imageView = imageView;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }
}
