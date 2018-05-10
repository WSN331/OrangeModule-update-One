package com.qiuyi.cn.orangemodule.Manager;

import com.qiuyi.cn.orangemodule.interfaceToutil.UdiskDeleteListener;

import java.io.File;

/**
 * Created by Administrator on 2018/5/5.
 */
public class AllUdiskManager {

    //单例模式
    private AllUdiskManager(){}
    private static class AllUdiskController{
        private static final AllUdiskManager manager = new AllUdiskManager();
    }
    public static AllUdiskManager getAllUdiskManagerInstance(){
        return AllUdiskController.manager;
    }

    private UdiskDeleteListener mListener;

    public void setmListener(UdiskDeleteListener mListener) {
        this.mListener = mListener;
    }

    public void udiskDelete(File file){
        if(mListener!=null){
            mListener.doUdiskDelete(file);
        }
    }
}
