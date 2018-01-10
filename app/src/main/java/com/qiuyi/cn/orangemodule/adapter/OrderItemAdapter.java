package com.qiuyi.cn.orangemodule.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.bean.Product;

import java.util.List;

/**
 * Created by Administrator on 2018/1/4.
 *
 * 订单中每一个条目的布局，属于订单模块总布局中的尾部分
 */
public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder>{

    //产品信息
    private List<Product> productList;
    public OrderItemAdapter(List<Product> productList){
        this.productList = productList;
    }

    //2 第二步
    @Override
    public OrderItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //绑定视图
        View view = View.inflate(parent.getContext(), R.layout.adapter_orderitem,null);
        return new ViewHolder(view);
    }

    //3 第三步
    @Override
    public void onBindViewHolder(OrderItemAdapter.ViewHolder holder, int position) {

        Product product = productList.get(position);
        holder.product_img.setImageResource(product.getImage());
        holder.product_name.setText(product.getName());
        holder.product_price.setText(product.getPrice());
        holder.product_count.setText("共"+product.getCount()+"件商品");

        //总价格
        float totalPrice = Float.parseFloat(product.getPrice())*product.getCount();
        holder.product_total.setText("¥"+totalPrice);
    }

    //1 第一步
    @Override
    public int getItemCount() {
        return productList.size();
    }

    //构建布局
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView product_img;
        TextView product_price,product_name,product_count,product_total;
        public ViewHolder(View itemView) {
            super(itemView);

            product_img = itemView.findViewById(R.id.product_img);
            product_name = itemView.findViewById(R.id.product_name);
            product_count = itemView.findViewById(R.id.product_count);
            product_price = itemView.findViewById(R.id.product_price);
            product_total = itemView.findViewById(R.id.product_total);
        }
    }
}
