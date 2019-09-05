package drug.erp.handle;
import drug.erp.api.ResModuleI;
import drug.erp.api.RoleModuleI;
import drug.erp.api.UserModuleI;
import drug.erp.constant.DBConst;
import drug.erp.intercepter.UserSession;
import framework.server.IceSessionContext;
import jdbc.imp.TomcatJDBC;
import util.Log4j;

import javax.management.relation.Role;
import java.util.Random;

/**
 * @Author: leeping
 * @Date: 2019/8/5 18:24
 * 用户-角色-资源 实现类
 */
public class UserRoleResImp implements UserModuleI, ResModuleI, RoleModuleI {

    @Override
    public void getResource(IceSessionContext context) {

    }

    @Override
    public void createUser(IceSessionContext context) {

    }

    @Override
    public void login(IceSessionContext context) {

    }

    @Override
    public void logout(IceSessionContext context) {

    }

    @Override
    public void getUserInfo(IceSessionContext context) {

    }


    @Override
    public void updateRole(IceSessionContext context) {

    }
}
