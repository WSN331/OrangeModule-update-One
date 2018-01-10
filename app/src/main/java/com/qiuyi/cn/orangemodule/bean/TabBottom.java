package com.qiuyi.cn.orangemodule.bean;

/**
 * Created by Administrator on 2017/12/26.
 */
public class TabBottom {
    private int selectedImage;
    private int unselectedImage;

    public TabBottom(int unselectedImage, int selectedImage) {
        this.unselectedImage = unselectedImage;
        this.selectedImage = selectedImage;
    }

    public int getSelectedImage() {
        return selectedImage;
    }

    public void setSelectedImage(int selectedImage) {
        this.selectedImage = selectedImage;
    }

    public int getUnselectedImage() {
        return unselectedImage;
    }

    public void setUnselectedImage(int unselectedImage) {
        this.unselectedImage = unselectedImage;
    }
}
