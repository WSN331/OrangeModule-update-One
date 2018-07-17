package com.qiuyi.cn.orangemoduleNew.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.oragee.banners.BannerView;
import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.activity.GoodsActivity;
import com.qiuyi.cn.orangemoduleNew.activity.OtherActivity;
import com.qiuyi.cn.orangemoduleNew.bean.Product;
import com.qiuyi.cn.orangemoduleNew.util.Constant;

import java.util.List;

/**
 * Created by Administrator on 2018/1/2.
 *
 * 推荐模块适配器
 */
public class RecommendPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    //context
    private Context context;
    //产品信息
    private List<Product> productList;

    public RecommendPagerAdapter(Context context, List<Product> productList){
        this.context = context;
        this.productList = productList;
    }


    public onItemClickListener myListener;

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            //第二个模块的点击
            case R.id.one:
            case R.id.two:
            case R.id.three:
            case R.id.four:
                Intent intent1 = new Intent(context, OtherActivity.class);
                context.startActivity(intent1);
                break;
            case R.id.backup_memory:
                Intent intent2 = new Intent(context,GoodsActivity.class);
                intent2.putExtra("title", Constant.module_names[0]);
                intent2.putExtra("price",Constant.module_prices[0]);
                intent2.putExtra("image",Constant.module_images[0]);
                context.startActivity(intent2);
                break;
            case R.id.speaker:
                Intent intent3 = new Intent(context,GoodsActivity.class);
                intent3.putExtra("title",Constant.module_names[1]);
                intent3.putExtra("price",Constant.module_prices[1]);
                intent3.putExtra("image",Constant.module_images[1]);
                context.startActivity(intent3);
                break;
            case R.id.temp_hum_mod:
                Intent intent4 = new Intent(context,GoodsActivity.class);
                intent4.putExtra("title",Constant.module_names[3]);
                intent4.putExtra("price",Constant.module_prices[3]);
                intent4.putExtra("image",Constant.module_images[3]);
                context.startActivity(intent4);
                break;
            case R.id.led:
                Intent intent5 = new Intent(context,GoodsActivity.class);
                intent5.putExtra("title",Constant.module_names[4]);
                intent5.putExtra("price",Constant.module_prices[4]);
                intent5.putExtra("image",Constant.module_images[4]);
                context.startActivity(intent5);
                break;
            case R.id.laser_pointer:
                Intent intent6 = new Intent(context,GoodsActivity.class);
                intent6.putExtra("title",Constant.module_names[2]);
                intent6.putExtra("price",Constant.module_prices[2]);
                intent6.putExtra("image",Constant.module_images[2]);
                context.startActivity(intent6);
                break;
            default:break;
        }
    }

    //点击事件
    public interface onItemClickListener{
        void OnItemClick(gridViewHolder view, int position, int type);
        void OnItemLongClick(View view, int position, int type);
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
            //第一种轮播模块布局
            view = View.inflate(parent.getContext(),R.layout.pager_headbanner,null);
            mViewHolder = new BannerViewHolder(view);
        }
        else if(viewType == 1){
            //第二种类型是类别栏
            view = View.inflate(parent.getContext(),R.layout.pager_typelist,null);
            mViewHolder = new typeViewHolder(view);
        }else if(viewType == 2){
            //第三种类型是错乱显示商品栏
            view = View.inflate(parent.getContext(),R.layout.pager_hotrecommend,null);
            mViewHolder = new shopViewHolder(view);
        }else if(viewType == 3){
            //第四种类型是图片标题
            view = View.inflate(parent.getContext(),R.layout.pager_headimage,null);
            mViewHolder = new typeImageViewHolder(view);
        }else if(viewType == 4){
            //第五种类型是GradView中显示的商品
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
            //轮播图片显示
            BannerViewHolder bholder = (BannerViewHolder) holder;
            bholder.myBanView.setViewList(productList.get(position).getViewList());
            bholder.myBanView.startLoop(true);
        } else if (getItemViewType(position)==1){
            //这里是title布局
            typeViewHolder tholder = (typeViewHolder) holder;
            //显示标题
            tholder.one.setOnClickListener(this);
        }else if(getItemViewType(position)==2){
            shopViewHolder sholder = (shopViewHolder) holder;
            sholder.memory.setOnClickListener(this);
            sholder.speaker.setOnClickListener(this);
            sholder.template.setOnClickListener(this);
            sholder.led.setOnClickListener(this);
            sholder.pint.setOnClickListener(this);
        }
        else if(getItemViewType(position)==3){
            typeImageViewHolder tiholder = (typeImageViewHolder) holder;
            tiholder.showView.setImageResource(R.drawable.myhome);
        }
        else if(getItemViewType(position)==4){
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
                    myListener.OnItemClick(gholder,position,4);
                }
            });
        }

    }


    /*
    * 第一个 轮播模块布局
    * */
    class BannerViewHolder extends RecyclerView.ViewHolder{
        BannerView myBanView;
        public BannerViewHolder(View itemView) {
            super(itemView);
            myBanView = itemView.findViewById(R.id.bannerView);
        }
    }

    /*
    * 第二个类别栏布局
    * */
    class typeViewHolder extends RecyclerView.ViewHolder{
        //上新，推荐，抢购，更多
        LinearLayout one,two,three,four;
        public typeViewHolder(View itemView) {
            super(itemView);
            one =  itemView.findViewById(R.id.one);
            two =  itemView.findViewById(R.id.two);
            three =  itemView.findViewById(R.id.three);
            four =  itemView.findViewById(R.id.four);
        }
    }

    /*
    * 第三个 商品栏模式布局
    * */
    private class shopViewHolder extends RecyclerView.ViewHolder {
        //存储设备,外接扬声器,温度传感器,闪光灯,激光笔
        FrameLayout memory,speaker,template,led,pint;
        public shopViewHolder(View view) {
            super(view);
            memory = view.findViewById(R.id.backup_memory);
            speaker = view.findViewById(R.id.speaker);
            template = view.findViewById(R.id.temp_hum_mod);
            led = view.findViewById(R.id.led);
            pint = view.findViewById(R.id.laser_pointer);
        }
    }

    /*
    * 第四个 图片标题布局
    * */
    private class typeImageViewHolder extends RecyclerView.ViewHolder {
        ImageView showView;
        public typeImageViewHolder(View view) {
            super(view);
            showView = view.findViewById(R.id.showView);
        }
    }

    /*
    * 第五个 gridView的布局
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
