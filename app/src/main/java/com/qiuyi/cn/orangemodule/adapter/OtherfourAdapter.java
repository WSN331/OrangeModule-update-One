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
 */
public class OtherfourAdapter extends RecyclerView.Adapter<OtherfourAdapter.ViewHolder> {

    //产品信息
    private List<Product> productList;

    public OtherfourAdapter(List<Product> productList){
        this.productList = productList;
    }

    public onItemClickListener myListener;
    //点击事件
    public interface onItemClickListener{
        void OnItemClick(ViewHolder view, int position, int type);
        void OnItemLongClick(View view,int position,int type);
    }
    public void setOnItemClickListener(onItemClickListener mOnItemClickListener){
        this.myListener = mOnItemClickListener;
    }


    //2
    @Override
    public OtherfourAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //第三种类型是GradView中显示的商品
        View view = View.inflate(parent.getContext(), R.layout.gridcontent,null);
        OtherfourAdapter.ViewHolder mViewHolder = new ViewHolder(view);
        return mViewHolder;
    }

    //3
    @Override
    public void onBindViewHolder(final OtherfourAdapter.ViewHolder holder, final int position) {
        //显示数据
        holder.produceView.setImageResource(productList.get(position).getImage());
        holder.cardView.setImageResource(R.drawable.card);
        holder.produceName.setText(productList.get(position).getName());
        holder.producePrice.setText(productList.get(position).getPrice());

        holder.produceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //这个是点击事件
                myListener.OnItemClick(holder,position,2);
            }
        });
    }

    // 1
    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView produceView,cardView;
        TextView produceName,producePrice;
        public ViewHolder(View itemView) {
            super(itemView);
            produceView = itemView.findViewById(R.id.grid_img);
            cardView = itemView.findViewById(R.id.grid_card);
            produceName = itemView.findViewById(R.id.grid_name);
            producePrice = itemView.findViewById(R.id.grid_price);
        }
    }
}
