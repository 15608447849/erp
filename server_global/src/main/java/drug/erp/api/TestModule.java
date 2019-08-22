package drug.erp.api;

import drug.erp.handle.TestApiImps;
import drug.erp.intercepter.Permission;
import framework.server.Api;
import framework.server.IceSessionContext;

/**
 * @Author: leeping
 * @Date: 2019/8/5 14:55
 * 测试模块
 */
public abstract class TestModule {

    @Permission(ignore = true)
    @Api(detail = "测试接口",imp = TestApiImps.class)
    public abstract void callback(IceSessionContext context);

}
