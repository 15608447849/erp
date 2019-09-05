package drug.erp.api;

import drug.erp.handle.UserRoleResImp;
import framework.server.Api;
import framework.server.IceSessionContext;

/**
 * @Author: leeping
 * @Date: 2019/9/5 22:41
 */
public interface RoleModuleI {

    @Api(detail = "新增/修改角色",imp = UserRoleResImp.class)
    void updateRole(IceSessionContext context);

}
