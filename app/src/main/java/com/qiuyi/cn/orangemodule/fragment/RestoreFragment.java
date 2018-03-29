package com.qiuyi.cn.orangemodule.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.adapter.BackUp_fm_adapter;
import com.qiuyi.cn.orangemodule.bean.MyItemFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/18.
 * 还原页面
 */
public class RestoreFragment extends Fragment implements View.OnClickListener{

    public Activity mActivity;
    //recyclerView
    private RecyclerView rl_restore;
    //还原
    private Button bt_restore;
    //适配器
    private BackUp_fm_adapter myAdapter;
    //LinnerLayoutManager
    private LinearLayoutManager myManager;
    //FileItem
    private List<MyItemFile> listFiles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mActivity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_restore,null);
        rl_restore = view.findViewById(R.id.rl_restore);
        bt_restore = view.findViewById(R.id.bt_restore);

        bt_restore.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
    }

    /**
     * 数据初始化
     */
    private void initData() {
        //添加数据
        addFileTypeData();

        myManager = new LinearLayoutManager(mActivity);

        rl_restore.setLayoutManager(myManager);
        myAdapter = new BackUp_fm_adapter(mActivity,listFiles);
        rl_restore.setAdapter(myAdapter);
    }

    //添加数据
    private void addFileTypeData() {
        listFiles = new ArrayList<>();

        for(int i=0;i<7;i++){
            MyItemFile itemFile = new MyItemFile("native_audio","文件大小：1.25GB");
            listFiles.add(itemFile);
        }

    }


}