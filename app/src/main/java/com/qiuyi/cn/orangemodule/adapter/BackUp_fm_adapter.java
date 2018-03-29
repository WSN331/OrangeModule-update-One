package com.qiuyi.cn.orangemodule.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.bean.MyItemFile;
import com.qiuyi.cn.orangemodule.fragment.BackUpFragment;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Administrator on 2018/3/18.
 * 备份
 */
public class BackUp_fm_adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private List<MyItemFile> listFiles;

    public BackUp_fm_adapter(Context context, List<MyItemFile> listFiles){
        this.listFiles = listFiles;
        this.context = context;
    }

    //2
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.fragment_backup_adapter,null);

        return new itemViewHolder(view);
    }

    //3
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MyItemFile item  = listFiles.get(position);
        if (item!=null){
            String name = item.getName();
            String size = item.getSize();
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
        private CheckBox checkBox;
        public itemViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            fileName = itemView.findViewById(R.id.fileName);
            fileSize = itemView.findViewById(R.id.fileSize);
            fileName = itemView.findViewById(R.id.checkBox);
        }

    }
}
