package com.qiuyi.cn.orangemodule.util;

/**
 * Created by Yang on 2017/7/11.
 * Function：接受信息的工具类
 */

public class MessageUtil {

    //字节转字符串，然后用字节字符串转为16进制字符串
    public static String bytesToHexString(String str){
        String[] dataList = str.split("\\r\\n");
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

    //得到甲醛数据
    public static String getJQData(String str){
        String mydata = "53";
        if(str.length()>=20){
            String jqdata = str.substring(str.indexOf("4546303030343031")+16);
            FileOutToWrite.write("--甲醛16进制--"+jqdata);

            String d1 = jqdata.substring(0,2);
            String d2 = jqdata.substring(2,4);
            int myint1 = Integer.parseInt(d1,16);
            int myint2 = Integer.parseInt(d2,16);
            mydata = String.valueOf(myint1)+String.valueOf(myint2);
            FileOutToWrite.write("--甲醛十进制--"+mydata);

        }
        return mydata;
    }

    public static String getAirData(String str) {
        String[] data1List = str.split("\\r\\n");
        String[] data2List = data1List[0].split(",");
        return data2List[3];
    }

    public static String[] getPMData(String str) {
        String[] dataList = str.split("\\r\\n");
        String pm1 = dataList[0];
        String pm25 = dataList[1];
        String pm10 = dataList[2];

        String[] pmList = new String[3];

        //pm1.0
        pmList[0] = formatPMData(pm1);
        //pm2.5
        pmList[1] = formatPMData(pm25);
        //pm2.5
        pmList[2] = formatPMData(pm10);

        return pmList;
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
            //连接状态
            data[2] = data2List[5];
        }
        return data;
    }
}
