package com.drug.intercepter;

import com.drug.bean.UserSession;
import framework.server.IServerInterceptor;
import framework.server.IceSessionContext;

import java.lang.reflect.Method;

/**
 * @Author: leeping
 * @Date: 2019/8/5 15:15
 * 权限检查
 */
public class AccessInterceptor implements IServerInterceptor {

    @Override
    public InterceptorResult interceptor(IceSessionContext context) {
        boolean isAccess = true; //默认允许
        String cause = null;
        Method m = context.method;
        Permission permission = m.getAnnotation(Permission.class);
        /*判断接口是否对用户权限进行拦截 条件:
         * 1.调用方法没有注解一定拦截权限
         * 2.注解显示不忽略拦截,拦截权限(默认不忽略)
         */
        if(permission == null || !permission.ignore()){
            UserSession userSession = context.getObject(UserSession.class);
            if(userSession == null) {
                isAccess = false;
                cause = "没有登陆的用户信息";
            }else{

            }
        }
        return new IServerInterceptor.InterceptorResult(!isAccess,cause);
    }

    @Override
    public int getPriority() {
        return 1;
    }
}
