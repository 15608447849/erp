package drug.erp.api;

import drug.erp.handle.UserRoleResImp;
import drug.erp.intercepter.Permission;
import framework.server.Api;
import framework.server.IceSessionContext;

/**
 * @Author: leeping
 * @Date: 2019/8/23 16:08
 */
public interface UserModuleI {

    @Api(detail = "管理员-创建用户,关联角色",imp = UserRoleResImp.class)
    void createUser(IceSessionContext context);

    @Permission(ignore = true)
    @Api(detail = "登陆,成功返回用户信息及随机token",imp = UserRoleResImp.class)
    void login(IceSessionContext context);

    @Permission(ignore = true)
    @Api(detail = "登出,移除用户缓存信息及token",imp = UserRoleResImp.class)
    void logout(IceSessionContext context);

    @Api(detail="前台程序通过token可获取用户信息",imp = UserRoleResImp.class)
    void getUserInfo(IceSessionContext context);

}
