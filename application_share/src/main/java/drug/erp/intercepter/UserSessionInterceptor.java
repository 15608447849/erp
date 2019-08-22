package drug.erp.intercepter;

import drug.erp.bean.vo.UserSession;
import framework.server.Interceptor;
import framework.server.IceSessionContext;

/**
 * @Author: leeping
 * @Date: 2019/8/5 15:21
 */
public class UserSessionInterceptor implements Interceptor {
    @Override
    public boolean intercept(IceSessionContext context) {
        //初始化用户信息
        UserSession userSession = new UserSession();
            userSession.name = "李世平";
            userSession.phone = "15608447849";
        context.putObject(UserSession.class,userSession);

        return false;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
