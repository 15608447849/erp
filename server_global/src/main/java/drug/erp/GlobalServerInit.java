package drug.erp;



import framework.server.Initializer;

import util.Log4j;

/**
 * @Author: leeping
 * @Date: 2019/8/5 15:01
 */
public class GlobalServerInit implements Initializer {
    @Override
    public void initialization(String serverName,String groupName) {
        Log4j.info("初始化 - " + serverName+" , 执行者: "+ getClass());
    }

    @Override
    public int priority() {
        return 1;
    }

}
