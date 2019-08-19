package com.drug.api;

import com.drug.dao.TestApiImps;
import com.drug.intercepter.Permission;
import framework.server.IceApi;
import framework.server.IceSessionContext;
import framework.server.Result;

/**
 * @Author: leeping
 * @Date: 2019/8/5 14:55
 */
public abstract class TestModule {
    @Permission(ignore = true)
    @IceApi(imp = TestApiImps.class,detail = "测试接口")
    public abstract Result callback(IceSessionContext context);

}
