package framework.server;

import Ice.Object;
import framework.iceabs.IceBoxServerAbs;
import objectref.ObjectRefUtil;

/**
 * 应用入口
 */
public class ServerIceBoxImp extends IceBoxServerAbs {

    public static String rpcGroupName;

    public static ServerImp INSTANCE;

    @Override
    protected Object specificServices() {
        if (INSTANCE == null){
            INSTANCE =  new ServerImp(_communicator,_serverName);
        }
        return INSTANCE;
    }


    @Override
    protected void addRpcGroup(String rpcName) {
        rpcGroupName = rpcName;
    }

    @Override
    protected void findJarAllClass(String classPath) {
        if (INSTANCE != null){
            ((ObjectRefUtil.IClassScan)INSTANCE).callback(classPath);
        }
    }
}
