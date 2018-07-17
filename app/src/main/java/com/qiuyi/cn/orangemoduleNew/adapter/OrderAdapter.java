package com.qiuyi.cn.orangemoduleNew.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.bean.Product;

import java.util.List;

/**
 * Created by Administrator on 2018/1/4.
 *
 * 订单模块总布局的adapter,分为头尾两块
 */
public class OrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //两块部分
    private static final int HEADER = 0;
    private static final int FOOTER = 1;
    private int currentType = HEADER;


    private Context context;
    private OrderItemAdapter adapter;
    //要显示的商品数据
    private List<Product> productList;
    public OrderAdapter(Context context, List<Product> productList){
        this.context = context;
        this.productList = productList;
    }


    //3 第三步
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder viewHolder = null;
        if(viewType == HEADER){
            view = View.inflate(parent.getContext(), R.layout.adapter_order,null);
            viewHolder = new orderViewHolder(view);
        }else if(viewType == FOOTER){
            view = View.inflate(parent.getContext(),R.layout.adapter_orderlist,null);
            viewHolder = new orderListViewHolder(view);
        }
        return viewHolder;
    }

    // 4第四步
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //头标题
        if (getItemViewType(position)==HEADER){
            //获取布局View
            orderViewHolder oholder = (orderViewHolder) holder;
        }
        //list部分
        else if(getItemViewType(position)==FOOTER){
            //再次使用Recycler布局
            orderListViewHolder olholder = (orderListViewHolder) holder;
            adapter = new OrderItemAdapter(productList);
            olholder.orderListView.setLayoutManager(new LinearLayoutManager(context));
            olholder.orderListView.setAdapter(adapter);
        }
    }

    //订单地点信息
    class orderViewHolder extends RecyclerView.ViewHolder {

        public orderViewHolder(View view) {
            super(view);
        }

    }
    //订单列表
    class orderListViewHolder extends RecyclerView.ViewHolder{
        RecyclerView orderListView;
        public orderListViewHolder(View view) {
            super(view);
            orderListView = view.findViewById(R.id.recyclerView_orderList);
        }
    }


    //1 第一步
    @Override
    public int getItemCount() {
        return 2;
    }

    //2 第二步
    @Override
    public int getItemViewType(int position) {
        switch(position){
            case HEADER:
                currentType = HEADER;
                break;
            case FOOTER:
                currentType = FOOTER;
                break;
            default:break;
        }
        return currentType;
    }


}
