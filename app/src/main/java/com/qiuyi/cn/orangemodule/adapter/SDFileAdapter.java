package com.qiuyi.cn.orangemodule.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.util.FileManager.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2018/3/27.
 */
//适配器
public class SDFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private Context context;
    private List<File> fileList;


    public SDFileAdapter(Context context,List<File> fileList) {
        this.fileList = fileList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.adapter_allfileshow,null);
        RecyclerView.ViewHolder viewholder = new SDFileViewHOlder(view);
        view.setOnClickListener(this);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SDFileViewHOlder mholder = (SDFileViewHOlder) holder;

        mholder.onBind(fileList.get(position));

        mholder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }


    private SD_OnItemClick itemClick;

    public void setOnItemClick(SD_OnItemClick itemClick){
        this.itemClick = itemClick;
    }
    public interface SD_OnItemClick{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    //下面是点击事件
    @Override
    public void onClick(View view) {
        if(itemClick!=null){
            itemClick.onItemClick(view, (int) view.getTag());
        }
    }

/*    @Override
    public boolean onLongClick(View view) {
        if(itemClick!=null){
            itemClick.onItemLongClick(view, (int) view.getTag());
            return true;
        }
        return false;
    }*/


    class SDFileViewHOlder extends RecyclerView.ViewHolder{

        TextView tv_filename;
        ImageView iv_file;

        public SDFileViewHOlder(View itemView) {
            super(itemView);
            tv_filename = itemView.findViewById(R.id.tv_filename);
            iv_file = itemView.findViewById(R.id.iv_file);
        }

        public void onBind(File file){
            tv_filename.setText(file.getName());

            if(file.isDirectory()){
                Glide.with(context)
                        .load(R.drawable.folder)
                        .into(iv_file);
            }else{
                Glide.with(context)
                        .load(FileUtils.getFileIconByPath(file.getPath()))
                        .into(iv_file);
            }
        }
    }

}
