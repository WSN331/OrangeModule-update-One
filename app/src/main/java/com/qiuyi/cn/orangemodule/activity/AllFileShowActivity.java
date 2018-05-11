package com.qiuyi.cn.orangemodule.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.Manager.AllUdiskManager;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.Secret.AESHelperUpdate2;
import com.qiuyi.cn.orangemodule.adapter.SDFileAdapter;
import com.qiuyi.cn.orangemodule.interfaceToutil.SDFileDeleteListener;
import com.qiuyi.cn.orangemodule.myview.CommomDialog;
import com.qiuyi.cn.orangemodule.myview.FileDetailDialog;
import com.qiuyi.cn.orangemodule.myview.MoreOperatePopWindow;
import com.qiuyi.cn.orangemodule.myview.MorePopWindow;
import com.qiuyi.cn.orangemodule.myview.MySelectDialog;
import com.qiuyi.cn.orangemodule.upanupdate.AllUdiskFileShowActivity;
import com.qiuyi.cn.orangemodule.util.DiskWriteToSD;
import com.qiuyi.cn.orangemodule.util.FileManager.FileUtils;
import com.qiuyi.cn.orangemodule.util.FileUtilOpen;
import com.qiuyi.cn.orangemodule.util.ShareFile;
import com.qiuyi.cn.orangemodule.util.WriteToUdisk;

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
 * Created by Administrator on 2018/3/27.
 * 展示所有文件
 */
public class AllFileShowActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener,SDFileDeleteListener,View.OnClickListener{

    //正常标题栏
    private RelativeLayout rl_normal_head;
    private ImageView iv_back,bt_search,iv_more;//返回，搜索，更多
    private TextView tv_title;//标题

    //选择的标题栏
    private RelativeLayout rl_select_head;
    private ImageView iv_cancle;//取消选择
    private TextView tv_selectNum;//选择的文件数
    private TextView bt_selectAll;//全选

    //目录
    private HorizontalScrollView myScrollView;
    private LinearLayout mGallery;
    private LayoutInflater mInflater;
    private Map<Integer,File> mFileMap;
    private Map<Integer,View> mViewMap;
    private int size = 0;
    //private TextView tv_fload;

    //显示
    private RecyclerView rl_fileshow;
    private SwipeRefreshLayout allFile_sl;

    //底部功能框
    private LinearLayout ll_pager_native_bom;//复制，移动，删除，重命名
    private LinearLayout ll_features;//粘贴,取消
    private TextView tv_delete,tv_copy,tv_move,tv_more,tv_paste,tv_cancle;

    //tv_rename被拿掉

    //手机根路径
    private String rootPath;
    private String currentPath;

    //获取文件
    private List<File> fileList;
    private SDFileAdapter sdfileAdapter;
    private GridLayoutManager myGridManager;

    //判断是否全选
    private boolean isSelectAll = true;

    //复制到U盘
    private LoadingDialog dialog;
    private LoadingDialog deleteDialog;
    private WriteToUdisk udiskUtil;//复制到U盘
    private DiskWriteToSD diskWriteToSD;//复制到本地

    //U盘文件删除
    //private UdiskDeleteListener udiskDeleteListener;
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
        setContentView(R.layout.activity_allfileshow);

        listUris = new ArrayList<>();
        listSS = new ArrayList<>();

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

        udiskUtil =  WriteToUdisk.getInstance(this.getApplicationContext(),this);
        diskWriteToSD = new DiskWriteToSD(AllFileShowActivity.this);

        //mAESHelper = new AESHelper();

        //udiskDeleteListener = new AllUdiskFileShowActivity();
        myManager = AllUdiskManager.getAllUdiskManagerInstance();

        initView();

        initData();
    }


    private int whereFromSearch;
    //初始化数据
    private void initData() {

        if(FileUtils.isSDCardAvailable()){
            //SD卡存在
            rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            Intent intent = getIntent();
            String searchPath = null;
            searchPath = intent.getStringExtra("filepath");
            if(searchPath!=null){
                //是从查询页面过来的
                currentPath = searchPath;
            }else{
                currentPath = rootPath;
            }

            //tv_fload.setText(currentPath);

            File currentFolder = new File(currentPath);

            //标题
            addFolder(currentFolder);

            readFileList(currentFolder);

            //从U盘控制界面过来
            int from = intent.getIntExtra("from",-1);
            boolean flag = intent.getBooleanExtra("select",false);
            if(from==2){
                //从U盘过来
                showPaste(from,flag, AllUdiskFileShowActivity.copyFileMap);
            }else if(from == 3){
                //从FileShow页面过来
                showPaste(from,flag, FileShowActivity.copyFileMap);
            }else if(from == 4){
                //从UFileShow过来
                showPaste(from,flag, UFileShowActivity.copyFileMap);
            }else if(from == 5){
                whereFromSearch = intent.getIntExtra("whereFrom",0);
                //从Search过去
                showPaste(from,flag, SearchActivity.copyFileMap);
            }
        }

    }

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
                                    final File defile = fileList.get(i);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            doDelete(defile);

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

                                    new MoreOperatePopWindow(AllFileShowActivity.this, new MoreOperatePopWindow.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(final MoreOperatePopWindow popupWindow, int position) {
                                            //分享
                                            if(position==1 && !finalIsDirectory){
                                                ShareFile.shareMultipleFiles(AllFileShowActivity.this,listUris);
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

                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        boolean isWrite = diskWriteToSD.writeToSD3(file,newFile);

                                                        if(isWrite){
                                                            file.delete();
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
                                                    new FileDetailDialog(AllFileShowActivity.this,R.style.dialog,fileList.get(finalLocation)).show();
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

/*                    //重命名
                    tv_rename.setOnClickListener(new View.OnClickListener() {
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
                                    copyFileMap.put(i,fileList.get(i));
                                }
                            }

                            if(count>0){
                                //选择，显示一下有哪几个选择了
                                selectHowToPaste(true);
                            }


                        }
                    });


/*                    //U盘存在
                    if(MainActivity.isHaveUpan){
                        //复制到U盘按钮
                        tv_copy.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        boolean[] flag = sdfileAdapter.getFlag();
                                        for(int i = flag.length-1;i>=0;i--){
                                            if(flag[i]){
                                                Log.e("select", "选中："+i);
                                                writeToUDick(fileList.get(i),udiskUtil.getCurrentFolder());
                                            }
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();
                                                //头部状态栏显示
                                                bt_selectAll.setVisibility(View.GONE);
                                                //底部状态栏显示
                                                ll_pager_native_bom.setVisibility(View.GONE);

                                                sdfileAdapter.setShowCheckBox(false);
                                                sdfileAdapter.ReFresh();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        });
                    }*/

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

            /*tv_fload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
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
     * 删除文件
     * @param file
     */
    private void doDelete(File file) {
        if(file.isDirectory()) {
            for (File deleteFile : file.listFiles()) {
                doDelete(deleteFile);
            }
        }
        file.delete();
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

        if(copyFileMap.size()<=0){
            return;
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
                    //本地,展示新的粘贴取消模块
                    showPaste(1,b,AllFileShowActivity.copyFileMap);
                }else if(flag ==2){
                    //U盘
                    if(MainActivity.isHaveUpan){
                        Intent intent = new Intent(AllFileShowActivity.this, AllUdiskFileShowActivity.class);
                        //带上标志1表示是从本地文件管理去到U盘文件管理
                        intent.putExtra("from",1);
                        intent.putExtra("select",b);
                        startActivity(intent);
                    }else{
                        Toast.makeText(AllFileShowActivity.this,"请插入U盘",Toast.LENGTH_SHORT).show();
                    }
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
    private void showPaste(final int whereFrom, final boolean flag, final Map<Integer,File> copyFileMap) {
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
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //写入选中的currentFolder中去
                                diskWriteToSD.WriteFileToSD(file,new File(currentPath));
                                if(flag && (whereFrom==1||whereFrom==3)){
                                    //是从剪切过来的
                                    doDelete(file);
                                }else if(flag && (whereFrom==2||whereFrom==4)){
                                    //从U盘过来的文件
                                    myManager.udiskDelete(file);
                                    //udiskDeleteListener.doUdiskDelete(file);
                                }else if(flag && whereFrom==5){
                                    if(whereFromSearch==1){
                                        //是从剪切过来的
                                        doDelete(file);
                                    }else if(whereFromSearch==2){
                                        //从U盘过来的文件
                                        myManager.udiskDelete(file);
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
                    }
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
    private void createNewFolder(String name, final int flag, final File myfile) {
        new CommomDialog(this, R.style.dialog, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, String folderName, boolean confirm) {
                if(confirm){
                    //点击了确定按钮
                    if(flag==1){
                        //新建文件夹
                        File file = new File(currentPath,folderName);
                        if(!file.exists()){
                            //文件夹不存在就创建
                            file.mkdirs();
                            sdfileAdapter.addFile(file);
                            Log.e("create", "新建文件夹"+file.getAbsolutePath());
                        }else{
                            Toast.makeText(AllFileShowActivity.this,"文件夹已存在",Toast.LENGTH_SHORT).show();
                        }
                    }else if(flag == 2){
                        //重命名文件夹
                        if(myfile!=null){
                            if(myfile.isDirectory()){
                                //是个文件夹
                                File newFile = new File(myfile.getParentFile(),folderName);

                                myfile.renameTo(newFile);
                            }else{
                                //是个文件
                                String newName = folderName+myfile.getName().substring(myfile.getName().lastIndexOf("."),myfile.getName().length());
                                File newFile = new File(myfile.getParentFile(),newName);
                                myfile.renameTo(newFile);
                            }
                        }
                        onRefresh();
                    }

                    dialog.dismiss();

/*                    rl_select_head.setVisibility(View.GONE);
                    rl_normal_head.setVisibility(View.VISIBLE);
                    ll_pager_native_bom.setVisibility(View.GONE);

                    sdfileAdapter.setShowCheckBox(false);
                    sdfileAdapter.ReFresh();*/

                }
            }
        }).setTitle(name).show();
    }

    //将选中的文件写入U盘
    private void writeToUDick(File file,DocumentFile documentFile) {
        if(file.isDirectory()){
            DocumentFile dirFile = documentFile.createDirectory(file.getName());
            for(File newFile:file.listFiles()){
                writeToUDick(newFile,dirFile);
            }
        }else{
            udiskUtil.writeToSDFile(this,file,documentFile);
        }
    }


    //初始化界面
    private void initView() {
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
                Intent intent = new Intent(new Intent(AllFileShowActivity.this,SearchActivity.class));
                intent.putExtra("main",true);
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
                new MorePopWindow(AllFileShowActivity.this, new MorePopWindow.OnItemClickListener() {
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

    @Override
    public void doDeleteSDFile(File file) {
        doDelete(file);
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
/*                                                final String filePath = newFile.getAbsolutePath();
                                                final String fileName = file.getName();
                                                final String fileDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(file.lastModified());
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            final String fileSize = Formatter.formatFileSize(AllFileShowActivity.this, FileUtils.getFileSize(file));
                                                            diskWriteToSD.writeToSD3(file,newFile);

                                                            //删除原来文件
                                                            file.delete();

                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    //存到数据库
                                                                    SecretFiles mySecretFile = new SecretFiles(filePath,fileName,fileSize,fileDate);
                                                                    mySecretFile.save();
                                                                    //UI更新
                                                                    popupWindow.dismiss();
                                                                    fileList.remove(finalLocation);
                                                                    UIShowHide();
                                                                    isMoreOperateshow = false;
                                                                }
                                                            });
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }).start();*/