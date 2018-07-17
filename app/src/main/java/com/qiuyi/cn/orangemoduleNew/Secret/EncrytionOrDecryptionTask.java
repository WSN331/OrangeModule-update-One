package com.qiuyi.cn.orangemoduleNew.Secret;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.File;

import javax.crypto.Cipher;

/**
 * Created by Administrator on 2018/5/8.
 */
public class EncrytionOrDecryptionTask extends AsyncTask<Void,Void,Boolean>{


    private Context context;
    private AESHelper mAESHelper;

    private File mSourceFile = null;
    private File mNewFile = null;
    private String mSeed = "";
    private boolean mIsEncrypt = false;

    /**
     *
     * @param isEncrypt 加密解密
     * @param sourceFile 源文件
     * @param newFile 加密后文件，或者解密后文件
     * @param seed  key
     */
    public EncrytionOrDecryptionTask(Context context,AESHelper mAESHelper,boolean isEncrypt, File sourceFile,
                                      File newFile, String seed) {
        this.context = context;
        this.mAESHelper = mAESHelper;

        this.mSourceFile = sourceFile;
        this.mNewFile = newFile;
        this.mSeed = seed;
        this.mIsEncrypt = isEncrypt;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean result = false;

        if (mIsEncrypt) {
            result = mAESHelper.AESCipher(Cipher.ENCRYPT_MODE, mSourceFile,
                    mNewFile, mSeed);
        } else {
            result = mAESHelper.AESCipher(Cipher.DECRYPT_MODE, mSourceFile,
                    mNewFile, mSeed);
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        String showMessage = "";

        if (mIsEncrypt) {
            showMessage = result ? "加密已完成" : "加密失败!";
        } else {
            showMessage = result ? "解密完成" : "解密失败!";
        }

        Toast.makeText(context,showMessage,Toast.LENGTH_SHORT).show();
    }
}
