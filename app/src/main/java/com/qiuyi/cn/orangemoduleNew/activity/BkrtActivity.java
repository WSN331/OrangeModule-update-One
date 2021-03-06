package com.qiuyi.cn.orangemoduleNew.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.qiuyi.cn.orangemoduleNew.MainActivity;
import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.fragment.BackUpFragment;
import com.qiuyi.cn.orangemoduleNew.fragment.RestoreFragment;
import com.qiuyi.cn.orangemoduleNew.util.FileManager.MyFileHelper;

/**
 * Created by Administrator on 2018/3/18.
 * 备份还原
 */
public class BkrtActivity extends Activity implements View.OnClickListener{

    //联系人存储的文件名
    public static final String PHONE_FILE = "Contacts.txt";

    //文件操作方法
    private MyFileHelper myFileHelper;

    private ImageView img_back;
    private RadioButton rb_backup;
    private RadioButton rb_restore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backupreduction);

        initView();
    }

    //初始化界面
    private void initView() {
        img_back = findViewById(R.id.img_back);
        rb_backup = findViewById(R.id.rb_backup);
        rb_restore = findViewById(R.id.rb_restore);

        img_back.setOnClickListener(this);
        rb_backup.setOnClickListener(this);
        rb_restore.setOnClickListener(this);

        myFileHelper = new MyFileHelper(getApplicationContext());

        changeFragment(new BackUpFragment());
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_back:
                onBackPressed();
                break;
            case R.id.rb_backup:
                changeFragment(new BackUpFragment());
                break;
            case R.id.rb_restore:
                if(MainActivity.isHaveUpan){
                    changeFragment(new RestoreFragment());
                }else{
                    new AlertDialog.Builder(this)
                            .setTitle("U盘")
                            .setMessage("请插入U盘").show();
                }
                break;
            default:
                break;
        }
    }

    //改变Fragment
    private void changeFragment(Fragment fm){
        FragmentManager supportFragmentManager = getFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        transaction.replace(R.id.fm,fm);
        transaction.commit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
