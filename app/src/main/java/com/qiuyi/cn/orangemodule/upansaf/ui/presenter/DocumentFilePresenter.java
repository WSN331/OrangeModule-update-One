package com.qiuyi.cn.orangemodule.upansaf.ui.presenter;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.provider.DocumentFile;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.qiuyi.cn.myloadingdialog.LoadingDialog;
import com.qiuyi.cn.orangemodule.upansaf.ui.FileActivity;
import com.qiuyi.cn.orangemodule.upansaf.ui.view.DocumentFileAdapter;
import com.qiuyi.cn.orangemodule.upansaf.ui.view.FileView;
import com.qiuyi.cn.orangemodule.upansaf.usb.FileUtil;
import com.qiuyi.cn.orangemodule.util.MyApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by ASUS on 2017/7/19 0019.
 */

public class DocumentFilePresenter implements FilePresenter{

    private FileView fileView;
    private Context context;
    private boolean loginFlag = false;    // 登录标记
    private List<DocumentFile> fileList;
    private List<DocumentFile> currentFolderList = new ArrayList<>();
    private DocumentFile currentFolder,rootFolder;  //当前目录,根目录
    private Uri rootUri;
    private boolean pasteFlag = false, deleteAfterPaste;
    Map<Integer, DocumentFile> copyFileMap;

    private static DocumentFilePresenter instance;

    private LoadingDialog loadingDialog = null;
    private LoadingDialog pastDialog = null;
    public LoadingDialog getLoadingDialog(String name){
        if(loadingDialog==null){
            loadingDialog=new LoadingDialog.Builder(fileView.getActivity())
                    .setMessage(name)
                    .setCancelable(false)
                    .setCancelOutside(false).create();
        }
        return loadingDialog;
    }
    private LoadingDialog getPastDialog(String name){
        if(pastDialog==null){
            pastDialog=new LoadingDialog.Builder(fileView.getActivity())
                    .setMessage(name)
                    .setCancelable(false)
                    .setCancelOutside(false).create();
        }
        return pastDialog;
    }

/*    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    //删除

                    break;
                case 1:

                    break;
            }
        }
    };*/

    /**
     * 获取运行缓存uri
     * @return
     */
    public static Uri getUri() {
        return instance!=null? instance.getRootUri() : null;
    }

    public static void setUri(Uri rootUri){
        instance.setRootUri(rootUri);
    }


    /**
     * 初始化解释器
     * @param fileView 页面
     * @param context 上下文
     * @param rootUri 根路径
     * @return 解释器
     */
    public static DocumentFilePresenter newInstance(FileView fileView, Context context, Uri rootUri) {
        DocumentFilePresenter.newInstance(fileView, context);
        instance.setRootUri(rootUri);
        return instance;
    }

    /**
     * 初始化解释器
     * @param fileView 页面
     * @param context 上下文
     * @return 解释器
     */
    public static DocumentFilePresenter newInstance(FileView fileView, Context context) {
        if (instance == null) {
            synchronized (DocumentFilePresenter.class) {
                instance = new DocumentFilePresenter();
            }
        }
        instance.setFileView(fileView);
        instance.setContext(context);
        instance.registerReceiver();
        return instance;
    }

    /**
     * 构造函数
     */
    protected DocumentFilePresenter() {
        fileList = new ArrayList<>();
        copyFileMap = new HashMap<>();
    }

    public void setFileView(FileView fileView) {
        this.fileView = fileView;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setRootUri(Uri rootUri) {
        this.rootUri = rootUri;
    }

    public Uri getRootUri() {
        return rootUri;
    }

    /**
     * 读取设备
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void readDeviceList(String name) {
        //使用该方法添加操作文件权限
        context.getContentResolver().takePersistableUriPermission(rootUri,Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        //由该Uri获取路径,取得文件目录
        rootFolder = DocumentFile.fromTreeUri(context,rootUri);

        if(name == null){
            currentFolder = rootFolder;
        }else{
            currentFolder = getCurrentFolder(rootFolder,name);
            /*currentFolder = rootFolder.findFile(name);*/

            List<DocumentFile> doclists = new ArrayList<>();
            DocumentFile preFile = currentFolder;
            int count = 0;
            while(preFile.getParentFile()!=null){
                doclists.add(preFile.getParentFile());
                preFile = preFile.getParentFile();
                count++;
                Log.e("count", "count: "+count);
            }

            for(int i = doclists.size()-1;i>=0;i--){
                currentFolderList.add(doclists.get(i));
            }

        }
        Log.i("TTTTT",currentFolder+"");
        getAllFiles();
    }
    //根据传进来的name找到路径
    private DocumentFile getCurrentFolder(DocumentFile rootFolder, String name) {
        List<DocumentFile> lists = getAllDirectory(rootFolder);
        for(DocumentFile file:lists){
            if(file.getName().equals(name)){
                return file;
            }
        }
        return null;
    }

    //寻找当前路径下所有文件夹
    private List<DocumentFile> getAllDirectory(DocumentFile rooFolder){
        List<DocumentFile> allDirectoryFiles = new ArrayList<>();
        for(DocumentFile direcFile:rooFolder.listFiles()){
            if(direcFile.isDirectory()){
                allDirectoryFiles.add(direcFile);
                allDirectoryFiles.addAll(getAllDirectory(direcFile));
            }
        }
        return allDirectoryFiles;
    }

    /**
     * 获取文件
     */
    private void getAllFiles() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                //UDiskLib.Init(context);
                fileList.clear();
                DocumentFile files[] = currentFolder.listFiles();
                if (files != null) {
                    for (DocumentFile f : files) {
                        System.out.println(f);
                        fileList.add(f);
                    }
                    Collections.sort(fileList, new Comparator<DocumentFile>() {
                        @Override
                        public int compare(DocumentFile o1, DocumentFile o2) {
                            if (o1.isDirectory()) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }
                    });
                    initAdapter();
                }
            }
        }).start();
    }


    private List<DocumentFile> showFileList;
    private int position=10;
    /**
     * 初始化界面相关的内容
     */
    private void initAdapter() {
        changeTitle();

/*        showFileList = new ArrayList<>();
        if(fileList!=null && fileList.size()>0){
            showFileList = fileList.subList(0,10);
        }else if(fileList.size()<10 && fileList!=null){
            showFileList = fileList;
        }*/


        DocumentFileAdapter adapter = new DocumentFileAdapter(context,fileList);
        //adapter.setSecretHeader(currentFolderList.size() == 0 && !loginFlag);
        fileView.setAdapter(adapter);
        initListener(adapter);
        if (fileList!=null && fileList.size()>0 && fileList.get(0)!=null){
            fileView.setRefreshing(false);
        }

/*        fileView.getRecycler().addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if(!recyclerView.canScrollVertically(-1)){
                    //滑动到顶端
                }else if(!recyclerView.canScrollVertically(1)) {
                    //滑动到底端
                    int lastposition = position + 10;
                    if (lastposition > fileList.size()) {
                        lastposition = fileList.size();
                    }
                    showFileList.addAll(fileList.subList(position, lastposition));
                    fileView.getAdapter().notifyDataSetChanged();
                    position = lastposition;
                }
            }
        });*/
    }

    /**
     * 变化标题
     */
    private void changeTitle() {
        String nowText,preText;
        nowText = " > " + currentFolder.getName();
/*        if (currentFolderList != null && currentFolderList.size() > 0) {
            if (currentFolderList.size() == 1 && loginFlag) {
                preText = "> 私密空间";
            } else {
                preText = currentFolderList.get(currentFolderList.size() - 1).getName();
            }
        } else if (loginFlag) {
            preText = nowText;
            nowText = "> 私密空间";
        } else {
            preText = "";
        }*/
        if(currentFolderList!=null && currentFolderList.size()>0){
            preText = currentFolderList.get(currentFolderList.size()-1).getName();
        }else{
            preText = "";
        }

        fileView.setTitle(preText, nowText);
    }

    /**
     * 列表项点击事件
     * @param adapter 适配器
     */
    private void initListener(final DocumentFileAdapter adapter) {

        adapter.setOnRecyclerViewItemClickListener(new DocumentFileAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                /*int index = getRealPosition(position);
                if (index > 0) {*/
                    /*DocumentFile file = fileList.get(index - 1);*/
                    DocumentFile file = fileList.get(position);
                    if (file.isDirectory()) {
                        currentFolderList.add(currentFolder);
                        currentFolder = file;
                        getAllFiles();
                        initAdapter();
                    } else {
                        FileUtil.openDocumentFile(file, context);
                    }
                /*} else {
                    fileView.showPasswordView();
                }*/
            }
            @Override
            public void onItemLongClick(View view, int position) {
/*                final int index = getRealPosition(position);
                if (index > 0) {*/
                    fileView.setToolBarType(FilePresenter.TOOL_BAR_LONG_CLICK);
                /*}*/
            }
            @Override
            public void onItemCheck(View view, int position, boolean check) {

            }
        });

    }


/*      获取文件下标+1 （因此这个值如果是0代表该项不是真实文件，而是私密空间栏）
      @param position 列表下标
      @return 文件下标+1*/

/*    private int getRealPosition(int position) {
        int index = position;
        if (currentFolderList.size() > 0 || loginFlag) {
            index ++;
        }
        return index;
    }*/

    //删除
    @Override
    public void deleteCheckFileList() {
        Map<Integer, Boolean> checkFileList = new HashMap<>();
        checkFileList.clear();
        checkFileList= fileView.getAdapter().getCheckMap();

        Integer[] size = new Integer[fileList.size()];

        for(int i=0;i<size.length;i++){
            size[i] = -1;
        }

        for (Integer index : checkFileList.keySet()) {
            if (checkFileList.get(index)) {
                size[index] = index;
            }
        }
        for(int i=fileList.size()-1;i>=0;i--){
            if(size[i] == i){
                doDelete(i,fileList.get(i));
            }
        }

        //删除
        fileView.setToolBarType(FilePresenter.TOOL_BAR_COMMON);
        fileView.getAdapter().changeCheckBoxVisibility(DocumentFileAdapter.ViewHolder.CHECK_INVISIBILITY);
    }

    @Override
    public void equalFileList(Context context) {

        /*int count=0;
        int num = 0;
        if(currentFolderList.size() == 0){
            num = fileList.size()-1;
        }else {
            num = fileList.size();
        }

        for(int index=0;index<num;index++){
            boolean s = FileUtil.transmitFile(fileList.get(index));
            if(s){
                count++;
            }
        }
        if(count>0){
            Toast.makeText(context,"上传成功",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context,"上传失败，文件有问题",Toast.LENGTH_SHORT).show();
        }
        fileView.setToolBarType(FilePresenter.TOOL_BAR_COMMON);
        fileView.getAdapter().changeCheckBoxVisibility(DocumentFileAdapter.ViewHolder.CHECK_INVISIBILITY);*/
    }



    /**
     * 执行删除文件
     * @param index 文件位置（1开始）
     * @param file 文件
     */
    private void doDelete(final int index, final DocumentFile file) {
/*        boolean result = FileUtil.deleteFile(file);
        Log.e("delete", result+"");
        if(result){
            removeData(index);
        }*/
        loadingDialog = getLoadingDialog("删除中...");
        loadingDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final boolean result = FileUtil.deleteFile(file);

                fileView.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismiss();
                        Log.e("delete", result+"");
                        if(result){
                            removeData(index);
                        }
                    }
                });

            }
        }).start();

/*        if (result && index>0) {
            removeData(index-1);
        }*/
    }


    /**
     * 删除某个item
     * @param index
     */
    private void removeData(int index) {
        fileView.getAdapter().removeData(index);
    }

    /**
     * 添加文件
     * @param file 文件
     */
    private void addData(DocumentFile file) {
        fileView.getAdapter().addData(file);
    }

    /**
     * 新建文件夹
     * @param name
     */
    @Override
    public void createFolder(String name) {
        DocumentFile newFile = FileUtil.createFile(context,name,currentFolder);
        addData(newFile);
        fileView.setToolBarType(FilePresenter.TOOL_BAR_COMMON);
    }

    /**
     * 重命名文件
     * @param name
     */
    @Override
    public void renameFile(String name) {
        int count = 0;
        int realIndex = -1;
        Map<Integer,Boolean> fileMap = fileView.getAdapter().getCheckMap();
        for(Integer index:fileMap.keySet()){
            if (!fileMap.get(index)) {
                continue;
            }
            count++;
            realIndex = index;
        }
        if(count == 1 && realIndex!=-1){
            DocumentFile myFile = fileList.get(realIndex);
            if(myFile.isDirectory()){
                Log.e("fileName", myFile.getName());
                myFile.renameTo(name);
            }else{
                Log.e("fileName", myFile.getName());
                String newName = name+myFile.getName().substring(myFile.getName().lastIndexOf("."),myFile.getName().length());
                Log.e("fileName",newName);
                myFile.renameTo(newName);
            }
            fileView.setToolBarType(FilePresenter.TOOL_BAR_COMMON);
            fileView.getAdapter().changeCheckBoxVisibility(DocumentFileAdapter.ViewHolder.CHECK_INVISIBILITY);
        }else{
            Toast.makeText(context,"请只选择一个文件进行重命名",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void copyFileList(boolean delete) {
        pasteFlag = true;

        deleteAfterPaste = delete;

        fileView.setToolBarType(FilePresenter.TOOL_BAR_PASTE);

        Map<Integer, Boolean> copyMap = fileView.getAdapter().getCheckMap();

        copyFileMap.clear();
        for (Integer index : copyMap.keySet()) {
            if (!copyMap.get(index)) {
                continue;
            }
/*            index = getRealPosition(index);
            DocumentFile copyFile = fileList.get(index-1);*/
            DocumentFile copyFile = fileList.get(index);
            copyFileMap.put(index, copyFile);
        }
        fileView.getAdapter().changeCheckBoxVisibility(DocumentFileAdapter.ViewHolder.CHECK_INVISIBILITY);
    }

    //粘贴
    @Override
    public void pasteFileList() {

        if(pasteFlag){
            for (final Integer index : copyFileMap.keySet()) {
                final DocumentFile copyFile = copyFileMap.get(index);

                pastDialog = getPastDialog("粘贴中...");
                pastDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final DocumentFile newFile = FileUtil.moveFile(context, copyFile, currentFolder);
                        fileView.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pastDialog.dismiss();

                                addData(newFile);
                                pasteFlag = false;
                                if (deleteAfterPaste) {
                                    doDelete(index, copyFile);
                                }
                            }
                        });

                    }
                }).start();

            }
        }else{
            Toast.makeText(context,"请选择你要移动的文件",Toast.LENGTH_SHORT).show();
        }

        //粘贴
        fileView.setToolBarType(FilePresenter.TOOL_BAR_COMMON);
    }




    @Override
    public boolean isRootView() {
        return currentFolderList.size() > 0;
    }

    @Override
    public void returnPreFolder() {
        currentFolder = currentFolderList.get(currentFolderList.size() - 1);
        currentFolderList.remove(currentFolderList.size() - 1);
        getAllFiles();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void refresh(String name) {
        if(name!=null){
            currentFolder=null;
        }
        if (currentFolder == null) {
            readDeviceList(name);
        } else {
            getAllFiles();
        }
    }

    @Override
    public void setLoginFlag(boolean loginFlag) {
        this.loginFlag = loginFlag;
    }

    @Override
    public boolean isLogin() {
        return loginFlag;
    }

    @Override
    public void unRegisterReceiver() {
        if (usbReceiver != null) {
            context.unregisterReceiver(usbReceiver);
        }
    }

    @Override
    public void registerReceiver() {
        //监听otg插入 拔出
        IntentFilter usbDeviceStateFilter = new IntentFilter();
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        usbDeviceStateFilter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        usbDeviceStateFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        usbDeviceStateFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        usbDeviceStateFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        usbDeviceStateFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        usbDeviceStateFilter.addDataScheme("file");

        context.registerReceiver(usbReceiver, usbDeviceStateFilter);

    }

    /**
     * u盘插拔广播接收器
     */
    private BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                //接收到U盘插入的广播
                case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                    fileView.onUDiskInsert(intent);
                    break;
                //接收到U盘拔出的广播
                case UsbManager.ACTION_USB_DEVICE_DETACHED:
                    fileView.onUDiskRemove(intent);
                    break;
                case Intent.ACTION_MEDIA_MOUNTED:
                    fileView.onUDiskInsert(intent);
                    break;
                case Intent.ACTION_MEDIA_REMOVED:
                    fileView.onUDiskRemove(intent);
                    break;
            }
        }
    };


    @Override
    public void pastSDFile(File file) {
        DocumentFile docRootFile = currentFolder;
        FileUtil.moveFileToSD(context, file, docRootFile);
    }


}
