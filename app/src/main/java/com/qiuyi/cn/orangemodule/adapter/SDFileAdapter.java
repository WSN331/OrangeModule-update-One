package com.qiuyi.cn.orangemodule.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.util.FileManager.FileUtils;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.FileBean;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2018/3/27.
 */
//适配器
public class SDFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener,View.OnLongClickListener{

    private Context context;
    private List<File> fileList;


    private SD_OnItemClick itemClick;

    //存储所有选中的位置
    private boolean[] flag;
    //判断当前checkBox是否显示
    private boolean isShowCheckBox = false;

    public boolean[] getFlag() {
        return flag;
    }

    public void setFlag(boolean[] flag) {
        this.flag = flag;
    }

    public boolean isShowCheckBox() {
        return isShowCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        isShowCheckBox = showCheckBox;
    }

    //选中全部
    public void selectAll(){
        for(int i=0;i<fileList.size();i++){
            flag[i] = true;
        }
        notifyDataSetChanged();
    }

    //取消全选
    public void noSelect(){
        for (int i=0;i<fileList.size();i++){
            flag[i] = false;
        }
        notifyDataSetChanged();
    }

    //页面刷新
    public void ReFresh(){
        flag = new boolean[fileList.size()];
        notifyDataSetChanged();
    }

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

    @Override
    public boolean onLongClick(View view) {
        if(itemClick!=null){
            itemClick.onItemLongClick(view, (int) view.getTag());
        }
        return false;
    }


    public SDFileAdapter(Context context,List<File> fileList) {
        this.fileList = fileList;
        this.context = context;

        flag = new boolean[fileList.size()];
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.adapter_allfileshow,null);
        RecyclerView.ViewHolder viewholder = new SDFileViewHOlder(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        SDFileViewHOlder mholder = (SDFileViewHOlder) holder;

        mholder.onBind(fileList.get(position));


        //checkBox显示
        if(isShowCheckBox){
            mholder.cb_item.setVisibility(View.VISIBLE);
        }else{
            mholder.cb_item.setVisibility(View.INVISIBLE);
        }
        mholder.cb_item.setOnCheckedChangeListener(null);
        mholder.cb_item.setChecked(flag[position]);
        mholder.cb_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                flag[position] = b;
            }
        });

        mholder.itemView.setTag(position);
    }



    @Override
    public int getItemCount() {
        return fileList.size();
    }


    class SDFileViewHOlder extends RecyclerView.ViewHolder{

        TextView tv_filename;
        ImageView iv_file;
        CheckBox cb_item;

        public SDFileViewHOlder(View itemView) {
            super(itemView);
            tv_filename = itemView.findViewById(R.id.tv_filename);
            iv_file = itemView.findViewById(R.id.iv_file);
            cb_item = itemView.findViewById(R.id.cb_item);
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
