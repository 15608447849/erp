package framework.server;

import Ice.Object;
import framework.iceabs.IceBoxServerAbs;
import objectref.ObjectRefUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * 应用入口
 */
public class ServerIceBoxImp extends IceBoxServerAbs {
    public static String rpcGroupName;
    public static ApiServerImps INSTANCE;
    private static final List<Initializer> list = new ArrayList<>();

    @Override
    protected Object specificServices(String serverName) {
        if (INSTANCE == null){
            INSTANCE =  new ApiServerImps(_communicator,serverName);
        }
        return INSTANCE;
    }

    @Override
    protected void addRpcGroup(String rpcName) {
        rpcGroupName = rpcName;
    }
    @Override
    protected void findJarAllClass(String classPath) throws Exception {
        Class<?> cls = Class.forName(classPath);
        if ( !cls.equals(Initializer.class) && Initializer.class.isAssignableFrom(cls)){
            list.add(((Initializer) ObjectRefUtil.createObject(cls,null)));
//            _communicator.getLogger().print("添加初始化类: "+ classPath);
        }
        if (INSTANCE != null){
            ((ObjectRefUtil.IClassScan)INSTANCE).callback(classPath);
        }
    }

    @Override
    protected void initialization() {
        if (INSTANCE == null) return;
        list.sort(Comparator.comparingInt(Initializer::priority));
        for (Initializer o : list){
            try {
                o.initialization(INSTANCE.serverName,rpcGroupName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
