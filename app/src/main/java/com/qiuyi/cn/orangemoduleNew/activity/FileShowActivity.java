package com.qiuyi.cn.orangemoduleNew.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiuyi.cn.myloadingdialog.LoadingDialog;
import com.qiuyi.cn.orangemoduleNew.MainActivity;
import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.Secret.AESHelperUpdate2;
import com.qiuyi.cn.orangemoduleNew.myview.CommomDialog;
import com.qiuyi.cn.orangemoduleNew.myview.FileDetailDialog;
import com.qiuyi.cn.orangemoduleNew.myview.MoreOperatePopWindow;
import com.qiuyi.cn.orangemoduleNew.myview.MySelectDialog;
import com.qiuyi.cn.orangemoduleNew.upanupdate.AllUdiskFileShowActivity;
import com.qiuyi.cn.orangemoduleNew.util.DiskWriteToSD;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.adapter.UFileAdapter;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.service.FindAllFile_II_Service;
import com.qiuyi.cn.orangemoduleNew.util.FileUtilOpen;
import com.qiuyi.cn.orangemoduleNew.util.ShareFile;
import com.qiuyi.cn.orangemoduleNew.util.WriteToUdisk;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/3/16.
 * 本地文件展示模块
 */
public class FileShowActivity extends Activity implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout myFileRefresh;
    private RecyclerView myFileShow;
    private GridLayoutManager myGridManager;

    //正常的标题栏
    private RelativeLayout rl_normal_head;
    private ImageView iv_back;//返回
    private TextView tv_title,tv_paixu;//标题,排序

    //选择的标题栏
    private RelativeLayout rl_select_head;
    private ImageView iv_cancle;//取消选择
    private TextView tv_selectNum;//选择的文件数
    private TextView bt_selectAll;//全选

    //底部导航模块
    private LinearLayout ll_pager_native_bom;
    private TextView tv_delete,tv_copy,tv_move,tv_more;;//复制，移动，删除，重命名

    //是否全选
    private boolean isSelectAll = true;

    private List<File> listFiles;


    private UFileAdapter ufileAdapter;

    private LoadingDialog deleteDialog;//删除

    private LoadingDialog dialog;
    private WriteToUdisk udiskUtil;
    private DiskWriteToSD diskWriteToSD;

    //分享
    private ArrayList<Uri> listUris;
    //添加收藏和私密
    private List<File> listSS;

    //是否显示了更多操作
    private boolean isMoreOperateshow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileshow);

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

        udiskUtil = WriteToUdisk.getInstance(this.getApplicationContext(),this);
        diskWriteToSD= new DiskWriteToSD(this.getApplicationContext());

        initView();

        initData();

        initBroadCast();
    }

    //广播监听初始化
    private void initBroadCast() {
        //数据刷新
        IntentFilter filter = new IntentFilter(SearchActivity.SearchActivity_getSDFile);
        registerReceiver(FileMsgreceiver,filter);
    }

    private BroadcastReceiver FileMsgreceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getBooleanExtra("findAllSDFile",false)){
                //本地信息查找完毕,刷新界面
                initData();
                myFileRefresh.setRefreshing(false);
            }
        }
    };


    /**
     * 初始化数据
     */
    private void initData() {
        //设置文件

        myGridManager = new GridLayoutManager(this,4);
        myFileShow.setLayoutManager(myGridManager);

        //获取数据，刷新界面
        whatTypeToget();
    }

    /*
    * 判断得到的数据类型
    * */
    private void whatTypeToget() {
        Intent intent = getIntent();
        switch (intent.getIntExtra("type",0)){
            case 0:
                tv_title.setText("图片");
                listFiles = MainActivity.listImages;
/*                newlistFiles.clear();
                for(ImageBean imageBean:listImages){
                    File file = new File(imageBean.getPath());
                    newlistFiles.add(file);
                }*/
                break;
            case 1:
                tv_title.setText("视频");
                listFiles = MainActivity.listVideos;
                break;
            case 2:
                tv_title.setText("文档");
                listFiles = MainActivity.listFiles;
                break;
            case 3:
                tv_title.setText("音乐");
                listFiles = MainActivity.listMusics;
                break;
            case 4:
                tv_title.setText("压缩包");
                listFiles = MainActivity.listFileZars;
                break;
        }


        ufileAdapter = new UFileAdapter(this,listFiles,myGridManager);
        myFileShow.setAdapter(ufileAdapter);

        //点击事件
        ufileAdapter.setOnFileItemClick(new UFileAdapter.FileItemClick() {
            @Override
            public void openFile(View view, int position,List<File> fileLists) {
                File file = listFiles.get(position);
                boolean isShowBox = ufileAdapter.isShowCheckBox();

                if(isShowBox){
                    //正在显示checkbox
                    boolean flag[] = ufileAdapter.getFlag();

                    flag[position] = !flag[position];

                    ufileAdapter.setFlag(flag);

                    int count = 0;
                    for(boolean flagment:flag){
                        if(flagment){
                            count++;
                        }
                    }
                    tv_selectNum.setText("已选("+count+")");

                    //判断是否全选了
                    pdSelect(count);

                    ufileAdapter.notifyDataSetChanged();
                }else{
                    FileUtilOpen.openFileByPath(getApplicationContext(),listFiles.get(position).getPath());
                }
            }

            @Override
            public void onLongClick(View view, final int position, final List<File> fileLists) {
                final File file = listFiles.get(position);

                //选择状态栏显示
                rl_normal_head.setVisibility(View.GONE);
                rl_select_head.setVisibility(View.VISIBLE);
                //底部导航栏按钮显示
                ll_pager_native_bom.setVisibility(View.VISIBLE);

                bt_selectAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isSelectAll){
                            ufileAdapter.selectAll();
                            isSelectAll = false;
                            bt_selectAll.setText("取消全选");
                            tv_selectNum.setText("已选("+listFiles.size()+")");
                        }else{
                            ufileAdapter.noSelect();
                            isSelectAll = true;
                            bt_selectAll.setText("全选");
                            tv_selectNum.setText("已选(0)");
                        }
                        ufileAdapter.notifyDataSetChanged();
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

                        ufileAdapter.setShowCheckBox(false);
                        ufileAdapter.ReFresh();
                    }
                });

                tv_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //选择，显示一下有哪几个选择了
                        boolean[] flag = ufileAdapter.getFlag();
                        for(int i = flag.length-1;i>=0;i--){
                            if(flag[i]){
                                //删除
                                Log.e("select", "选中："+i);
                                deleteDialog.show();

                                final File defile = listFiles.get(i);

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

                                listFiles.remove(i);
                            }
                        }
                        ufileAdapter.ReFresh();

                    }
                });


                //更多操作模块
                tv_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final int[] myflag ={-1,-1,-1,-1,-1};
                        //选择
                        boolean[] flag = ufileAdapter.getFlag();
                        int count = 0;
                        int location = -1;
                        boolean isDirectory = false;

                        listUris.clear();
                        listSS.clear();

                        for(int i = flag.length-1;i>=0;i--){
                            if(flag[i]){
                                //分享
                                File file = listFiles.get(i);
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
                            File colFile = listFiles.get(location);
                            //collectionFiles = DBUtil.getCollectFile(colFile.getPath());
                            //在MyFile->CollectionDirectory文件夹下查找文件是否存在
                            collectFile = diskWriteToSD.findCollectionFile(colFile,AllFileShowActivity.CollectionDirectory_Name);
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

                                new MoreOperatePopWindow(FileShowActivity.this, new MoreOperatePopWindow.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(final MoreOperatePopWindow popupWindow, int position) {
                                        //分享
                                        if(position==1 && !finalIsDirectory){
                                            ShareFile.shareMultipleFiles(FileShowActivity.this,listUris);
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
                                                final File file = listFiles.get(finalLocation);

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
                                        if(position==3 && !finalIsDirectory){
                                            //为空，操作就是收藏
                                            final File file = listFiles.get(finalLocation);

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

                                                        listFiles.remove(finalLocation);
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
                                                createNewFolder("重命名文件",2,listFiles.get(finalLocation));
                                                popupWindow.dismiss();
                                                UIShowHide();
                                                isMoreOperateshow = false;
                                            }
                                        }
                                        //详情
                                        if(position==5){
                                            //重命名和详情都需要点击的是一个
                                            if(finalCount ==1){
                                                new FileDetailDialog(FileShowActivity.this,R.style.dialog,listFiles.get(finalLocation)).show();
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
                        boolean[] flag = ufileAdapter.getFlag();
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
                        boolean[] flag = ufileAdapter.getFlag();
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

                ufileAdapter.setShowCheckBox(true);
                boolean[] flag = ufileAdapter.getFlag();

                flag[position] = true;

                ufileAdapter.setFlag(flag);

                tv_selectNum.setText("已选(1)");
                //判断是否全选了
                pdSelect(1);

                ufileAdapter.notifyDataSetChanged();
            }

            @Override
            public void changeCount(int count) {
                tv_selectNum.setText("已选("+count+")");
                //判断是否全选
                pdSelect(count);
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
        boolean[] flag = ufileAdapter.getFlag();
        for(int i = flag.length-1;i>=0;i--){
            if(flag[i]){
                copyFileMap.put(i,listFiles.get(i));
            }
        }

        UIShowHide();
/*        rl_select_head.setVisibility(View.GONE);
        rl_normal_head.setVisibility(View.VISIBLE);
        ll_pager_native_bom.setVisibility(View.GONE);
        ufileAdapter.setShowCheckBox(false);
        ufileAdapter.ReFresh();*/

        new MySelectDialog(this, R.style.dialog, new MySelectDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, Integer flag) {
                if(flag==1){
                    //本地
                    Intent intent = new Intent(FileShowActivity.this, AllFileShowActivity.class);
                    //带上标志3表示是从fileShowActivity过去的
                    intent.putExtra("from",3);
                    intent.putExtra("select",b);
                    startActivity(intent);
                }else if(flag ==2){
                    //U盘
                    if(MainActivity.isHaveUpan){
                        Intent intent = new Intent(FileShowActivity.this, AllUdiskFileShowActivity.class);
                        //带上标志3表示是从fileShowActivity过去的
                        intent.putExtra("from",3);
                        intent.putExtra("select",b);
                        startActivity(intent);
                    }else{
                        Toast.makeText(FileShowActivity.this,"请插入U盘",Toast.LENGTH_SHORT).show();
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
                        onRefresh();
                    }
                    dialog.dismiss();
                }
            }
        }).setTitle(name).show();
    }


    //判断是否全选
    private void pdSelect(int count) {
        if(count==listFiles.size()){
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



    @Override
    public void onRefresh() {
        //选择状态栏显示

        myFileRefresh.setRefreshing(true);

        //启动查找本地文件的服务
        //startService(new Intent(FileShowActivity.this,FindFileMsg_Service.class));
        startService(new Intent(FileShowActivity.this, FindAllFile_II_Service.class));

        rl_select_head.setVisibility(View.GONE);
        rl_normal_head.setVisibility(View.VISIBLE);
        ll_pager_native_bom.setVisibility(View.GONE);

        ufileAdapter.setShowCheckBox(false);
        ufileAdapter.ReFresh();
    }


    //操作完之后的UI显示，相当于取消
    private void UIShowHide(){
        //选择状态栏显示
        rl_select_head.setVisibility(View.GONE);
        rl_normal_head.setVisibility(View.VISIBLE);
        ll_pager_native_bom.setVisibility(View.GONE);

        ufileAdapter.setShowCheckBox(false);
        ufileAdapter.ReFresh();
    }







    /**
     * 初始化界面
     */
    private void initView() {
        myFileShow = findViewById(R.id.fileshow_rl);
        myFileRefresh = findViewById(R.id.fileshow_sl);

        myFileRefresh.setColorSchemeColors(Color.RED);
        myFileRefresh.setOnRefreshListener(this);

        //正常标题栏
        rl_normal_head = findViewById(R.id.rl_normal_head);
        iv_back = findViewById(R.id.allfileshow_iv_back);//返回
        tv_paixu = findViewById(R.id.tv_paixu);
        tv_title = findViewById(R.id.allfileshow_tv_title); //标题

        //选择checkbox出来之后显示的标题栏
        rl_select_head = findViewById(R.id.rl_select_head);
        iv_cancle = findViewById(R.id.rl_allfileshow_title_cancle); //取消选择
        tv_selectNum = findViewById(R.id.rl_allfileshow_title_select);//选择的文件数
        bt_selectAll = findViewById(R.id.bt_selectAll);//全选按钮

        //底部导航栏
        ll_pager_native_bom = findViewById(R.id.ll_pager_native_bom);
        tv_delete = findViewById(R.id.tv_delete);
        tv_copy = findViewById(R.id.tv_copy);
        tv_move = findViewById(R.id.tv_move);
        tv_more = findViewById(R.id.tv_more);

        //返回键
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //排序
        tv_paixu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }


    @Override
    protected void onResume() {
        onRefresh();
        super.onResume();
    }
}
