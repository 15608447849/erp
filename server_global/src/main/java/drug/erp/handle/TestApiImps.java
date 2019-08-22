package drug.erp.handle;

import drug.erp.api.TestModule;
import drug.erp.bean.vo.UserSession;
import framework.server.IceSessionContext;

/**
 * @Author: leeping
 * @Date: 2019/8/5 18:24
 */
public class TestApiImps extends TestModule {
    @Override
    public void callback(IceSessionContext context) {
        UserSession session = context.getObject(UserSession.class);
        context.print("接入用户: "+ session);
        context.getResult().success("访问成功",session);
    }
}
