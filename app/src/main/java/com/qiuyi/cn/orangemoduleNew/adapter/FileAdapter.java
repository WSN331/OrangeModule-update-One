package com.qiuyi.cn.orangemoduleNew.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/1/5.
 *
 * U盘模块的Adapter
 */
public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder> implements View.OnClickListener,View.OnLongClickListener{

    Context context;

    //要展示的文件
    private List<File> fileList;
    private OnItemClick itemClick;

    public FileAdapter(Context context, List<File> fileList) {
        this.context = context;
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

            long blockSize = 0;

            if(file.isDirectory()){
                holder.iv_icon.setImageResource(R.drawable.folder);

                try {
                    blockSize = getFileSizes(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else{
                holder.iv_icon.setImageResource(FileUtil.getFileIcon(end));

                try {
                    blockSize = getFileSize(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            holder.tv_name.setText(name);

            holder.tv_size.setText(Formatter.formatFileSize(context,blockSize));

            holder.tv_time.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(file.lastModified())));

            holder.itemView.setTag(position);
        }
    }


    /**
     * 获取指定文件大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }


    //1
    @Override
    public int getItemCount() {
        return fileList!=null?fileList.size():0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_icon;
        TextView tv_name,tv_size,tv_time;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_icon = itemView.findViewById(R.id.iv_icon);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_size = itemView.findViewById(R.id.tv_size);
            tv_time = itemView.findViewById(R.id.tv_time);
        }

    }
}
