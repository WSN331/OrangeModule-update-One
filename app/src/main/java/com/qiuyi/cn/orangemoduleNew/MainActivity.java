package com.qiuyi.cn.orangemoduleNew;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.provider.DocumentFile;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiuyi.cn.orangemoduleNew.activity.BasePermission;
import com.qiuyi.cn.orangemoduleNew.activity.BkrtActivity;
import com.qiuyi.cn.orangemoduleNew.activity.CollectionActivity;
import com.qiuyi.cn.orangemoduleNew.activity.SearchActivity;
import com.qiuyi.cn.orangemoduleNew.adapter.LeftListViewAdapter;
import com.qiuyi.cn.orangemoduleNew.bean.LeftItem;
import com.qiuyi.cn.orangemoduleNew.fragment.FileControllFragment;
import com.qiuyi.cn.orangemoduleNew.fragment.MainFragmentEquipment;
import com.qiuyi.cn.orangemoduleNew.fragment.MainFragmentSmartHome;
import com.qiuyi.cn.orangemoduleNew.fragment.MainFragmentTalk;
import com.qiuyi.cn.orangemoduleNew.interfaceToutil.BasePermissionListener;
import com.qiuyi.cn.orangemoduleNew.myview.MyPopWindow;
import com.qiuyi.cn.orangemoduleNew.upansaf.db.bean.AppInfo;
import com.qiuyi.cn.orangemoduleNew.upansaf.db.util.DBUtil;
import com.qiuyi.cn.orangemoduleNew.upansaf.db.util.PermissionDialog;
import com.qiuyi.cn.orangemoduleNew.util.Constant;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.MyFileHelper;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.bean1.FileBean;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.contacts.ContactBean;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.service.FindAllFile_II_Service;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.service.FindAllFile_Service;
import com.qiuyi.cn.orangemoduleNew.util.UsbCommunication;
import com.qiuyi.cn.orangemoduleNew.util.WriteToUdisk;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BasePermission implements View.OnClickListener{

    //判断现在是哪个Fragment
    private int state = 0;

    //基本权限
    private static final String[] ACTION_PERMISSION = {
            //存储权限
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            //读取权限
            Manifest.permission.READ_EXTERNAL_STORAGE,
            //联系人读取权限
            Manifest.permission.READ_CONTACTS,
            //联系人存储权限
            Manifest.permission.WRITE_CONTACTS
    };


    //添加其他布局
    public FrameLayout mAddView;
    //侧边栏
    public ListView listView;
    //下拉菜单
    public TextView myTextView;
    //侧滑栏
    private DrawerLayout drawerLayout;
    //按钮
    private ImageView myImgView;


    //测试指令的按钮
    @BindView(R.id.sendByte)
    Button sendByte;

    //主界面，下面的三个选项
    @BindView(R.id.ll_equip)
    LinearLayout ll_equip;
    @BindView(R.id.ll_equip_img)
    ImageView ll_equip_img;
    @BindView(R.id.ll_forum)
    LinearLayout ll_forum;
    @BindView(R.id.ll_forum_img)
    ImageView ll_forum_img;
    @BindView(R.id.ll_smarthome)
    LinearLayout ll_smarthome;
    @BindView(R.id.ll_smarthome_img)
    ImageView ll_smarthome_img;


    @BindView(R.id.rl_bottom)
    RelativeLayout rl_bottom;

    //下面的选项栏，两个
    @BindView(R.id.rg_button)
    RadioGroup rg_button;
    @BindView(R.id.main_ll)
    LinearLayout main_ll;

    //备份还原+收藏
    @BindView(R.id.rb_reduction)
    RadioButton my_reduction;
    @BindView(R.id.rb_collection)
    RadioButton my_collection;

    @BindView(R.id.img_search)
    ImageView mySearch;

    //全局静态变量存储所有获取的文件数据
    public static List<File> MY_ALLFILLES_II = new ArrayList<>();//所有文件
    public static List<FileBean> MY_ALLFILES = new ArrayList<>();//最近前100个文件
    public static List<File> listMusics = new ArrayList<>();//音乐
    public static List<File> listVideos = new ArrayList<>();//视频
    public static List<File> listImages = new ArrayList<>();//图片
    public static List<File> listFiles = new ArrayList<>();//文件
    public static List<File> listFileZars = new ArrayList<>();//压缩包
    public static JSONObject constacts = null;

    //U盘全部文件
    public static List<File> listUPANAllFiles = new ArrayList<>();//全部文件
    public static List<File> listUPANMusics = new ArrayList<>();//音乐
    public static List<File> listUPANVideos = new ArrayList<>();//视频
    public static List<File> listUPANImages = new ArrayList<>();//图片
    public static List<File> listUPANFiles = new ArrayList<>();//文件
    public static List<File> listUPANFileZars = new ArrayList<>();//压缩包
    public static List<ContactBean> listContacts = new ArrayList<>();//联系人

    public static File constactUP = null;

    //U盘备份的文件
    public static File uMusic = null;//音乐
    public static File uVideo = null;//视频
    public static File uImages = null;//图片
    public static File uFiles = null;//文件


    //检查U盘是否存在的类
    private MyFileHelper myHelper;

    //获取U盘删除，修改权限
    private WriteToUdisk udiskWrite;

    //U盘路径
    public static File rootUFile = null;
    //U盘路径
    public static DocumentFile rootUDFile = null;

    //判定U盘是否存在
    public static boolean isHaveUpan = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //数据库存储，放到了MyApplication中，可以从AndroidManifest中查看
/*        SugarContext.init(this);*/

        //判断U盘是否存在
        myHelper = new MyFileHelper(getApplicationContext());
        udiskWrite = WriteToUdisk.getInstance(getApplicationContext(),this);

        //得到基础权限
        getBasePermission();

        //获得U盘权限
        getUDiskPermission();

        //初始化标题栏和侧滑栏
        initView();

        //改变中间的部分
        initFragmentLayout();

        //显示第一个模块
        changeFragment(new MainFragmentEquipment());
        ll_equip_img.setImageResource(R.drawable.equipment2);


        sendByte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //查找设备
                UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                HashMap<String,UsbDevice> deviceList = usbManager.getDeviceList();
                Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
                while(deviceIterator.hasNext()){
                    Log.e("MySend", "正在寻找设备"+deviceList.size());
                    UsbDevice device = deviceIterator.next();
                    //寻找设备,框架模块
                    if(device.getVendorId()==1155 && device.getProductId()==22336){
                        Log.e("MySend", "找到设备发送指令");
                        UsbCommunication communication = new UsbCommunication(getApplicationContext(),device);
                        String myOrder = "EF,0000,01,0";
                        communication.sendMessage(myOrder.getBytes());
                    }
                }
                Log.e("MySend", "没有找到设备");
            }
        });

    }

    private AppInfo udiskInfo = null;
    //获取U盘权限和路径
    private void getUDiskPermission() {
        File file = myHelper.isUPState();
        if(file!=null){
            isHaveUpan = true;
            //存储U盘路径
            MainActivity.rootUFile = file;

            //获取U盘相关信息
            udiskInfo = DBUtil.getAppInfo(file.getAbsolutePath());
            if(udiskInfo!=null){
                //若U盘信息不存在，那么这就是一个新U盘
                udiskWrite.initPresenter(Uri.parse(udiskInfo.getRootUriPath()));

                MainActivity.rootUDFile = udiskWrite.getCurrentFolder();
            }else{
                udiskInfo = DBUtil.getAppInfo(null);
                udiskInfo.setRootPath(file.getAbsolutePath());
                //申请权限
                intentToOpen();
            }
        }
    }

    /**
     * 进入选择U盘的页面
     */
    private void intentToOpen() {
        new android.support.v7.app.AlertDialog.Builder(this)
                .setTitle("获取权限")
                .setMessage("需要获取U盘权限")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new PermissionDialog(myActivity, new PermissionDialog.OnDialogClick() {
                            @Override
                            public void onClick(Dialog dialog, boolean confirm) {
                                if(confirm){
                                    Intent intent=new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);//ACTION_OPEN_DOCUMENT
                                    startActivityForResult(intent,42);
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

            DBUtil.setUri(udiskInfo,rootUri.toString());

            udiskWrite.initPresenter(rootUri);

            //存储U盘可以读写
            MainActivity.rootUDFile = udiskWrite.getCurrentFolder();
        }else{
            Toast.makeText(this,"请给予权限才能备份",Toast.LENGTH_LONG).show();
        }
    }


    //改变中间部分
    private void initFragmentLayout() {
        ll_equip.setOnClickListener(this);
        ll_forum.setOnClickListener(this);
        ll_smarthome.setOnClickListener(this);
        my_reduction.setOnClickListener(this);
        my_collection.setOnClickListener(this);
        mySearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_equip:
                changeFragment(new MainFragmentEquipment());
                ll_equip_img.setImageResource(R.drawable.equipment2);
                ll_forum_img.setImageResource(R.drawable.talk3);
                ll_smarthome_img.setImageResource(R.drawable.smart1);
                break;
            case R.id.ll_forum:
                changeFragment(new MainFragmentTalk());
                ll_equip_img.setImageResource(R.drawable.equipment1);
                ll_forum_img.setImageResource(R.drawable.talk4);
                ll_smarthome_img.setImageResource(R.drawable.smart1);
                break;
            case R.id.ll_smarthome:
                changeFragment(new MainFragmentSmartHome());
                ll_equip_img.setImageResource(R.drawable.equipment1);
                ll_forum_img.setImageResource(R.drawable.talk3);
                ll_smarthome_img.setImageResource(R.drawable.smart2);
                break;
            case R.id.rb_reduction:
                Intent intent = new Intent(myActivity,BkrtActivity.class);
                startActivity(intent);
                break;
            case R.id.rb_collection:
                Intent intent_collection = new Intent(myActivity,CollectionActivity.class);
                startActivity(intent_collection);
                break;
            case R.id.img_search:
                Intent intent_search = new Intent(myActivity,SearchActivity.class);
                intent_search.putExtra("main",true);
                startActivity(intent_search);
                break;
            default:
                break;
        }
    }

    //标题初始化
    private void initView() {
        listView = findViewById(R.id.drawerLayout_listView);
        myTextView = findViewById(R.id.tv_drop);
        drawerLayout = findViewById(R.id.drawerLayout);
        myImgView = findViewById(R.id.img_more);
        mAddView = findViewById(R.id.myView);


        //禁止手势滑动
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        //打开手势滑动
        //drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);


        final List<LeftItem> list = new ArrayList<LeftItem>();
        for(int i = 0; i< Constant.IMAGES.length; i++){
            LeftItem item = new LeftItem(Constant.IMAGES[i],Constant.TEXTSHOW[i]);
            list.add(item);
        }

        LeftListViewAdapter adapter = new LeftListViewAdapter(this,list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //隐藏侧滑栏
                showDrawerLayout();
            }
        });

        myImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.LEFT);//侧滑打开
            }
        });

        myTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPopWinShow = !isPopWinShow;
                if(state == 0){
                    if(isPopWinShow){
                        showMainPop();
                    }
                }else if(state == 1){
                    if(isPopWinShow){
                        showFilePop();
                    }
                }
            }
        });
    }

    private boolean isPopWinShow = false;

    //展示文件管理界面的PopWindow
    private void showFilePop() {
        //状态0表示现在是文件管理在显示
        new MyPopWindow(getApplicationContext(), new MyPopWindow.OnItemClickListener() {
            @Override
            public void onItemClick(MyPopWindow popupWindow, int position) {
                switch (position){
                    case 1:
                        popupWindow.dismiss();
                        break;
                    case 2:
                        //现在是文件管理模块
                        //去到咔咔模块
                        //首先判断有没有权限
                        goToMainActivity(new MainFragmentEquipment());
                        popupWindow.dismiss();
                        break;
                    case 3:
                        popupWindow.dismiss();
                        break;
                    case 4:
                        popupWindow.dismiss();
                        break;
                }
            }
        }).setText("首页").showAsDropDown(myTextView);
    }


    //展示主页的popWindow
    private void showMainPop() {
        //状态0表示现在是主页在显示
        new MyPopWindow(getApplicationContext(), new MyPopWindow.OnItemClickListener() {
            @Override
            public void onItemClick(MyPopWindow popupWindow, int position) {
                switch (position){
                    case 1:
                        popupWindow.dismiss();
                        break;
                    case 2:
                        //现在是卡卡模块
                        //去到文件管理模块
                        //检查是否有权限进行操作
                        if(isHavePermission(denied)){
                            goToFileControll(new FileControllFragment());
                            popupWindow.dismiss();
                        }else{
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("权限申请")
                                    .setMessage("对不起，您没有给予相关权限，无法进行操作。" +
                                            "请到设置界面给予app相关权限(存储,获取联系人)")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            gotoHuaweiPermission();
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).show();
                        }
                        break;
                    case 3:
                        popupWindow.dismiss();
                        break;
                    case 4:
                        popupWindow.dismiss();
                        break;
                }
            }
        }).showAsDropDown(myTextView);
    }


    //侧滑栏显示隐藏控制
    private void showDrawerLayout() {
        if(!drawerLayout.isDrawerOpen(Gravity.LEFT)){
            drawerLayout.openDrawer(Gravity.LEFT);
        }else{
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }


    //改变中间显示的Fragment
    public void changeFragment(Fragment myFragment){
        FragmentManager myManager = getSupportFragmentManager();
        FragmentTransaction myTransaction = myManager.beginTransaction();
        myTransaction.replace(R.id.myView,myFragment);
        myTransaction.commit();
    }


    public static boolean isFromUdisk = false;
    //展示文件管理模块
    public void showFileControll(){
        FileControllFragment fm = new FileControllFragment();
        //去到文件管理模块
        goToFileControll(fm);
        isFromUdisk = true;
    }

    //去到文件管理模块
    private void goToFileControll(Fragment fm){
        changeFragment(fm);
        myTextView.setText("文件管理");

        //rl_bottom.setVisibility(View.GONE);
        rl_bottom.setVisibility(View.VISIBLE);
        rg_button.setVisibility(View.VISIBLE);
        //判断是否有U盘
/*        if(isHaveUpan){
            my_reduction.setVisibility(View.VISIBLE);
        }*/
        my_reduction.setVisibility(View.VISIBLE);

        main_ll.setVisibility(View.INVISIBLE);

        mySearch.setVisibility(View.VISIBLE);
        state = 1;
    }
    //还原主模块
    private void goToMainActivity(Fragment fm) {
        changeFragment(fm);

        myTextView.setText("咔咔");
        main_ll.setVisibility(View.VISIBLE);

        File file = myHelper.findUdiskPath();
        if(file==null){
            isHaveUpan = false;
        }

        rl_bottom.setVisibility(View.VISIBLE);
        rg_button.setVisibility(View.GONE);

        mySearch.setVisibility(View.GONE);

        ll_equip_img.setImageResource(R.drawable.equipment2);
        state = 0;
    }


    //检查是否有需要的权限
    public Boolean isHavePermission(List<String> denied){
        if(denied !=null){
            for(String permission:denied){
                for(int i=0;i<ACTION_PERMISSION.length;i++){
                    if(permission.equals(ACTION_PERMISSION[i])){
                        //如果有拒绝的权限则返回false,不让进行下一步操作
                        return false;
                    }
                }
            }
        }
        return true;
    }


    private List<String> denied;
    //获取基础权限
    private void getBasePermission(){
        //调用父类方法
        requestToPermission(ACTION_PERMISSION, new BasePermissionListener() {
            @Override
            public void onGranted() {
                //所有授权成功
                Log.e("USB","基础权限授权成功");

                if(MainActivity.MY_ALLFILES == null || MainActivity.MY_ALLFILES.size()<=0){
                    startService(new Intent(myActivity, FindAllFile_Service.class));
                }
                if(MainActivity.MY_ALLFILLES_II == null || MainActivity.MY_ALLFILLES_II.size()<=0){
                    startService(new Intent(myActivity,FindAllFile_II_Service.class));
                }
            }
            @Override
            public void onGrantedSuccess(List<String> grantedPermission) {
                //获取成功的授权
                Log.e("USB","部分基础权限授权成功");
            }
            @Override
            public void onDenied(List<String> deniedPermission) {
                //获取失败的授权
                Log.e("USB","部分基础权限授权失败");
                denied = deniedPermission;
            }
        });
    }



    /**
     * 华为的权限管理页面
     */
    private void gotoHuaweiPermission() {
        try {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ComponentName comp = new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");//华为权限管理
            intent.setComponent(comp);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(getAppDetailSettingIntent());
        }
    }
    /**
     * 获取应用详情页面intent
     *
     * @return
     */
    private Intent getAppDetailSettingIntent() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        return localIntent;
    }
}




/*
*/
