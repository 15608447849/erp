package drug.erp.intercepter;
import framework.server.Interceptor;
import framework.server.IceSessionContext;
import util.GsonUtils;
import util.Log4j;

/**
 * @Author: leeping
 * @Date: 2019/8/5 15:21
 */
public class UserSessionInterceptor implements Interceptor {
    @Override
    public boolean intercept(IceSessionContext context) {
        //根据 token,尝试获取用户信息
        String token = context.getToken();
        UserSession userSession = UserSession.tryGetUserInfo(token);
        context.putObject(userSession);
        return false;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
