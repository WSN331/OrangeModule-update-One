package com.qiuyi.cn.orangemodule.pager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.qiuyi.cn.orangemodule.MainActivity;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.activity.UdiskActivity;
import com.qiuyi.cn.orangemodule.bean.UsbMyDevice;
import com.qiuyi.cn.orangemodule.service.UsbComService;
import com.qiuyi.cn.orangemodule.service.UsbJQService;
import com.qiuyi.cn.orangemodule.service.UsbPMService;
import com.qiuyi.cn.orangemodule.service.UsbService;
import com.qiuyi.cn.orangemodule.service.UsbWaterService;
import com.qiuyi.cn.orangemodule.upansaf.ui.FileActivity;
import com.qiuyi.cn.orangemodule.upanupdate.AllUdiskFileShowActivity;
import com.qiuyi.cn.orangemodule.util.FileOutToWrite;
import com.qiuyi.cn.orangemodule.util.UsbCommunication;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/12/27.
 * 这个是4个功能模块的作用逻辑
 */
public class DevicePager extends BasePager{

    //空气
    private static final String ACTION1 ="com.yangjian.testPM.RECEIVER";
    //水质
    private static final String Action4 ="com.yangjian.testWater.RECEIVER";
    //甲醛
    private static final String ACTION2 ="com.yangjian.testJQ.RECEIVER";
    //U盘
    private static final String ACTION3 ="com.yangjian.testUSBpan.RECEIVER";

    //usb线的广播
    private final static String TAGUSB = "android.hardware.usb.action.USB_STATE";
    //外设的广播
    public static final String TAGIN = "android.hardware.usb.action.USB_DEVICE_ATTACHED";
    public static final String TAGOUT = "android.hardware.usb.action.USB_DEVICE_DETACHED";

    //U盘模块
    @BindView(R.id.usbConnect)
    CardView usbConnect;
    //U盘模块
    @BindView(R.id.udisk_layout)
    LinearLayout uDisk;

    //空气模块
    @BindView(R.id.air_layout)
    LinearLayout airLayout;
    //pm1.0模块
    @BindView(R.id.tv_pm10)
    TextView tv_pm10;
    //pm2.5模块
    @BindView(R.id.tv_pm25)
    TextView tv_pm25;
    //pm10模块
    @BindView(R.id.tv_pm100)
    TextView tv_pm100;

    //甲醛模块
    @BindView(R.id.tv_cascophen)
    TextView tv_jq;
    //甲醛模块
    @BindView(R.id.cascophen_layout)
    LinearLayout ll_jq;

    //水质模块
    @BindView(R.id.water_layout)
    LinearLayout ll_water;
    //水质模块
    @BindView(R.id.tv_tds)
    TextView tv_tds;


    //甲醛，水质，空气模块等级评价显示
    @BindView(R.id.air_rank)
    TextView airShow;
    @BindView(R.id.water_rank)
    TextView waterShow;
    @BindView(R.id.jq_rank)
    TextView jqShow;

    //检测usb连接
    private UsbCommunication communication;
    //USB插入服务
    private Intent intent = null;
    private UsbComServiceConn usbComServiceConn;
    private UsbComService.MyBind myBind;


    //监听外设与usb存储设备
    private MainMessageReceiver myMsg;
    private IntentFilter intentFilterMy;
    //监听Pm广播
    private PmMessageReceiver pmMsg;
    private IntentFilter intentFilter;
    //监听Water广播
    private WaterMessageReceiver waterMsg;
    private IntentFilter waterFilter;
    //监听JQ广播
    private JqMessageReceiver jqMsg;
    private IntentFilter jqFilter;
    //监听U盘广播
    private UsbMessageReceiver usbMsg;
    private IntentFilter usbFilter;

    public DevicePager(Activity mActivity) {
        super(mActivity);
    }

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_device,null);
        ButterKnife.bind(this,view);
        return view;
    }

    //具体的功能实现在这里
    @Override
    public void initData() {

        //监听广播数据
        initBroadCast();

        //启动全局USB插入服务
        intent = new Intent(mActivity, UsbComService.class);
        usbComServiceConn = new UsbComServiceConn();
        //绑定服务
        mActivity.bindService(intent,usbComServiceConn, Context.BIND_AUTO_CREATE);


        if(MainActivity.isHaveUpan){
            usbConnect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

/*                    MainActivity mainActivity = (MainActivity) mActivity;

                    mainActivity.showFileControll();*/


                    Intent intent = new Intent(mActivity,AllUdiskFileShowActivity.class);
                    mActivity.startActivity(intent);
                }
            });
        }
    }

    //监听广播数据
    private void initBroadCast() {

        //注册设备插入拔出的广播
        myMsg = new MainMessageReceiver();
        intentFilterMy = new IntentFilter();
        intentFilterMy.addAction(TAGIN);
        intentFilterMy.addAction(TAGOUT);
        intentFilterMy.addAction(TAGUSB);
        mActivity.registerReceiver(myMsg,intentFilterMy);

        //注册Pm广播
        pmMsg = new PmMessageReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION1);
        mActivity.registerReceiver(pmMsg,intentFilter);

        //注册Water广播
        waterMsg = new WaterMessageReceiver();
        waterFilter = new IntentFilter();
        waterFilter.addAction(Action4);
        mActivity.registerReceiver(waterMsg,waterFilter);

        //注册Jq广播
        jqMsg = new JqMessageReceiver();
        jqFilter = new IntentFilter();
        jqFilter.addAction(ACTION2);
        mActivity.registerReceiver(jqMsg,jqFilter);

        //注册U盘广播
        usbMsg = new UsbMessageReceiver();
        usbFilter = new IntentFilter();
        usbFilter.addAction(ACTION3);
        mActivity.registerReceiver(usbMsg,usbFilter);
    }

    //外设与usb存储设备
    public class MainMessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            //判断连接
            if(action.equals(TAGIN)){

                //解除原有的绑定
                mActivity.unbindService(usbComServiceConn);
                //启动全局USB插入服务
                intent = new Intent(mActivity, UsbComService.class);
                usbComServiceConn = new UsbComServiceConn();
                //绑定服务
                mActivity.bindService(intent,usbComServiceConn, Context.BIND_AUTO_CREATE);
            }
            //判断移除
            if(action.equals(TAGOUT)){
                //解除原有的绑定
                mActivity.unbindService(usbComServiceConn);
                //启动全局USB插入服务
                intent = new Intent(mActivity, UsbComService.class);
                usbComServiceConn = new UsbComServiceConn();
                //绑定服务
                mActivity.bindService(intent,usbComServiceConn, Context.BIND_AUTO_CREATE);
            }

            //判断Usb大容量设备连接移除,这里的大容量设备指的是电脑
            if(action.equals(TAGUSB)){
                boolean connected = intent.getExtras().getBoolean("connected");
                if (connected) {
                    uDisk.setBackgroundColor(Color.parseColor("#ff975a"));
                } else {
                    uDisk.setBackgroundColor(Color.parseColor("#8a8a8a"));
                }
            }
        }
    }

    //水质模块接收广播
    public class WaterMessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String waterData = intent.getStringExtra("water");

            if(waterData!=null){
                ll_water.setBackgroundColor(Color.parseColor("#ff975a"));
                tv_tds.setText(waterData+"ppm");

                int water = Integer.parseInt(waterData);
                if(water<=9){
                    //纯净水（优）
                    waterShow.setText("纯净水（优）");
                    waterShow.setBackgroundColor(Color.parseColor("#7cfc00"));
                }else if(9<water && water<=60){
                    //矿化水（良）
                    waterShow.setText("矿泉水（良）");
                    waterShow.setBackgroundColor(Color.parseColor("#eeee00"));
                }else if(60<water && water<=100){
                    //净化水（差）
                    waterShow.setText("净化水（差）");
                    waterShow.setBackgroundColor(Color.parseColor("#ee7600"));
                }else if(100<water && water<=300){
                    //自来水
                    waterShow.setText("自来水");
                    waterShow.setBackgroundColor(Color.parseColor("#ee7600"));
                }else if(300<water){
                    //污染水
                    waterShow.setText("污染水");
                    waterShow.setBackgroundColor(Color.parseColor("#ee0000"));

                }

            }

        }
    }


    //甲醛模块接收广播
    public class JqMessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String jqString = intent.getStringExtra("jq");
            Float jqint = Float.parseFloat(jqString)/1000;
            if(jqString!=null && !jqString.equals("123")){
                ll_jq.setBackgroundColor(Color.parseColor("#ff975a"));
                tv_jq.setText(jqint+"mg/m³");

                if(jqint<=0.05){
                    //优
                    jqShow.setText("优");
                    jqShow.setBackgroundColor(Color.parseColor("#7cfc00"));
                }else if(0.05<jqint && jqint<=0.08){
                    //良
                    jqShow.setText("良");
                    jqShow.setBackgroundColor(Color.parseColor("#eeee00"));
                }else if(0.08<jqint && jqint<=0.1){
                    //轻度污染(不适宜居住)
                    jqShow.setText("轻度污染（不适宜居住）");
                    jqShow.setBackgroundColor(Color.parseColor("#ee7600"));
                }else if(0.1<jqint){
                    //重度污染(甲醛含量超标)
                    jqShow.setText("重度污染（甲醛含量超标）");
                    jqShow.setBackgroundColor(Color.parseColor("#ee0000"));
                }


            }/*else{
                ll_jq.setBackgroundColor(Color.parseColor("#8a8a8a"));
                tv_jq.setText("0.000mg/m³");
            }*/
        }
    }

    //Pm2.5空气模块接收广播
    public class PmMessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String[] pmString = intent.getStringArrayExtra("pmvalue");

            if(pmString !=null && pmString.length>=3){
                airLayout.setBackgroundColor(Color.parseColor("#ff975a"));
                //pm1.0
                String text1 = "PM1.0含量："+pmString[0]+"μg/m³";
                //pm2.5
                String text2 = "PM2.5含量："+pmString[1]+"μg/m³";
                //pm10
                String text3 = "PM10含量："+pmString[2]+"μg/m³";
                tv_pm10.setText(text1);
                tv_pm25.setText(text2);
                tv_pm100.setText(text3);

                Float airMsg = Float.parseFloat(pmString[1]);
                if(airMsg<=50){
                    //优
                    airShow.setText("优");
                    airShow.setBackgroundColor(Color.parseColor("#7cfc00"));
                }else if(50<airMsg && airMsg<=100){
                    //良
                    airShow.setText("良");
                    airShow.setBackgroundColor(Color.parseColor("#eeee00"));
                }else if (100<airMsg && airMsg<=150){
                    //轻度污染
                    airShow.setText("轻度污染");
                    airShow.setBackgroundColor(Color.parseColor("#ee7600"));
                }else if (150<airMsg && airMsg<=200){
                    //中度污染
                    airShow.setText("中度污染");
                    airShow.setBackgroundColor(Color.parseColor("#ee0000"));
                }else if (200<airMsg && airMsg<=300){
                    //重度污染
                    airShow.setText("重度污染");
                    airShow.setBackgroundColor(Color.parseColor("#ff00ff"));
                }else if (300<airMsg){
                    //极度污染
                    airShow.setText("极度污染");
                    airShow.setBackgroundColor(Color.parseColor("#8b008b"));
                }
            }/*else{
                airLayout.setBackgroundColor(Color.parseColor("#8a8a8a"));
                tv_pm10.setText("PM1.0含量：0μg/m³");
                tv_pm25.setText("PM2.5含量：0μg/m³");
                tv_pm100.setText("PM10含量：0μg/m³");
            }*/
        }
    }
    //U盘模块广播
    public class UsbMessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String isConn = intent.getStringExtra("isConn");

            if(isConn.equals("1")){
                uDisk.setBackgroundColor(Color.parseColor("#ff975a"));
            }else{
                uDisk.setBackgroundColor(Color.parseColor("#8a8a8a"));
            }
        }
    }

    //服务绑定后需要运行的步骤，同步数据
    private class UsbComServiceConn implements ServiceConnection{
        @Override
        public void onServiceConnected(final ComponentName componentName, IBinder iBinder) {
            //这个Ibinder就是service中传递过来的myBind;
            myBind = (UsbComService.MyBind) iBinder;

            myBind.setData(mActivity);

            //得到service,调用回调函数，获取数据
            myBind.getService().setOnDeviceCallback(new UsbComService.usbDeviceCallback() {
                @Override
                public void dataChanged(final List<UsbDevice> deviceList) {

                    //得到数据，返回主线程
                    //子线程无法执行UI,所以需要到主线程进行操作
                    //1 使用handler的操作
                    //2 使用runOnUiThread操作,这个的原理是将当前线程发布到事件队列UI线程中
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mActivity,"现在有"+deviceList.size()+"个设备接入",Toast.LENGTH_SHORT).show();
                            int n = 0;
                            synchronized (deviceList){
                                Iterator<UsbDevice> it = deviceList.iterator();
                                while(it.hasNext()){
                                    UsbDevice nowDevice = it.next();
                                    //不同实例对象
                                    communication = new UsbCommunication(mActivity,nowDevice);

                                    if(communication.getDeviceConnection()!=null){
                                        //框架模块
                                        if(nowDevice.getVendorId()==1155 && nowDevice.getProductId()==22336){
                                            n++;
                                            if(n==1){

                                                //框架模块
                                                Bundle bundle = new Bundle();
                                                bundle.putParcelable("usbDevice",nowDevice);
                                                Intent newIntent = new Intent(mActivity, UsbService.class);
                                                //这里有点不同
                                                newIntent.putExtras(bundle);
                                                //启动接收数据服务
                                                mActivity.startService(newIntent);
                                            }
/*                                            if(n==1){
                                                //空气模块
                                                Bundle bundle = new Bundle();
                                                bundle.putParcelable("usbDevice",nowDevice);
                                                Intent newIntent = new Intent(mActivity, UsbPMService.class);
                                                //这里有点不同
                                                newIntent.putExtras(bundle);
                                                //启动接收数据服务
                                                mActivity.startService(newIntent);
                                            }*/
/*                                            if(n==1){
                                                //水质模块
                                                Bundle bundle = new Bundle();
                                                bundle.putParcelable("usbDevice",nowDevice);
                                                Intent newIntent = new Intent(mActivity, UsbWaterService.class);
                                                //这里有点不同
                                                newIntent.putExtras(bundle);
                                                //启动接收数据服务
                                                mActivity.startService(newIntent);
                                            }*/

                                        }
                                        //甲醛模块
                                        if(nowDevice.getVendorId()==887 && nowDevice.getProductId()==30004){
                                            //当前插入的设备
                                            Bundle bundle = new Bundle();
                                            bundle.putParcelable("usbDevice",nowDevice);
                                            Intent newIntent = new Intent(mActivity, UsbJQService.class);
                                            //这里有点不同
                                            newIntent.putExtras(bundle);
                                            //启动接收数据服务
                                            mActivity.startService(newIntent);
                                        }
                                    }
                                }
                            }
                        }
                    });

                }
            });
        }

        //服务结束时杀死
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            myBind = null;
        }
    }
}

