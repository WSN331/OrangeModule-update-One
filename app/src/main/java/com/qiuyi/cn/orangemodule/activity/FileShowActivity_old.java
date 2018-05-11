package com.qiuyi.cn.orangemodule.activity;

import android.app.Activity;

/**
 * Created by Administrator on 2018/3/16.
 * 文件展示模块
 */
public class FileShowActivity_old extends Activity{

    /*private SwipeRefreshLayout myFileRefresh;
    private RecyclerView myFileShow;
    private GridLayoutManager myGridManager;

    //正常的标题栏
    private RelativeLayout rl_normal_head;
    private ImageView iv_back;//返回
    private TextView tv_title,tv_paixu;//标题,排序

    //选择的标题栏
    private RelativeLayout rl_select_head;
    private ImageView iv_cancle;//取消选择
    private TextView tv_selectNum;//选择的文件数
    private TextView bt_selectAll;//全选

    //底部导航模块
    private LinearLayout ll_pager_native_bom;
    private TextView tv_delete,tv_copy,tv_move,tv_more;;//复制，移动，删除，重命名


    //是否全选
    private boolean isSelectAll = true;

    private List<MusicBean> listMusics;//音乐
    private List<VideoBean> listVideos;//视频
    private List<ImageBean> listImages;//图片
    private List<FileBean> listFiles;//文件
    private List<FileBean> listFileZars;//压缩包
    private List<File> newlistFiles;

    private UFileAdapter ufileAdapter;

    //本地文件的删除
    private SDFileDeleteListener sdFileDeleteListener;
    //U盘文件的删除
    private AllUdiskManager myManager;

    private LoadingDialog dialog;

    private WriteToUdisk udiskUtil;//复制到U盘
    private DiskWriteToSD diskWriteToSD;//复制到本地

    //用来复制的copyMap
    public static Map<Integer,File> copyFileMap = new HashMap<>();

    //分享
    private ArrayList<Uri> listUris;
    //添加收藏和私密
    private List<File> listSS;

    //是否显示了更多操作
    private boolean isMoreOperateshow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileshow);

        listUris = new ArrayList<>();
        listSS = new ArrayList<>();

        dialog = new LoadingDialog.Builder(this)
                .setMessage("复制中")
                .setCancelable(false)
                .setCancelOutside(false)
                .create();

        //udiskUtil = WriteToUdisk.getInstance(this.getApplicationContext(),this);
        diskWriteToSD = new DiskWriteToSD(FileShowActivity_old.this);

        initView();

        initData();
    }

    *//**
     * 初始化数据
     *//*
    private void initData() {
        //设置文件

        myGridManager = new GridLayoutManager(this,4);
        myFileShow.setLayoutManager(myGridManager);

        //获取数据，刷新界面
        whatTypeToget();

    }

    *//*
    * 判断得到的数据类型
    * *//*
    private void whatTypeToget() {
        Intent intent = getIntent();
        switch (intent.getIntExtra("type",0)){
            case 0:
                tv_title.setText("照片");
                listImages = MainActivity.listImages;
                break;
            case 1:
                listVideos = MainActivity.listVideos;
                break;
            case 2:
                listFiles = MainActivity.listFiles;

                break;
            case 3:
                listMusics = MainActivity.listMusics;

                break;
            case 4:
                listFileZars = MainActivity.listFileZars;
                break;
            default:
                break;
        }
    }


    *//**
     * 重命名+新建
     * @param name title
     * @param flag 1 新建，2 重命名
     *//*
    private void createNewFolder(String name, final int flag, final File myfile) {
        new CommomDialog(this, R.style.dialog, new CommomDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, String folderName, boolean confirm) {
                if(confirm){
                    if(flag == 2){
                        //重命名文件夹
                        if(myfile!=null){
                            if(myfile.isDirectory()){
                                //是个文件夹
                                File newFile = new File(myfile.getParentFile(),folderName);

                                myfile.renameTo(newFile);
                            }else{
                                //是个文件
                                String newName = folderName+myfile.getName().substring(myfile.getName().lastIndexOf("."),myfile.getName().length());
                                File newFile = new File(myfile.getParentFile(),newName);
                                myfile.renameTo(newFile);
                            }
                        }
                    }

                    dialog.dismiss();
                }
            }
        }).setTitle(name).show();
    }



    *//**
     * 复制+移动
     * @param b false=复制，true=移动（复制后，删除原来）
     *//*
    private void selectHowToPaste(final boolean b,Map<Integer,File> copyFileMap) {
        if(copyFileMap.size()<=0){
            return;
        }

        rl_select_head.setVisibility(View.GONE);
        rl_normal_head.setVisibility(View.VISIBLE);
        ll_pager_native_bom.setVisibility(View.GONE);


        new MySelectDialog(this, R.style.dialog, new MySelectDialog.OnCloseListener() {
            @Override
            public void onClick(Dialog dialog, Integer flag) {
                if(flag==1){
                    //本地
                    Intent intent = new Intent(FileShowActivity_old.this, AllFileShowActivity.class);
                    //带上标志3表示是从fileShowActivity过去的
                    intent.putExtra("from",3);
                    intent.putExtra("select",b);
                    startActivity(intent);
                }else if(flag ==2){
                    //U盘
                    if(MainActivity.isHaveUpan){
                        Intent intent = new Intent(FileShowActivity_old.this, AllUdiskFileShowActivity.class);
                        //带上标志3表示是从fileShowActivity过去的
                        intent.putExtra("from",3);
                        intent.putExtra("select",b);
                        startActivity(intent);
                    }else{
                        Toast.makeText(FileShowActivity_old.this,"请插入U盘",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).show();
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
    }

    //Video是否全部选中
    private void isToSelectVideoAll(int position, List<VideoBean> allVideoBean, boolean[] flag) {
        int rooti = position-1;
        int rootj = position+1;
        while(allVideoBean.get(rooti).getFiletype()!=0){
            rooti--;
        }
        while (allVideoBean.get(rootj).getFiletype()!=3){
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
    }

    //File是否全部选中
    private void isToSelectFileAll(int position, List<FileBean> allFileBean, boolean[] flag) {
        int rooti = position-1;
        int rootj = position+1;
        while(allFileBean.get(rooti).getFiletype()!=0){
            rooti--;
        }
        while (allFileBean.get(rootj).getFiletype()!=3){
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
    }

    //Music是否全部选中
    private void isToSelectMusicAll(int position, List<MusicBean> allMusicBean, boolean[] flag) {
        int rooti = position-1;
        int rootj = position+1;
        while(allMusicBean.get(rooti).getFiletype()!=0){
            rooti--;
        }
        while (allMusicBean.get(rootj).getFiletype()!=3){
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
    }


    //选中的是title标题栏，下面需要全部选中
    private void selectAll(List<ImageBean> allFileBean,int position,boolean[] flag){
        for(int i=position;i<allFileBean.size();i++){
            ImageBean allFile = allFileBean.get(i);
            if(allFile.getFiletype()==3){
                flag[i] = !flag[i];
                break;
            }
            flag[i] = !flag[i];
        }
    }

    //选中的是title标题栏，下面需要全部选中
    private void selectVideoAll(List<VideoBean> allFileBean,int position,boolean[] flag){
        for(int i=position;i<allFileBean.size();i++){
            VideoBean allFile = allFileBean.get(i);
            if(allFile.getFiletype()==3){
                flag[i] = !flag[i];
                break;
            }
            flag[i] = !flag[i];
        }
    }

    //选中的是title标题栏，下面需要全部选中
    private void selectMusicAll(List<MusicBean> allFileBean,int position,boolean[] flag){
        for(int i=position;i<allFileBean.size();i++){
            MusicBean allFile = allFileBean.get(i);
            if(allFile.getFiletype()==3){
                flag[i] = !flag[i];
                break;
            }
            flag[i] = !flag[i];
        }
    }

    //选中的是title标题栏，下面需要全部选中
    private void selectFileAll(List<FileBean> allFileBean,int position,boolean[] flag){
        for(int i=position;i<allFileBean.size();i++){
            FileBean allFile = allFileBean.get(i);
            if(allFile.getFiletype()==3){
                flag[i] = !flag[i];
                break;
            }
            flag[i] = !flag[i];
        }
    }




    *//**
     * 初始化界面
     *//*
    private void initView() {
        //展示列表
        myFileShow = findViewById(R.id.fileshow_rl);

        //正常标题栏
        rl_normal_head = findViewById(R.id.rl_normal_head);
        iv_back = findViewById(R.id.allfileshow_iv_back);//返回
        tv_paixu = findViewById(R.id.tv_paixu);
        tv_title = findViewById(R.id.allfileshow_tv_title); //标题

        //选择checkbox出来之后显示的标题栏
        rl_select_head = findViewById(R.id.rl_select_head);
        iv_cancle = findViewById(R.id.rl_allfileshow_title_cancle); //取消选择
        tv_selectNum = findViewById(R.id.rl_allfileshow_title_select);//选择的文件数
        bt_selectAll = findViewById(R.id.bt_selectAll);//全选按钮

        //底部导航栏
        ll_pager_native_bom = findViewById(R.id.ll_pager_native_bom);
        tv_delete = findViewById(R.id.tv_delete);
        tv_copy = findViewById(R.id.tv_copy);
        tv_move = findViewById(R.id.tv_move);
        tv_more = findViewById(R.id.tv_more);

        //返回键
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //排序
        tv_paixu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
*/
}


/*
imgAdapter.setOnImageItemClick(new ImageAdapter.ImageItemClick() {
@Override
public void openImage(View view, int position, List<ImageBean> allImageBean) {
        ImageBean imageBean = allImageBean.get(position);

        boolean isShowBox = imgAdapter.isShowCheckBox();

        if(isShowBox){
        if(imageBean.getFiletype()!=3&& imageBean.getFiletype()!=0){
        //正在显示checkbox
        boolean[] flag = imgAdapter.getFlag();

        flag[position] = !flag[position];

        //如果当前position是选中状态，那么就从这个点去找开始和结束点
        isToSelectImgAll(position,allImageBean,flag);

        imgAdapter.setFlag(flag);


        int count = 0;
        for(boolean flagment:flag){
        if(flagment){
        count++;
        }
        }
        tv_selectNum.setText("已选("+count+")");

        //判断是否全选了
        if(count==allImageBean.size()){
        bt_selectAll.setText("取消全选");
        isSelectAll = false;
        }else{
        bt_selectAll.setText("全选");
        isSelectAll = true;
        }

        imgAdapter.notifyDataSetChanged();
        }
        }else{
        if(imageBean.getFiletype()==1){
        FileUtilOpen.openFileByPath(getApplicationContext(),imageBean.getPath());
        }
        }
        }

@Override
public void onLongClick(View view, int position, final List<ImageBean> allFileBean) {

        ImageBean imgBean = allFileBean.get(position);

        //按得不是间隔处
        if(imgBean.getFiletype()!=3){
        //选择状态栏显示
        rl_normal_head.setVisibility(View.GONE);
        rl_select_head.setVisibility(View.VISIBLE);
        //底部导航栏按钮显示
        ll_pager_native_bom.setVisibility(View.VISIBLE);

        bt_selectAll.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
        if(isSelectAll){
        imgAdapter.selectAll();
        isSelectAll = false;
        bt_selectAll.setText("取消全选");
        tv_selectNum.setText("已选("+allFileBean.size()+")");
        }else{
        imgAdapter.noSelect();
        isSelectAll = true;
        bt_selectAll.setText("全选");
        tv_selectNum.setText("已选(0)");
        }
        imgAdapter.notifyDataSetChanged();
        }
        });

        //取消按钮
        iv_cancle.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
        //选择状态栏显示
        rl_select_head.setVisibility(View.GONE);
        rl_normal_head.setVisibility(View.VISIBLE);
        ll_pager_native_bom.setVisibility(View.GONE);

        imgAdapter.setShowCheckBox(false);
        imgAdapter.ReFresh();
        }
        });

        tv_delete.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
        //选择，显示一下有哪几个选择了
        boolean[] flag = imgAdapter.getFlag();
        for(int i = flag.length-1;i>=0;i--){
        if(flag[i]){
        Log.e("select", "选中："+i);

*/
/*                                            deleteDialog.show();
                                            final File defile = fileList.get(i);

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    doDelete(defile);

                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            deleteDialog.dismiss();
                                                            tv_selectNum.setText("已选(0)");
                                                        }
                                                    });
                                                }
                                            }).start();*//*


        allFileBean.remove(i);
        }
        }

        tv_selectNum.setText("已选(0)");
        imgAdapter.ReFresh();
        }
        });

        //复制
        tv_copy.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
        boolean[] flag = imgAdapter.getFlag();
        for(int i = flag.length-1;i>=0;i--){
        if(flag[i]){
        Log.e("select", "选中："+i);
        if(allFileBean.get(i).getFiletype()!=0 && allFileBean.get(i).getFiletype()!=3)
        {
        File file = new File(allFileBean.get(i).getPath());
        copyFileMap.put(i,file);
        }
        }
        }
        //选择，显示一下有哪几个选择了
        selectHowToPaste(false,copyFileMap);

        imgAdapter.setShowCheckBox(false);
        imgAdapter.ReFresh();
        }
        });

        //移动
        tv_move.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {

        boolean[] flag = imgAdapter.getFlag();
        for(int i = flag.length-1;i>=0;i--){
        if(flag[i]){
        Log.e("select", "选中："+i);
        if(allFileBean.get(i).getFiletype()!=0 && allFileBean.get(i).getFiletype()!=3)
        {
        File file = new File(allFileBean.get(i).getPath());
        copyFileMap.put(i,file);
        }
        }
        }
        //选择，显示一下有哪几个选择了
        selectHowToPaste(true,copyFileMap);

        imgAdapter.setShowCheckBox(false);
        imgAdapter.ReFresh();
        }
        });


        //更多操作模块
        tv_more.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
final int[] myflag ={-1,-1,-1,-1,-1};
        //选择
        boolean[] flag = imgAdapter.getFlag();
        int count = 0;
        int location = -1;
        boolean isDirectory = false;

        listUris.clear();
        listSS.clear();

        for(int i = flag.length-1;i>=0;i--){
        if(flag[i]){
        Log.e("select", "选中："+i);
        if(allFileBean.get(i).getFiletype()!=0 && allFileBean.get(i).getFiletype()!=3)
        {
        File file = new File(allFileBean.get(i).getPath());
        listUris.add(Uri.fromFile(file));
        //收藏和私密
        listSS.add(file);
        count++;
        location=i;
        }
        }
        }
        File collectFile = null;
        if(count==1){
        //当只有一个文件的时候，判断一下这个文件是否在收藏中
        File colFile = new File(allFileBean.get(location).getPath());
        //collectionFiles = DBUtil.getCollectFile(colFile.getPath());
        //在MyFile->CollectionDirectory文件夹下查找文件是否存在
        collectFile = diskWriteToSD.findCollectionFile(colFile,AllFileShowActivity.CollectionDirectory_Name);
        if(collectFile!=null){
        //若为true就是已经收藏了
        myflag[1] = 2;
        }

        if(isDirectory){
        //有文件夹
        myflag[0] = 1;//分享不行
        myflag[2] = 1;//添加私密不行
        }
        }else if(count>1){
        myflag[1] = 1;
        myflag[2] = 1;
        myflag[3] = 1;
        myflag[4] = 1;
        if(isDirectory){
        myflag[0] = 1;//分享不行
        }
        }


final int finalCount = count;//选中的数量
final int finalLocation = location;//文件的位置
final boolean finalIsDirectory = isDirectory;//文件的种类
final File finalCollection = collectFile;//文件手否收藏
        //final CollectionFiles finalCollectionFiles = collectionFiles;//文件是否在收藏中

        if(count>0){
        isMoreOperateshow = !isMoreOperateshow;
        if(isMoreOperateshow){

        new MoreOperatePopWindow(FileShowActivity_old.this, new MoreOperatePopWindow.OnItemClickListener() {
@Override
public void onItemClick(final MoreOperatePopWindow popupWindow, int position) {
        //分享
        if(position==1 && !finalIsDirectory){
        ShareFile.shareMultipleFiles(FileShowActivity_old.this,listUris);
        popupWindow.dismiss();

        //选择状态栏显示
        rl_select_head.setVisibility(View.GONE);
        rl_normal_head.setVisibility(View.VISIBLE);
        ll_pager_native_bom.setVisibility(View.GONE);

        imgAdapter.setShowCheckBox(false);
        imgAdapter.ReFresh();

        isMoreOperateshow = false;
        }

        //添加收藏
        if(position==2 && finalCount==1){
        //只有一个对象
        if(finalCollection!=null){
        //已经在收藏中，那么操作就是取消收藏
        new Thread(new Runnable() {
@Override
public void run() {
        finalCollection.delete();
        runOnUiThread(new Runnable() {
@Override
public void run() {
        popupWindow.dismiss();

        //选择状态栏显示
        rl_select_head.setVisibility(View.GONE);
        rl_normal_head.setVisibility(View.VISIBLE);
        ll_pager_native_bom.setVisibility(View.GONE);

        imgAdapter.setShowCheckBox(false);
        imgAdapter.ReFresh();

        isMoreOperateshow = false;
        }
        });
        }
        }).start();
        }else{
//为空，操作就是收藏
final File file = new File(allFileBean.get(finalLocation).getPath());

        new Thread(new Runnable() {
@Override
public void run() {
        diskWriteToSD.writeSelectFileToSD(file,AllFileShowActivity.CollectionDirectory_Name);
        runOnUiThread(new Runnable() {
@Override
public void run() {
        popupWindow.dismiss();

        //选择状态栏显示
        rl_select_head.setVisibility(View.GONE);
        rl_normal_head.setVisibility(View.VISIBLE);
        ll_pager_native_bom.setVisibility(View.GONE);

        imgAdapter.setShowCheckBox(false);
        imgAdapter.ReFresh();

        isMoreOperateshow = false;
        }
        });
        }
        }).start();

        }

        }
        //添加私密
        if(position==3 && !finalIsDirectory){
//为空，操作就是收藏
final File file = new File(allFileBean.get(finalLocation).getPath());

        //得到的MyFile->.SecretDirectory文件夹
        File mySecretDirectory = diskWriteToSD.getSDCardFile(AllFileShowActivity.SecretDirectory_Name);
        String newFileName = AESHelperUpdate2.encrypt(AllFileShowActivity.PASSWORD_STRING,file.getName()+"*"+new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
final File newFile = new File(mySecretDirectory.getAbsoluteFile()+"/"+newFileName);

        new Thread(new Runnable() {
@Override
public void run() {
        boolean isWrite = diskWriteToSD.writeToSD3(file,newFile);
        if(isWrite){
        file.delete();
        }
        runOnUiThread(new Runnable() {
@Override
public void run() {
        //UI更新
        popupWindow.dismiss();
        isMoreOperateshow = false;

        rl_select_head.setVisibility(View.GONE);
        rl_normal_head.setVisibility(View.VISIBLE);
        ll_pager_native_bom.setVisibility(View.GONE);

        imgAdapter.setShowCheckBox(false);
        imgAdapter.ReFresh();
        }
        });
        }
        }).start();

        }
        //重命名
        if(position==4){
        //重命名和详情都需要点击的是一个
        if(finalCount ==1){
        createNewFolder("重命名文件",2,new File(allFileBean.get(finalLocation).getPath()));
        popupWindow.dismiss();

        rl_select_head.setVisibility(View.GONE);
        rl_normal_head.setVisibility(View.VISIBLE);
        ll_pager_native_bom.setVisibility(View.GONE);

        imgAdapter.setShowCheckBox(false);
        imgAdapter.ReFresh();

        isMoreOperateshow = false;
        }
        }
        //详情
        if(position==5){
        //重命名和详情都需要点击的是一个
        if(finalCount ==1){
        new FileDetailDialog(FileShowActivity_old.this,R.style.dialog,new File(allFileBean.get(finalLocation).getPath())).show();
        isMoreOperateshow = !isMoreOperateshow;
        popupWindow.dismiss();

        rl_select_head.setVisibility(View.GONE);
        rl_normal_head.setVisibility(View.VISIBLE);
        ll_pager_native_bom.setVisibility(View.GONE);

        imgAdapter.setShowCheckBox(false);
        imgAdapter.ReFresh();

        isMoreOperateshow = false;
        }
        }
        }
        }).setTitle(myflag).showUp2(tv_more);
        }

        }
        }
        });

*/
/*                            //有U盘存在
                            if(MainActivity.isHaveUpan){
                                tv_copy.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //选择，显示一下有哪几个选择了
                                        dialog.show();

                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                boolean[] flag = imgAdapter.getFlag();
                                                for(int i = flag.length-1;i>=0;i--){
                                                    if(flag[i]){
                                                        Log.e("select", "选中："+i);
                                                        if(allFileBean.get(i).getFiletype()!=0 && allFileBean.get(i).getFiletype()!=3)
                                                        {
                                                            File file = new File(allFileBean.get(i).getPath());
                                                            udiskUtil.writeToSDFile(getApplicationContext(),file,udiskUtil.getCurrentFolder());
                                                        }
                                                    }
                                                }
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        dialog.dismiss();
                                                        //头部状态栏显示
                                                        bt_selectAll.setVisibility(View.GONE);
                                                        //底部状态栏显示
                                                        ll_pager_native_bom.setVisibility(View.GONE);

                                                        imgAdapter.setShowCheckBox(false);
                                                        imgAdapter.ReFresh();
                                                    }
                                                });
                                            }
                                        }).start();
                                    }
                                });
                            }*//*



        imgAdapter.setShowCheckBox(true);
        boolean[] flag = imgAdapter.getFlag();

        if(imgBean.getFiletype()==0){
        selectAll(allFileBean,position,flag);
        }else{
        flag[position] = true;
        isToSelectImgAll(position,allFileBean,flag);
        }

        imgAdapter.setFlag(flag);

        imgAdapter.notifyDataSetChanged();
        }
        }

@Override
public void changeCount(int count,List<ImageBean> sorListFile) {
        tv_selectNum.setText("已选("+count+")");
        //判断是否全选了
        if(count==sorListFile.size()){
        bt_selectAll.setText("取消全选");
        isSelectAll = false;
        }else{
        bt_selectAll.setText("全选");
        isSelectAll = true;
        }
        }
        });
*/
