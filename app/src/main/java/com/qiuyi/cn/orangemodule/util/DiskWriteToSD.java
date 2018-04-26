package com.qiuyi.cn.orangemodule.util;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import com.qiuyi.cn.orangemodule.util.FileManager.contacts.ContactBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/9.
 * 将文件从U盘中写入SDCard
 */
public class DiskWriteToSD {

    private Context context;
    private File rootFile;

    public DiskWriteToSD(Context context){
        this.context = context;
        this.rootFile = Environment.getExternalStorageDirectory();
    }

    public File getRootFile(){
        return rootFile;
    }

    //判断是否有外置存储卡
    public boolean isSDCardState(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }

    //创建保存U盘文件的目录:照片，视频，文档，音乐，联系人
    public File getSDCardFile(String fileName){
        File myFileToRead = null;
        if(isSDCardState()){
            //得到root目录
            File parent_path = Environment.getExternalStorageDirectory();
            //在root目录下创建myFile文件夹
            File myFile = new File(parent_path.getAbsoluteFile(),"MyFile");
            myFile.mkdirs();
            //在myFile文件夹下创建文件
            File file = new File(myFile.getAbsoluteFile(),fileName);
            file.mkdirs();
            myFileToRead = file;
        }
        return myFileToRead;
    }


    //将文件夹写入SDCard,传进来的文件夹
    public void writeDirectory(File file){
        if(file.isDirectory()){
            File rootFile = getSDCardFile(file.getName());
            writeToSD1(file,rootFile);
        }
    }

    //循环写入
    private void writeToSD1(File file,File rootFile) {
        for(File nowFile:file.listFiles()){
            if(nowFile.isDirectory()){
               File nowDirectory = new File(rootFile.getAbsolutePath(),file.getName());
                nowDirectory.mkdirs();
                writeToSD1(nowFile,nowDirectory);
            }else{
                writeToSD2(nowFile,rootFile);
            }
        }
    }


    //将文件写入SDCard
    public void writeToSD2(File file,File rootFile){
        if(isSDCardState()){
            File targetFile = new File(rootFile.getAbsolutePath(),file.getName());

            FileInputStream in = null;
            FileOutputStream out = null;
            try {
                in = new FileInputStream(file);
                out = new FileOutputStream(targetFile);
                byte[] buf = new byte[1024 * 8];
                while(in.read(buf)!=-1){
                    out.write(buf);
                    out.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    if(in!=null){
                        in.close();
                    }
                    if(out!=null){
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    //将文件写入SDCard
    public void writeFileToSD(File file,String name){
        if(isSDCardState()){
            File targetFileDec = getSDCardFile(name);

            File targetFile = new File(targetFileDec.getAbsolutePath(),file.getName());

            FileInputStream in = null;
            FileOutputStream out = null;
            try {
                in = new FileInputStream(file);
                out = new FileOutputStream(targetFile);
                byte[] buf = new byte[1024 * 8];
                while(in.read(buf)!=-1){
                    out.write(buf);
                    out.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    if(in!=null){
                        in.close();
                    }
                    if(out!=null){
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //将从文件中读取的联系人添加到本地手机中
    public void sendToSDCardPhone(List<ContactBean> listBean, Context context) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
        int rawContactInsertIndex = -1;
        for(ContactBean myContact:listBean) {
            if (myContact != null) {
                //有了它才能实现整整的批量添加
                rawContactInsertIndex = ops.size();

                ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .withYieldAllowed(true)
                        .build());

                // 添加姓名
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, myContact.getLastname().trim())
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, myContact.getLastname().trim())
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.PHONETIC_FAMILY_NAME, myContact.getPhoneticFirstName().trim())
                        .withYieldAllowed(true).build());
                // 添加号码
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        //手机
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, myContact.getMobile().trim())
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                        .withYieldAllowed(true).build());
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        //住宅电话
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,myContact.getHomeNum().trim())
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                        .withYieldAllowed(true).build());
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        //单位电话
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,myContact.getJobNum().trim())
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                        .withYieldAllowed(true).build());
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        //总机电话
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,myContact.getTel().trim())
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MAIN)
                        .withYieldAllowed(true).build());
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        //单位传真
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,myContact.getWorkFax().trim())
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK)
                        .withYieldAllowed(true).build());
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        //住宅传真
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,myContact.getHomeFax().trim())
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME)
                        .withYieldAllowed(true).build());
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        //寻呼机
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,myContact.getPager().trim())
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_PAGER)
                        .withYieldAllowed(true).build());
                //添加生日与纪念日
                if(myContact.getBirthday()!=null){
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                            //生日
                            .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, format.format(myContact.getBirthday()))
                            .withValue(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY)
                            .withYieldAllowed(true).build());
                }
                if(myContact.getAnniversary()!=null){
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE)
                            //纪念日
                            .withValue(ContactsContract.CommonDataKinds.Event.START_DATE, format.format(myContact.getAnniversary()))
                            .withValue(ContactsContract.CommonDataKinds.Event.TYPE, ContactsContract.CommonDataKinds.Event.TYPE_ANNIVERSARY)
                            .withYieldAllowed(true).build());
                }
                //添加Email
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        //住宅邮件
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA,myContact.getHomeEmail().trim())
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM)
                        .withYieldAllowed(true).build());
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        //住宅邮件
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA,myContact.getHomeEmail().trim())
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME)
                        .withYieldAllowed(true).build());
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        //单位邮件
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA,myContact.getJobEmail().trim())
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM)
                        .withYieldAllowed(true).build());
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        //单位邮件
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA,myContact.getJobEmail().trim())
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                        .withYieldAllowed(true).build());
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        //手机邮件
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA,myContact.getMobileEmail().trim())
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM)
                        .withYieldAllowed(true).build());
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                        //手机邮件
                        .withValue(ContactsContract.CommonDataKinds.Email.DATA,myContact.getMobileEmail().trim())
                        .withValue(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_MOBILE)
                        .withYieldAllowed(true).build());
                //添加即时消息
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                        //QQ及时消息
                        .withValue(ContactsContract.CommonDataKinds.Im.DATA,myContact.getInstantsMsg().trim())
                        .withValue(ContactsContract.CommonDataKinds.Im.PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_QQ)
                        .withYieldAllowed(true).build());
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                        //工作及时消息
                        .withValue(ContactsContract.CommonDataKinds.Im.DATA,myContact.getWorkMsg().trim())
                        .withValue(ContactsContract.CommonDataKinds.Im.PROTOCOL, ContactsContract.CommonDataKinds.Im.TYPE_CUSTOM)
                        .withYieldAllowed(true).build());
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE)
                        //工作及时消息
                        .withValue(ContactsContract.CommonDataKinds.Im.DATA,myContact.getWorkMsg().trim())
                        .withValue(ContactsContract.CommonDataKinds.Im.PROTOCOL, ContactsContract.CommonDataKinds.Im.PROTOCOL_MSN)
                        .withYieldAllowed(true).build());
                //添加备注信息
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Note.NOTE, myContact.getRemark().trim())
                        .withYieldAllowed(true).build());
                //添加昵称信息
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Nickname.NAME, myContact.getNickName().trim())
                        .withYieldAllowed(true).build());
                //添加组织信息
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)
                        //工作单位
                        .withValue(ContactsContract.CommonDataKinds.Organization.COMPANY,myContact.getCompany().trim())
                        //职位名称
                        .withValue(ContactsContract.CommonDataKinds.Organization.TITLE,myContact.getJobTitle().trim())
                        .withValue(ContactsContract.CommonDataKinds.Organization.TYPE, ContactsContract.CommonDataKinds.Organization.TYPE_CUSTOM)
                        .withYieldAllowed(true).build());
                //添加通讯地址
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                        //工作单位通讯地址
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET,myContact.getStreet().trim())
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
                        .withYieldAllowed(true).build());
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                        //住宅通讯地址
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET,myContact.getHomeStreet().trim())
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME)
                        .withYieldAllowed(true).build());
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE)
                        //其他通讯地址
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.STREET,myContact.getOtherStreet().trim())
                        .withValue(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER)
                        .withYieldAllowed(true).build());
                //以上实现批量装入
            }
        }
        if(ops!=null){
            //这里实现真正的添加
            try {
                context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                Log.e("AddSuccess","添加成功"+ops.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    //将未填写的信息填写完成，不存在填空
    public void init(ContactBean myBean) {
        if(myBean!=null){
            if( myBean.getMobile()==null){
                myBean.setMobile("");
            }
            if(myBean.getHomeNum()==null){
                myBean.setHomeNum("");
            }
            if(myBean.getJobNum()==null){
                myBean.setJobNum("");
            }
            if(myBean.getTel()==null){
                myBean.setTel("");
            }
            if(myBean.getWorkFax()==null){
                myBean.setWorkFax("");
            }
            if(myBean.getHomeFax()==null){
                myBean.setHomeFax("");
            }
            if(myBean.getPager()==null){
                myBean.setPager("");
            }
            if(myBean.getHomeEmail()==null){
                myBean.setHomeEmail("");
            }
            if(myBean.getJobEmail()==null){
                myBean.setJobEmail("");
            }
            if(myBean.getMobileEmail()==null){
                myBean.setMobileEmail("");
            }
            if(myBean.getInstantsMsg()==null){
                myBean.setInstantsMsg("");
            }
            if(myBean.getWorkMsg()==null){
                myBean.setWorkMsg("");
            }
            if(myBean.getRemark()==null){
                myBean.setRemark("");
            }
            if(myBean.getLastname()==null){
                myBean.setLastname("");
            }
            if(myBean.getPhoneticFirstName()==null){
                myBean.setPhoneticFirstName("");
            }
            if(myBean.getCompany()==null){
                myBean.setCompany("");
            }
            if(myBean.getJobTitle()==null){
                myBean.setJobTitle("");
            }
            if(myBean.getNickName()==null){
                myBean.setNickName("");
            }
            if(myBean.getHomeStreet()==null){
                myBean.setHomeStreet("");
            }
            if(myBean.getStreet()==null){
                myBean.setStreet("");
            }
            if(myBean.getOtherStreet()==null){
                myBean.setOtherStreet("");
            }
        }

    }

}
