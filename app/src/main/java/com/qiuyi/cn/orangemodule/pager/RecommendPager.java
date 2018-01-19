package com.qiuyi.cn.orangemodule.pager;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.oragee.banners.BannerView;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.activity.GoodsActivity;
import com.qiuyi.cn.orangemodule.activity.OtherActivity;
import com.qiuyi.cn.orangemodule.adapter.FramePagerAdapter;
import com.qiuyi.cn.orangemodule.adapter.RecommendPagerAdapter;
import com.qiuyi.cn.orangemodule.bean.Product;
import com.qiuyi.cn.orangemodule.util.Constant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/12/28.
 *
 * 推荐页面
 */
public class RecommendPager extends BasePager{

    @BindView(R.id.myRecyclerView)
    RecyclerView myView;

    //存储商品
    private List<Product> productList;
    //商品适配器
    private RecommendPagerAdapter recomAdapter;
    //GridLayoutManager的控制器
    private GridLayoutManager gridLayoutManager;


    public RecommendPager(Activity mActivity) {
        super(mActivity);
    }

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_recommend,null);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void initData() {
        productList = new ArrayList<>();

        //第一个模块 这里是轮播模块
        List<View> viewList = new ArrayList<>();
        for(int i=0;i< Constant.slid_images.length;i++){
            ImageView imageView = new ImageView(mActivity);
            imageView.setImageResource(Constant.slid_images[i]);
            viewList.add(imageView);
        }
        Product product1 = new Product();
        product1.setMyflag(0);
        product1.setViewList(viewList);
        productList.add(product1);


        //第二个模块
        Product product2 = new Product();
        product2.setMyflag(1);
        productList.add(product2);

        //第三个模块
        Product product3 = new Product();
        product3.setMyflag(2);
        productList.add(product3);

        //第四个模块
        Product product4 = new Product();
        product4.setMyflag(3);
        productList.add(product4);

        //第五个模块
        for(int i=4;i<Constant.module_images.length;i++){
            Product product = new Product(
                    Constant.module_images[i],Constant.module_names[i],Constant.module_prices[i],
                    Constant.module_type[0],4);
            productList.add(product);
        }


        //gridView的布局设置，分为两列
        gridLayoutManager = new GridLayoutManager(mActivity,2);
        //标题栏占两列，其他栏占一列
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
            @Override
            public int getSpanSize(int position) {
                if(position==0 || position==1||position==2 || position==3){
                    return 2;
                }else{
                    return 1;
                }
            }
        });

        myView.setLayoutManager(gridLayoutManager);
        //初始化适配器
        recomAdapter = new RecommendPagerAdapter(mActivity,productList);
        //给视图加上适配器
        myView.setAdapter(recomAdapter);

        recomAdapter.setOnItemClickListener(new RecommendPagerAdapter.onItemClickListener() {
            @Override
            public void OnItemClick(RecommendPagerAdapter.gridViewHolder view, int position, int type) {
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
