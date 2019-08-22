package framework.iceabs;

import Ice.Object;
import Ice.*;
import IceBox.Service;
import framework.server.Initializer;
import framework.server.IceProperties;
import objectref.ObjectRefUtil;
import properties.abs.ApplicationPropertiesBase;
import util.Log4j;

import java.lang.Exception;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static Ice.Application.communicator;

public abstract class IceBoxServerAbs implements Service {

    private ObjectAdapter _adapter;
    protected Communicator _communicator;

    @Override
    public void start(String name, Communicator communicator, String[] args) {
        Log4j.info("服务参数: " + Arrays.toString(args));
        String repGroup = args.length >=1 ? args[0] : null;
        ApplicationPropertiesBase.initStaticFields(IceProperties.class);
        initIceLogger(name,(CommunicatorI) communicator);
        _communicator = communicator;
        _adapter = _communicator.createObjectAdapter(name);
        //创建servant
        Ice.Object object = specificServices(name);
        //关联servant
        relationID(name,object,communicator,repGroup);
        //初始化应用
        initApplication();
        //激活适配器
        _adapter.activate();
    }

    //初始化 系统应用
    private void initApplication() {
        long time = System.currentTimeMillis();
        ObjectRefUtil.scanJarAllClass(classPath -> {
            try {
                if (classPath.startsWith(IceProperties.pkgSrv)){
                    findJarAllClass(classPath);
                }
            } catch (Exception ignored) { }

        });
        initialization();
        _communicator.getLogger().print("应用初始化耗时:"+ (System.currentTimeMillis() - time)+"ms");
//        IceDispatchInterceptor.getInstance().startServer();
    }

    private void initIceLogger(String name,CommunicatorI ic) {
        Logger logger = ic.getInstance().initializationData().logger;
        if (!(logger instanceof IceLog4jLogger)){
            ic.getInstance().initializationData().logger = new IceLog4jLogger(name);
            initIceLogger(name, (CommunicatorI) communicator());
        }
    }

    private void relationID(String serverName,Ice.Object object,Communicator communicator,String groupName) {
//        IceDispatchInterceptor interceptor = IceDispatchInterceptor.getInstance();
        Identity identity = communicator.stringToIdentity(serverName);
//        _adapter.add(interceptor.addIceObject(identity,object),identity);
        _adapter.add(object,identity);
        //配置rpc组信息
        if (groupName == null || groupName.length()==0) return ;
        identity = communicator.stringToIdentity(groupName);
//        _adapter.add(interceptor.addIceObject(identity,object),identity);
        _adapter.add(object,identity);
        addRpcGroup(groupName);
        _communicator.getLogger().print("服务: "+serverName +" ,加入负载均衡组 " + groupName);
    }

    protected abstract Object specificServices(String serverName);

    //sub imps
    protected abstract void addRpcGroup(String rpcName);

    protected abstract void findJarAllClass(String classPath) throws Exception;

    protected abstract void initialization();

    @Override
    public void stop() {
        _adapter.destroy();
        _communicator.getLogger().print("服务销毁");
    }

    public static void main(String[] args) {
//        args = new String[]{"[--IceBox.Server 666", "--Ice.MessageSizeMax=4096", "--Ice.Config=C:\\IDEAWORK\\erp\\Z_SERVERSTART\\node-global/./node-db/servers/globalServer-group-box-1/config/config"};
        args = new String[]{"--IceBox.Service.globalService_1=framework.server.ServerIceBoxImp 123456", "--Ice.MessageSizeMax=4096", "--Ice.Config=C:\\IDEAWORK\\erp\\Z_SERVERSTART\\node-global/./node-db/servers/globalServer-group-box-1/config/config"};
        IceLog4jLogger log4jLogger = new IceLog4jLogger("system");
        log4jLogger.print("启动服务 ,args = " + Arrays.toString(args));
        InitializationData initData = new InitializationData();
        initData.properties = Util.createProperties();
        initData.properties.setProperty("Ice.Admin.DelayCreation", "1");
        initData.logger = log4jLogger;
        IceBox.Server server = new IceBox.Server();
        int code = server.main("IceBox.Server", args, initData);
        System.exit(code);
    }

}
