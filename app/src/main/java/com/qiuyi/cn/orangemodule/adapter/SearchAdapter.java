package com.qiuyi.cn.orangemodule.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.util.FileManager.FileUtils;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.FileBean;
import com.qiuyi.cn.orangemodule.util.FileUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/3/21.
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener,View.OnLongClickListener{

    private Context context;
    private List<File> listFile;

    //存储所有选中的位置
    private boolean[] flag;
    //判断当前checkBox是否显示
    private boolean isShowCheckBox = false;

    public boolean isShowCheckBox() {
        return isShowCheckBox;
    }

    public void setShowCheckBox(boolean showCheckBox) {
        isShowCheckBox = showCheckBox;
    }

    public boolean[] getFlag() {
        return flag;
    }

    public void setFlag(boolean[] flag) {
        this.flag = flag;
    }

    //选中全部
    public void selectAll(){
        for(int i=0;i<listFile.size();i++){
            flag[i] = true;
        }
        notifyDataSetChanged();
    }

    //取消全选
    public void noSelect(){
        for (int i=0;i<listFile.size();i++){
            flag[i] = false;
        }
        notifyDataSetChanged();
    }

    //页面刷新
    public void ReFresh(){
        flag = new boolean[listFile.size()];
        notifyDataSetChanged();
    }

    private FileItemClick fileItemClick;

    public interface FileItemClick{
        void openFile(View view, int position);
        void onItemLongClick(View view,int position);
    }

    public void setOnFileItemClick(FileItemClick myClick){
        this.fileItemClick = myClick;
    }

    @Override
    public void onClick(View view) {
        if(fileItemClick!=null){
            fileItemClick.openFile(view, (int) view.getTag());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(fileItemClick!=null){
            fileItemClick.onItemLongClick(view, (int) view.getTag());
        }
        return false;
    }


    public SearchAdapter(Context context, List<File> listFile, GridLayoutManager layoutManager){
        this.context = context;
        this.listFile = listFile;

        flag = new boolean[listFile.size()];

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(getItemViewType(position)==0 || getItemViewType(position) == 2 || getItemViewType(position) == 3){
                    return 4;
                }else{
                    return 1;
                }
            }
        });
    }


    //3 创建布局
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder mViewHolder = null;
        if(viewType == 1){
            //文件展示部分 图片文件
            view = View.inflate(context,R.layout.pager_recently_adapter_image_layout,null);
            mViewHolder = new myImageView(view);
        }else{
            //文本文件，压缩包
            view = View.inflate(context,R.layout.pager_recently_adapter_file_layout,null);
            mViewHolder = new myFileView(view);
        }
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return mViewHolder;
    }

    //4 数据填充判断
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        File info = listFile.get(position);

        if(getItemViewType(position)==1){
            //图片
            myImageView myholder = (myImageView) holder;
            myholder.onBind(info);

            //checkBox显示
            if(isShowCheckBox){
                myholder.cb_img.setVisibility(View.VISIBLE);
            }else{
                myholder.cb_img.setVisibility(View.INVISIBLE);
            }
            myholder.cb_img.setOnCheckedChangeListener(null);
            myholder.cb_img.setChecked(flag[position]);
            myholder.cb_img.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    flag[position] = b;
                }
            });


            myholder.itemView.setTag(position);
        }
        if(getItemViewType(position)==2){
            //文件
            myFileView myholder = (myFileView) holder;
            myholder.onBind(info);

            //checkBox显示
            if(isShowCheckBox){
                myholder.cb_item.setVisibility(View.VISIBLE);
            }else{
                myholder.cb_item.setVisibility(View.INVISIBLE);
            }
            myholder.cb_item.setOnCheckedChangeListener(null);
            myholder.cb_item.setChecked(flag[position]);
            myholder.cb_item.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    flag[position] = b;
                }
            });

            myholder.itemView.setTag(position);
        }

    }

    //1
    @Override
    public int getItemCount() {
        return listFile.size();
    }

    //2
    @Override
    public int getItemViewType(int position) {
        File file = listFile.get(position);
        if(FileUtils.getFileType(file.getPath())==FileUtils.TYPE_IMG){
            return 1;
        }else{
            return 2;
        }
    }


    //图片存放
    class myImageView extends RecyclerView.ViewHolder{

        private ImageView myImageView;
        private CheckBox cb_img;

        public myImageView(View itemView) {
            super(itemView);
            myImageView = itemView.findViewById(R.id.myImageView);
            cb_img = itemView.findViewById(R.id.cb_img);
        }

        public void onBind(File info){
            //info.getPath()
            Glide.with(context)
                    .load(info.getPath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(myImageView);
        }
    }

    //文件存放
    class myFileView extends RecyclerView.ViewHolder{

        private TextView fileName,fileSize,fileDate;
        private ImageView imageView;
        private CheckBox cb_item;

        public myFileView(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileName);
            fileSize = itemView.findViewById(R.id.fileSize);
            fileDate = itemView.findViewById(R.id.fileDate);
            imageView = itemView.findViewById(R.id.imageView);

            cb_item = itemView.findViewById(R.id.cb_item);
        }

        public void onBind(File info){

            fileName.setText(info.getName());
            fileDate.setText(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(info.lastModified()));
            try {
                if(info.isDirectory()){
                    fileSize.setText(Formatter.formatFileSize(context, FileUtils.getFileSizes(info)));
                }else{
                    fileSize.setText(Formatter.formatFileSize(context, FileUtils.getFileSize(info)));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            if(info.isDirectory()){
                Glide.with(context)
                        .load(R.drawable.folder)
                        .override(120,120)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            }else{
                Glide.with(context)
                        .load(FileUtils.getFileIconByPath(info.getPath()))
                        .override(120,120)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);
            }

        }
    }


}
