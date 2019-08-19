package com.drug.dao;


import jdbc.define.option.RowName;

/**
 * @Author: leeping
 * @Date: 2019/8/16 17:44
 */
public class UserDaoBean {

    @RowName("oid")
    String id;
    @RowName("userid")
    String userid;
    String username ;
    String userpw;
    String realname;
    String roleid;
    String ss;

    @Override
    public String toString() {
        return "UserDaoBean{" +
                "id='" + id + '\'' +
                ", userid='" + userid + '\'' +
                ", username='" + username + '\'' +
                ", userpw='" + userpw + '\'' +
                ", realname='" + realname + '\'' +
                ", roleid='" + roleid + '\'' +
                ", cstatus='" + ss + '\'' +
                '}';
    }
}


