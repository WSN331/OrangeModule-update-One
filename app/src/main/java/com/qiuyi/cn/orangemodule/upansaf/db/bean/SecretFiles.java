package com.qiuyi.cn.orangemodule.upansaf.db.bean;

import com.orm.SugarRecord;
import com.orm.dsl.Unique;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/7.
 */
public class SecretFiles extends SugarRecord implements Serializable{

    @Unique
    private String filePath;//文件路径是唯一键
    private String fileName;//文件名
    private String fileSize;//文件大小
    private String fileDate;//文件修改时间

    public SecretFiles(){};

    public SecretFiles(String filePath, String fileName,String fileSize,String fileDate){
        this.filePath = filePath;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileDate = fileDate;
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

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileDate() {
        return fileDate;
    }

    public void setFileDate(String fileDate) {
        this.fileDate = fileDate;
    }
}
