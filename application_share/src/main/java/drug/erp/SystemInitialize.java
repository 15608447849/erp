package drug.erp;

import drug.erp.constant.DBConst;
import framework.server.Initializer;
import jdbc.define.log.JDBCLogger;
import jdbc.imp.TomcatJDBC;
import util.Log4j;

import java.util.List;

import static Ice.Application.communicator;

/**
 * @Author: leeping
 * @Date: 2019/3/18 15:37
 */
public class SystemInitialize implements Initializer {
    @Override
    public void initialization(String serverName,String groupName){
        try {
            //异步日志
            System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        } catch (Exception e) {
            communicator().getLogger().error(serverName+" , 初始化错误: "+ e);
        }
        //设置JDBC的日志
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
        //分服务加载JDBC
        initDataBaseProp(groupName);
    }

    private void initDataBaseProp(String serverName){
        try {
            List<String> configs = DBConst.serverDBConfigFiles(serverName);
            Log4j.info(serverName+" 加载数据库连接池配置文件: "+ configs);
            TomcatJDBC.initialize(configs);
        } catch (Exception e) {
            Log4j.error(e);
        }
    }

    @Override
    public int priority() {
        return 0;
    }
}
