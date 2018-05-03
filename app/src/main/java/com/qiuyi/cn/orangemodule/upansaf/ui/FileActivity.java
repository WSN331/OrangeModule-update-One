package com.qiuyi.cn.orangemodule.upansaf.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/*import com.ethanco.lib.PasswordDialog;
import com.ethanco.lib.abs.OnPositiveButtonListener;*/
import com.qiuyi.cn.myloadingdialog.LoadingDialog;
import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.activity.SearchActivity;
import com.qiuyi.cn.orangemodule.upansaf.db.util.DBUtil;
import com.qiuyi.cn.orangemodule.upansaf.db.util.PermissionDialog;
import com.qiuyi.cn.orangemodule.upansaf.ui.presenter.DocumentFilePresenter;
import com.qiuyi.cn.orangemodule.upansaf.ui.presenter.FilePresenter;
import com.qiuyi.cn.orangemodule.upansaf.ui.view.DocumentFileAdapter;
import com.qiuyi.cn.orangemodule.upansaf.ui.view.FileView;
import com.qiuyi.cn.orangemodule.upansaf.ui.view.MyItemDecoration;
/*import com.qiuyi.cn.orangemodule.upansaf.usb.jnilib.UDiskConnection;
import com.qiuyi.cn.orangemodule.upansaf.usb.jnilib.UDiskLib;*/
import com.orm.SugarContext;

import org.w3c.dom.Text;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class FileActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, FileView {

    public static final int BEFOREPB = 100;
    public static final int AFTERPB = 101;

    private static String TAG = "MainActivity";

    private int index= 0;
    private Context context;
    private ProgressBar pbShow;
    private LinearLayout llShow;

    private TextView tvDebug, preFolder;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    //private UDiskLib uDiskLib;
    private boolean alreadyLogin = false;    // SDK操作完后判断是否还处于登录的标记

    private FilePresenter filePresenter;

    private DocumentFileAdapter adapter;

    private LinearLayout linearCommon, linearLongClick, linearPaste;
    private TextView tv_writeToUp;
    private TextView copyBtn, cutBtn, pasteBtn, deleteBtn,
            refreshBtn, createBtn, cancelBtn, equal_btn,uDiskBtn,selectAll_btn,rename_btn;

    private ImageView img_search;

    private AlertDialog dialog;

    private boolean isSelectAll = false;

    private LoadingDialog dialogCopy;
    private LoadingDialog dialogDelete;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:

                    break;
                case 1:

                    break;
            }
        }
    };

/*    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            llShow.setVisibility(View.INVISIBLE);
            dialog.dismiss();
        }
    };*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_udisksaf_main);

        context = getApplicationContext();
/*        //数据库存储
        SugarContext.init(this);*/

        initView();
        initPermission();


    }

    /**
     * 初始化权限
     */
    private void initPermission() {
        Uri uri = DocumentFilePresenter.getUri();
        if (uri!=null) {
            initPresenter(uri);
        } else {
            String uriStr = DBUtil.getAppInfo(MainActivity.rootUFile.getAbsolutePath()).getRootUriPath();
            if (uriStr != null && !uriStr.equals("")) {
                uri = Uri.parse(uriStr);
                initPresenter(uri);
            } else {
                intentToOpen();
            }
        }
    }

    /**
     * 进入选择U盘的页面
     */
    private void intentToOpen() {
        new AlertDialog.Builder(FileActivity.this)
                .setTitle("获取权限")
                .setMessage("需要获取U盘权限")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        new PermissionDialog(FileActivity.this, new PermissionDialog.OnDialogClick() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if(confirm){
                                    Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);//ACTION_OPEN_DOCUMENT
                                    startActivityForResult(intent, 42);
                                }
                            }
                        }).show();
                    }
                })
                .setNegativeButton("否", null)
                .setCancelable(false)
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        Uri rootUri;
        if (requestCode == 42 && resultCode == Activity.RESULT_OK && resultData != null) {
            rootUri = resultData.getData();

            DBUtil.setUri(DBUtil.getAppInfo(MainActivity.rootUFile.getAbsolutePath()),rootUri.toString());

            initPresenter(rootUri);
        } else {
            DocumentFilePresenter.newInstance(this, context);
        }
    }

    /**
     * 初始化表示器
     * @param rootUri
     */
    private void initPresenter(Uri rootUri) {
        filePresenter = DocumentFilePresenter.newInstance(this, context, rootUri);
        onRefresh();
    }

    /**
     * 初始化界面
     */
    private void initView() {
        //pbShow = (ProgressBar) findViewById(R.id.pb_show);

        //llShow = (LinearLayout) findViewById(R.id.ll_show);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        preFolder = (TextView) findViewById(R.id.pre_folder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyItemDecoration(this, MyItemDecoration.VERTICAL_LIST));
        swipeRefreshLayout.setColorSchemeColors(Color.YELLOW, Color.BLUE, Color.RED, Color.GREEN);
        swipeRefreshLayout.setOnRefreshListener(this);
        tvDebug = (TextView) findViewById(R.id.tv_debug);
        linearCommon = (LinearLayout) findViewById(R.id.ll_1);
        linearLongClick = (LinearLayout) findViewById(R.id.ll_2);
        img_search = (ImageView) findViewById(R.id.img_search);
        //et_search = (EditText) findViewById(R.id.et_search);
        //linearPaste = (LinearLayout) findViewById(R.id.ll_3);
        initClick();
    }

    /**
     * 初始化点击事件
     */
    private void initClick() {
        preFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preFolder.getText() != null && !preFolder.getText().equals("")) {
                    onBackPressed();
                }
            }
        });

        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //去到搜索界面,来自U盘
                Intent intent = new Intent(new Intent(FileActivity.this,SearchActivity.class));
                intent.putExtra("udisk",true);
                startActivity(intent);
            }
        });
        initClickTooBar();
    }

    @Override
    public void onRefresh() {
        tvDebug.setText("");
        swipeRefreshLayout.setRefreshing(true);

        //新建文件夹
        createBtn.setVisibility(View.VISIBLE);

        linearCommon.setVisibility(View.GONE);
        linearLongClick.setVisibility(View.GONE);
        if (filePresenter == null) {
            initPermission();
        } else {
            Intent intent = getIntent();
            String path = null;
            path = intent.getStringExtra("ufilename");
            filePresenter.refresh(path);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        filePresenter.unRegisterReceiver();
        doLogout();
    }

    @Override
    public void onBackPressed() {
        if (adapter.changeCheckBoxVisibility(DocumentFileAdapter.ViewHolder.CHECK_INVISIBILITY)) {
            if (filePresenter.isRootView()) {
                filePresenter.returnPreFolder();
            } else if (filePresenter.isLogin()) {
                logout();
            } else {
                super.onBackPressed();
            }
        } else {
            setToolBarType(FilePresenter.TOOL_BAR_COMMON);
        }
    }

    @Override
    public void setTitle(final String preText, final String nowText) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                preFolder.setText(preText);
                tvDebug.setText(nowText);
            }
        });

    }

    @Override
    public void setAdapter(final DocumentFileAdapter adapter) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FileActivity.this.adapter = adapter;
                recyclerView.setAdapter(adapter);
                adapter.setRecyclerView(recyclerView);
            }
        });
    }

    @Override
    public DocumentFileAdapter getAdapter() {
        return adapter;
    }

    @Override
    public RecyclerView getRecycler() {
        return recyclerView;
    }

    @Override
    public void setRefreshing(final boolean b) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(b);
            }
        });
    }

    @Override
    public void onUDiskInsert(Intent intent) {
        //进行读写操作
        Log.e(TAG, "U盘插入");
        if (filePresenter != null) {
            filePresenter.setLoginFlag(alreadyLogin);
        }
        alreadyLogin = false;
        initPermission();
    }

    @Override
    public void onUDiskRemove(Intent intent) {
        UsbDevice device_out = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
        if (device_out != null) {
            //更新界面
            Log.e(TAG, "U盘拔出");
            adapter.notifyDataSetChanged();
            doLogout();
        }
    }

    @Override
    public void setToolBarType(int type) {
        switch (type) {
            case FilePresenter.TOOL_BAR_COMMON:

                createBtn.setVisibility(View.VISIBLE);
                //linearPaste.setVisibility(View.VISIBLE);

                //全选按钮
                selectAll_btn.setVisibility(View.GONE);
                //复制剪贴
                linearCommon.setVisibility(View.GONE);
                //粘贴取消
                linearLongClick.setVisibility(View.GONE);

                break;
            case FilePresenter.TOOL_BAR_LONG_CLICK:

                selectAll_btn.setVisibility(View.VISIBLE);
                linearCommon.setVisibility(View.VISIBLE);

                linearLongClick.setVisibility(View.GONE);
                createBtn.setVisibility(View.GONE);
                //linearPaste.setVisibility(View.INVISIBLE);
                break;
            case FilePresenter.TOOL_BAR_PASTE:
                //粘贴取消显示
                linearLongClick.setVisibility(View.VISIBLE);

                createBtn.setVisibility(View.GONE);
                selectAll_btn.setVisibility(View.GONE);
                linearCommon.setVisibility(View.GONE);
                //linearPaste.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public Activity getActivity() {
        return FileActivity.this;
    }

    /**
     * 初始化工具栏
     */
    private void initClickTooBar() {


        copyBtn = (TextView) findViewById(R.id.copy_btn);
        cutBtn = (TextView) findViewById(R.id.cut_btn);
        pasteBtn = (TextView) findViewById(R.id.paste_btn);
        deleteBtn = (TextView) findViewById(R.id.delete_btn);
        //refreshBtn = (TextView) findViewById(R.id.refresh_btn);
        createBtn = (TextView) findViewById(R.id.create_btn);
        cancelBtn = (TextView) findViewById(R.id.cancel_btn);
        //uDiskBtn = (TextView) findViewById(R.id.newUdisk_btn);
        selectAll_btn = (TextView) findViewById(R.id.selectAll_btn);
        rename_btn = (TextView) findViewById(R.id.rename_btn);

/*        tv_writeToUp = (TextView) findViewById(R.id.tv_writeToUp);
        tv_writeToUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = Environment.getExternalStorageDirectory().getAbsoluteFile();
                File file1 = new File(file.getAbsolutePath()+"/myFile");

                filePresenter.pastSDFile(file1);
            }
        });*/

        //重命名
        rename_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewFolder("重命名文件",2);
            }
        });

        //全选
        selectAll_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSelectAll){
                    adapter.noCheckAllView();
                    selectAll_btn.setText("全选");
                    isSelectAll = false;
                }else{
                    adapter.checkAllView();
                    selectAll_btn.setText("取消");
                    isSelectAll = true;
                }
            }
        });

/*        //新U盘
        uDiskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清除原来数据，获取新U盘的插入
                showDialog();
            }
        });*/


        //复制
        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setTextSelect()){
                    filePresenter.copyFileList(false);
                }else{
                    Toast.makeText(getApplicationContext(),"请选择你要复制的文件",Toast.LENGTH_SHORT).show();
                }

            }
        });

        //剪切
        cutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(setTextSelect()){
                    filePresenter.copyFileList(true);
                }else{
                    Toast.makeText(getApplicationContext(),"请选择你要剪切的文件",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //粘贴
        pasteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCopy = getLoadingDialog("粘贴中...");
                dialogCopy.show();

                filePresenter.pasteFileList();

                dialogCopy.dismiss();
/*                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        //粘贴
                        dialogCopy.dismiss();
                    }
                }).start();*/
            }
        });
        //删除
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setTextSelect()){
/*                    dialogDelete = getLoadingDialog("删除中...");
                    dialogDelete.show();*/

                    filePresenter.deleteCheckFileList();

                    //删除
                    //dialogDelete.dismiss();
                }else{
                    Toast.makeText(getApplicationContext(),"请选择你要删除的文件",Toast.LENGTH_SHORT).show();
                }

            }
        });
        //刷新
/*        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });*/
        //取消
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createBtn.setVisibility(View.VISIBLE);
                //linearPaste.setVisibility(View.VISIBLE);

                selectAll_btn.setVisibility(View.GONE);
                linearCommon.setVisibility(View.GONE);
                linearLongClick.setVisibility(View.GONE);
            }
        });

        //创建新文件夹
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewFolder("新建文件夹",1);
            }
        });
    }

    /**
     * 上传时效果设置
     */
/*    public void progressSet(){
        View view = View.inflate(getApplicationContext(),R.layout.view,null);
        dialog = new AlertDialog.Builder(this, R.style.TransparentWindowBg)
                .setView(view)
                .create();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ERROR);

        dialog.setCancelable(false);
        dialog.show();

        llShow.setVisibility(View.VISIBLE);
    }*/

    /**
     * 当没有选中项时，三个功能键不能点击
     */
    private boolean setTextSelect() {
        Map<Integer,Boolean> checkmap = new HashMap<>();
        checkmap.clear();
        checkmap = adapter.getCheckMap();
        int count = 0;
        for(Integer position:checkmap.keySet()){
            if(checkmap.get(position)){
                count++;
            }
        }
        if(count==0){
            return false;
        }else{
            return true;
        }
    }


    /**
     * 创建新建文件夹
     */
    private void createNewFolder(String name, final int flag) {
        final View view = View.inflate(FileActivity.this,R.layout.saf_createfile_layout,null);

        final Dialog dialog = new AlertDialog.Builder(FileActivity.this)
                .setView(view).show();

        TextView title = (TextView) view.findViewById(R.id.tv_title);
        title.setText(name);

        Button confirm = (Button) view.findViewById(R.id.confirm_Btn);
        Button can = (Button) view.findViewById(R.id.can_Btn);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etName = (EditText) view.findViewById(R.id.et_name);
                final String name = etName.getText().toString();

                if(!TextUtils.isEmpty(name)){
                    if(flag == 1){
                        //新建文件夹
                        filePresenter.createFolder(name);
                    }else if(flag == 2){
                        //重命名文件
                        filePresenter.renameFile(name);
                    }
                    dialog.dismiss();
                }else{
                    Toast.makeText(FileActivity.this,"请输入文件夹名称",Toast.LENGTH_SHORT).show();
                }
            }
        });
        can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 执行登录
     * @param text 密码
     */
    private void doLogin(final String text) {
/*        uDiskLib = UDiskLib.create(context);
        UDiskConnection.create(uDiskLib, new UDiskConnection.Action() {
            @Override
            public int action(UDiskLib diskLib) {
                return uDiskLib.smiLoginDeviceByStr(text);
            }
        }).success(new UDiskConnection.CallBack() {
            @Override
            public void call(int result) {
                Toast.makeText(context, "login success", Toast.LENGTH_SHORT);
                alreadyLogin = true;
                filePresenter.setLoginFlag(alreadyLogin);
                DBUtil.initWrongPassCount();
                onRefresh();
            }
        }).error(new UDiskConnection.CallBack() {
            @Override
            public void call(int result) {
                int wrongCount = DBUtil.getWrongPassCount();
                wrongCount++;
                if (wrongCount>=9) {
                    Toast.makeText(context, "超过9次了", Toast.LENGTH_SHORT).show();

                    destroyAllData();

                    DBUtil.initWrongPassCount();
                } else {
                    Toast.makeText(context, "已经" + wrongCount + "次了", Toast.LENGTH_SHORT).show();
                    DBUtil.setWrongPassCount(wrongCount);
                }
                onRefresh();

            }
        }).close().doAction();*/

    }

    @Override
    public void showPasswordView() {
/*        PasswordDialog.Builder builder = new PasswordDialog.Builder(FileActivity.this)
                .setTitle(R.string.please_input_password)  //Dialog标题
                .setBoxCount(4) //设置密码位数
                .setBorderNotFocusedColor(R.color.colorSecondaryText) //边框颜色
                .setDotNotFocusedColor(R.color.colorSecondaryText)  //密码圆点颜色
                .setPositiveListener(new OnPositiveButtonListener() {
                    @Override //确定
                    public void onPositiveClick(DialogInterface dialog, int which, String text) {
                        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                        doLogin(text);
                    }
                })
                .setNegativeListener(new DialogInterface.OnClickListener() {
                    @Override //取消
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "取消", Toast.LENGTH_SHORT).show();
                    }
                });
        builder.create().show();*/
    }

    /**
     * 私密空间退出登录的弹框
     */
    private void logout() {
        new AlertDialog.Builder(FileActivity.this)
                .setTitle("退出登录")
                .setMessage("确定退出登录吗？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doLogout();
                    }
                })
                .setNegativeButton("否", null)
                .show();
    }

    /**
     * 私密空间执行退出的方法
     */
    private void doLogout() {

/*        uDiskLib = UDiskLib.create(context);
        UDiskConnection.create(uDiskLib, new UDiskConnection.Action() {
            @Override
            public int action(UDiskLib diskLib) {
                return uDiskLib.smiLogoutDevice();
            }
        }).success(new UDiskConnection.CallBack() {
            @Override
            public void call(int result) {
                Toast.makeText(context, "logOut success", Toast.LENGTH_SHORT);
                filePresenter.setLoginFlag(false);
                onRefresh();
            }
        }).close().doAction();*/
    }

    /**
     *  私密空间执行销毁数据的方法
     */
    private void destroyAllData(){
/*        uDiskLib = UDiskLib.create(context);
        UDiskConnection.create(uDiskLib,new UDiskConnection.Action(){
            @Override
            public int action(UDiskLib diskLib) {
                return uDiskLib.smiEraseAllData();
            }
        }).success(new UDiskConnection.CallBack() {
            @Override
            public void call(int result) {
                Toast.makeText(context,"deleteAllData success",Toast.LENGTH_SHORT);
                onRefresh();
            }
        }).close().doAction();*/

    }


    //dialog显示
    public void showDialog(){
        new AlertDialog.Builder(FileActivity.this)
        .setTitle("新U盘接入")
        .setMessage("确定删除原来使用的U盘信息？")
        .setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intentToOpen();
            }
        })
        .setNegativeButton("否", null)
        .setCancelable(false)
        .show();
    }

    //获取加载框
    public LoadingDialog getLoadingDialog(String name){
        LoadingDialog.Builder loadBuilder=new LoadingDialog.Builder(FileActivity.this)
                .setMessage(name)
                .setCancelable(false)
                .setCancelOutside(false);
        return loadBuilder.create();
    }
}
//equal_btn = (TextView) findViewById(R.id.equal_btn);

/*        equal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressSet();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pbShow.setProgress(0);
                        filePresenter.equalFileList(getApplicationContext());
                        pbShow.setMax(100);
                        for(int i=1;i<=100;i++)
                        {
                            pbShow.setProgress(i);
                            try {
                                Thread.sleep(10+ new Random().nextInt(20));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        mHandler.sendEmptyMessage(0);
                    }
                }).start();
            }
        });*/