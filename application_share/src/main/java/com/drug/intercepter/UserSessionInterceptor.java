package com.drug.intercepter;

import com.drug.bean.UserSession;
import framework.server.IServerInterceptor;
import framework.server.IceSessionContext;

/**
 * @Author: leeping
 * @Date: 2019/8/5 15:21
 */
public class UserSessionInterceptor implements IServerInterceptor {
    @Override
    public InterceptorResult interceptor(IceSessionContext context) {

        //初始化用户信息
        UserSession userSession = new UserSession();
            userSession.name = "李世平";
            userSession.phone = "15608447849";
        context.putObject(UserSession.class,userSession);
        return new InterceptorResult(false,null);
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
