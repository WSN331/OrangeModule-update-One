package com.qiuyi.cn.orangemodule.upansaf.db.bean;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/7.
 */
public class CollectionFiles extends SugarRecord implements Serializable{

    @Unique
    private String filePath;//文件路径是唯一键
    private String fileName;//文件名

    public CollectionFiles(){};

    public CollectionFiles(String filePath,String fileName){
        this.filePath = filePath;
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
