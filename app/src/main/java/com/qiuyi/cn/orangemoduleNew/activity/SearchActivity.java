package com.qiuyi.cn.orangemoduleNew.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiuyi.cn.myloadingdialog.LoadingDialog;
import com.qiuyi.cn.orangemoduleNew.MainActivity;
import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.Secret.AESHelperUpdate2;
import com.qiuyi.cn.orangemoduleNew.adapter.SearchAdapter;
import com.qiuyi.cn.orangemoduleNew.myview.CommomDialog;
import com.qiuyi.cn.orangemoduleNew.myview.FileDetailDialog;
import com.qiuyi.cn.orangemoduleNew.myview.MoreOperatePopWindow;
import com.qiuyi.cn.orangemoduleNew.myview.MySelectDialog;
import com.qiuyi.cn.orangemoduleNew.upansaf.ui.FileActivity;
import com.qiuyi.cn.orangemoduleNew.upanupdate.AllUdiskFileShowActivity;
import com.qiuyi.cn.orangemoduleNew.util.Constant;
import com.qiuyi.cn.orangemoduleNew.util.DiskWriteToSD;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.ConstantValue;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.service.FindAllFile_II_Service;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.service.FindUpanMsg_Service;
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

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.qiuyi.cn.orangemoduleNew.MainActivity.listFiles;

/**
 * Created by Administrator on 2018/4/2.
 * 搜索页面
 */
public class SearchActivity extends Activity implements View.OnClickListener,TextView.OnEditorActionListener{

    //全选和图案
    @BindView(R.id.bt_selectAll)
    TextView bt_selectAll;
    @BindView(R.id.img_selectAll)
    ImageView img_selectAll;

    //底部功能栏
    @BindView(R.id.ll_pager_native_bom)
    LinearLayout ll_pager_native_bom;
    @BindView(R.id.tv_copy)
    TextView tv_copy;
    @BindView(R.id.tv_move)
    TextView tv_move;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    @BindView(R.id.tv_more)
    TextView tv_more;
    @BindView(R.id.tv_cancel)
    TextView tv_cancel;

    //显示的两个部分
    @BindView(R.id.ll_bottom)
    LinearLayout ll_bottom;
    @BindView(R.id.rl_show)
    RecyclerView rl_show;

    @BindView(R.id.et_search)
    EditText mySearch;
    @BindView(R.id.tv_camera)
    TextView tv_camera;
    @BindView(R.id.tv_qq)
    TextView tv_qq;
    @BindView(R.id.tv_wechat)
    TextView tv_wechat;
    @BindView(R.id.tv_screen)
    TextView tv_screen;

    //广播
    public static final String SearchActivity_getSDFile = "com.qiuyi.cn.SearchActivity";


    //所有文件
    private List<File> listFile;

    private SearchAdapter mySearchAdapter;
    private GridLayoutManager myGridManager;

    private int whereFrom = 0;

    //复制中
    private LoadingDialog dialog;
    //搜索中
    private LoadingDialog searchdialog;
    //删除中
    private LoadingDialog deleteDialog;


    private boolean isSelectAll = true;

    private WriteToUdisk udiskUtil;
    private DiskWriteToSD diskWriteToSD;

    //查找所有的UP存在文件
    private List<DocumentFile> listDocFiles;


    //分享
    private ArrayList<Uri> listUris;
    //添加收藏和私密
    private List<File> listSS;

    //是否显示了更多操作
    private boolean isMoreOperateshow = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        ButterKnife.bind(this);


        udiskUtil = WriteToUdisk.getInstance(this.getApplicationContext(),this);
        diskWriteToSD= new DiskWriteToSD(this.getApplicationContext());


        listUris = new ArrayList<>();
        listSS = new ArrayList<>();

        searchdialog = new LoadingDialog.Builder(this)
                .setMessage("搜索中...")
                .setCancelable(false)
                .setCancelOutside(false)
                .create();

        dialog = new LoadingDialog.Builder(this)
                .setCancelOutside(false)
                .setCancelable(false)
                .setMessage("复制中...")
                .create();

        deleteDialog = new LoadingDialog.Builder(this)
                .setMessage("删除中")
                .setCancelable(false)
                .setCancelOutside(false)
                .create();

        //这里是专门的输入查找
        Intent intent = getIntent();
        //本地所有文件
        boolean main = intent.getBooleanExtra("main",false);
        boolean umain = intent.getBooleanExtra("udisk",false);
        if(main){
            //查找本地文件
            whereFrom = 1;
            //初始化QQ,WeChat等特殊文件的查找
            initFileData();
        }
        if(umain){
            //查找U盘内文件
            whereFrom = 2;

            listDocFiles = new ArrayList<>();
            getAllDocFile();
            //初始化U盘文件
            initUFileData();
        }

        initListener();

        initBroadCast();
    }

    //获得所有Doc文件
    private void getAllDocFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                listDocFiles.clear();
                listDocFiles.add(MainActivity.rootUDFile);
                listDocFiles.addAll(udiskUtil.getAllFile(MainActivity.rootUDFile));
                Log.e("docFile", "查找完毕");
            }
        }).start();
    }

    //广播接收
    private void initBroadCast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SearchActivity_getSDFile);
        filter.addAction(Constant.FINDUPAN_MSG);
        registerReceiver(myAllfileDataReceiver,filter);
    }

    private BroadcastReceiver myAllfileDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra("findAllSDFile",false)){
                //所有文件查找完毕
                listFile = MainActivity.MY_ALLFILLES_II;
            }
            if(intent.getBooleanExtra("findOkUpan",false)){
                //U盘文件查找完毕
                listFile = MainActivity.listUPANAllFiles;
            }
        }
    };


    //初始化
    private void initFileData() {
        startService(new Intent(SearchActivity.this,FindAllFile_II_Service.class));
    }

    private void initUFileData(){
        startService(new Intent(SearchActivity.this, FindUpanMsg_Service.class));
    }



    //初始化监听事件
    private void initListener() {
        img_selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tv_camera.setOnClickListener(this);
        tv_qq.setOnClickListener(this);
        tv_wechat.setOnClickListener(this);
        tv_screen.setOnClickListener(this);

        mySearch.setOnEditorActionListener(this);
    }


    @Override
    public void onClick(final View view) {

        final Timer timer = new Timer();
        searchdialog.show();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e("timer", "timer run");
                if(listFile!=null){
                    timer.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("timer", "timer out");
                            switch (view.getId()){
                                case R.id.tv_camera:
                                    showRecycler(listFile, ConstantValue.CAMERA_KEY);
                                    break;
                                case R.id.tv_qq:
                                    showRecycler(listFile, ConstantValue.QQ_IMAGE_KEY);
                                    break;
                                case R.id.tv_wechat:
                                    showRecycler(listFile, ConstantValue.WECHART_IMAGE_KEY);
                                    break;
                                case R.id.tv_screen:
                                    showRecycler(listFile, ConstantValue.SCREENSHOT_KEY);
                                    break;
                                default:
                                    break;
                            }
                            searchdialog.dismiss();
                        }
                    });
                }
            }
        },1000);
    }


    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            //写点击搜索键后的操作
            Log.e("search","搜索");

            final Timer timer = new Timer();
            searchdialog.show();
/*            new Thread(new Runnable() {
                @Override
                public void run() {*/

                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if(listFile!=null){
                                final List<File> ufiles = getNativeFile(listFile,mySearch.getText().toString());
                                timer.cancel();

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(ufiles.size()>0){
                                            showInAdapter(ufiles,mySearch.getText().toString());

                                        }else{
                                            Toast.makeText(SearchActivity.this,"对不起，没有您要查找的内容",Toast.LENGTH_SHORT).show();
                                            ll_bottom.setVisibility(View.VISIBLE);
                                            rl_show.setVisibility(View.GONE);
                                        }
                                        searchdialog.dismiss();
                                    }
                                });

                            }
                        }
                    },1000);
/*                }
            }).start();*/

            return true;
        }
        return false;
    }


    //搜索框,本地所有文件（包括文件夹）
    private List<File> getNativeFile(List<File> listFile, String key) {
        List<File> keyListFile = new ArrayList<>();
        if(listFile.size()>0 && listFile!=null && key!=null && !key.equals("")){
            for(File nativeFile:listFile){
                if(nativeFile.getName().toLowerCase().contains(key.toLowerCase())){
                    keyListFile.add(nativeFile);
                }
            }
        }
        return keyListFile;
    }


    //从所有数据中，找到匹配规则的文件
    private void showRecycler(List<File> listFileBean, String key) {

        List<File> keyListFile = new ArrayList<>();
        if(listFileBean!=null){
            //得到相应规则的文件
            for(File filebean:listFileBean){
                if(filebean.getPath().matches(key)){
                    keyListFile.add(filebean);
                }
            }

            if(keyListFile.size()>0){
                showInAdapter(keyListFile,key);
            }else{
                Toast.makeText(SearchActivity.this,"对不起，没有您要查找的内容",Toast.LENGTH_SHORT).show();
                ll_bottom.setVisibility(View.VISIBLE);
                rl_show.setVisibility(View.GONE);
            }

        }
    }

    //调用adapter显示
    private void showInAdapter(final List<File> keyListFile,String name) {

        //按照文件夹，文件，字母排序
        Collections.sort(keyListFile, new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                if (f1.isFile()) {
                    if (f2.isFile()) {
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    } else {
                        return 1;
                    }
                } else {
                    if (f2.isFile()) {
                        return -1;
                    } else {
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    }
                }
            }
        });


        myGridManager = new GridLayoutManager(this,4);
        rl_show.setLayoutManager(myGridManager);
        mySearchAdapter = new SearchAdapter(this,keyListFile,myGridManager,name);
        rl_show.setAdapter(mySearchAdapter);

        ll_bottom.setVisibility(View.GONE);
        rl_show.setVisibility(View.VISIBLE);

        mySearchAdapter.setOnFileItemClick(new SearchAdapter.FileItemClick() {
            @Override
            public void openFile(View view, int position) {
                File file = keyListFile.get(position);

                boolean isShowBox = mySearchAdapter.isShowCheckBox();
                if(isShowBox){
                    //正在显示checkbox
                    boolean flag[] = mySearchAdapter.getFlag();

                    flag[position] = !flag[position];

                    mySearchAdapter.setFlag(flag);
                    mySearchAdapter.notifyDataSetChanged();
                }else{
                    if(file.isDirectory()){
                        switch (whereFrom){
                            case 1:
                                Intent intent = new Intent(SearchActivity.this,AllFileShowActivity.class);
                                intent.putExtra("filepath",file.getAbsolutePath());
                                startActivity(intent);
                                break;
                            case 2:
                                Intent intentUFile = new Intent(SearchActivity.this,FileActivity.class);
                                intentUFile.putExtra("ufilename",file.getName());
                                startActivity(intentUFile);
                                break;
                            default:
                                break;
                        }
                    }else{
                        FileUtilOpen.openFileByPath(getApplicationContext(),keyListFile.get(position).getPath());
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

                //图案消失，全选按钮显示
                img_selectAll.setVisibility(View.GONE);
                bt_selectAll.setVisibility(View.VISIBLE);
                //底部导航栏显示
                ll_pager_native_bom.setVisibility(View.VISIBLE);

                bt_selectAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isSelectAll){
                            mySearchAdapter.selectAll();
                            bt_selectAll.setText("取消全选");
                            isSelectAll = false;
                        }else{
                            mySearchAdapter.noSelect();
                            bt_selectAll.setText("全选");
                            isSelectAll = true;
                        }
                        mySearchAdapter.notifyDataSetChanged();
                    }
                });

                //取消按钮
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        img_selectAll.setVisibility(View.VISIBLE);
                        bt_selectAll.setVisibility(View.GONE);
                        ll_pager_native_bom.setVisibility(View.GONE);

                        mySearchAdapter.setShowCheckBox(false);
                        mySearchAdapter.ReFresh();
                    }
                });

                tv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //选择，显示一下有哪几个选择了
                        boolean[] flag = mySearchAdapter.getFlag();
                        switch (whereFrom){
                            case 1:
                                for(int i = flag.length-1;i>=0;i--){
                                    if(flag[i]){
                                        Log.e("select", "选中："+i);
                                        //本地的直接删除
                                        deleteDialog.show();

                                        final File defile = keyListFile.get(i);

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                doDelete(defile);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        deleteDialog.dismiss();
                                                    }
                                                });
                                            }
                                        }).start();
                                        keyListFile.remove(i);
                                    }
                                }
                                //来自本地
                                break;
                            case 2:
                                //来自U盘
                                for(int i = flag.length-1;i>=0;i--){
                                    if(flag[i]){
                                        Log.e("select", "选中："+i);
                                        //U盘的
                                        //从当前目录下去找
                                        final DocumentFile deleteFile = findDocFile(keyListFile.get(i));

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                doDeleteUP(deleteFile);

                                                listDocFiles.remove(deleteFile);

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        deleteDialog.dismiss();
                                                    }
                                                });
                                            }
                                        }).start();

                                        keyListFile.remove(i);
                                    }
                                }
                                break;
                        }

                        mySearchAdapter.ReFresh();
                    }
                });

                //更多模块
                tv_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final int[] myflag ={-1,-1,-1,-1,-1};
                        //选择
                        boolean[] flag = mySearchAdapter.getFlag();
                        int count = 0;
                        int location = -1;
                        boolean isDirectory = false;

                        listUris.clear();
                        listSS.clear();

                        for(int i = flag.length-1;i>=0;i--){
                            if(flag[i]){
                                //分享
                                File file = keyListFile.get(i);
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

                        File collectFile = null;
                        if(count==1){
                            //当只有一个文件的时候，判断一下这个文件是否在收藏中
                            File colFile = keyListFile.get(location);
                            //在MyFile->CollectionDirectory文件夹下查找文件是否存在
                            collectFile = diskWriteToSD.findCollectionFile(colFile, AllUdiskFileShowActivity.CollectionDirectory_Name);
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
                        final File finalCollection = collectFile;//文件是否收藏

                        if(count>0){
                            isMoreOperateshow = !isMoreOperateshow;
                            if(isMoreOperateshow){

                                new MoreOperatePopWindow(SearchActivity.this, new MoreOperatePopWindow.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(final MoreOperatePopWindow popupWindow, int position) {
                                        //分享
                                        if(position==1 && !finalIsDirectory){
                                            ShareFile.shareMultipleFiles(SearchActivity.this,listUris);
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
                                                final File file = keyListFile.get(finalLocation);

                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        diskWriteToSD.writeSelectFileToSD(file, AllFileShowActivity.CollectionDirectory_Name);
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
                                        if(position==3 && !finalIsDirectory && whereFrom==1){
                                            //来自本地

                                            //为空，操作就是收藏
                                            final File file = keyListFile.get(finalLocation);

                                            //得到的MyFile->.SecretDirectory文件夹
                                            File mySecretDirectory = diskWriteToSD.getSDCardFile(AllFileShowActivity.SecretDirectory_Name);
                                            String newFileName = AESHelperUpdate2.encrypt(AllFileShowActivity.PASSWORD_STRING,file.getName()+"*"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                            final File newFile = new File(mySecretDirectory.getAbsoluteFile()+"/"+newFileName);

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    boolean isWrite = diskWriteToSD.writeToSD3(file,newFile);

                                                    if(isWrite){
                                                        file.delete();

                                                        keyListFile.remove(finalLocation);

                                                    }
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            //UI更新
                                                            popupWindow.dismiss();
                                                            isMoreOperateshow = false;
                                                            UIShowHide();
                                                            //onRefresh();
                                                        }
                                                    });
                                                }
                                            }).start();

                                        }
                                        //添加私密
                                        if(position==3 && !finalIsDirectory && whereFrom==2){
                                            //为空，操作就是收藏
                                            final File file = keyListFile.get(finalLocation);

                                            //得到的MyFile->.SecretDirectory文件夹
                                            File mySecretDirectory = diskWriteToSD.getSDCardFile(AllUdiskFileShowActivity.SecretDirectory_Name);
                                            String newFileName = AESHelperUpdate2.encrypt(AllUdiskFileShowActivity.PASSWORD_STRING,file.getName()+"*"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
                                            final File newFile = new File(mySecretDirectory.getAbsoluteFile()+"/"+newFileName);

                                            final DocumentFile deleteFile = findDocFile(file);

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    boolean isWrite = diskWriteToSD.writeToSD3(file,newFile);

                                                    if(isWrite){
                                                        doDeleteUP(deleteFile);

                                                        keyListFile.remove(finalLocation);

                                                        listDocFiles.remove(deleteFile);
                                                    }
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            //UI更新
                                                            popupWindow.dismiss();
                                                            isMoreOperateshow = false;

                                                            UIShowHide();
                                                            //onRefresh();
                                                        }
                                                    });
                                                }
                                            }).start();

                                        }



                                        //重命名
                                        if(position==4 && whereFrom ==1){
                                            //重命名和详情都需要点击的是一个
                                            if(finalCount ==1){
                                                createNewFolder("重命名文件",2,keyListFile.get(finalLocation));
                                                popupWindow.dismiss();
                                                UIShowHide();
                                                isMoreOperateshow = false;
                                            }
                                        }
                                        //重命名
                                        if(position==4 && whereFrom==2){
                                            //重命名和详情都需要点击的是一个
                                            if(finalCount ==1){
                                                createNewFolderUP("重命名文件",2,keyListFile.get(finalLocation));
                                                popupWindow.dismiss();
                                                UIShowHide();
                                                isMoreOperateshow = false;
                                            }
                                        }


                                        //详情
                                        if(position==5){
                                            //重命名和详情都需要点击的是一个
                                            if(finalCount ==1){
                                                new FileDetailDialog(SearchActivity.this,R.style.dialog,keyListFile.get(finalLocation)).show();
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

                //复制
                tv_copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        int count = 0;
                        boolean[] flag = mySearchAdapter.getFlag();
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
                        boolean[] flag = mySearchAdapter.getFlag();
                        for(int i = flag.length-1;i>=0;i--){
                            if(flag[i]){
                                count++;
                            }
                        }

                        if(count>0){
                            //选择，显示一下有哪几个选择了
                            selectHowToPaste(true);
                        }
                    }
                });



                mySearchAdapter.setShowCheckBox(true);
                boolean flag[] = mySearchAdapter.getFlag();
                flag[position] = true;

                mySearchAdapter.setFlag(flag);
                mySearchAdapter.notifyDataSetChanged();

            }
        });
    }



    /**
     * 复制+移动
     * @param b false=复制，true=移动（复制后，删除原来）
     */
    public static Map<Integer,File> copyFileMap = new HashMap<>();
    private void selectHowToPaste(final boolean b) {
        copyFileMap.clear();
        boolean[] flag = mySearchAdapter.getFlag();
        for(int i = flag.length-1;i>=0;i--){
            if(flag[i]){
                copyFileMap.put(i,listFiles.get(i));
            }
        }

        UIShowHide();

        new MySelectDialog(this, R.style.dialog, new MySelectDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, Integer flag) {
                if(flag==1){
                    //本地
                    Intent intent = new Intent(SearchActivity.this, AllFileShowActivity.class);
                    //带上标志3表示是从fileShowActivity过去的
                    intent.putExtra("from",5);
                    intent.putExtra("select",b);
                    intent.putExtra("whereFrom",whereFrom);
                    startActivity(intent);
                }else if(flag ==2){
                    //U盘
                    if(MainActivity.isHaveUpan){
                        Intent intent = new Intent(SearchActivity.this, AllUdiskFileShowActivity.class);
                        //带上标志3表示是从fileShowActivity过去的
                        intent.putExtra("from",5);
                        intent.putExtra("select",b);
                        intent.putExtra("whereFrom",whereFrom);
                        startActivity(intent);
                    }else{
                        Toast.makeText(SearchActivity.this,"请插入U盘",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).show();
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
                    if(flag == 2){
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
                        UIShowHide();
                        //onRefresh();
                    }
                    dialog.dismiss();
                }
            }
        }).setTitle(name).show();
    }



    /**
     * 重命名+新建
     * @param name title
     * @param flag 1 新建，2 重命名
     */
    private void createNewFolderUP(final String name, final int flag, final File myfile) {
        new CommomDialog(this, R.style.dialog, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, final String folderName, boolean confirm) {
                if(confirm){
                    if(flag == 2){
                        //重命名文件夹
                        if(myfile!=null){
                            //找到要重命名的文件夹
                            DocumentFile nowFile = findDocFile(myfile);

                            listDocFiles.remove(nowFile);

                            //是个文件
                            String newName = folderName+myfile.getName().substring(myfile.getName().lastIndexOf("."),myfile.getName().length());
                            Log.e("fileName", nowFile.getName());
                            nowFile.renameTo(newName);

                            dialog.dismiss();

                            //addListAllDocFile(nowFile);
                            listDocFiles.add(nowFile);
                        }

                    }
                    UIShowHide();
                    //onRefresh();
                }
            }
        }).setTitle(name).show();
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
     * 删除文件
     * @param docFile
     */
    private void doDeleteUP(DocumentFile docFile) {
        if(docFile.exists()){
            if(docFile.isDirectory()){
                for(DocumentFile nowDocFile:docFile.listFiles()){
                    doDeleteUP(nowDocFile);
                }
            }
            docFile.delete();
        }
    }

    /**
     * 这里是一个全部文件的查找
     * 将File文件转换成DocumentFile
     * @param file
     * @return
     */
    private DocumentFile findDocFile(File file){
        DocumentFile myFile = udiskUtil.getDocFile(listDocFiles,file.getPath());

        if(myFile==null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SearchActivity.this,"请稍等，您的操作太快了~",Toast.LENGTH_SHORT);
                    return;
                }
            });
        }
        return myFile;
    }


    //操作完之后的UI显示，相当于取消
    private void UIShowHide(){
        img_selectAll.setVisibility(View.VISIBLE);
        bt_selectAll.setVisibility(View.GONE);
        ll_pager_native_bom.setVisibility(View.GONE);

        mySearchAdapter.setShowCheckBox(false);
        mySearchAdapter.ReFresh();
    }

}


/*    //将选中的文件写入U盘
    private void writeToUDick(File file,DocumentFile documentFile) {
        if(file.isDirectory()){
            DocumentFile dirFile = documentFile.createDirectory(file.getName());
            for(File newFile:file.listFiles()){
                writeToUDick(newFile,dirFile);
            }
        }else{
            udiskUtil.writeToSDFile(this,file,documentFile);
        }
    }*/

/*switch (whereFrom){
                    case 1:
                        //手机拷贝到U盘
                        tv_copytoUdisk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        boolean[] flag = mySearchAdapter.getFlag();
                                        for(int i = flag.length-1;i>=0;i--){
                                            if(flag[i]){
                                                Log.e("select", "选中："+i);
                                                writeToUDick(keyListFile.get(i),udiskUtil.getCurrentFolder());
                                            }
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();

                                                //头部状态栏显示
                                                bt_selectAll.setVisibility(View.GONE);
                                                img_selectAll.setVisibility(View.VISIBLE);
                                                //底部状态栏显示
                                                ll_function_bottom.setVisibility(View.GONE);

                                                mySearchAdapter.setShowCheckBox(false);
                                                mySearchAdapter.ReFresh();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        });
                        break;
                    case 2:
                        //U盘拷贝到手机
                        tv_copytoSD.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.show();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {

                                        boolean[] flag = mySearchAdapter.getFlag();
                                        for(int i = flag.length-1;i>=0;i--){
                                            if(flag[i]){
                                                Log.e("select", "选中："+i);
                                                File file = keyListFile.get(i);
                                                if(file.isDirectory()){
                                                    diskWriteToSD.writeDirectory(file);
                                                }else{
                                                    diskWriteToSD.writeFileToSD(file,"uFiles");
                                                }
                                            }
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                dialog.dismiss();

                                                //头部状态栏显示
                                                bt_selectAll.setVisibility(View.GONE);
                                                img_selectAll.setVisibility(View.VISIBLE);
                                                //底部状态栏显示
                                                ll_function_bottom.setVisibility(View.GONE);

                                                mySearchAdapter.setShowCheckBox(false);
                                                mySearchAdapter.ReFresh();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        });
                        break;
                    default:
                        break;
                }*/