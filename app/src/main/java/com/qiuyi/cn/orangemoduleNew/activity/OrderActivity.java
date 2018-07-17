package com.qiuyi.cn.orangemoduleNew.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.adapter.OrderAdapter;
import com.qiuyi.cn.orangemoduleNew.bean.Product;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/1/4.
 *
 * 订单页面
 *
 */
public class OrderActivity extends Activity{

    //订单返回模块
    @BindView(R.id.tv_sureOrder)
    TextView sureOrder;
    //显示模块
    @BindView(R.id.recyclerView_order)
    RecyclerView orderView;
    //总价展示
    @BindView(R.id.tv_showMoney)
    TextView showMoney;
    //订单提交
    @BindView(R.id.tv_submit)
    TextView subMit;

    //订单适配器
    private OrderAdapter orderAdapter;
    //总价
    private String totalPrice;
    //购买的商品
    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);

        //初始化数据
        initData();
        //初始化界面
        initView();
    }

    //界面初始化
    private void initView() {
        orderView.setLayoutManager(new LinearLayoutManager(this));
        //展示总价
        showMoney.setText(totalPrice);
        //放入产品
        if(productList!=null){
            orderAdapter = new OrderAdapter(getApplicationContext(),productList);
            orderView.setAdapter(orderAdapter);
        }
    }

    //数据初始化
    private void initData() {
        totalPrice = "0";
        //获取传输过来的intent
        Intent intent = getIntent();
        productList = (List<Product>) intent.getSerializableExtra("buys");
        totalPrice = intent.getStringExtra("totalPrice");

        listenerClick();
    }

    private void listenerClick() {
        //结算按钮
        subMit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderActivity.this,PayendActivity.class);
                startActivity(intent);
            }
        });
    }


}
