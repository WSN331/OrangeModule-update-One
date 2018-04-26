package com.qiuyi.cn.orangemodule.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.bean.MyItemFile;
import com.qiuyi.cn.orangemodule.fragment.BackUpFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/18.
 * 备份
 */
public class BackUp_fm_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener,View.OnLongClickListener{

    private Context context;
    private List<MyItemFile> listFiles;
    private BackupOnClick itemClick;

    private boolean isShowCheckBox = false;
    private boolean[] flag;

    public BackUp_fm_adapter(Context context, List<MyItemFile> listFiles){
        this.listFiles = listFiles;
        this.context = context;
    }

    public interface BackupOnClick{
        void onBackItemClick(View view,int position);
        void onBackLongItemClick(View view,int position);
    }

    public void setOnBackUpClickListener(BackupOnClick backupOnClick){
        this.itemClick = backupOnClick;
    }

    @Override
    public void onClick(View view) {
        if(itemClick!=null){
            itemClick.onBackItemClick(view, (Integer) view.getTag());
        }
    }
    @Override
    public boolean onLongClick(View view) {
        if(itemClick!=null){
            itemClick.onBackLongItemClick(view, (Integer) view.getTag());
            return true;
        }
        return false;
    }


    //2
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.fragment_backup_adapter,null);

        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return new itemViewHolder(view);
    }

    public void setShowCheckBox(boolean showCheckBox) {
        this.isShowCheckBox = showCheckBox;
    }

    public boolean[] getFlag() {
        return flag;
    }

    //3
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        MyItemFile item  = listFiles.get(position);
        if (item!=null){
            itemViewHolder itemViewHolder = (BackUp_fm_adapter.itemViewHolder) holder;

            itemViewHolder.onBind(item);

            flag = new boolean[listFiles.size()];

            //itemViewHolder.checkBox.setOnCheckedChangeListener(null);//先设置一次CheckBox的选中监听器，传入参数null

            itemViewHolder.checkBox.setChecked(flag[position]);

            if(isShowCheckBox){
                itemViewHolder.checkBox.setVisibility(View.VISIBLE);
            }else{
                itemViewHolder.checkBox.setVisibility(View.INVISIBLE);
            }

            itemViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    flag[position] = b;
                }
            });


            itemViewHolder.itemView.setTag(position);
        }
    }

    //1
    @Override
    public int getItemCount() {
        return listFiles.size();
    }

    class itemViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageView;
        private TextView fileName,fileSize;
        public CheckBox checkBox;
        public itemViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            fileName = itemView.findViewById(R.id.fileName);
            fileSize = itemView.findViewById(R.id.fileSize);
            checkBox = itemView.findViewById(R.id.checkBox);
        }

        public void onBind(MyItemFile itemFile){
            Glide.with(context)
                    .load(itemFile.getIcon())
                    .into(imageView);
            fileName.setText(itemFile.getName());
            fileSize.setText(itemFile.getSize());
        }
    }
}
