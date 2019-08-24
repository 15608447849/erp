package drug.erp.handle;
import drug.erp.api.TestModuleI;
import drug.erp.constant.DBConst;
import drug.erp.intercepter.UserSession;
import framework.server.IceSessionContext;
import jdbc.imp.TomcatJDBC;
import util.Log4j;

import java.util.Random;

/**
 * @Author: leeping
 * @Date: 2019/8/5 18:24
 */
public class TestApiImps implements TestModuleI {
    @Override
    public void currentUser(IceSessionContext context) {
        try {
            Thread.sleep(new Random().nextInt(500)+10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        UserSession userSession = context.getObject(UserSession.class);
        Log4j.info("获取当前访问客户端的用户信息: " + userSession);
        if (userSession != null){
            context.getResult().success(userSession);
        }else{
            context.FAIL("用户不存在");
        }
    }

    @Override
    public void addNewUser(IceSessionContext context) {
        String[] arr = context.getArrayParam();
        String sql = "INSERT INTO "+ DBConst.Table.TB_USER_INFO +" ( userid, username,userpwd,uphone)  VALUES ( ?,?,?,? )";
        int i = TomcatJDBC.DAO.update(sql,arr);
        if (i>0) {
            context.SUCCESS("已添加用户") ;
        } else {
            context.FAIL("用户添加失败,已存在或信息不正确");
        }
    }
}
