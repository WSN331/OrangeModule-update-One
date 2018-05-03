package com.qiuyi.cn.orangemodule.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.provider.DocumentFile;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.fs.FileSystem;
import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;
import com.github.mjdev.libaums.partition.Partition;
import com.orm.SugarContext;
import com.qiuyi.cn.myloadingdialog.LoadingDialog;
import com.qiuyi.cn.orangemodule.activity.BkrtActivity;
import com.qiuyi.cn.orangemodule.upansaf.db.util.DBUtil;
import com.qiuyi.cn.orangemodule.upansaf.db.util.PermissionDialog;
import com.qiuyi.cn.orangemodule.upansaf.ui.presenter.DocumentFilePresenter;
import com.qiuyi.cn.orangemodule.upansaf.ui.presenter.FilePresenter;
import com.qiuyi.cn.orangemodule.upansaf.ui.view.FileView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * Created by Administrator on 2018/4/9.
 */
public class WriteToUdisk extends Activity{

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private Context context;
    private Activity mActivity;
    private LoadingDialog dialog;
    private ExecutorService executorService;

    private DocumentFile currentFolder = null;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Activity getmActivity() {
        return mActivity;
    }

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public DocumentFile getCurrentFolder() {
        return currentFolder;
    }

    private WriteToUdisk(){}
    private static class WriteToUdiskUtil{
        private static final WriteToUdisk writeToUdisk = new WriteToUdisk();
    }
    public static final WriteToUdisk getInstance(Context context,Activity activity){
        WriteToUdiskUtil.writeToUdisk.setmActivity(activity);
        WriteToUdiskUtil.writeToUdisk.setContext(context);
        return WriteToUdiskUtil.writeToUdisk;
    }

/*    //1 初始化获取Uri
    public void init(){
        *//*SugarContext.init(context);*//*

        initPermission();
    }*/

/*    *//**
     * 初始化权限
     *//*
    private void initPermission() {
        Uri uri = DocumentFilePresenter.getUri();
        if (uri!=null) {
            initPresenter(uri);
        } else {
            String uriStr = DBUtil.getUri();


            if (uriStr != null && !uriStr.equals("")) {
                uri = Uri.parse(uriStr);
                initPresenter(uri);
            } else {
                intentToOpen();
            }
        }
    }*/

    /**
     * 进入选择U盘的页面
     */
/*    private void intentToOpen() {
        new AlertDialog.Builder(mActivity)
                .setTitle("获取权限")
                .setMessage("需要获取U盘权限")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        new PermissionDialog(mActivity, new PermissionDialog.OnDialogClick() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if(confirm){
                                    Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);//ACTION_OPEN_DOCUMENT
                                    mActivity.startActivityForResult(intent, 42);
                                }
                            }
                        }).show();
                    }
                })
                .setNegativeButton("否", null)
                .setCancelable(false)
                .show();
    }*/

/*    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        Uri rootUri;
        if (requestCode == 42 && resultCode == Activity.RESULT_OK && resultData != null) {
            rootUri = resultData.getData();

            DBUtil.setUri(rootUri.toString());

            initPresenter(rootUri);
        }else{
            Toast.makeText(mActivity,"请给予权限才能备份",Toast.LENGTH_LONG).show();
        }
    }*/


    /**
     * 初始化表示器
     * @param rootUri
     */
    /**
     * 读取设备
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void initPresenter(Uri rootUri) {
        //使用该方法添加操作文件权限
        context.getContentResolver().takePersistableUriPermission(rootUri,Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        //由该Uri获取路径,取得文件目录
        currentFolder = DocumentFile.fromTreeUri(context, rootUri);
    }

    //得到所有DocmentFile文件夹
    public List<DocumentFile> getAllDocDirectory(DocumentFile rootFile){
        List<DocumentFile> listsFile = new ArrayList<>();
        for(DocumentFile docFile : rootFile.listFiles()){
            if(docFile.isDirectory()){
                listsFile.add(docFile);
                listsFile.addAll(getAllDocDirectory(docFile));
            }
        }
        return listsFile;
    }

    //得到DocumentFile所有文件
    public List<DocumentFile> getAllFile(DocumentFile rootFile){

        List<DocumentFile> listsFile = new ArrayList<>();
        List<Callable<List<DocumentFile>>> partions = new ArrayList<>();

        for(final DocumentFile docFile : rootFile.listFiles()){
            listsFile.add(docFile);
            if(docFile.isDirectory()){
                partions.add(new Callable<List<DocumentFile>>() {
                    @Override
                    public List<DocumentFile> call() throws Exception {
                        return getDocFileList(docFile);
                    }
                });
            }
        }

        if(partions.size()>0){
            //启动并发
            ExecutorService executorPool = Executors.newCachedThreadPool();
            List<FutureTask<List<DocumentFile>>> listTask = new ArrayList<>();
            for (Callable<List<DocumentFile>> callable : partions) {
                FutureTask<List<DocumentFile>> futureTask = new FutureTask<List<DocumentFile>>(callable);
                listTask.add(futureTask);
                executorPool.submit(futureTask);
            }

            try {
                for (FutureTask<List<DocumentFile>> task : listTask) {
                    listsFile.addAll(task.get());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                if(executorPool!=null){
                    executorPool.shutdown();
                }
            }
        }

        Log.e("allDocFile", "查找完毕1");

        return listsFile;
    }

    public List<DocumentFile> getDocFileList(DocumentFile currentFolder){
        List<DocumentFile> newFiles = new ArrayList<>();
        for(DocumentFile docFile: currentFolder.listFiles()){
            if(docFile.isDirectory()){
                newFiles.addAll(getDocFileList(docFile));
            }else{
                newFiles.add(docFile);
            }
        }
        return newFiles;
    }



    //找到相应的DocmentFile包括文件夹
    public DocumentFile getDocFile(List<DocumentFile> listsFile,String name){
        for(DocumentFile docFile:listsFile){
            Log.e("docName", "getDocFile: "+docFile.getName());
            if(docFile.getName().equals(name)){
                return docFile;
            }
        }
        return null;
    }


    //创建文件夹
    public DocumentFile createDirectory(String name,DocumentFile outfile){
        DocumentFile newFile = outfile.createDirectory(name);
        return newFile;
    }

    //判断文件是否存在
    public boolean checkFile(DocumentFile rootFile,File file){
        for(DocumentFile docFile:rootFile.listFiles()){
            if(docFile.getName().equals(file.getName()) && docFile.length() == file.length()){
                //文件名与长度相同，证明文件已经存在
                return true;
            }
        }
        return false;
    }

    //查找文件夹是否存在
    public DocumentFile findUFile(DocumentFile rootFile,String name){
        DocumentFile dirFile = null;
        dirFile = rootFile.findFile(name);
        if(dirFile!=null){
            dirFile.delete();
        }
        dirFile = rootFile.createDirectory(name);
        return dirFile;
    }

    //查找文件是否存在
    public DocumentFile findUPFile(DocumentFile rootFile,File file){
        DocumentFile newFile = null;
        newFile = rootFile.findFile(file.getName());
        if(newFile==null){
            newFile = rootFile.createFile(com.qiuyi.cn.orangemodule.util.FileUtil.getMIMEType(file), file.getName());
        }
        return newFile;
    }

    //2 写操作
    public void writeToSDFile(Context context,File infile,DocumentFile outfile){
        Log.i("TTTTTT","开始移动");
        FileInputStream in = null;
        OutputStream output = null;
        try {
/*            DocumentFile newFile = null;
            newFile = outfile.findFile(infile.getName());
            if(newFile==null){
                newFile = outfile.createFile(com.qiuyi.cn.orangemodule.util.FileUtil.getMIMEType(infile), infile.getName());
            }*/
            DocumentFile newFile = outfile.createFile(com.qiuyi.cn.orangemodule.util.FileUtil.getMIMEType(infile), infile.getName());
            output = context.getContentResolver().openOutputStream(newFile.getUri());
            in = new FileInputStream(infile);
            byte[] buf = new byte[1024*4];
            int len;
            while((len = in.read(buf))!=-1){
                output.write(buf,0,len);
                output.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(output!=null){
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //2 字符串写操作
    public void writeStrToSDFile(Context context,String content,DocumentFile outfile,String fileName){
        Log.i("TTTTTT","开始移动");
        OutputStream output = null;
        try {
            DocumentFile newFile = null;
            newFile = outfile.findFile(fileName);
            if(newFile==null){
                newFile = outfile.createFile("text/plain", fileName);
            }
            output = context.getContentResolver().openOutputStream(newFile.getUri());

            output.write(content.getBytes());
            output.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            if(output!=null){
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 移动文件
     * @param
     * @param
     */
    public static DocumentFile moveFile(Context context, DocumentFile infile, DocumentFile outfile){
        Log.i("TTTTTT","开始移动");
        InputStream input = null;
        OutputStream output = null;
        try {
            DocumentFile newFile;
            if(infile.isDirectory()){
                newFile = outfile.createDirectory(infile.getName());

                for(DocumentFile file:infile.listFiles()){
                    moveFile(context,file,newFile);
                }

            }else{
                newFile = outfile.createFile(infile.getType(), infile.getName());
            }
            OutputStream out = context.getContentResolver().openOutputStream(newFile.getUri());
            input = context.getContentResolver().openInputStream(infile.getUri());
            byte[] buf = new byte[1024*4];
            int len;
            while((len = input.read(buf))!=-1){
                out.write(buf,0,len);
                out.flush();
            }
            return newFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally{
            if(output!=null){
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(input!=null){
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
/*    private UsbMassStorageDevice[] storageDevices;
    private UsbFile usbFileFolder;
    private List<UsbFile> usbFiles = new ArrayList<>();*/
/*
    //调用顺序 2 将字符串写入U盘
    public void writeToUPString(String content){
        UsbFile newUsbFileFolder = null;
        UsbFile constactsFile = null;
        UsbFileOutputStream uos = null;
        try {
            newUsbFileFolder = usbFileFolder.createDirectory("联系人");
            constactsFile = newUsbFileFolder.createFile(BkrtActivity.PHONE_FILE);

            uos = new UsbFileOutputStream(constactsFile);
            uos.write(content.getBytes());
            uos.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if(uos!=null){
                    uos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //调用顺序 2 将文件写入U盘
    public void writeToUP(final File file,int i){
        if(usbFileFolder == null){
            Toast.makeText(context,"没有U盘插入",Toast.LENGTH_SHORT).show();
        }else{

            dialog.show();

            //根据i创建文件夹
            UsbFile newUsbFileFolder = null;

            try {
                if(i==0){
                    newUsbFileFolder = usbFileFolder.createDirectory("照片");
                }
                if(i==1){
                    newUsbFileFolder = usbFileFolder.createDirectory("视频");
                }
                if(i==2){
                    newUsbFileFolder = usbFileFolder.createDirectory("文档");
                }
                if(i==3){
                    newUsbFileFolder = usbFileFolder.createDirectory("音乐");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            final UsbFile finalNewUsbFileFolder = newUsbFileFolder;

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    readSDFile(file, finalNewUsbFileFolder);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //关闭device
                            for(UsbMassStorageDevice device:storageDevices){
                                Log.e("关闭设备","设备关闭");
                                device.close();
                            }
                            //写完之后
                            dialog.dismiss();
                        }
                    });
                }
            });

        }
    }

    private void readSDFile(final File f, UsbFile folder) {
        UsbFile usbFile = null;
        if (f.isDirectory()) {//如果选择是个文件夹
            try {
                usbFile = folder.createDirectory(f.getName());
                for (File sdFile : f.listFiles()) {
                    readSDFile(sdFile, usbFile);
                }
            } catch (IOException e) {
                Log.e("异常","创建文件目录时异常");
            }
        } else {//如果选了一个文件
            try {
                usbFile = folder.createFile(f.getName());
                saveSDFile2OTG(usbFile, f);
            } catch (IOException e) {
                Log.e("异常","创建文件异常");
            }
        }
    }

    private void saveSDFile2OTG(final UsbFile usbFile, final File f) {
        try {//开始写入
            FileInputStream fis = new FileInputStream(f);//读取选择的文件的
            UsbFileOutputStream uos = new UsbFileOutputStream(usbFile);
            redFileStream(uos, fis);
        } catch (final Exception e) {
            Log.e("异常","写入出错，执行删除");
            try {
                usbFile.delete();
            } catch (IOException e1) {
                Log.e("异常","删除失败");
            }
        }
    }

    private void redFileStream(OutputStream os, InputStream is) throws IOException {
        *//**
 *  写入文件到U盘同理 要获取到UsbFileOutputStream后 通过
 *  f.createNewFile();调用 在U盘中创建文件 然后获取os后
 *  可以通过输出流将需要写入的文件写到流中即可完成写入操作
 *//*
        int bytesRead = 0;
        byte[] buffer = new byte[1024 * 8];
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        os.close();
        is.close();
    }



    //调用顺序 1 获取权限与设备
    public boolean getDeviceAndPermission(){
        //注册监听自定义广播
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        context.registerReceiver(mUsbReceiver, filter);

        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        //获取存储设备
        storageDevices = UsbMassStorageDevice.getMassStorageDevices(context);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_USB_PERMISSION), 0);

        //没有设备直接结束
        if (storageDevices.length == 0){
            return false;
        }

        for (UsbMassStorageDevice device : storageDevices) {//可能有几个 一般只有一个 因为大部分手机只有1个otg插口
            if (usbManager.hasPermission(device.getUsbDevice())) {//有就直接读取设备是否有权限
                readDevice(device);
            } else {//没有就去发起意图申请
                usbManager.requestPermission(device.getUsbDevice(), pendingIntent); //该代码执行后，系统弹出一个对话框，
            }
        }
        return true;
    }

    //获取文件根路径
    private void readDevice(UsbMassStorageDevice device) {
        try {
            device.init();//初始化
            Partition partition = device.getPartitions().get(0);
            FileSystem currentFs = partition.getFileSystem();
            UsbFile root = currentFs.getRootDirectory();//获取根目录
            usbFileFolder = root;

            //线程
            executorService = Executors.newCachedThreadPool();//30大小的线程池

            dialog = new LoadingDialog.Builder(context)
                    .setMessage("正在备份中...")
                    .setCancelable(false)
                    .setCancelOutside(false).create();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //获取权限的广播
    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_USB_PERMISSION://接受到自定义广播
                    UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {  //允许权限申请
                        if (usbDevice != null) {  //Do something
                            Log.e("writeToUdisk","用户已授权，可以进行读取操作");
                            readDevice(getUsbMass(usbDevice));
                        } else {
                            Log.e("writeToUdisk","未获取到设备信息");
                        }
                    } else {
                        Log.e("writeToUdisk","用户未授权，读取失败");
                    }
                    break;
            }
        }
    };

    private UsbMassStorageDevice getUsbMass(UsbDevice usbDevice) {
        for (UsbMassStorageDevice device : storageDevices) {
            if (usbDevice.equals(device.getUsbDevice())) {
                return device;
            }
        }
        return null;
    }*/