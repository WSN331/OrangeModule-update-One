package com.qiuyi.cn.orangemoduleNew.util.FileManager;

import android.os.Environment;
import android.os.StatFs;

import com.qiuyi.cn.orangemoduleNew.R;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FileUtils {

    /**文档类型*/
    public static final int TYPE_DOC = 0;
    /**apk类型*/
    public static final int TYPE_APK = 1;
    /**压缩包类型*/
    public static final int TYPE_ZIP = 2;

    /*
    * mp3类型
    * */
    public static final int TYPE_MP3 = 3;

    /*MP4类型*/
    public static final int TYPE_MP4 = 4;

    /*图片类型*/
    public static final int TYPE_IMG = 5;

    /**
     * 判断文件是否存在
     * @param path 文件的路径
     * @return
     */
    public static boolean isExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static int getFileType(String path) {
        path = path.toLowerCase();
        if (path.endsWith(".doc") || path.endsWith(".docx")
                || path.endsWith(".xls") || path.endsWith(".xlsx")
                || path.endsWith(".type_ppt") || path.endsWith(".pptx") || path.endsWith(".ppt")
                || path.endsWith(".type_pdf") || path.endsWith(".pdf")
                /*||path.endsWith(".txt")*/ || path.endsWith(".type_txt")
                /*|| path.endsWith(".xml")*/|| path.endsWith(".type_xml")) {
            return TYPE_DOC;
        }else if (path.endsWith(".apk")) {
            return TYPE_APK;
        }else if (path.endsWith(".zip") || path.endsWith(".rar") || path.endsWith(".tar") || path.endsWith(".gz")) {
            return TYPE_ZIP;
        }else if(path.endsWith(".mp3") || path.endsWith(".flac") || path.endsWith(".m3u")
                || path.endsWith(".m4a") || path.endsWith(".m4b")||path.endsWith(".mp2")
                ||path.endsWith(".mp3")||path.endsWith(".mpga")||path.endsWith(".ogg")
                ||path.endsWith(".wav")||path.endsWith(".wma")||path.endsWith(".wmv")){
            return TYPE_MP3;
        }else if(path.endsWith(".3gp") || path.endsWith(".asf") || path.endsWith(".avi")
                || path.endsWith(".m4u")|| path.endsWith(".m4v")|| path.endsWith(".mov")
                || path.endsWith(".mp4")|| path.endsWith(".mpe")|| path.endsWith(".mpeg")
                || path.endsWith(".mpg")|| path.endsWith(".mpg4")){
            return TYPE_MP4;
        }else if(path.endsWith(".gif") || path.endsWith(".jpeg") || path.endsWith(".jpg")
                || path.endsWith(".png")){
            return TYPE_IMG;
        }else{
            return -1;
        }
    }


    /**通过文件名获取文件图标*/
    public static int getFileIconByPath(String path){
        path = path.toLowerCase();
        int iconId = R.drawable.unknow_file_icon;
        if(path.endsWith(".gif") || path.endsWith(".jpeg") || path.endsWith(".jpg")
                || path.endsWith(".png")){
            //是图片返回1
            iconId = 1;
        }else if (path.endsWith(".type_txt") || path.endsWith(".txt")){
            iconId = R.drawable.type_txt;
        }else if(path.endsWith(".doc") || path.endsWith(".docx")){
            iconId = R.drawable.type_doc;
        }else if(path.endsWith(".xls") || path.endsWith(".xlsx")){
            iconId = R.drawable.type_xls;
        }else if(path.endsWith(".type_ppt") || path.endsWith(".pptx") || path.endsWith(".ppt")){
            iconId = R.drawable.type_ppt;
        }else if(path.endsWith(".type_xml") || path.endsWith(".xml")){
            iconId = R.drawable.type_xml;
        }else if(path.endsWith(".type_pdf") || path.endsWith(".pdf")){
            iconId = R.drawable.type_pdf;
        }else if(path.endsWith(".htm") || path.endsWith(".type_html")){
            iconId = R.drawable.type_html;
        }else if(path.endsWith(".mp3") || path.endsWith(".flac") || path.endsWith(".m3u")
                || path.endsWith(".m4a") || path.endsWith(".m4b")||path.endsWith(".mp2")
                ||path.endsWith(".mp3")||path.endsWith(".mpga")||path.endsWith(".ogg")
                ||path.endsWith(".wav")||path.endsWith(".wma")||path.endsWith(".wmv")){
            iconId = R.drawable.mp3;
        }else if(path.endsWith(".3gp") || path.endsWith(".asf") || path.endsWith(".avi")
                || path.endsWith(".m4u")|| path.endsWith(".m4v")|| path.endsWith(".mov")
                || path.endsWith(".mp4")|| path.endsWith(".mpe")|| path.endsWith(".mpeg")
                || path.endsWith(".mpg")|| path.endsWith(".mpg4")){
            iconId = R.drawable.mp4;
        }else if(path.endsWith(".zip") || path.endsWith(".rar")
                || path.endsWith(".tar") || path.endsWith(".gz")){
            iconId = R.drawable.zip;
        }

        return iconId;
    }

    /**通过文件名获取文件属于什么文件*/
    public static int getFileKey(String path){
        if(path.matches(ConstantValue.QQ_IMAGE_KEY)){
            return ConstantValue.KEY_QQ;
        }else if(path.matches(ConstantValue.WECHART_IMAGE_KEY)){
            return ConstantValue.KEY_WECHAT;
        }else if(path.matches(ConstantValue.SCREENSHOT_KEY)){
            return ConstantValue.KEY_SCREENSHOT;
        }else if(path.matches(ConstantValue.CAMERA_KEY)){
            return ConstantValue.KEY_CAMERA;
        }else if(path.matches(ConstantValue.GIF_KEY)){
            return ConstantValue.KEY_GIF;
        }else{
            return -1;
        }
    }

    /*通过ID获取类别，QQ,WECHAT文字*/
    public static String getFilename(int filename){
        if(filename == 0){
            return "QQ";
        }else if(filename == 1){
            return "微信";
        }else if(filename == 2){
            return "截图";
        }else if(filename == 3){
            return "相机";
        }else if(filename == 4){
            return "GIF动图";
        }else{
            return "其他文件";
        }
    }

    /*通过ID获取类别，QQ,WECHAT文字*/
    public static int getFilenameIcon(int filename){
        if(filename == 0){
            return R.drawable.qq;
        }else if(filename == 1){
            return R.drawable.wechat;
        }else if(filename == 2){
            return R.drawable.slide;
        }else if(filename == 3){
            return R.drawable.camera;
        }else if(filename == 4){
            return R.drawable.gift;
        }else{
            return R.drawable.other;
        }
    }


    /**是否是图片文件*/
    public static boolean isPicFile(String path){
        path = path.toLowerCase();
        if (path.endsWith(".jpg") || path.endsWith(".jpeg") || path.endsWith(".png")){
            return true;
        }
        return false;
    }


    /** 判断SD卡是否挂载 */
    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }



    /**
     * 从文件的全名得到文件的拓展名
     *
     * @param filename
     * @return
     */
    public static String getExtFromFilename(String filename) {
        int dotPosition = filename.lastIndexOf('.');
        if (dotPosition != -1) {
            return filename.substring(dotPosition + 1, filename.length());
        }
        return "";
    }
    /**
     * 读取文件的修改时间
     *
     * @param f
     * @return
     */
    public static String getModifiedTime(File f) {
        Calendar cal = Calendar.getInstance();
        long time = f.lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        cal.setTimeInMillis(time);
        // System.out.println("修改时间[2] " + formatter.format(cal.getTime()));
        // 输出：修改时间[2] 2009-08-17 10:32:38
        return formatter.format(cal.getTime());
    }


    //int类型的大小已经不够作为容量的类型，int能够作为2G的存储类型
    public static long getAvailSize(String path) {
        // 获得一个磁盘状态对象
        StatFs stat = new StatFs(path);

        long blockSize = stat.getBlockSize();   // 获得一个扇区的大小

        long availableBlocks = stat.getAvailableBlocks();   // 获得可用的扇区数量

        return availableBlocks * blockSize;
    }

    //获得总存储容量
    public static long getTotalSize(String path) {
        // 获得一个磁盘状态对象
        StatFs stat = new StatFs(path);

        long blockSize = stat.getBlockSize();   // 获得一个扇区的大小

        long totalBlocks = stat.getBlockCount();    // 获得扇区的总数

        return totalBlocks * blockSize;
    }


    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try{
                fis = new FileInputStream(file);
                size = fis.available();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                fis.close();
            }

        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    public static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }


}
