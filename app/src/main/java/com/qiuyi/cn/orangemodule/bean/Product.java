package com.qiuyi.cn.orangemodule.bean;

import android.view.View;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/1/2.
 *
 * 商品类
 */
public class Product implements Serializable{

    //商品图片
    private int image;
    //商品名称
    private String name;
    //商品价格
    private String price;
    //商品类型
    private String type;
    //显示类型
    private int myflag;
    //商品数量
    private int count;
    //商品是否被选中
    private boolean isSelected;
    //商品轮播图
    private List<View> viewList;

    public Product(){

    }

    public Product(int image,String name,String price,String type,int myflag){
        this.image = image;
        this.name = name;
        this.price = price;
        this.type = type;
        this.myflag = myflag;
    }

    public Product(int image,String name,String price,int count,boolean isSelected){
        this.image = image;
        this.name = name;
        this.price = price;
        this.count = count;
        this.isSelected = isSelected;
    }


    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMyflag() {
        return myflag;
    }

    public void setMyflag(int myflag) {
        this.myflag = myflag;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public List<View> getViewList() {
        return viewList;
    }

    public void setViewList(List<View> viewList) {
        this.viewList = viewList;
    }
}
