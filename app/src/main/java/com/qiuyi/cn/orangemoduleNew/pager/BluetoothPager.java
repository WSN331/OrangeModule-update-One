package com.qiuyi.cn.orangemoduleNew.pager;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.activity.GoodsActivity;
import com.qiuyi.cn.orangemoduleNew.adapter.FramePagerAdapter;
import com.qiuyi.cn.orangemoduleNew.bean.Product;
import com.qiuyi.cn.orangemoduleNew.util.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/12/28.
 */
public class BluetoothPager extends BasePager{

    @BindView(R.id.FramePager_RecyclerView)
    RecyclerView myFrameView;

    //存储商品
    private List<Product> productList;
    //商品的适配器
    private FramePagerAdapter framePagerAdapter;
    //GridLayoutManager的控制器
    private GridLayoutManager gridLayoutManager;

    public BluetoothPager(Activity mActivity) {
        super(mActivity);
    }

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_bluetooth,null);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void initData() {
        productList = new ArrayList<>();

        //native_img
        Product imageProduct = new Product(R.drawable.paq8,null,null,null, Constant.module_myflag[0]);
        productList.add(imageProduct);
        //标题
        Product titleProduct = new Product(0,null,null,Constant.module_type[2],Constant.module_myflag[1]);
        productList.add(titleProduct);

        //初始化商品数据
        for(int i=0;i< Constant.module_images.length;i++){
            Product product = new Product(
                    Constant.module_images[i],Constant.module_names[i],Constant.module_prices[i],
                    Constant.module_type[0],Constant.module_myflag[2]);
            //填充数据
            productList.add(product);
        }

        //gridView的布局设置，分为两列
        gridLayoutManager = new GridLayoutManager(mActivity,2);
        //标题栏占两列，其他栏占一列
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
            @Override
            public int getSpanSize(int position) {
                if(position==0 || position==1){
                    return 2;
                }else{
                    return 1;
                }
            }
        });
        myFrameView.setLayoutManager(gridLayoutManager);
        //初始化适配器
        framePagerAdapter = new FramePagerAdapter(productList);
        //给视图加上适配器
        myFrameView.setAdapter(framePagerAdapter);

        //点击前往商品详情页
        framePagerAdapter.setOnItemClickListener(new FramePagerAdapter.onItemClickListener() {
            @Override
            public void OnItemClick(FramePagerAdapter.gridViewHolder view, int position, int type) {
                Intent intent = new Intent(mActivity,GoodsActivity.class);
                intent.putExtra("title",productList.get(position).getName());
                intent.putExtra("price",productList.get(position).getPrice());
                intent.putExtra("image",productList.get(position).getImage());
                mActivity.startActivity(intent);
            }

            @Override
            public void OnItemLongClick(View view, int position, int type) {

            }
        });
    }
}
