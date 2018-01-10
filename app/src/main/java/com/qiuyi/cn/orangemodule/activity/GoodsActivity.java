package com.qiuyi.cn.orangemodule.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;


import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.bean.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
* 商品详情界面
* */
public class GoodsActivity extends AppCompatActivity {

    @BindView(R.id.device_btn)
    ImageView deviceBtn;
    @BindView(R.id.shop_btn)
    ImageView shopBtn;
    @BindView(R.id.mine_btn)
    TextView mineBtn;
    @BindView(R.id.mine_btn2)
    TextView mineBtn2;
    @BindView(R.id.activity_goods)
    LinearLayout activityGoods;
    @BindView(R.id.iv_module_image)
    ImageView ivModuleImage;
    @BindView(R.id.tv_module_title)
    TextView tvModuleTitle;
    @BindView(R.id.tv_module_price)
    TextView tvModulePrice;


    //产品序列
    private List<Product> productList = new ArrayList<>();
    private Float mTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods);
        ButterKnife.bind(this);

        initData();
        initListener();
    }

    //数据初始化
    private void initData() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String price = intent.getStringExtra("price");
        int image = intent.getIntExtra("image", R.drawable.paq5);
        tvModuleTitle.setText(title);
        tvModulePrice.setText(price);
        ivModuleImage.setImageResource(image);

        price = price.substring(price.indexOf("¥")+1);
        mTotalPrice = Float.parseFloat(price)*1;
        //初始化产品
        Product product = new Product(image,title,price,1,false);
        productList.add(product);
    }

    //购物车点击事件
    private void initListener() {
        //购物车模块
        shopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingCarActivity.startActivity(GoodsActivity.this);
            }
        });

        //购买模块
        mineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GoodsActivity.this,OrderActivity.class);
                intent.putExtra("buys",(Serializable) productList);
                intent.putExtra("totalPrice",mTotalPrice+"");
                startActivity(intent);
            }
        });
    }

}
