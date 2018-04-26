package com.qiuyi.cn.orangemodule.upansaf.db.bean;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;
import com.orm.dsl.Column;
import com.orm.dsl.Table;

import java.io.Serializable;

/**
 * Created by ASUS on 2017/7/27 0027.
 */
@Table(name = "app_info")
public class AppInfo extends SugarRecord implements Serializable{

    @Column(name="wrongPass")
    private Integer wrongPass;

    @Column(name="rootUriPath")
    private String rootUriPath;

    @Column(name="rootPath")
    private String rootPath;


    public Integer getWrongPass() {
        return wrongPass;
    }

    public void setWrongPass(Integer wrongPass) {
        this.wrongPass = wrongPass;
    }

    public String getRootUriPath() {
        return rootUriPath;
    }

    public void setRootUriPath(String rootUriPath) {
        this.rootUriPath = rootUriPath;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

}


