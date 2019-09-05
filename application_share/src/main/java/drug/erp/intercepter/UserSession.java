package drug.erp.intercepter;

import drug.erp.constant.DBConst;
import jdbc.imp.TomcatJDBC;
import util.StringUtils;

import java.util.List;

/**
 * @Author: leeping
 * @Date: 2019/8/5 15:20
 * 用户信息
 */
public class UserSession {
    private UserSession(){};

    public int userid;//用户唯一id
    public String uaccount;//账号
    public String upwd;//密码
    public String uname;//用户名
    public String uphone;//手机号码
    public int[] roleCodeArr;//角色复合码数组

    /* 先查询缓存,缓存不存在,远程调用获取用户信息 */
    static UserSession tryGetUserInfo(String token) {
        if (token!=null){

        }
       return  null;
    }

}
