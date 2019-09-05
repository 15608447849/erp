package drug.erp.api;

import drug.erp.handle.UserRoleResImp;
import framework.server.Api;
import framework.server.IceSessionContext;

/**
 * @Author: leeping
 * @Date: 2019/9/5 22:25
 */
public interface ResModuleI {
    @Api(detail = "用户获取资源,如果携带上级资源码,返回子集,否则返回二级菜单资源",imp = UserRoleResImp.class)
    void getResource(IceSessionContext context);
}
