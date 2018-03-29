package com.qiuyi.cn.orangemodule.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.qiuyi.cn.orangemodule.R;
import com.qiuyi.cn.orangemodule.bean.FileInfo;
import com.qiuyi.cn.orangemodule.util.Constant;
import com.qiuyi.cn.orangemodule.util.FileManager.ConstantValue;
import com.qiuyi.cn.orangemodule.util.FileManager.FileUtils;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.FileBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.ImageBean;
import com.qiuyi.cn.orangemodule.util.FileUtil;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/3/14.
 * 最近模块的Adapter
 */
public class RecentlyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{

    private Context context;
    private List<FileBean> listFile;
    private GridLayoutManager myGridmanager;

    //有几个标题
    private int textItemCount;
    //每个时间段的文件个数
    private List<Integer> sortList;

    //统计不同时间段的文件
    private Map<Integer,List<FileBean>> sortTimeList;

    //所有ImageBean同一
    private List<FileBean> sorListFile;


    private FileItemClick fileItemClick;
    public interface FileItemClick{
        void openFile(View view, int position, List<FileBean> allFileBean);
    }

    public void setOnFileItemClick(FileItemClick myClick){
        this.fileItemClick = myClick;
    }

    @Override
    public void onClick(View view) {
        if(fileItemClick!=null){
            fileItemClick.openFile(view, (int) view.getTag(),sorListFile);
        }
    }


    public RecentlyAdapter(Context context, final List<FileBean> listFile, GridLayoutManager myGridmanager){
        this.context = context;
        this.listFile = listFile;
        this.myGridmanager = myGridmanager;
        //标题数
        textItemCount = sorFileList(listFile);

        sorListFile = new ArrayList<>();

        newSortTime(textItemCount,this.listFile);
/*        new Thread(new Runnable() {
            @Override
            public void run() {
                newSortTime(textItemCount,listFile);
            }
        }).start();*/


        myGridmanager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(getItemViewType(position)==0 || getItemViewType(position) == 3 || getItemViewType(position)==2){
                    return 4;
                }else{
                    return 1;
                }
            }
        });
    }

    //新排序,获取每个时间段的文件
    private void newSortTime(int textItemCount, List<FileBean> listFile){
        int tmp = 0;
        int k = 0;
        for(int i=1;i<=textItemCount;i++){
            List<FileBean> mySTFiles = new ArrayList<>();
            tmp +=sortList.get(i-1);
            //时间相同
            for(int j=k;j<tmp;j++){
                FileBean myFile = listFile.get(j);
                mySTFiles.add(myFile);
            }
            k += sortList.get(i-1);

            newSortTYPE(mySTFiles);
        }
    }

    //排序，获取相同时间段，相同类型的文件（同一时间）
    private void newSortTYPE(List<FileBean> list) {

        for(int i=0;i<ConstantValue.TYPE_FILE.length;i++){
            List<FileBean> nowlist = new ArrayList<>();
            for(int j=0;j<list.size();j++){
                FileBean myBean = list.get(j);
                //找到同一类型数据
                if(FileUtils.getFileType(myBean.getPath()) == ConstantValue.TYPE_FILE[i]){
                    nowlist.add(myBean);
                }
            }
            newSortKey(nowlist);
        }
    }

    //根据类别排序(同一时间，同一类型)
    private void newSortKey(List<FileBean> nowlist) {
        if(nowlist.size() > 0 && nowlist !=null){
            for(int i=0;i<ConstantValue.FILE_KEY.length;i++){
                List<FileBean> sameType = new ArrayList<>();

                FileBean newBeanTitle = new FileBean();
                newBeanTitle.setFiletype(0);
                newBeanTitle.setFilename(ConstantValue.FILE_KEY[i]);
                newBeanTitle.setTime(nowlist.get(0).getTime());
                sameType.add(newBeanTitle);

                for(int j=0;j<nowlist.size();j++){
                    FileBean myBean = nowlist.get(j);
                    //找到同一类别数据
                    if(FileUtils.getFileKey(myBean.getPath())==ConstantValue.FILE_KEY[i]){
                        sameType.add(myBean);
                    }
                }

                FileBean newBeanLine = new FileBean();
                newBeanLine.setFiletype(3);
                sameType.add(newBeanLine);

                if(sameType.size() > 2){
                    sorListFile.addAll(sameType);
                }
            }
        }
    }





    //文件按时间分开
    private int sorFileList(List<FileBean> listFile) {

        sortList = new ArrayList<>();

        //返回需要多少标题
        int imgNumIdentical = 0;
        int i = 0;
        int count = 0;//统计标题数量
        int allNumber = listFile.size();

        if(allNumber == 1){
            imgNumIdentical = 1;
            sortList.add(1);
            return 1;//
        }
        while(i <= allNumber-2 && allNumber >= 2){
            FileBean one = listFile.get(i);
            FileBean two = listFile.get(i+1);

            if(isOneDay(one,two)){
                //同一天
                if(i == allNumber-2){
                    //若是到了最后一次比较
                    imgNumIdentical +=2;
                    sortList.add(imgNumIdentical);
                    i += 2;
                    count += 1;
                    break;
                }else{
                    imgNumIdentical += 1;
                    i++;
                }
            }else{

                count++;
                if(i == allNumber-2){
                    //到了最后两天
                    imgNumIdentical += 1;
                    sortList.add(imgNumIdentical);
                    sortList.add(1);
                    i += 2;
                    count += 1;
                    break;
                }
                //不是同一天
                imgNumIdentical += 1;
                sortList.add(imgNumIdentical);
                i++;


                imgNumIdentical = 0;
            }
        }
        return count;
    }

    //判断是否是同一天
    public boolean isOneDay(FileBean info1, FileBean info2) {
        String day1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date(info1.getTime()*1000L));
        String day2 = new SimpleDateFormat("yyyy-MM-dd").format(new Date(info2.getTime()*1000L));
        return day1.equals(day2);
    }



    //3 创建布局
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder mViewHolder = null;
        if(viewType == 0){
            //标题栏
            view = View.inflate(context,R.layout.pager_recently_adapter_title_layout,null);
            mViewHolder = new titleViewHolder(view);
        }else if(viewType == 1){
            //文件展示部分 图片文件
            view = View.inflate(context,R.layout.pager_recently_adapter_image_layout,null);
            mViewHolder = new myImageView(view);
        }else if(viewType == 2){
            //文本文件，native_zar
            view = View.inflate(context,R.layout.pager_recently_adapter_file_layout,null);
            mViewHolder = new myFileView(view);
        }else{
            //间隔
            view = View.inflate(context,R.layout.pager_recently_adapter_line_layout,null);
            mViewHolder = new linePart(view);
        }
        view.setOnClickListener(this);

        return mViewHolder;
    }

    //4 数据填充判断
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FileBean info = sorListFile.get(position);

        //调用getType来判断
        if(getItemViewType(position)==0){
            //标题
            titleViewHolder myholder = (titleViewHolder) holder;
            myholder.onBind(info);

            myholder.itemView.setTag(position);
        }
        if(getItemViewType(position)==1){
            //native_img
            myImageView myholder = (myImageView) holder;
            myholder.onBind(info);

            myholder.itemView.setTag(position);
        }
        if(getItemViewType(position)==2){
            //文件
            myFileView myholder = (myFileView) holder;
            myholder.onBind(info);

            myholder.itemView.setTag(position);
        }
        if(getItemViewType(position)==3){
            //间隔
            linePart myholder = (linePart) holder;

            myholder.itemView.setTag(position);
        }
    }

    //1
    @Override
    public int getItemCount() {
        return sorListFile.size();
    }

    //2
    @Override
    public int getItemViewType(int position) {
        return sorListFile.get(position).getFiletype();
    }


    //adapter
    public void changeList(List<FileBean> listFileBean) {
        if(listFileBean.size()>0 && listFileBean!=null){
            this.listFile.clear();
            this.listFile.addAll(listFileBean);
            //标题数
            textItemCount = sorFileList(listFile);
            newSortTime(textItemCount,this.listFile);
            this.notifyDataSetChanged();
        }
    }


    //标题栏
    class titleViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgFlag;
        private TextView myTitle;
        private TextView fileDate;

        public titleViewHolder(View itemView) {
            super(itemView);
            imgFlag = itemView.findViewById(R.id.imgFlag);
            myTitle = itemView.findViewById(R.id.mtTitle);
            fileDate = itemView.findViewById(R.id.fileDate);
        }

        public void onBind(FileBean fileInfo){
            imgFlag.setImageResource(FileUtils.getFilenameIcon(fileInfo.getFilename()));
            //fileInfo.getPath()
/*            Glide.with(context)
                    .load(R.drawable.qq)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgFlag);*/
            myTitle.setText(FileUtils.getFilename(fileInfo.getFilename()));
            fileDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(fileInfo.getTime()*1000L)));
        }
    }

    //图片存放
    class myImageView extends RecyclerView.ViewHolder{

        private ImageView myImageView;

        public myImageView(View itemView) {
            super(itemView);
            myImageView = itemView.findViewById(R.id.myImageView);
        }

        public void onBind(FileBean info){
            //info.getPath()
            Glide.with(context)
                    .load(info.getPath())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(myImageView);
        }
    }

    //文件存放
    class myFileView extends RecyclerView.ViewHolder{

        private TextView fileName,fileSize;
        private ImageView imageView;

        public myFileView(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileName);
            fileSize = itemView.findViewById(R.id.fileSize);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void onBind(FileBean info){
            fileName.setText(info.getName());
            fileSize.setText(Formatter.formatFileSize(context,info.getSize()));
            Glide.with(context)
                    .load(info.getIconId())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
        }
    }

    //间距
    class linePart extends RecyclerView.ViewHolder{

        private LinearLayout myLinearyout;
        public linePart(View itemView) {
            super(itemView);
            myLinearyout = itemView.findViewById(R.id.myLinearyout);
        }
    }

}

    /*                    if(FileUtils.getFileType(myFile.getPath()) == ConstantValue.TYPE_FILE[type]){
                        //属于同一类别
                        for(int key=0;key<ConstantValue.FILE_KEY.length;key++){
                            //顺序是QQ,WECHAT,SCREEN,CAMERA,GIF
                            if(myFile.getName().matches(ConstantValue.FILE_KEY[key])){
                                newListFile.add(myFile);
                            }else{

                            }
                        }
                    }*/