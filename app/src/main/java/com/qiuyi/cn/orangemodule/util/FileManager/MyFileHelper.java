package com.qiuyi.cn.orangemodule.util.FileManager;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import com.google.gson.Gson;
import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.activity.BkrtActivity;
import com.qiuyi.cn.orangemodule.util.FileManager.contacts.ContactBean;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/1/29.
 * 我的文件操作类
 */
public class MyFileHelper {

    private Context context;

    public MyFileHelper(Context context) {
        this.context = context;
    }

    //是否有外部存储卡
    public boolean isSDCardState(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }

    //外部是否有U盘
    public File isUPState(){
        File file = findUdiskPath();
        if(file!=null){
            return file;
        }else{
            return null;
        }
    }

    //查找U盘路径
    public File findUdiskPath() {

        File currentFolder = null;

        //存储得到的文件路径
        String[] result = null;
        //得到存储管理
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        //利用反射调用storageManager的系统方法
        try {
            //利用反射
            Method method = StorageManager.class.getMethod("getVolumePaths");
            method.setAccessible(true);
            try {
                result = (String[]) method.invoke(storageManager);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < result.length; i++) {
                Log.e("MyHelper-path----> ", result[i] + "");
                if (result[i] != null && result[i].startsWith("/storage") && !result[i].startsWith("/storage/emulated/0")) {
                    currentFolder = new File(result[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return currentFolder;
    }

    //获取U盘存储联系人文件
    public File getUPCardFile(String fileName){
        File myFileToRead = null;
        if(MainActivity.isHaveUpan){
            //得到root目录
            File parent_path = MainActivity.rootUFile;
            //在root目录下创建myFile文件夹
            File myFile = new File(parent_path.getAbsoluteFile(),"联系人");
            myFile.mkdirs();
            //在myFile文件夹下创建文件
            File file = new File(myFile.getAbsoluteFile(),fileName);
            myFileToRead = file;
        }
        return myFileToRead;
    }

    //获取U盘指定文件夹
    public File getUPCardTFile(File currentFile,String directoryName){
        File myFileToRead = null;
        if(MainActivity.isHaveUpan){
            //得到root目录
            File parent_path = currentFile;
            File myFile = null;
            for(File file:currentFile.listFiles()){
                if(file.getName().equals(directoryName) && file.listFiles().length>0){
                    //在root目录下得到myFile文件夹
                    myFile = new File(parent_path.getAbsoluteFile(),directoryName);
                }
            }
            myFileToRead = myFile;
        }
        return myFileToRead;
    }

    //从本地存储的联系人文件中返回联系人信息
    public List<ContactBean> getPhoneFile() {
        File file = getUPCardFile(BkrtActivity.PHONE_FILE);
        MainActivity.constactUP = file;

        String myPhoneStr = readFileToUPCard(file);
        if(myPhoneStr!=null){
            try {
                JSONObject jsonObject = new JSONObject(myPhoneStr);
                Gson gson = new Gson();
                List<ContactBean> myList = new ArrayList<>();
                for(int i=0;i<jsonObject.length();i++){
                    JSONObject object = (JSONObject) jsonObject.get("contact"+i);
                    ContactBean contactBean = gson.fromJson(String.valueOf(object),ContactBean.class);
                    myList.add(contactBean);
                }
                return myList;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    //读文件
    public String readFileToUPCard(File file){
        if(MainActivity.isHaveUpan && file!=null){
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                byte[] buf = new byte[1024];
                int len;
                StringBuilder sb = new StringBuilder();
                while((len = fis.read(buf)) != -1){
                    sb.append(new String(buf,0,len));
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(fis!=null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    //向U盘写文件
    public void writeFileToSDCard(String content,String fileName){
        if(MainActivity.isHaveUpan){
            File file = getUPCardFile(fileName);

            Log.e("contactData",file.getAbsolutePath());

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                fos.write(content.getBytes());
                fos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(fos!=null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }



}
