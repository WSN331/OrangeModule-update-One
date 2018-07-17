package com.qiuyi.cn.orangemoduleNew.upanupdate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiuyi.cn.myloadingdialog.LoadingDialog;
import com.qiuyi.cn.orangemoduleNew.MainActivity;
import com.qiuyi.cn.orangemoduleNew.Manager.AllUdiskManager;
import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.Secret.AESHelperUpdate2;
import com.qiuyi.cn.orangemoduleNew.activity.AllFileShowActivity;
import com.qiuyi.cn.orangemoduleNew.activity.FileShowActivity;

import com.qiuyi.cn.orangemoduleNew.activity.SearchActivity;
import com.qiuyi.cn.orangemoduleNew.activity.UFileShowActivity;
import com.qiuyi.cn.orangemoduleNew.adapter.SDFileAdapter;
import com.qiuyi.cn.orangemoduleNew.interfaceToutil.SDFileDeleteListener;
import com.qiuyi.cn.orangemoduleNew.interfaceToutil.UdiskDeleteListener;
import com.qiuyi.cn.orangemoduleNew.myview.CommomDialog;
import com.qiuyi.cn.orangemoduleNew.myview.FileDetailDialog;
import com.qiuyi.cn.orangemoduleNew.myview.MoreOperatePopWindow;
import com.qiuyi.cn.orangemoduleNew.myview.MorePopWindow;
import com.qiuyi.cn.orangemoduleNew.myview.MySelectDialog;
import com.qiuyi.cn.orangemoduleNew.util.DiskWriteToSD;
import com.qiuyi.cn.orangemoduleNew.util.FileUtilOpen;
import com.qiuyi.cn.orangemoduleNew.util.ShareFile;
import com.qiuyi.cn.orangemoduleNew.util.WriteToUdisk;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/5/2.
 */
public class AllUdiskFileShowActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener,UdiskDeleteListener,View.OnClickListener{

    //正常标题栏
    private RelativeLayout rl_normal_head;
    private ImageView iv_back,bt_search,iv_more;//返回，搜索，更多
    private TextView tv_title;//标题

    //选择的标题栏
    private RelativeLayout rl_select_head;
    private ImageView iv_cancle;//取消选择
    private TextView tv_selectNum;//选择的文件数
    private TextView bt_selectAll;//全选

    //显示
    private RecyclerView rl_fileshow;
    private SwipeRefreshLayout allFile_sl;

    //底部功能框
    private LinearLayout ll_pager_native_bom;//复制，移动，删除，重命名
    private LinearLayout ll_features;//粘贴,取消
    private TextView tv_delete,tv_copy,tv_move,tv_more,tv_paste,tv_cancle;

    //tv_rename

    //目录
    private HorizontalScrollView myScrollView;
    private LinearLayout mGallery;
    private LayoutInflater mInflater;
    private Map<Integer,File> mFileMap;
    private Map<Integer,View> mViewMap;
    private int size = 0;
    //private TextView tv_fload;

    //获取文件
    private List<File> fileList;
    private SDFileAdapter sdfileAdapter;
    private GridLayoutManager myGridManager;

    //判断是否全选
    private boolean isSelectAll = true;

    //U盘根路径
    private String rootPath;
    private String currentPath;

    //复制到U盘
    private LoadingDialog dialog;//粘贴
    private LoadingDialog deleteDialog;//删除
    private LoadingDialog mydialog;//创建

    private WriteToUdisk udiskUtil;//复制到U盘
    private DiskWriteToSD diskWriteToSD;//复制到本地

    private List<DocumentFile> listDocFile;//U盘下的所有文件（文件+文件夹）
    //rivate List<DocumentFile> listDocDirectory;//U盘下所有文件夹

    //本地文件的删除
    private SDFileDeleteListener sdFileDeleteListener;
    //U盘文件的删除
    private AllUdiskManager myManager;

    //分享
    private ArrayList<Uri> listUris;
    //添加收藏和私密
    private List<File> listSS;
    //收藏夹的名字
    public static final String CollectionDirectory_Name = "CollectionDirectory";
    //添加私密
    public static final String SecretDirectory_Name = ".SecretDirectory";
    public static final String PASSWORD_STRING = "12345678";


    //这个是用来加密的，后来看了看腾讯的文件管理，发现不加密也行
    // private AESHelper mAESHelper;
    // private EncrytionOrDecryptionTask mTask = null;

    //是否显示了更多操作
    private boolean isMoreOperateshow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alludiskfileshow);

        listUris = new ArrayList<>();
        listSS = new ArrayList<>();

        udiskUtil = WriteToUdisk.getInstance(this.getApplicationContext(),this);
        diskWriteToSD = new DiskWriteToSD(AllUdiskFileShowActivity.this);

        sdFileDeleteListener = new AllFileShowActivity();
        myManager = AllUdiskManager.getAllUdiskManagerInstance();
        myManager.setmListener(this);

        listDocFile = new ArrayList<>();
        //listDocDirectory = new ArrayList<>();

        //获取所有文件
        getAllDocFile();
        //获取所有文件夹
        //getAllDocDirectory();

        initView();

        initData();

    }


    private int whereFromSearch;
    //初始化数据
    private void initData() {
        if(MainActivity.rootUFile!=null){
            rootPath = MainActivity.rootUFile.getAbsolutePath();

            //从search模块过来
            Intent intent = getIntent();
            String path = null;
            path = intent.getStringExtra("ufilename");

            if(path!=null){
                currentPath = path;
            }else{
                currentPath = rootPath;
            }

            File currentFolder = new File(currentPath);

            //标题
            addFolder(currentFolder);
            //tv_fload.setText(currentPath);

            readFileList(currentFolder);

            //从本地控制界面过来
            int from = intent.getIntExtra("from",-1);
            boolean flag = intent.getBooleanExtra("select",false);
            if(from==1){
                //从本地过来
                showPaste(from,flag, AllFileShowActivity.copyFileMap);
            }else if(from == 3){
                //从ShowActivity过来
                showPaste(from,flag, FileShowActivity.copyFileMap);
            }else if(from == 4){
                //从uFileShow过来
                showPaste(from,flag, UFileShowActivity.copyFileMap);
            }else if(from == 5){
                whereFromSearch = intent.getIntExtra("whereFrom",0);
                //从Search过去
                showPaste(from,flag, SearchActivity.copyFileMap);
            }


        }
    }



    //在粘贴，删除，新建后对listDocFile进行更新
    private void addListAllDocFile(DocumentFile newFile) {
        if(newFile.isDirectory() && newFile.listFiles()!=null){
            for (DocumentFile docFile:newFile.listFiles()){
                addListAllDocFile(docFile);
            }
        }
        listDocFile.add(newFile);
    }

    //获得所有Doc文件
    private void getAllDocFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                listDocFile.clear();
                listDocFile.add(MainActivity.rootUDFile);
                listDocFile.addAll(udiskUtil.getAllFile(MainActivity.rootUDFile));
                Log.e("docFile", "查找完毕");
            }
        }).start();
    }

    //获取所有文件夹
/*    private void getAllDocDirectory(){
        listDocDirectory = udiskUtil.getAllDocDirectory(MainActivity.rootUDFile);
        for(DocumentFile docFile : listDocDirectory){
            Log.e("docName", docFile.getName());
        }
    }*/

    //读取文件
    private void readFileList(final File currentFolder){
        fileList.clear();

        for(File file:currentFolder.listFiles()){
            fileList.add(file);
        }

        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                if (f1.isFile()) {
                    if (f2.isFile()) {
                        return 0;
                    } else {
                        return 1;
                    }
                } else {
                    if (f2.isFile()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        });

        /*if(sdfileAdapter == null){*/
        sdfileAdapter = new SDFileAdapter(this,fileList);
        rl_fileshow.setLayoutManager(myGridManager);
        rl_fileshow.setAdapter(sdfileAdapter);

        sdfileAdapter.setOnItemClick(new SDFileAdapter.SD_OnItemClick() {
            @Override
            public void onItemClick(View view, int position) {
                File file = fileList.get(position);

                boolean isShowBox = sdfileAdapter.isShowCheckBox();

                //处于可以选择的状态
                if(isShowBox){

                    //正在显示checkbox
                    boolean[] flag = sdfileAdapter.getFlag();

                    flag[position] = !flag[position];

                    sdfileAdapter.setFlag(flag);

                    int count = 0;
                    for(boolean flagment:flag){
                        if(flagment){
                            count++;
                        }
                    }
                    tv_selectNum.setText("已选("+count+")");

                    //判断是否全选了
                    pdSelect(count);

                    sdfileAdapter.notifyDataSetChanged();
                }else{
                    if(file.isDirectory()){

                        /*currentPath += "/"+file.getName();
                        tv_fload.setText(currentPath);*/

                        addFolder(file);
                        currentPath = file.getAbsolutePath();

                        readFileList(file);
                    }else{
                        FileUtilOpen.openFileByPath(getApplicationContext(),file.getPath());
                    }
                }

            }
            @Override
            public void onItemLongClick(View view, int position) {

                //选择状态栏显示
                rl_normal_head.setVisibility(View.GONE);
                rl_select_head.setVisibility(View.VISIBLE);
                //底部导航栏按钮显示
                ll_pager_native_bom.setVisibility(View.VISIBLE);

                //全选按钮
                bt_selectAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isSelectAll){
                            sdfileAdapter.selectAll();
                            bt_selectAll.setText("取消全选");
                            isSelectAll = false;
                            tv_selectNum.setText("已选("+fileList.size()+")");
                        }else{
                            sdfileAdapter.noSelect();
                            bt_selectAll.setText("全选");
                            isSelectAll = true;
                            tv_selectNum.setText("已选(0)");
                        }
                        sdfileAdapter.notifyDataSetChanged();
                    }
                });

                //取消按钮
                iv_cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //选择状态栏显示
                        rl_select_head.setVisibility(View.GONE);
                        rl_normal_head.setVisibility(View.VISIBLE);
                        ll_pager_native_bom.setVisibility(View.GONE);

                        sdfileAdapter.setShowCheckBox(false);
                        sdfileAdapter.ReFresh();
                    }
                });

                //删除按钮
                tv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //选择，显示一下有哪几个选择了
                        boolean[] flag = sdfileAdapter.getFlag();
                        for(int i = flag.length-1;i>=0;i--){
                            if(flag[i]){
                                Log.e("select", "选中："+i);
                                deleteDialog.show();

                                //final int finalI = i;
                                //从当前目录下去找

                                final DocumentFile deleteFile = findDocFile(fileList.get(i));

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //DocumentFile deleteFile = findDocFile(fileList.get(finalI));
                                        doDelete(deleteFile);

                                        listDocFile.remove(deleteFile);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                deleteDialog.dismiss();
                                                tv_selectNum.setText("已选(0)");
                                            }
                                        });
                                    }
                                }).start();

                                fileList.remove(i);
                            }
                        }
                        sdfileAdapter.ReFresh();
                    }
                });


                //更多操作模块
                tv_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final int[] myflag ={-1,-1,-1,-1,-1};
                        //选择
                        boolean[] flag = sdfileAdapter.getFlag();
                        int count = 0;
                        int location = -1;
                        boolean isDirectory = false;

                        listUris.clear();
                        listSS.clear();

                        for(int i = flag.length-1;i>=0;i--){
                            if(flag[i]){
                                //分享
                                File file = fileList.get(i);
                                if(!file.isDirectory()){
                                    listUris.add(Uri.fromFile(file));
                                }else{
                                    isDirectory = true;
                                }
                                //收藏和私密
                                listSS.add(file);
                                count++;
                                location=i;
                            }
                        }

                        //CollectionFiles collectionFiles = null;
                        File collectFile = null;
                        if(count==1){
                            //当只有一个文件的时候，判断一下这个文件是否在收藏中
                            File colFile = fileList.get(location);
                            //collectionFiles = DBUtil.getCollectFile(colFile.getPath());
                            //在MyFile->CollectionDirectory文件夹下查找文件是否存在
                            collectFile = diskWriteToSD.findCollectionFile(colFile,CollectionDirectory_Name);
                            if(collectFile!=null){
                                //若为true就是已经收藏了
                                myflag[1] = 2;
                            }

                            if(isDirectory){
                                //有文件夹
                                myflag[0] = 1;//分享不行
                                myflag[2] = 1;//添加私密不行
                            }
                        }else if(count>1){
                            myflag[1] = 1;
                            myflag[2] = 1;
                            myflag[3] = 1;
                            myflag[4] = 1;
                            if(isDirectory){
                                myflag[0] = 1;//分享不行
                            }
                        }


                        final int finalCount = count;//选中的数量
                        final int finalLocation = location;//文件的位置
                        final boolean finalIsDirectory = isDirectory;//文件的种类
                        final File finalCollection = collectFile;//文件手否收藏
                        //final CollectionFiles finalCollectionFiles = collectionFiles;//文件是否在收藏中

                        if(count>0){
                            isMoreOperateshow = !isMoreOperateshow;
                            if(isMoreOperateshow){

                                new MoreOperatePopWindow(AllUdiskFileShowActivity.this, new MoreOperatePopWindow.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(final MoreOperatePopWindow popupWindow, int position) {
                                        //分享
                                        if(position==1 && !finalIsDirectory){
                                            ShareFile.shareMultipleFiles(AllUdiskFileShowActivity.this,listUris);
                                            popupWindow.dismiss();
                                            UIShowHide();
                                            isMoreOperateshow = false;
                                        }

                                        //添加收藏
                                        if(position==2 && finalCount==1){
                                            //只有一个对象
                                            if(finalCollection!=null){
                                                //已经在收藏中，那么操作就是取消收藏
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        finalCollection.delete();
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                popupWindow.dismiss();
                                                                UIShowHide();
                                                                isMoreOperateshow = false;
                                                            }
                                                        });
                                                    }
                                                }).start();
                                            }else{
                                                //为空，操作就是收藏
                                                final File file = fileList.get(finalLocation);

                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        diskWriteToSD.writeSelectFileToSD(file,CollectionDirectory_Name);
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                popupWindow.dismiss();
                                                                UIShowHide();
                                                                isMoreOperateshow = false;
                                                            }
                                                        });
                                                    }
                                                }).start();

                                            }

                                        }
                                        //添加私密
                                        if(position==3 && !finalIsDirectory){
                                            //为空，操作就是收藏
                                            final File file = fileList.get(finalLocation);

                                            //得到的MyFile->.SecretDirectory文件夹
                                            File mySecretDirectory = diskWriteToSD.getSDCardFile(SecretDirectory_Name);
                                            String newFileName = AESHelperUpdate2.encrypt(PASSWORD_STRING,file.getName()+"*"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                            final File newFile = new File(mySecretDirectory.getAbsoluteFile()+"/"+newFileName);

                                            final DocumentFile deleteFile = findDocFile(file);

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    boolean isWrite = diskWriteToSD.writeToSD3(file,newFile);

                                                    if(isWrite){
                                                        doDelete(deleteFile);
                                                        listDocFile.remove(deleteFile);
                                                    }
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            //UI更新
                                                            popupWindow.dismiss();
                                                            isMoreOperateshow = false;
                                                            onRefresh();
                                                        }
                                                    });
                                                }
                                            }).start();

                                        }
                                        //重命名
                                        if(position==4){
                                            //重命名和详情都需要点击的是一个
                                            if(finalCount ==1){
                                                createNewFolder("重命名文件",2,fileList.get(finalLocation));
                                                popupWindow.dismiss();
                                                UIShowHide();
                                                isMoreOperateshow = false;
                                            }
                                        }
                                        //详情
                                        if(position==5){
                                            //重命名和详情都需要点击的是一个
                                            if(finalCount ==1){
                                                new FileDetailDialog(AllUdiskFileShowActivity.this,R.style.dialog,fileList.get(finalLocation)).show();
                                                isMoreOperateshow = !isMoreOperateshow;
                                                popupWindow.dismiss();
                                                UIShowHide();
                                                isMoreOperateshow = false;
                                            }
                                        }
                                    }
                                }).setTitle(myflag).showUp2(tv_more);
                            }

                        }
                    }
                });




                //重命名
/*                tv_rename.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //选择
                        boolean[] flag = sdfileAdapter.getFlag();
                        int count = 0;
                        int position = -1;
                        for(int i = flag.length-1;i>=0;i--){
                            if(flag[i]){
                                count++;
                                position = i;
                            }
                        }
                        if(count==1){
                            createNewFolder("重命名文件",2,fileList.get(position));
                        }
                    }
                });*/

                //复制
                tv_copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int count = 0;
                        boolean[] flag = sdfileAdapter.getFlag();
                        for(int i = flag.length-1;i>=0;i--){
                            if(flag[i]){
                                count++;
                            }
                        }

                        if(count>0){
                            //选择，显示一下有哪几个选择了
                            selectHowToPaste(false);
                        }

                    }
                });

                //移动
                tv_move.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int count = 0;
                        boolean[] flag = sdfileAdapter.getFlag();
                        for(int i = flag.length-1;i>=0;i--){
                            if(flag[i]){
                                count++;
                            }
                        }
                        if(count>0){
                            selectHowToPaste(true);
                        }
                    }
                });


                sdfileAdapter.setShowCheckBox(true);
                boolean flag[] = sdfileAdapter.getFlag();
                flag[position] = true;

                sdfileAdapter.setFlag(flag);

                tv_selectNum.setText("已选(1)");

                //判断是否全选了
                pdSelect(1);

                sdfileAdapter.notifyDataSetChanged();
            }

            @Override
            public void changeCount(int nowcount) {
                tv_selectNum.setText("已选("+nowcount+")");
                //判断是否全选
                pdSelect(nowcount);
            }
        });

/*        tv_fload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!currentPath.equals(rootPath)) {
                    if(sdfileAdapter.isShowCheckBox()){
                        //bt_selectAll.setVisibility(View.GONE);
                        ll_pager_native_bom.setVisibility(View.GONE);

                        sdfileAdapter.setShowCheckBox(false);
                        sdfileAdapter.ReFresh();
                    }
                    currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
                    tv_fload.setText(currentPath);
                    readFileList(new File(currentPath));
                }
            }
        });*/
    }

    //判断是否全选
    private void pdSelect(int count) {
        if(count==fileList.size()){
            bt_selectAll.setText("取消全选");
            isSelectAll = false;
        }else{
            bt_selectAll.setText("全选");
            isSelectAll = true;
        }
    }


    /**
     * 这里是一个全部文件的查找
     * 将File文件转换成DocumentFile
     * @param file
     * @return
     */
    private DocumentFile findDocFile(File file){
        /*listDocFile = udiskUtil.getAllFile(MainActivity.rootUDFile);*/
        DocumentFile myFile = udiskUtil.getDocFile(listDocFile,file.getPath());

        if(myFile==null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(AllUdiskFileShowActivity.this,"请稍等，您的操作太快了~",Toast.LENGTH_SHORT);
                    return;
                }
            });
        }
        //return udiskUtil.getDocFile(listDocFile,file.getPath());
        return myFile;
    }

/*    *//**
     *
     * @param currentFile
     * @return
     *//*
    private DocumentFile findDocFileNew(File currentFile){

    }*/



    /**
     * 删除文件
     * @param docFile
     */
    private void doDelete(DocumentFile docFile) {
        if(docFile.exists()){
            if(docFile.isDirectory()){
                for(DocumentFile nowDocFile:docFile.listFiles()){
                    doDelete(nowDocFile);
                }
            }
            docFile.delete();
        }
    }


    /**
     * 复制+移动
     * @param b false=复制，true=移动（复制后，删除原来）
     */
    public static Map<Integer,File> copyFileMap = new HashMap<>();
    private void selectHowToPaste(final boolean b) {
        copyFileMap.clear();
        boolean[] flag = sdfileAdapter.getFlag();
        for(int i = flag.length-1;i>=0;i--){
            if(flag[i]){
                copyFileMap.put(i,fileList.get(i));
            }
        }
        rl_select_head.setVisibility(View.GONE);
        rl_normal_head.setVisibility(View.VISIBLE);
        ll_pager_native_bom.setVisibility(View.GONE);
        sdfileAdapter.setShowCheckBox(false);
        sdfileAdapter.ReFresh();

        new MySelectDialog(this, R.style.dialog, new MySelectDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, Integer flag) {
                if(flag==1){
                    //本地
                    Intent intent = new Intent(AllUdiskFileShowActivity.this, AllFileShowActivity.class);
                    //带上标志2表示是从U盘管理去到本地文件管理
                    intent.putExtra("from",2);
                    intent.putExtra("select",b);
                    startActivity(intent);
                }else if(flag ==2){
                    //U盘
                    showPaste(2,b,AllUdiskFileShowActivity.copyFileMap);
                }
            }
        }).show();
    }


    /**
     * 粘贴取消模块
     * @param whereFrom 来自于本地或者U盘，1 本地 2 U盘
     * @param flag 剪切还是复制 false 复制 true 剪切
     * @param copyFileMap
     */
    private void showPaste(final int whereFrom,final boolean flag, final Map<Integer,File> copyFileMap) {
        //粘贴取消模块展示
        ll_features.setVisibility(View.VISIBLE);

        //粘贴
        tv_paste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<Integer,File> myCopyMap = copyFileMap;
                if(myCopyMap!=null && myCopyMap.size()>0){
                    for(final Integer index:myCopyMap.keySet()){
                        final File file = myCopyMap.get(index);

                        dialog.show();

                        final DocumentFile currentFolder = findDocFile(new File(currentPath));
                        final DocumentFile befordFile = findDocFile(file);

                        if(currentFolder!=null){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //写入选中的currentFolder中去
                                    //DocumentFile newFile = findDocFile(file);
                                    DocumentFile newFile = writeToUDick(file,currentFolder);

                                    //将新加入的文件添加到ListDoc中
                                    addListAllDocFile(newFile);

                                    //udiskUtil.moveFile(AllUdiskFileShowActivity.this,newFile,currentFolder);

                                    if(flag && (whereFrom==2||whereFrom==4)){
                                        //是从剪切过来的
                                        if(befordFile!=null){
                                            doDelete(befordFile);
                                            listDocFile.remove(befordFile);
                                        }
                                    }else if(flag && (whereFrom==1||whereFrom==3)){
                                        sdFileDeleteListener.doDeleteSDFile(file);
                                    }else if(flag && whereFrom==5){
                                        if(whereFromSearch==1){
                                            //是从剪切过来的
                                            sdFileDeleteListener.doDeleteSDFile(file);
                                        }else if(whereFromSearch==2){
                                            //从U盘过来的文件
                                            if(befordFile!=null){
                                                doDelete(befordFile);
                                                listDocFile.remove(befordFile);
                                            }
                                        }
                                    }

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();
                                            sdfileAdapter.addFile(file);
                                            ll_features.setVisibility(View.GONE);
                                            onRefresh();
                                        }
                                    });
                                }
                            }).start();
                        }/*else{
                            dialog.dismiss();
                            Toast.makeText(AllUdiskFileShowActivity.this,"您的操作太快了，请慢慢来",Toast.LENGTH_SHORT).show();
                        }*/
                    }
                    //getAllDocFile();
                }
            }
        });

        //取消
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_features.setVisibility(View.GONE);
            }
        });
    }



    /**
     * 重命名+新建
     * @param name title
     * @param flag 1 新建，2 重命名
     */
    private void createNewFolder(final String name, final int flag, final File myfile) {
        new CommomDialog(this, R.style.dialog, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, final String folderName, boolean confirm) {
                if(confirm){
                    //点击了确定按钮
                    if(flag==1){
                        dialog.dismiss();

                        mydialog.show();
                        //新建文件夹
                        DocumentFile findFile = findDocFile(new File(currentPath,folderName));
                        if(findFile==null){
                            //文件夹不存在就创建
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    DocumentFile rootFile = findDocFile(new File(currentPath));
                                    final DocumentFile newFile = rootFile.createDirectory(folderName);

                                    //将新建的文件夹加入
                                    addListAllDocFile(newFile);
                                    //listDocFile.add(newFile);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            Log.e("create", "新建文件夹"+newFile.getName());
                                            onRefresh();

                                            mydialog.dismiss();
                                        }
                                    });
                                }
                            }).start();

                        }else{
                            mydialog.dismiss();
                            Toast.makeText(AllUdiskFileShowActivity.this,"文件夹已存在",Toast.LENGTH_SHORT).show();
                        }
                    }else if(flag == 2){
                        //重命名文件夹
                        if(myfile!=null){
                            //找到要重命名的文件夹
                            DocumentFile nowFile = findDocFile(myfile);

                            listDocFile.remove(nowFile);

                            if(nowFile.isDirectory()){
                                //是个文件夹
                                Log.e("fileName", nowFile.getName());
                                nowFile.renameTo(folderName);
                            }else{
                                //是个文件
                                String newName = folderName+myfile.getName().substring(myfile.getName().lastIndexOf("."),myfile.getName().length());
                                Log.e("fileName", nowFile.getName());
                                nowFile.renameTo(newName);
                            }

                            dialog.dismiss();

                            addListAllDocFile(nowFile);
                            //listDocFile.add(nowFile);
                        }

                    }
                    onRefresh();
/*                    rl_select_head.setVisibility(View.GONE);
                    rl_normal_head.setVisibility(View.VISIBLE);
                    ll_pager_native_bom.setVisibility(View.GONE);

                    sdfileAdapter.setShowCheckBox(false);
                    sdfileAdapter.ReFresh();*/

                    //getAllDocFile();
                }
            }
        }).setTitle(name).show();
    }


    //将选中的文件写入U盘
    private DocumentFile writeToUDick(File file,DocumentFile documentFile) {
        if(file.isDirectory()){
            DocumentFile dirFile = documentFile.createDirectory(file.getName());
            for(File newFile:file.listFiles()){
                writeToUDick2(newFile,dirFile);
            }
            return dirFile;
        }else{
            DocumentFile newFile = udiskUtil.writeToSDFile(this,file,documentFile);
            return newFile;
        }
    }

    //将选中文件写入U盘2方法
    private void writeToUDick2(File file,DocumentFile documentFile) {
        if(file.isDirectory()){
            DocumentFile dirFile = documentFile.createDirectory(file.getName());
            for(File newFile:file.listFiles()){
                writeToUDick2(newFile,dirFile);
            }
        }else{
            DocumentFile newFile = udiskUtil.writeToSDFile(this,file,documentFile);
        }
    }


    //初始化界面
    private void initView() {
        dialog = new LoadingDialog.Builder(this)
                .setMessage("复制中")
                .setCancelable(false)
                .setCancelOutside(false)
                .create();

        deleteDialog = new LoadingDialog.Builder(this)
                .setMessage("删除中")
                .setCancelable(false)
                .setCancelOutside(false)
                .create();

        mydialog = new LoadingDialog.Builder(AllUdiskFileShowActivity.this)
                .setMessage("创建中")
                .setCancelable(false)
                .setCancelOutside(false)
                .create();

        //正常标题栏
        rl_normal_head = findViewById(R.id.rl_normal_head);
        iv_back = findViewById(R.id.allfileshow_iv_back);//返回
        bt_search = findViewById(R.id.img_search);//搜索
        iv_more = findViewById(R.id.img_more); //更多
        tv_title = findViewById(R.id.allfileshow_tv_title); //标题

        //选择checkbox出来之后显示的标题栏
        rl_select_head = findViewById(R.id.rl_select_head);
        iv_cancle = findViewById(R.id.rl_allfileshow_title_cancle); //取消选择
        tv_selectNum = findViewById(R.id.rl_allfileshow_title_select);//选择的文件数
        bt_selectAll = findViewById(R.id.bt_selectAll);//全选按钮

        //目录
        myScrollView = findViewById(R.id.horizontalSV);
        mGallery = findViewById(R.id.ll_show_folder);
        mInflater = LayoutInflater.from(this);
        mViewMap = new HashMap<>();
        mFileMap = new HashMap<>();
        //tv_fload = findViewById(R.id.tv_floader);

        //展示列表
        rl_fileshow = findViewById(R.id.allFile_rl);
        allFile_sl = findViewById(R.id.allFile_sl);
        //底部导航栏
        ll_pager_native_bom = findViewById(R.id.ll_pager_native_bom);
        tv_delete = findViewById(R.id.tv_delete);
        tv_copy = findViewById(R.id.tv_copy);
        tv_move = findViewById(R.id.tv_move);

        tv_more = findViewById(R.id.tv_more);
        //tv_rename = findViewById(R.id.tv_rename);

        ll_features = findViewById(R.id.ll_pager_native_bom_features);
        tv_cancle = findViewById(R.id.tv_cancel);
        tv_paste = findViewById(R.id.tv_paste);

        allFile_sl.setColorSchemeColors(Color.RED);
        allFile_sl.setOnRefreshListener(this);

        //搜索键
        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //去到搜索界面,来自U盘
                Intent intent = new Intent(new Intent(AllUdiskFileShowActivity.this,SearchActivity.class));
                intent.putExtra("udisk",true);
                startActivity(intent);
            }
        });

        //返回键
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //更多
        iv_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MorePopWindow(AllUdiskFileShowActivity.this, new MorePopWindow.OnItemClickListener() {
                    @Override
                    public void onItemClick(PopupWindow popupWindow, int position) {
                        if(position==1){
                            //新建文件夹
                            createNewFolder("新建文件夹",1,null);
                        }else if(position==2){
                            //排序
                        }
                    }
                }).showAsDropDown(iv_more,-200,40);
            }
        });

        fileList = new ArrayList<>();
        myGridManager = new GridLayoutManager(this,1);
    }

    @Override
    public void onBackPressed() {

        //现在的长度
        int id = mFileMap.keySet().size()-1;

        //到达根目录
        if(id==0){
            super.onBackPressed();
        }else{
            File file = mFileMap.get(id-1);
            //将之后的全部删除
            mFileMap.remove(id);
            mGallery.removeView(mViewMap.get(id));
            mViewMap.remove(id);

            size = id;

            currentPath = file.getAbsolutePath();
            onRefresh();
        }


/*        if (!currentPath.equals(rootPath)) {
            currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
            tv_fload.setText(currentPath);
            readFileList(new File(currentPath));
        }else{
            super.onBackPressed();
        }*/
    }

    @Override
    public void onRefresh() {
        allFile_sl.setRefreshing(true);
        readFileList(new File(currentPath));

        //bt_selectAll.setVisibility(View.GONE);
        rl_select_head.setVisibility(View.GONE);
        rl_normal_head.setVisibility(View.VISIBLE);
        ll_pager_native_bom.setVisibility(View.GONE);

        sdfileAdapter.setShowCheckBox(false);
        sdfileAdapter.ReFresh();

        allFile_sl.setRefreshing(false);
    }

    //操作完之后的UI显示，相当于取消
    private void UIShowHide(){
        //选择状态栏显示
        rl_select_head.setVisibility(View.GONE);
        rl_normal_head.setVisibility(View.VISIBLE);
        ll_pager_native_bom.setVisibility(View.GONE);

        sdfileAdapter.setShowCheckBox(false);
        sdfileAdapter.ReFresh();
    }

    //删除文件
    @Override
    public void doUdiskDelete(File file) {
        final DocumentFile beforeFile  = findDocFile(file);

        new Thread(new Runnable() {
            @Override
            public void run() {
                doDelete(beforeFile);
                beforeFile.delete();
            }
        }).start();

        listDocFile.remove(beforeFile);

        //修改有问题
    }

    @Override
    protected void onResume() {
        onRefresh();
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        File file = mFileMap.get(id);

        //将之后的全部删除
        for(int i=mFileMap.keySet().size();i>id;i--){
            mFileMap.remove(i);
            mGallery.removeView(mViewMap.get(i));
            mViewMap.remove(i);
        }

        size = id+1;

        currentPath = file.getAbsolutePath();

        onRefresh();
    }

    //添加路径标签
    public void addFolder(File file){
        View newView = mInflater.inflate(R.layout.file_folder,mGallery,false);
        TextView tv_title = newView.findViewById(R.id.tv_title);
        tv_title.setText(file.getName()+" > ");
        tv_title.setId(size);

        tv_title.setOnClickListener(this);

        mFileMap.put(size,file);
        mViewMap.put(size,newView);
        size++;

        mGallery.addView(newView);

        final Timer timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                myScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                timer.cancel();
            }
        },100L);
    }


}
