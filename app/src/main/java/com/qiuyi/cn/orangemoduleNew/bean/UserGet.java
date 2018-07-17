package com.qiuyi.cn.orangemoduleNew.bean;

/**
 * Created by Administrator on 2018/1/26.
 * gson对象,登录返回的user对象
 */
public class UserGet {
    public User user;
    public int loginResult;
    public class User{
        public Integer id;
        public String name;
        public String nickname;
        public String account;
        public String accessToken;
        public String icon;
/*        public Date createTime;*/

        @Override
        public String toString() {
            return "User{" +
                    "accessToken='" + accessToken + '\'' +
                    ", account='" + account + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "UserGet{" +
                "user=" + user +
                ", loginResult=" + loginResult +
                '}';
    }
}
