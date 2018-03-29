package com.qiuyi.cn.orangemodule.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.bean.LeftItem;

import java.util.List;

/**
 * Created by Administrator on 2018/3/13.
 * ListViewAdapter
 */
public class LeftListViewAdapter extends BaseAdapter {

    private Context context;
    private List<LeftItem> listItem;

    public LeftListViewAdapter(Context context,List<LeftItem> listItem){
        this.context = context;
        this.listItem = listItem;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int i) {
        return listItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View contentView, ViewGroup viewGroup) {

        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;

        LeftItem item = listItem.get(position);
        if(position == 0){
            if(contentView == null){
                contentView = LayoutInflater.from(context).inflate(R.layout.listview1_layout,null);
                holder1 = new ViewHolder1();

                contentView.setTag(holder1);
            }else{
                holder1 = (ViewHolder1) contentView.getTag();
            }
        }else{
            if(contentView == null){
                contentView = LayoutInflater.from(context).inflate(R.layout.listview2_layout,null);
                holder2 = new ViewHolder2();
                holder2.myImgShow = contentView.findViewById(R.id.img_show);
                holder2.myTextShow = contentView.findViewById(R.id.tv_show);
                contentView.setTag(holder2);
            }else{
                holder2 = (ViewHolder2) contentView.getTag();
            }
            holder2.myImgShow.setImageResource(item.getImageView());
            holder2.myTextShow.setText(item.getItemText());
        }
        return contentView;
    }


    public class ViewHolder2 {
        ImageView myImgShow;
        TextView myTextShow;
    }

    public class ViewHolder1 {
    }
}
