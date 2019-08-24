package drug.erp.intercepter;

import framework.server.Interceptor;
import framework.server.IceSessionContext;

import java.lang.reflect.Method;

/**
 * @Author: leeping
 * @Date: 2019/8/5 15:15
 * 权限检查
 */
public class AccessInterceptor implements Interceptor {

    @Override
    public boolean intercept(IceSessionContext context) {
        boolean isAccess = true; //默认允许访问接口
        String cause = null;
        Method m = context.getCallMethod();
        Permission permission = m.getAnnotation(Permission.class);
        /*判断接口是否对用户权限进行拦截 条件:
         * 1.调用方法没有注解一定拦截权限
         * 2.注解显示不忽略拦截,拦截权限(默认不忽略)
         */
        if(permission == null || !permission.ignore()){
            UserSession userSession = context.getObject(UserSession.class);
            if(userSession == null) {
                isAccess = false;
                context.INTERCEPT("用户未登录,找不到用户信息");
            }
        }
        return !isAccess ;
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
