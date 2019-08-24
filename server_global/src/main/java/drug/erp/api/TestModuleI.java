package drug.erp.api;

import drug.erp.handle.TestApiImps;
import drug.erp.intercepter.Permission;
import framework.server.Api;
import framework.server.IceSessionContext;

/**
 * @Author: leeping
 * @Date: 2019/8/23 16:08
 */
public interface TestModuleI {
    @Permission(ignore = true)
    @Api(detail = "打印当前用户",imp = TestApiImps.class)
    void currentUser(IceSessionContext context);

    @Permission(ignore = true)
    @Api(detail = "添加用户信息",imp = TestApiImps.class)
    void addNewUser(IceSessionContext context);

}
