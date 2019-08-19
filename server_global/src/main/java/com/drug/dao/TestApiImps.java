package com.drug.dao;

import com.drug.api.TestModule;
import com.drug.bean.UserSession;
import framework.server.IceSessionContext;
import framework.server.Result;

/**
 * @Author: leeping
 * @Date: 2019/8/5 18:24
 */
public class TestApiImps extends TestModule {
    @Override
    public Result callback(IceSessionContext context) {
        UserSession session = context.getObject(UserSession.class);
        context.logger.print("接入: "+ session);
        return new Result().success("访问成功");
    }
}
