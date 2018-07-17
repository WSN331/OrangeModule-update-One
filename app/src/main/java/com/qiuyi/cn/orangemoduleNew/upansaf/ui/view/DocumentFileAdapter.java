package com.qiuyi.cn.orangemoduleNew.upansaf.ui.view;

import android.content.Context;
import android.support.v4.provider.DocumentFile;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qiuyi.cn.orangemoduleNew.R;
import com.qiuyi.cn.orangemoduleNew.upansaf.usb.FileUtil;
import com.qiuyi.cn.orangemoduleNew.util.MyApplication;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 杨健 on 2017/5/10.
 */

public class DocumentFileAdapter extends RecyclerView.Adapter<DocumentFileAdapter.ViewHolder> implements View.OnLongClickListener, View.OnClickListener {

    public Context mContext;
    // 文件列表
    private List<DocumentFile> data;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener = null;
    // 是否以私密空间item打头
    private boolean secretHeader;

    private int checkBoxVisibility;
    private RecyclerView recyclerView;

    Map<Integer, Boolean> checkMap;

    public DocumentFileAdapter(Context context,List<DocumentFile> data) {
        this.data = data;
        checkBoxVisibility = ViewHolder.CHECK_INVISIBILITY;
        checkMap = new HashMap<>();
    }

    /**
     * 设置当前适配的列表视图
     * @param recyclerView 视图
     */
    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    /**
     * 获取复选框点击情况
     * @return 编号——》是否点击  键值对
     */
    public Map<Integer, Boolean> getCheckMap() {
        return checkMap;
    }

    /**
     * 页面刷新
     */
    private void viewRefresh() {
        checkMap = new HashMap<>();
        notifyDataSetChanged();
    }

    /**
     * 改变与判断复选框是否显示
     * @param checkBoxVisibility 复选框是否显示的编号
     * @return 状态是否与传入值相同
     */
    public boolean changeCheckBoxVisibility(int checkBoxVisibility) {
        if (this.checkBoxVisibility == checkBoxVisibility) {
            return true;
        }
        checkMap = new HashMap<>();
        this.checkBoxVisibility = checkBoxVisibility;
        viewRefresh();
        return false;
    }

    /**
     * 设置是否为“私有空间”栏目打头
     * @param secretHeader 是否
     */
    public void setSecretHeader(boolean secretHeader) {
        this.secretHeader = secretHeader;
        if (secretHeader) {
            data.add(null);
        }
    }

    /**
     * 移除栏目数据
     * @param position 位置
     */
    public void removeData(int position) {
        data.remove(position);
        viewRefresh();
    }

    /**
     * 添加栏目数据
     * @param file 文件
     */
    public void addData(DocumentFile file){
        if (secretHeader) {
            data.add(data.size()-1, file);
        } else {
            data.add(file);
        }
        viewRefresh();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saf_file_item_layout, parent, false);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

            DocumentFile file = data.get(position);
            if (file == null) {
                return;
            }
            holder.onBind(file);

            holder.itemView.setTag(position);

            holder.setCheckBoxVisibility(checkBoxVisibility);

            holder.check_file.setChecked(checkMap.get(position)!=null && checkMap.get(position));

            holder.check_file.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    View view = (View) buttonView.getParent();
                    checkMap.put((int)view.getTag(), isChecked);
                    if (onRecyclerViewItemClickListener != null) {
                        onRecyclerViewItemClickListener.onItemCheck(view, (int) view.getTag(), isChecked);
                    }
                }
            });
        /*} else {
            holder.setCheckBoxVisibility(ViewHolder.CHECK_INVISIBILITY);
            holder.iv_file.setImageResource(R.drawable.secret_space);
            holder.tv_file.setText("私密空间");
            holder.itemView.setTag(0);
        }*/
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    @Override
    public boolean onLongClick(View v) {
        changeCheckBoxVisibility(ViewHolder.CHECK_VISIBILITY);
        checkView(v);
        if (onRecyclerViewItemClickListener != null) {
            onRecyclerViewItemClickListener.onItemLongClick(v, (int) v.getTag());
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (onRecyclerViewItemClickListener != null) {
            if (checkBoxVisibility == ViewHolder.CHECK_INVISIBILITY) {
                onRecyclerViewItemClickListener.onItemClick(v, (int) v.getTag());
            } else {
                checkView(v);
            }
        }
    }

    /**
     * 选中（取消）某个view的复选框
     * @param v view
     */
    private void checkView(View v) {
        ViewHolder viewHolder = getViewHolder(v);
        if(viewHolder != null) {
            viewHolder.check();
        }
    }

    /**
     * 选中所有View的复选框
     */
    public void checkAllView(){
        for(int i=0;i<data.size();i++){
            checkMap.put(i,true);
        }
        notifyDataSetChanged();
    }

    /**
     * 取消全选
     */
    public void noCheckAllView(){
        checkMap.clear();
        notifyDataSetChanged();
    }

    /**
     * 获取某个view对应的viewHolder
     * @param view view
     * @return viewHolder
     */
    private ViewHolder getViewHolder(View view) {
        if (recyclerView != null) {
            return (ViewHolder) recyclerView.getChildViewHolder(view);
        }
        return null;
    }

    /**
     * 单个栏目的viewHolder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public static final int CHECK_VISIBILITY = View.VISIBLE;
        public static final int CHECK_INVISIBILITY = View.INVISIBLE;

        private ImageView iv_file;
        private TextView tv_file;
        private CheckBox check_file;

        private TextView fileSize;
        private TextView tv_time;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            fileSize = (TextView) itemView.findViewById(R.id.tv_size);
            iv_file = (ImageView) itemView.findViewById(R.id.file_icon);
            tv_file = (TextView) itemView.findViewById(R.id.file_name);
            check_file = (CheckBox) itemView.findViewById(R.id.file_check);
        }

        public void setCheckBoxVisibility(int visibility) {
            check_file.setVisibility(visibility);
        }

        public boolean check() {
            boolean b = check_file.isChecked();
            b = !b;
            check_file.setChecked(b);
            return b;
        }

        //显示绑定
        public void onBind(DocumentFile file){

            String name = file.getName();
            if(name==null){
                return;
            }
            String end = name.substring(name.lastIndexOf(".") + 1, name.length()).toLowerCase();

            if (file.isDirectory()) {
                Glide.with(MyApplication.getContext())
                        .load(R.drawable.folder)
                        .into(iv_file);
                //iv_file.setImageResource(R.drawable.folder);
                fileSize.setText(sizeToMb(getDirectorySize(file)));
            } else {
                if(FileUtil.getFileImage(end) == R.drawable.jpg){
                    Glide.with(MyApplication.getContext())
                            .load(file.getUri())
                            .override(48,48)
                            .centerCrop()
                            .into(iv_file);
                }else{
                    Glide.with(MyApplication.getContext())
                            .load(FileUtil.getFileImage(end))
                            .into(iv_file);
                }
                fileSize.setText(sizeToMb(file.length()));
            }
            tv_file.setText(name);
            tv_time.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(file.lastModified())));
        }

    }

    /**
     * 列表点击事件接口
     */
    public interface OnRecyclerViewItemClickListener {
        /**
         * 单击事件的回调
         * @param view 选中的view
         * @param position 选中的序号
         */
        void onItemClick(View view, int position);

        /**
         * 长按事件回调
         * @param view 选中的view
         * @param position 选中的序号
         */
        void onItemLongClick(View view, int position);

        /**
         * 当复选框被选中（或取消）时候需要触发的事件（不包含复选框的选中动作和点击存放表的改变）
         * @param view 选中栏目的view
         * @param position 选中栏目的标号
         * @param check 是否被选中
         */
        void onItemCheck(View view, int position, boolean check);
    }

    /**
     * 设置列表的点击事件
     * @param onRecyclerViewItemClickListener
     */
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }


    //长度转化
    private static String sizeToMb(long size){
        if(size>1024*1024){
            return Math.floor((double)(1.0*size/1024/1024)*100)/100+"mb";
        }else if(size < 1024){
            return size+"b";
        }else{
            return Math.floor((double)(1.0*size/1024)*100)/100+"kb";
        }
    }

    //获取文件夹长度
    private static long getDirectorySize(DocumentFile file){
        long blockSize = 0;

        for(DocumentFile docFile : file.listFiles()){
            if(docFile.isDirectory()){
                blockSize += getDirectorySize(docFile);
            }else{
                blockSize += docFile.length();
            }
        }
        return blockSize;
    }

}
