package com.qiuyi.cn.orangemodule.upanupdate;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiuyi.cn.myloadingdialog.LoadingDialog;
import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.activity.AllFileShowActivity;
import com.qiuyi.cn.orangemodule.activity.SearchActivity;
import com.qiuyi.cn.orangemodule.adapter.SDFileAdapter;
import com.qiuyi.cn.orangemodule.interfaceToutil.SDFileDeleteListener;
import com.qiuyi.cn.orangemodule.interfaceToutil.UdiskDeleteListener;
import com.qiuyi.cn.orangemodule.myview.CommomDialog;
import com.qiuyi.cn.orangemodule.myview.MorePopWindow;
import com.qiuyi.cn.orangemodule.myview.MySelectDialog;
import com.qiuyi.cn.orangemodule.upansaf.ui.FileActivity;
import com.qiuyi.cn.orangemodule.util.DiskWriteToSD;
import com.qiuyi.cn.orangemodule.util.FileManager.MyFileManager;
import com.qiuyi.cn.orangemodule.util.FileUtilOpen;
import com.qiuyi.cn.orangemodule.util.WriteToUdisk;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/5/2.
 */
public class AllUdiskFileShowActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener,UdiskDeleteListener{

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
    private TextView tv_delete,tv_copy,tv_move,tv_rename,tv_paste,tv_cancle;

    //目录
    private TextView tv_fload;

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
    private List<DocumentFile> listDocDirectory;//U盘下所有文件夹

    private SDFileDeleteListener sdFileDeleteListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alludiskfileshow);

        udiskUtil = WriteToUdisk.getInstance(this.getApplicationContext(),this);
        diskWriteToSD = new DiskWriteToSD(AllUdiskFileShowActivity.this);

        sdFileDeleteListener = new AllFileShowActivity();

        listDocFile = new ArrayList<>();
        //listDocDirectory = new ArrayList<>();
        //获取所有文件
        getAllDocFile();

        initView();

        initData();

    }

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

            tv_fload.setText(currentPath);
            File currentFolder = new File(currentPath);
            readFileList(currentFolder);

            //从本地控制界面过来
            int from = intent.getIntExtra("from",-1);
            boolean flag = intent.getBooleanExtra("select",false);
            if(from==1){
                //从本地过来
                showPaste(from,flag, AllFileShowActivity.copyFileMap);
            }


        }
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
                    sdfileAdapter.notifyDataSetChanged();
                }else{
                    if(file.isDirectory()){
                        currentPath += "/"+file.getName();
                        tv_fload.setText(currentPath);
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

                                final int finalI = i;

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DocumentFile deleteFile = findDocFile(fileList.get(finalI));
                                        doDelete(deleteFile);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                deleteDialog.dismiss();
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

                //重命名
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
                });

                //复制
                tv_copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //选择，显示一下有哪几个选择了
                        selectHowToPaste(false);
                    }
                });

                //移动
                tv_move.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selectHowToPaste(true);
                    }
                });


                sdfileAdapter.setShowCheckBox(true);
                boolean flag[] = sdfileAdapter.getFlag();
                flag[position] = true;

                sdfileAdapter.setFlag(flag);

                tv_selectNum.setText("已选(1)");
                sdfileAdapter.notifyDataSetChanged();
            }

            @Override
            public void changeCount(int nowcount) {
                tv_selectNum.setText("已选("+nowcount+")");
            }
        });

        tv_fload.setOnClickListener(new View.OnClickListener() {
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
        });
    }


    /**
     * 将File文件转换成DocumentFile
     * @param file
     * @return
     */
    private DocumentFile findDocFile(File file){
        /*listDocFile = udiskUtil.getAllFile(MainActivity.rootUDFile);*/
        return udiskUtil.getDocFile(listDocFile,file.getName());
    }


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
                        if(currentFolder!=null){
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    //写入选中的currentFolder中去
                                    //DocumentFile newFile = findDocFile(file);
                                    writeToUDick(file,currentFolder);
                                    //udiskUtil.moveFile(AllUdiskFileShowActivity.this,newFile,currentFolder);

                                    if(flag && whereFrom==2){
                                        //是从剪切过来的
                                        DocumentFile newFile = findDocFile(file);
                                        doDelete(newFile);
                                    }else if(flag && whereFrom==1){
                                        sdFileDeleteListener.doDeleteSDFile(file);
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
                        }else{
                            dialog.dismiss();
                            Toast.makeText(AllUdiskFileShowActivity.this,"您的操作太快了，请慢慢来",Toast.LENGTH_SHORT).show();
                        }
                        getAllDocFile();
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
    private void createNewFolder(final String name, final int flag, final File myfile) {
        new CommomDialog(this, R.style.dialog, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, final String folderName, boolean confirm) {
                if(confirm){
                    //点击了确定按钮
                    if(flag==1){
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
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mydialog.dismiss();
                                            Log.e("create", "新建文件夹"+newFile.getName());
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
                        }

                    }
                    onRefresh();
/*                    rl_select_head.setVisibility(View.GONE);
                    rl_normal_head.setVisibility(View.VISIBLE);
                    ll_pager_native_bom.setVisibility(View.GONE);

                    sdfileAdapter.setShowCheckBox(false);
                    sdfileAdapter.ReFresh();*/

                    getAllDocFile();
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
        tv_fload = findViewById(R.id.tv_floader);

        //展示列表
        rl_fileshow = findViewById(R.id.allFile_rl);
        allFile_sl = findViewById(R.id.allFile_sl);
        //底部导航栏
        ll_pager_native_bom = findViewById(R.id.ll_pager_native_bom);
        tv_delete = findViewById(R.id.tv_delete);
        tv_copy = findViewById(R.id.tv_copy);
        tv_move = findViewById(R.id.tv_move);
        tv_rename = findViewById(R.id.tv_rename);
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
        if (!currentPath.equals(rootPath)) {
            currentPath = currentPath.substring(0, currentPath.lastIndexOf("/"));
            tv_fload.setText(currentPath);
            readFileList(new File(currentPath));
        }else{
            super.onBackPressed();
        }
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

    //删除文件
    @Override
    public void doUdiskDelete(File file) {
        doDelete(findDocFile(file));
    }

    @Override
    protected void onResume() {
        onRefresh();
        super.onResume();
    }
}
