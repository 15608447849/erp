package com.drug;

import framework.server.IIceInitialize;
import jdbc.define.log.JDBCLogger;
import util.Log4j;

import static Ice.Application.communicator;

/**
 * @Author: leeping
 * @Date: 2019/3/18 15:37
 */
public class SystemInitialize implements IIceInitialize {
    @Override
    public void startUp(String serverName){
        try {
            //异步日志
            System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        } catch (Exception e) {
//            e.printStackTrace();
            communicator().getLogger().error(serverName+" , 初始化错误: "+ e);
        }
        JDBCLogger.setLogger(new JDBCLogger.JDBCSessionLogInterface() {
            @Override
            public void info(String message) {
                Log4j.info(message);
            }

            @Override
            public void error(String desc, Throwable e) {
                Log4j.error(desc,e);
            }
        });
    }

    @Override
    public int priority() {
        return 0;
    }
}
