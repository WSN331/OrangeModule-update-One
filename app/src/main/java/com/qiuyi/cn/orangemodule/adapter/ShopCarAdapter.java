package com.qiuyi.cn.orangemodule.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.bean.Product;

import java.util.List;

/**
 * Created by Yang on 2017/12/19.
 *
 * Function：购物车适配器
 */

public class ShopCarAdapter extends RecyclerView.Adapter<ShopCarAdapter.ViewHolder> {

    private Context context;

    //需要展示的商品数据
    private List<Product> data;

    private OnResfreshListener mOnResfreshListener;

    public ShopCarAdapter(Context context, List<Product> data) {
        this.context = context;
        this.data = data;
    }

    //2 创建布局
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shopcart, parent, false);
        return new ViewHolder(view);
    }

    //3 绑定事件
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Product product = data.get(position);

        if (product != null) {
            holder.iv_module_image.setImageResource(product.getImage());
            holder.tv_module_title.setText(product.getName());
            holder.tv_module_price.setText(product.getPrice());
            holder.tv_module_number.setText(product.getCount() + "");

            //设置按钮的选择或未选择，显示不同的按钮
            if (product.isSelected()) {
                holder.iv_module_select.setImageResource(R.drawable.shopcart_selected);
                holder.iv_shop_select.setImageResource(R.drawable.shopcart_selected);
            } else {
                holder.iv_module_select.setImageResource(R.drawable.shopcart_unselected);
                holder.iv_shop_select.setImageResource(R.drawable.shopcart_unselected);
            }

            //从局部的选择改变全选的效果
            if (mOnResfreshListener != null) {

                boolean isSelected = false;

                for (int i = 0; i < data.size(); i++) {
                    if (!data.get(i).isSelected()) {
                        isSelected = false;
                        break;
                    } else {
                        isSelected = true;
                    }
                }
                //在这里调用外面的方法
                mOnResfreshListener.onResfresh(isSelected);
            }

            //商品数量减少
            holder.iv_module_minus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (product.getCount() > 1) {
                        int count = product.getCount() -1;
                        product.setCount(count);
                        notifyDataSetChanged();
                    }
                }
            });

            //商品数量增加
            holder.iv_module_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count = product.getCount() + 1;
                    product.setCount(count);
                    notifyDataSetChanged();
                }
            });

            //删除商品
            holder.iv_module_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.remove(product);
                    notifyDataSetChanged();
                }
            });

            //默认选中
            holder.iv_module_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (product.isSelected()) {
                        product.setSelected(false);
                        holder.iv_module_select.setImageResource(R.drawable.shopcart_unselected);
                        holder.iv_shop_select.setImageResource(R.drawable.shopcart_unselected);
                    } else {
                        product.setSelected(true);
                        holder.iv_module_select.setImageResource(R.drawable.shopcart_selected);
                        holder.iv_shop_select.setImageResource(R.drawable.shopcart_selected);
                    }
                    notifyDataSetChanged();
                }
            });

            //默认选中
            holder.iv_shop_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (product.isSelected()) {
                        product.setSelected(false);
                        holder.iv_module_select.setImageResource(R.drawable.shopcart_unselected);
                        holder.iv_shop_select.setImageResource(R.drawable.shopcart_unselected);
                    } else {
                        product.setSelected(true);
                        holder.iv_module_select.setImageResource(R.drawable.shopcart_selected);
                        holder.iv_shop_select.setImageResource(R.drawable.shopcart_selected);
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }

    //1 统计数量
    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_module_title;
        private ImageView iv_module_image;
        private ImageView iv_module_select;
        private ImageView iv_shop_select;
        private TextView tv_module_number;
        private TextView tv_module_price;
        private ImageView iv_module_minus;
        private ImageView iv_module_add;
        private ImageView iv_module_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_module_title = (TextView) itemView.findViewById(R.id.tv_item_shopcart_module_name);
            iv_module_image = (ImageView) itemView.findViewById(R.id.iv_item_shopcart__pic);
            iv_module_select = (ImageView) itemView.findViewById(R.id.iv_item_shopcart_module_select);
            iv_shop_select = (ImageView) itemView.findViewById(R.id.iv_item_shopcart_shopselect);
            tv_module_number = (TextView) itemView.findViewById(R.id.et_item_shopcart_module_num);
            tv_module_price = (TextView) itemView.findViewById(R.id.tv_item_shopcart_module_price);
            iv_module_minus = (ImageView) itemView.findViewById(R.id.iv_item_shopcart_module_minus);
            iv_module_add = (ImageView) itemView.findViewById(R.id.iv_item_shopcart_module_add);
            iv_module_delete = (ImageView) itemView.findViewById(R.id.iv_item_shopcart_module_delete);
        }
    }

    //监听全选的接口
    public interface OnResfreshListener{
        void onResfresh(boolean isSelect);
    }

    public void setResfreshListener(OnResfreshListener mOnResfreshListener){
        this.mOnResfreshListener = mOnResfreshListener;
    }
}
