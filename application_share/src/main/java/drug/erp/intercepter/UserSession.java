package drug.erp.intercepter;

import drug.erp.constant.DBConst;
import jdbc.imp.TomcatJDBC;
import util.Log4j;
import util.StringUtils;

import java.util.List;

/**
 * @Author: leeping
 * @Date: 2019/8/5 15:20
 * 用户信息
 */
public class UserSession {
    private UserSession(){};

    public String name;
    public String phone;

    /* 先查询缓存,缓存不存在,远程调用获取用户信息 */
    static UserSession tryGetUserInfo(String token) {
        if (token!=null){
            String sql = "SELECT username,uphone FROM " + DBConst.Table.TB_USER_INFO +" WHERE username=?";
            List<Object[]> lines = TomcatJDBC.DAO.query(sql,new Object[]{token});
            TomcatJDBC.DAO.printLines(lines);
            if (lines.size() > 0){
                Object[] o = lines.get(0);
                UserSession u = new UserSession();
                u.name = StringUtils.obj2Str(o[0]);
                u.phone = StringUtils.obj2Str(o[1]);
                return u;
            }
        }
       return  null;
    }


    @Override
    public String toString() {
        return "UserSession{" +
                "name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
