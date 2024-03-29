package drug.erp.api;

import drug.erp.handle.UserRoleResImp;
import framework.server.Api;
import framework.server.IceSessionContext;

/**
 * @Author: leeping
 * @Date: 2019/9/5 22:25
 */
public interface ResModuleI {
    @Api(detail = "用户获取资源,返回所有资源对象",imp = UserRoleResImp.class)
    void getResource(IceSessionContext context);
}
