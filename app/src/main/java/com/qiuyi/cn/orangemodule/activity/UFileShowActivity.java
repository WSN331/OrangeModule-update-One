package com.qiuyi.cn.orangemodule.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.provider.DocumentFile;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qiuyi.cn.myloadingdialog.LoadingDialog;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.adapter.RecentlyAdapter;
import com.qiuyi.cn.orangemodule.util.DiskWriteToSD;
import com.qiuyi.cn.orangemodule.util.FileManager.FileUtils;
import com.qiuyi.cn.orangemodule.util.FileManager.adapter.FileAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.adapter.ImageAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.adapter.MusicAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.adapter.UFileAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.adapter.VideoAdapter;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.FileBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.ImageBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.MusicBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.VideoBean;
import com.qiuyi.cn.orangemodule.util.FileUtilOpen;
import com.qiuyi.cn.orangemodule.util.WriteToUdisk;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/16.
 * U盘文件展示模块
 */
public class UFileShowActivity extends Activity{

    private RecyclerView myFileShow;
    private GridLayoutManager myGridManager;

    //全选按钮
    private TextView bt_selectAll;
    //底部导航模块
    private LinearLayout ll_pager_native_bom;
    //删除，拷贝到U盘，取消
    private TextView tv_delete,tv_copy,tv_cancel;

    //是否全选
    private boolean isSelectAll = true;

    private List<File> listFiles;//文件

    private UFileAdapter ufileAdapter;

    private LoadingDialog dialog;
    private WriteToUdisk udiskUtil;
    private DiskWriteToSD diskWriteToSD;

    private List<DocumentFile> listsDocFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ufileshow);

        dialog = new LoadingDialog.Builder(this)
                .setMessage("复制中")
                .setCancelable(false)
                .setCancelOutside(false)
                .create();
        udiskUtil = WriteToUdisk.getInstance(this.getApplicationContext(),this);
        diskWriteToSD= new DiskWriteToSD(this.getApplicationContext());

        listsDocFiles = udiskUtil.getAllFile(udiskUtil.getCurrentFolder());

        initView();

        initData();
    }

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

        listFiles = (List<File>) intent.getSerializableExtra("listUFile");

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
                    ufileAdapter.notifyDataSetChanged();
                }else{
                    FileUtilOpen.openFileByPath(getApplicationContext(),listFiles.get(position).getPath());
                }
            }

            @Override
            public void onLongClick(View view, final int position, final List<File> fileLists) {
                final File file = listFiles.get(position);

                //头部状态栏显示
                bt_selectAll.setVisibility(View.VISIBLE);
                //底部状态栏显示
                ll_pager_native_bom.setVisibility(View.VISIBLE);

                bt_selectAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isSelectAll){
                            ufileAdapter.selectAll();
                            isSelectAll = false;
                            bt_selectAll.setText("取消全选");
                        }else{
                            ufileAdapter.noSelect();
                            isSelectAll = true;
                            bt_selectAll.setText("全选");
                        }
                        ufileAdapter.notifyDataSetChanged();
                    }
                });

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //头部状态栏显示
                        bt_selectAll.setVisibility(View.GONE);
                        //底部状态栏显示
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
                                //?需要刷新前面一个界面。。。。。。
                                //删除
                                DocumentFile file = udiskUtil.getDocFile(listsDocFiles,fileLists.get(i).getName());
                                file.delete();
                                listsDocFiles.remove(file);
                                fileLists.remove(i);
                                //ufileAdapter.notifyDataSetChanged();
                            }
                        }
                        ufileAdapter.ReFresh();
                    }
                });

                tv_copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //选择，显示一下有哪几个选择了
                        dialog.show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                boolean[] flag = ufileAdapter.getFlag();
                                for(int i = flag.length-1;i>=0;i--){
                                    if(flag[i]){
                                        Log.e("select", "选中："+i);
                                        File file = listFiles.get(i);
                                        diskWriteToSD.writeFileToSD(file,"uFiles");
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

                                        ufileAdapter.setShowCheckBox(false);
                                        ufileAdapter.ReFresh();
                                    }
                                });
                            }
                        }).start();
                    }
                });

                ufileAdapter.setShowCheckBox(true);
                boolean[] flag = ufileAdapter.getFlag();

                flag[position] = true;

                ufileAdapter.setFlag(flag);

                ufileAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 初始化界面
     */
    private void initView() {
        myFileShow = findViewById(R.id.fileshow_rl);

        bt_selectAll = findViewById(R.id.bt_selectAll);
        ll_pager_native_bom = findViewById(R.id.ll_pager_native_bom);
        tv_delete = findViewById(R.id.tv_delete);
        tv_copy = findViewById(R.id.tv_copy);
        tv_cancel = findViewById(R.id.tv_cancel);
    }

}
