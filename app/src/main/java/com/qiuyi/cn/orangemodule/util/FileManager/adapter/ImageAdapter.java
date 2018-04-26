package com.qiuyi.cn.orangemodule.util.FileManager.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.FileBean;
import com.qiuyi.cn.orangemodule.util.FileManager.bean1.ImageBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/3/21.
 */
public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener,View.OnLongClickListener{

    private Context context;
    //有几个标题
    private int textItemCount;
    //每个时间段的图片个数
    private List<Integer> sortList;

    private List<ImageBean> listFile;
    //所有ImageBean同一
    private List<ImageBean> sorListFile;


    private ImageItemClick ImageItemClick;
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
        for(int i=0;i<sorListFile.size();i++){
            flag[i] = true;
        }
        notifyDataSetChanged();
    }

    //取消全选
    public void noSelect(){
        for (int i=0;i<sorListFile.size();i++){
            flag[i] = false;
        }
        notifyDataSetChanged();
    }

    public interface ImageItemClick{
        void openImage(View view, int position, List<ImageBean> allImageBean);
        void onLongClick(View view,int position,List<ImageBean> allFileBean);
    }

    public void setOnImageItemClick(ImageItemClick myClick){
        this.ImageItemClick = myClick;
    }

    @Override
    public void onClick(View view) {
        if(ImageItemClick!=null){
            ImageItemClick.openImage(view, (int) view.getTag(),sorListFile);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if(ImageItemClick!=null){
            ImageItemClick.onLongClick(view,(int)view.getTag(),sorListFile);
        }
        return false;
    }

    //页面刷新
    public void ReFresh(){
        flag = new boolean[sorListFile.size()];
        notifyDataSetChanged();
    }



    //新排序
    private List<ImageBean> newSort(int textItemCount,List<ImageBean> listFile){
        List<ImageBean> newListFile = new ArrayList<>();
        int tmp = 0;
        int k = 0;
        for(int i=1;i<=textItemCount;i++){
            ImageBean newBeanTitle = new ImageBean();
            newBeanTitle.setFiletype(0);
            newBeanTitle.setDate(listFile.get(k).getDate());
            newListFile.add(newBeanTitle);

            tmp +=sortList.get(i-1);
            for(int j=k;j<tmp;j++){
                newListFile.add(listFile.get(j));
            }
            k += sortList.get(i-1);

            ImageBean newBeanLine = new ImageBean();
            newBeanLine.setFiletype(3);
            newListFile.add(newBeanLine);
        }
        return newListFile;
    }


    public ImageAdapter(Context context, List<ImageBean> listFile, GridLayoutManager layoutManager){
        this.context = context;
        this.listFile = listFile;

        //得到有多少类型图片，按照图片时间划分
        textItemCount = sorImageList(listFile);

        //重新排序
        sorListFile = newSort(textItemCount,listFile);

        flag = new boolean[sorListFile.size()];

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(getItemViewType(position)==0 || getItemViewType(position) == 3){
                    return 4;
                }else{
                    return 1;
                }
            }
        });
    }

    //图片按时间分开
    private int sorImageList(List<ImageBean> listFile) {

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
            ImageBean one = listFile.get(i);
            ImageBean two = listFile.get(i+1);

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
    public boolean isOneDay(ImageBean info1, ImageBean info2) {
        String day1 = new SimpleDateFormat("yyyy-MM-dd").format(new Date(info1.getDate()));
        String day2 = new SimpleDateFormat("yyyy-MM-dd").format(new Date(info2.getDate()));
        return day1.equals(day2);
    }



    //3 创建布局
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder mViewHolder = null;
        if(viewType == 0){
            //标题栏
            view = View.inflate(context,R.layout.pager_adapter_title_layout,null);
            mViewHolder = new titleViewHolder(view);
        }else if(viewType == 1){
            //文件展示部分 图片文件
            view = View.inflate(context,R.layout.pager_adapter_image_layout,null);
            mViewHolder = new myImageView(view);
        }else if(viewType == 2){
            //文本文件，压缩包
            view = View.inflate(context,R.layout.pager_recently_adapter_file_layout,null);
            mViewHolder = new myFileView(view);
        }else{
            //间隔
            view = View.inflate(context,R.layout.pager_recently_adapter_line_layout,null);
            mViewHolder = new linePart(view);
        }
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        return mViewHolder;
    }

    //4 数据填充判断
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ImageBean info = sorListFile.get(position);

        //调用getType来判断
        if(getItemViewType(position)==0){
            //标题
            titleViewHolder myholder = (titleViewHolder) holder;
            myholder.onBind(info);

            //checkBox显示
            if(isShowCheckBox){
                myholder.cb_ad_title.setVisibility(View.VISIBLE);
            }else{
                myholder.cb_ad_title.setVisibility(View.INVISIBLE);
            }
            myholder.cb_ad_title.setOnCheckedChangeListener(null);
            myholder.cb_ad_title.setChecked(flag[position]);
            myholder.cb_ad_title.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                    for(int i=position;i<sorListFile.size();i++){
                        if(getItemViewType(i)==3){
                            flag[i] = b;
                            break;
                        }
                        flag[i] = b;
                    }
                    /*flag[position] = b;*/
                    Log.e("change", b+"");
                    notifyDataSetChanged();
                }
            });

            myholder.itemView.setTag(position);
        }
        if(getItemViewType(position)==1){
            //图片
            myImageView myholder = (myImageView) holder;
            myholder.onBind(info);

            //checkBox显示
            if(isShowCheckBox){
                myholder.cb_ad_img.setVisibility(View.VISIBLE);
            }else{
                myholder.cb_ad_img.setVisibility(View.INVISIBLE);
            }
            myholder.cb_ad_img.setOnCheckedChangeListener(null);
            myholder.cb_ad_img.setChecked(flag[position]);
            myholder.cb_ad_img.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    flag[position] = b;

                    isToSelectImgAll(position,sorListFile,flag);
                }
            });

            myholder.itemView.setTag(position);
        }
/*        if(getItemViewType(position)==2){
            //文件
            myFileView myholder = (myFileView) holder;
            myholder.onBind(info);

            myholder.itemView.setTag(position);
        }*/
        if(getItemViewType(position)==3){
            //间隔
            linePart myholder = (linePart) holder;

            myholder.itemView.setTag(position);
        }

    }

    //Music是否全部选中
    private void isToSelectImgAll(int position, List<ImageBean> allImageBean, boolean[] flag) {
        int rooti = position-1;
        int rootj = position+1;
        while(allImageBean.get(rooti).getFiletype()!=0){
            rooti--;
        }
        while (allImageBean.get(rootj).getFiletype()!=3){
            rootj++;
        }

        flag[rooti] = true;
        flag[rootj] = true;

        for (int i=rooti+1;i<rootj;i++){
            if(!flag[i]){
                flag[rooti] = false;
                flag[rootj] = false;
                break;
            }
        }

        notifyDataSetChanged();
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


    //标题栏
    class titleViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_ad_title;
        private CheckBox cb_ad_title;

        public titleViewHolder(View itemView) {
            super(itemView);
            tv_ad_title = itemView.findViewById(R.id.tv_ad_title);
            cb_ad_title = itemView.findViewById(R.id.cb_ad_title);
        }

        public void onBind(ImageBean fileInfo){
/*            imgFlag.setImageResource(R.drawable.myimg);
            //fileInfo.getPath()
            Glide.with(context)
                    .load(R.drawable.qq)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgFlag);
            myTitle.setText("图片");*/

            tv_ad_title.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(fileInfo.getDate())));
        }
    }

    //图片存放
    class myImageView extends RecyclerView.ViewHolder{

        private ImageView myImageView;
        private CheckBox cb_ad_img;

        public myImageView(View itemView) {
            super(itemView);
            myImageView = itemView.findViewById(R.id.myImageView);
            cb_ad_img = itemView.findViewById(R.id.cb_ad_img);
        }

        public void onBind(ImageBean info){
            //info.getPath()
            Glide.with(context)
                    .load(info.getPath())
                    .override(120,120)
                    .centerCrop()
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

        public void onBind(ImageBean info){
/*            fileName.setText(info.getName());
            fileSize.setText(info.getSize());*/
            Glide.with(context)
                    .load(R.drawable.qq)
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
