package com.qiuyi.cn.orangemoduleNew.upan.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/1/15.
 * Service
 * OnCreate->onStartCommand()->ServiceRunning->onDestroy();
 * OnCreate->onBind()->CLientBoundService->onUnBind()->onDestroy();
 *
 */
public class myService extends Service {

    public static final String ACTION = "com.qiuyi.start";
    private Intent intent = new Intent(ACTION);
    private Timer timer = new Timer();
    private Socket socket = null;
    private BufferedReader reader = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //创建
    @Override
    public void onCreate() {
        super.onCreate();
    }

    //运行
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        sendMsgToServerPC();

        return super.onStartCommand(intent, flags, startId);
    }

    //不停的给服务端发数据
    private void sendMsgToServerPC() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e("aaa","start");
                InetAddress address = null;
                try {
                    address = InetAddress.getByName("39.108.97.130");
                    /*address = InetAddress.getByName("192.168.2.142");*/

                    socket = new Socket("39.108.97.130",9999);

                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream(),"UTF-8"));
                    String line = reader.readLine();

                    Log.e("sss",line);

                    if(line.equals("0")){
                        sendBroadcast(intent);
                        onDestroy();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        },3000,1000);
    }

    //销毁
    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        try {
            if(reader!=null){
                reader.close();
            }
            if(socket!=null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
