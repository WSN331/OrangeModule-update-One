package com.qiuyi.cn.orangemodule.util.FileManager.contacts;

import java.util.Date;

/**
 * Created by Administrator on 2018/1/30.
 * 联系人类
 */
public class ContactBean {

/*    private List<ContactPerson> contact;*/
/*    class ContactPerson{*/
        private String mobile;//手机
        private String homeNum;//住宅电话
        private String jobNum;//单位电话
        private String tel;//总机电话
        private String workFax;//单位传真
        private String homeFax;//住宅传真
        private String pager;//寻呼机
        private Date birthday;//生日
        private Date anniversary;//纪念日
        private String homeEmail;//住宅邮件地址
        private String jobEmail;//单位邮件地址
        private String mobileEmail;//手机邮件地址
        private String instantsMsg;//QQ及时通信消息
        private String workMsg;//工作及时消息
        private String remark;//备注信息
        private String lastname;//姓名
        private String phoneticFirstName;//姓名拼音
        private String company;//工作单位
        private String jobTitle;//职位
        private String nickName;//昵称信息
        private String homeStreet;//家庭通讯地址
        private String street;//单位通讯地址
        private String otherStreet;//其他通讯地址


        public String getMobile() {
                return mobile;
        }

        public void setMobile(String mobile) {
                this.mobile = mobile;
        }

        public String getHomeNum() {
                return homeNum;
        }

        public void setHomeNum(String homeNum) {
                this.homeNum = homeNum;
        }

        public String getJobNum() {
                return jobNum;
        }

        public void setJobNum(String jobNum) {
                this.jobNum = jobNum;
        }

        public String getTel() {
                return tel;
        }

        public void setTel(String tel) {
                this.tel = tel;
        }

        public String getWorkFax() {
                return workFax;
        }

        public void setWorkFax(String workFax) {
                this.workFax = workFax;
        }

        public String getHomeFax() {
                return homeFax;
        }

        public void setHomeFax(String homeFax) {
                this.homeFax = homeFax;
        }

        public String getPager() {
                return pager;
        }

        public void setPager(String pager) {
                this.pager = pager;
        }

        public Date getBirthday() {
                return birthday;
        }

        public void setBirthday(Date birthday) {
                this.birthday = birthday;
        }

        public Date getAnniversary() {
                return anniversary;
        }

        public void setAnniversary(Date anniversary) {
                this.anniversary = anniversary;
        }

        public String getHomeEmail() {
                return homeEmail;
        }

        public void setHomeEmail(String homeEmail) {
                this.homeEmail = homeEmail;
        }

        public String getJobEmail() {
                return jobEmail;
        }

        public void setJobEmail(String jobEmail) {
                this.jobEmail = jobEmail;
        }

        public String getMobileEmail() {
                return mobileEmail;
        }

        public void setMobileEmail(String mobileEmail) {
                this.mobileEmail = mobileEmail;
        }

        public String getInstantsMsg() {
                return instantsMsg;
        }

        public void setInstantsMsg(String instantsMsg) {
                this.instantsMsg = instantsMsg;
        }

        public String getWorkMsg() {
                return workMsg;
        }

        public void setWorkMsg(String workMsg) {
                this.workMsg = workMsg;
        }

        public String getRemark() {
                return remark;
        }

        public void setRemark(String remark) {
                this.remark = remark;
        }

        public String getLastname() {
                return lastname;
        }

        public void setLastname(String lastname) {
                this.lastname = lastname;
        }

        public String getPhoneticFirstName() {
                return phoneticFirstName;
        }

        public void setPhoneticFirstName(String phoneticFirstName) {
                this.phoneticFirstName = phoneticFirstName;
        }

        public String getCompany() {
                return company;
        }

        public void setCompany(String company) {
                this.company = company;
        }

        public String getJobTitle() {
                return jobTitle;
        }

        public void setJobTitle(String jobTitle) {
                this.jobTitle = jobTitle;
        }

        public String getNickName() {
                return nickName;
        }

        public void setNickName(String nickName) {
                this.nickName = nickName;
        }

        public String getHomeStreet() {
                return homeStreet;
        }

        public void setHomeStreet(String homeStreet) {
                this.homeStreet = homeStreet;
        }

        public String getStreet() {
                return street;
        }

        public void setStreet(String street) {
                this.street = street;
        }

        public String getOtherStreet() {
                return otherStreet;
        }

        public void setOtherStreet(String otherStreet) {
                this.otherStreet = otherStreet;
        }

        @Override
    public String toString() {
        return "ContactBean{" +
                "mobile='" + mobile + '\'' +
                ", lastname='" + lastname + '\'' +
                ", company='" + company + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                '}';
    }

    /*    }*/
}
