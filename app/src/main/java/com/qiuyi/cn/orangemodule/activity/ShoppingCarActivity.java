package com.qiuyi.cn.orangemodule.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.adapter.ShopCarAdapter;
import com.qiuyi.cn.orangemodule.bean.Product;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/*
* 购物车界面
* */
public class ShoppingCarActivity extends AppCompatActivity {

    @BindView(R.id.rlv_shopcart)
    RecyclerView rlvShopcart;
    @BindView(R.id.tv_shopcart_addselect)
    ImageView tvShopcartAddselect;
    @BindView(R.id.tv_shopcart_totalprice)
    TextView tvShopcartTotalprice;
    @BindView(R.id.tv_shopcart_totalnum)
    TextView tvShopcartTotalnum;
    //去结算按钮
    @BindView(R.id.tv_shopcart_submit)
    TextView tvShopcartSubmit;
    @BindView(R.id.ll_pay)
    LinearLayout llPay;
    @BindView(R.id.rl_shopcart)
    RelativeLayout rlShopcart;
    @BindView(R.id.activity_shopping_car)
    LinearLayout activityShoppingCar;

    //加入购物车的商品
    private List<Product> data = new ArrayList<>();

    //要购买付钱的商品
    private List<Product> buys = new ArrayList<>();
    //购物车适配器
    private ShopCarAdapter adapter;
    //是否选择
    private boolean selected;
    //总价格
    private float mTotalPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_car);
        ButterKnife.bind(this);

        //初始化数据
        initData();

        rlvShopcart.setLayoutManager(new LinearLayoutManager(this));
        //填充数据
        adapter = new ShopCarAdapter(this, data);
        rlvShopcart.setAdapter(adapter);

        //设置监听
        initListener();

    }

    //页面跳转
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ShoppingCarActivity.class);
        context.startActivity(intent);
    }


    //数据初始化并装载
    private void initData() {
        Product p1 = new Product(R.drawable.led, "LED模块", "100",1, true);
        Product p2 = new Product(R.drawable.laser_pointer, "激光笔模块", "200", 1, true);
        Product p3 = new Product(R.drawable.laser_pointer, "激光笔模块", "200", 1, true);
        Product p4 = new Product(R.drawable.laser_pointer, "激光笔模块", "200", 1, true);
        Product p5 = new Product(R.drawable.laser_pointer, "激光笔模块", "200", 1, true);
        Product p6 = new Product(R.drawable.laser_pointer, "激光笔模块", "200", 1, true);
        data.add(p1);
        data.add(p2);
        data.add(p3);
        data.add(p4);
        data.add(p5);
        data.add(p6);
    }

    private void initListener() {
        //总价的计算
        adapter.setResfreshListener(new ShopCarAdapter.OnResfreshListener() {
            @Override
            public void onResfresh(boolean isSelect) {
                selected = isSelect;
                if (isSelect) {
                    Drawable left = getResources().getDrawable(R.drawable.shopcart_selected);

                    tvShopcartAddselect.setImageDrawable(left);
                } else {
                    Drawable left = getResources().getDrawable(R.drawable.shopcart_unselected);

                    tvShopcartAddselect.setImageDrawable(left);
                }

                //总价
                float totalPrice = 0;
                //总数量
                int totalNum = 0;

                buys.clear();

                for (int i = 0; i < data.size() ; i++) {
                    if (data.get(i).isSelected()) {
                        totalPrice += Float.parseFloat(data.get(i).getPrice()) * data.get(i).getCount();
                        totalNum += data.get(i).getCount();
                        buys.add(data.get(i));
                    }
                }
                mTotalPrice = totalPrice;
                tvShopcartTotalprice.setText("总价：" + mTotalPrice);
                tvShopcartTotalnum.setText("共" + totalNum + "件商品");
            }
        });


        //全选点击
        tvShopcartAddselect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected = !selected;
                if (selected) {
                    Drawable left = getResources().getDrawable(R.drawable.shopcart_selected);

                    tvShopcartAddselect.setImageDrawable(left);

                    for (int i = 0; i < data.size(); i++) {
                        data.get(i).setSelected(true);
                    }
                } else {
                    Drawable left = getResources().getDrawable(R.drawable.shopcart_unselected);

                    tvShopcartAddselect.setImageDrawable(left);

                    for (int i = 0; i < data.size(); i++) {
                        data.get(i).setSelected(false);
                    }
                }
                //改变list
                adapter.notifyDataSetChanged();
            }
        });

        //支付按钮
        tvShopcartSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShoppingCarActivity.this,OrderActivity.class);
                intent.putExtra("buys",(Serializable) buys);
                intent.putExtra("totalPrice",mTotalPrice+"");
                startActivity(intent);
            }
        });
    }
}
