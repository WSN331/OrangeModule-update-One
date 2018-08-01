package com.qiuyi.cn.orangemoduleNew.util;

import android.util.Log;

/**
 * Created by Yang on 2017/7/11.
 * Function：接受信息的工具类
 */

public class MessageUtil {

    //字节转16进制字符串
    public static String bytesToHex(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    //字节转字符串，然后用字节字符串转为16进制字符串
    public static String bytesToHexString(String str){
        String[] dataList = str.split("\r\n");
        byte[] src = dataList[0].getBytes();
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    //字节数组转字符串
    public static String byte2String(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        String str = new String(byteArray);
        return str;
    }


    //得到空气数据
    public static String[] getPMData(String str){
        String[] mydata = new String[3];
        if(str.length()>=20){
            String jqdata = str.substring(str.indexOf("4546303030323033")+16);

            String d1 = jqdata.substring(0,4);
            String d2 = jqdata.substring(4,8);
            String d3 = jqdata.substring(8,12);
            //16进制字符串转为int型
            int myint1 = Integer.parseInt(d1,16);
            int myint2 = Integer.parseInt(d2,16);
            int myint3 = Integer.parseInt(d3,16);
            Log.e("myint",myint1+"-"+myint2+"-"+"-"+myint3);

            //int转为float
            float myfloat1 = (float) (myint1*1.0);
            float myfloat2 = (float) (myint2*1.0);
            float myfloat3 = (float) (myint3*1.0);

            float myfloat4 = myfloat2-180;
            if(myfloat4<=200 && myfloat4 >=40){
                //Pm1.0,pm2.5,pm10
                mydata[0] = String.valueOf(myfloat1);
                mydata[1] = String.valueOf(myfloat4);
                mydata[2] = String.valueOf(myfloat3);
            }else if(myfloat4<40){
                myfloat4 = 50;
                mydata[0] = String.valueOf(myfloat1);
                mydata[1] = String.valueOf(myfloat4);
                mydata[2] = String.valueOf(myfloat3);
            }
            else{
                mydata = null;
            }

        }
        return mydata;
    }


    //得到水质数据
    public static String getWaterData(String str){
        String[] nowStr = str.split(",");
        String mydata = nowStr[nowStr.length-1];
        return mydata;

/*        String mydata = null;
        if(str.length()>=24){
            String waterData = str.substring(str.indexOf("4546303030333031")+16);

            String w1 = waterData.substring(0,2);
            String w2 = waterData.substring(2,4);
            String w3 = waterData.substring(4,6);
            String w4 = waterData.substring(6,8);

            int myw1 = Integer.parseInt(w1,16)-48;
            int myw2 = Integer.parseInt(w2,16)-48;
            int myw3 = Integer.parseInt(w3,16)-48;
            int myw4 = Integer.parseInt(w4,16)-48;
            Log.e("myint",myw1+"-"+myw2+"-"+"-"+myw3+"-"+myw4);
            mydata = String.valueOf(myw1)+String.valueOf(myw2)+String.valueOf(myw3)+String.valueOf(myw4);
        }
        return mydata;*/
    }

    //得到甲醛数据
    public static String getJQData(String str){
        String[] nowStr = str.split(",");
        String mydata = nowStr[nowStr.length-1];
        return mydata;
    }



    //框架数据转换并获取
    public static String[] formatFrameData(String string) {
        String[] data1List = string.split("\\r\\n");
        String[] data2List = data1List[0].split(",");

        String[] data = new String[3];
        if(data2List.length>=6)
        {
            //电压
            data[0] = data2List[3];
            //电量
            data[1] = data2List[4].substring(1);
            if(data[1].equals("00")){
                data[1] = "100";
            }
            //连接状态
            data[2] = data2List[5];
        }
        return data;
    }
}

/*    public static String getAirData(String str) {
        String[] data1List = str.split("\\r\\n");
        String[] data2List = data1List[0].split(",");
        return data2List[3];
    }


    public static String formatPMData(String string) {
        if (string.startsWith("PM1.0 : ")) {
            return string.substring("PM1.0 : ".length() - 1, string.length());
        } else if (string.startsWith("PM2.5 : ")) {
            return string.substring("PM2.5 : ".length() - 1, string.length());
        } else if (string.startsWith("PM10  : ")) {
            return string.substring("PM10  : ".length() - 1, string.length());
        } else {
            return "";
        }
    }*/