package com.qiuyi.cn.orangemodule.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.util.FileUtil;

import java.io.File;
import java.util.List;

import butterknife.OnClick;

/**
 * Created by Administrator on 2018/1/5.
 *
 * U盘模块的Adapter
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> implements View.OnClickListener,View.OnLongClickListener{

    //要展示的文件
    private List<File> fileList;
    private OnItemClick itemClick;

    public FileAdapter(List<File> fileList){
        this.fileList = fileList;
    }

    //下面是点击事件
    @Override
    public void onClick(View view) {
        if(itemClick!=null){
            itemClick.onItemClick(view, (int) view.getTag());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(itemClick!=null){
            itemClick.onItemLongClick(view, (int) view.getTag());
            return true;
        }
        return false;
    }

    public interface OnItemClick{
        void onItemClick(View view,int position);
        void onItemLongClick(View view,int position);
    }

    public void setOnItemClick(OnItemClick itemClick){
        this.itemClick = itemClick;
    }


    //2
    @Override
    public FileAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_file,null);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new ViewHolder(view);
    }

    //3
    @Override
    public void onBindViewHolder(FileAdapter.ViewHolder holder, int position) {

        File file = fileList.get(position);
        if(file !=null){
            //得到文件名
            String name = file.getName();
            //得到文件后缀
            String end = name.substring(name.lastIndexOf(".")+1).toLowerCase();
            if(file.isDirectory()){
                holder.iv_icon.setImageResource(R.drawable.folder);
            }else{
                holder.iv_icon.setImageResource(FileUtil.getFileIcon(end));
            }
            holder.tv_name.setText(name);

            holder.itemView.setTag(position);
        }
    }


    //1
    @Override
    public int getItemCount() {
        return fileList!=null?fileList.size():0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_icon;
        TextView tv_name;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_name = itemView.findViewById(R.id.tv_name);
        }

    }
}
