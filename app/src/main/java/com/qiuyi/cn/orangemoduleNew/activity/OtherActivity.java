package com.qiuyi.cn.orangemoduleNew.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.adapter.OtherfourAdapter;
import com.qiuyi.cn.orangemoduleNew.bean.Product;
import com.qiuyi.cn.orangemoduleNew.util.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/1/4.
 *
 * 上新 推荐 抢购 更多模块的内容
 */
public class OtherActivity extends Activity{

    @BindView(R.id.myRecyclerView)
    RecyclerView myView;

    //商品适配器
    private OtherfourAdapter myAdapter;
    //存储商品
    private List<Product> productList = new ArrayList<>();
    //GridLayoutManager的控制器
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otherfour);
        ButterKnife.bind(this);

        initData();
    }

    //初始化数据
    private void initData() {

        //初始化商品数据
        for(int i = 0; i< Constant.module_images.length; i++){
            Product product = new Product(
                    Constant.module_images[i],Constant.module_names[i],Constant.module_prices[i],
                    Constant.module_type[0],Constant.module_myflag[2]);
            //填充数据
            productList.add(product);
        }

        //gridView的布局设置，分为两列
        gridLayoutManager = new GridLayoutManager(this,2);
        myView.setLayoutManager(gridLayoutManager);

        //初始化适配器
        myAdapter = new OtherfourAdapter(productList);
        //给视图加上适配器
        myView.setAdapter(myAdapter);

        //点击前往商品详情页
        myAdapter.setOnItemClickListener(new OtherfourAdapter.onItemClickListener() {
            @Override
            public void OnItemClick(OtherfourAdapter.ViewHolder view, int position, int type) {
                Intent intent = new Intent(OtherActivity.this,GoodsActivity.class);
                intent.putExtra("title",productList.get(position).getName());
                intent.putExtra("price",productList.get(position).getPrice());
                intent.putExtra("image",productList.get(position).getImage());
                startActivity(intent);
            }

            @Override
            public void OnItemLongClick(View view, int position, int type) {

            }
        });
    }
}
