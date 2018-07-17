package com.qiuyi.cn.orangemoduleNew.myview;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.FileUtils;
import com.qiuyi.cn.orangemoduleNew.util.FileUtil;

import java.io.File;
import java.text.SimpleDateFormat;


public class FileDetailDialog extends Dialog implements View.OnClickListener{

    //文件名称，种类，大小，分析，时间，路径
    private TextView tv_filename;
    private TextView tv_filetype;
    private TextView tv_filesize;
    private TextView tv_fileinclude;
    private TextView tv_updatetime;
    private TextView tv_filepath;
    private TextView tv_sure;

    private Context context;
    private OnCloseListener listener;
    private String positiveName;
    private String title;
    private File mFile;

    private int fileNum = 0,directory=0;

    public FileDetailDialog(Context context, int themeResId, File file) {
        //这里就可以直接定义样式
        super(context, themeResId);
        this.context = context;
        this.mFile = file;
        this.listener = listener;
    }

    public FileDetailDialog setTitle(String title){
        this.title = title;
        return this;
    }

    public FileDetailDialog setPositiveButton(String name){
        this.positiveName = name;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filedetaildialog_commom);
        setCanceledOnTouchOutside(false);

        initView();
    }

    private void initView(){
        tv_filename = findViewById(R.id.tv_filename);
        tv_filetype = findViewById(R.id.tv_filetype);
        tv_filesize = findViewById(R.id.tv_fileSize);
        tv_fileinclude = findViewById(R.id.tv_fileinclude);
        tv_updatetime = findViewById(R.id.tv_updatetime);
        tv_filepath = findViewById(R.id.tv_filepath);

        tv_sure = findViewById(R.id.tv_sure);
        tv_sure.setOnClickListener(this);

        //文件名
        tv_filename.setText("文件名称："+mFile.getName());

        if(mFile.isDirectory()){
            //文件种类
            tv_filetype.setText("文件种类：文件夹");
            //文件分析
            analysis(mFile);
            tv_fileinclude.setText("文件分析："+" 文件夹："+directory+" 文件："+fileNum);
            getFilesizeTask myTask = new getFilesizeTask();
            myTask.execute(mFile);
        }else{
            //文件种类
            tv_filetype.setText("文件种类："+FileUtil.getMIMEType(mFile));
            //文件分析
            tv_fileinclude.setText("文件分析："+" 文件：1");
            //文件大小

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final String sizes = Formatter.formatFileSize(context, FileUtils.getFileSize(mFile));
                        tv_filesize.post(new Runnable() {
                            @Override
                            public void run() {
                                tv_filesize.setText("文件大小："+sizes);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }


        //修改时间
        tv_updatetime.setText("修改时间："+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(mFile.lastModified()));
        //路径
        tv_filepath.setText("文件路径："+mFile.getAbsolutePath());
    }

    //文件分析
    private void analysis(File mFile) {
        for(File noeFile:mFile.listFiles()){
            if(noeFile.isDirectory()){
                directory++;
                analysis(noeFile);
            }else{
                fileNum++;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_sure:
                this.dismiss();
                break;
        }
    }

    public interface OnCloseListener{
        void onClick(Dialog dialog, boolean confirm);
    }

    class getFilesizeTask extends AsyncTask<File,Void,String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(File... files) {
            File myFile = files[0];
            String sizes = null;
            try {
                sizes = "文件大小："+Formatter.formatFileSize(context, FileUtils.getFileSizes(mFile));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return sizes;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tv_filesize.setText(s);
        }
    }
}
