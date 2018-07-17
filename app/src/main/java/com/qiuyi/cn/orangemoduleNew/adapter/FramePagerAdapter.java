package com.qiuyi.cn.orangemoduleNew.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.bean.Product;

import java.util.List;

/**
 * Created by Administrator on 2018/1/2.
 *
 * 框架模块适配器
 */
public class FramePagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //产品信息
    private List<Product> productList;

    public FramePagerAdapter(List<Product> productList){
        this.productList = productList;
    }


    public onItemClickListener myListener;
    //点击事件
    public interface onItemClickListener{
        void OnItemClick(gridViewHolder view, int position, int type);
        void OnItemLongClick(View view,int position,int type);
    }
    public void setOnItemClickListener(onItemClickListener mOnItemClickListener){
        this.myListener = mOnItemClickListener;
    }


    //3 创建布局
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder mViewHolder = null;
        if(viewType==0){
            view = View.inflate(parent.getContext(),R.layout.headimage,null);
            mViewHolder = new imageViewHolder(view);
        }
        else if(viewType == 1){
            //第二种类型是标题栏
            view = View.inflate(parent.getContext(),R.layout.headtitle,null);
            mViewHolder = new titleViewHolder(view);
        }else if(viewType == 2){
            //第三种类型是GradView中显示的商品
            view = View.inflate(parent.getContext(),R.layout.gridcontent,null);
            mViewHolder = new gridViewHolder(view);
        }
        return mViewHolder;
    }


    //4 数据填充
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //调用getType函数来判断
        if(getItemViewType(position)==0){
            //头图片显示
            imageViewHolder iholder = (imageViewHolder) holder;
            iholder.headImage_img.setImageResource(productList.get(position).getImage());
        }
        else if (getItemViewType(position)==1){
            //这里是title布局
            titleViewHolder tholder = (titleViewHolder) holder;
            //显示标题
            tholder.myTitle.setText(productList.get(position).getType());
        }else if(getItemViewType(position)==2){
            //grid布局
            final gridViewHolder gholder = (gridViewHolder) holder;
            //显示数据
            gholder.produceView.setImageResource(productList.get(position).getImage());
            gholder.cardView.setImageResource(R.drawable.card);
            gholder.produceName.setText(productList.get(position).getName());
            gholder.producePrice.setText(productList.get(position).getPrice());

            gholder.produceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //这个是点击事件
                    myListener.OnItemClick(gholder,position,2);
                }
            });
        }

    }


    /*
    * 构建布局，这里是TitleView的布局
    * */
    class titleViewHolder extends RecyclerView.ViewHolder{
        TextView myTitle;
        public titleViewHolder(View itemView) {
            super(itemView);
            myTitle = itemView.findViewById(R.id.headtitle_title);
        }
    }
    /*
    * gridView的布局
    * */
    public class gridViewHolder extends RecyclerView.ViewHolder{
        ImageView produceView,cardView;
        TextView produceName,producePrice;
        public gridViewHolder(View itemView) {
            super(itemView);

            produceView = itemView.findViewById(R.id.grid_img);
            cardView = itemView.findViewById(R.id.grid_card);
            produceName = itemView.findViewById(R.id.grid_name);
            producePrice = itemView.findViewById(R.id.grid_price);
        }
    }
    /*
    * imageView的布局
    * */
    class imageViewHolder extends RecyclerView.ViewHolder{
        ImageView headImage_img;
        public imageViewHolder(View itemView) {
            super(itemView);
            headImage_img = itemView.findViewById(R.id.headImage_img);
        }
    }


    //1 首先调用这里
    @Override
    public int getItemCount() {
        return productList.size();
    }

    //2 然后判断类型
    @Override
    public int getItemViewType(int position) {
        return productList.get(position).getMyflag();
    }
}
