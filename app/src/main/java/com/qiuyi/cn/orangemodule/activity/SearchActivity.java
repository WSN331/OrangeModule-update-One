package com.qiuyi.cn.orangemodule.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
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

import com.qiuyi.cn.myloadingdialog.LoadingDialog;
import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.adapter.SearchAdapter;
import com.qiuyi.cn.orangemodule.upansaf.ui.FileActivity;
import com.qiuyi.cn.orangemodule.util.DiskWriteToSD;
import com.qiuyi.cn.orangemodule.util.FileManager.ConstantValue;
import com.qiuyi.cn.orangemodule.util.FileManager.FileUtils;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.FileBean;
import com.qiuyi.cn.orangemodule.util.FileUtilOpen;
import com.qiuyi.cn.orangemodule.util.WriteToUdisk;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    LinearLayout ll_function_bottom;
    @BindView(R.id.tv_delete)
    TextView tv_delete;
    @BindView(R.id.tv_copytoUdisk)
    TextView tv_copytoUdisk;
    @BindView(R.id.tv_copytoSD)
    TextView tv_copytoSD;
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

    //所有文件
    private List<File> listFile;

    private SearchAdapter mySearchAdapter;
    private GridLayoutManager myGridManager;

    private int whereFrom = 0;
    private LoadingDialog dialog;

    private boolean isSelectAll = true;

    private WriteToUdisk udiskUtil;
    private DiskWriteToSD diskWriteToSD;

    private List<DocumentFile> listDocFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        ButterKnife.bind(this);


        udiskUtil = WriteToUdisk.getInstance(this.getApplicationContext(),this);
        diskWriteToSD= new DiskWriteToSD(this.getApplicationContext());

        dialog = new LoadingDialog.Builder(this)
                .setCancelOutside(false)
                .setCancelable(false)
                .setMessage("粘贴中...")
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
            listDocFile = udiskUtil.getAllFile(udiskUtil.getCurrentFolder());
            //初始化U盘文件
            initUFileData();
        }

        initListener();

    }


    //初始化
    private void initFileData() {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                listFile = MainActivity.MY_ALLFILLES_II;
                if(listFile.size()>0 && listFile!=null){
                    timer.cancel();
                    Log.e("search","数据接收完毕" );
                }
            }
        },1000,1000);
    }

    private void initUFileData(){
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                listFile = MainActivity.listUPANAllFiles;
                if(listFile.size()>0 && listFile!=null){
                    timer.cancel();
                    Log.e("search","数据接收完毕" );
                }
            }
        },1000,1000);
    }



    //初始化监听事件
    private void initListener() {
        tv_camera.setOnClickListener(this);
        tv_qq.setOnClickListener(this);
        tv_wechat.setOnClickListener(this);
        tv_screen.setOnClickListener(this);

        mySearch.setOnEditorActionListener(this);
    }


    @Override
    public void onClick(View view) {
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
    }


    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            //写点击搜索键后的操作
            Log.e("search","搜索");

            List<File> ufiles = getNativeFile(listFile,mySearch.getText().toString());
            showInAdapter(ufiles);

            return true;
        }
        return false;
    }


    //搜索框,本地所有文件（包括文件夹）
    private List<File> getNativeFile(List<File> listFile, String key) {
        List<File> keyListFile = new ArrayList<>();
        if(listFile.size()>0 && listFile!=null && key!=null && !key.equals("")){
            for(File nativeFile:listFile){
                if(nativeFile.getPath().toLowerCase().contains(key)){
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
            showInAdapter(keyListFile);
        }
    }

    //调用adapter显示
    private void showInAdapter(final List<File> keyListFile) {

        myGridManager = new GridLayoutManager(this,4);
        rl_show.setLayoutManager(myGridManager);
        mySearchAdapter = new SearchAdapter(this,keyListFile,myGridManager);
        rl_show.setAdapter(mySearchAdapter);

        ll_bottom.setVisibility(View.INVISIBLE);
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
                ll_function_bottom.setVisibility(View.VISIBLE);

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
                        ll_function_bottom.setVisibility(View.GONE);

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
                                        keyListFile.get(i).delete();
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
                                        udiskUtil.getDocFile(listDocFile,keyListFile.get(i).getName()).delete();
                                        keyListFile.remove(i);
                                    }
                                }
                                break;
                        }

                        mySearchAdapter.ReFresh();
                    }
                });

                switch (whereFrom){
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
                }


                mySearchAdapter.setShowCheckBox(true);
                boolean flag[] = mySearchAdapter.getFlag();
                flag[position] = true;

                mySearchAdapter.setFlag(flag);
                mySearchAdapter.notifyDataSetChanged();

            }
        });
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


}
