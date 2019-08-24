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
        Log4j.info("服务组: " + Arrays.toString(args));
        String repGroup = args.length >=1 ? args[0] : null;
        ApplicationPropertiesBase.initStaticFields(IceProperties.class);
        initIceLogger(name,(CommunicatorI) communicator);
        _communicator = communicator;
        _adapter = _communicator.createObjectAdapter(name);
        //关联servant
        relationID(name,communicator,repGroup);
        //初始化应用
        initApplication();
        //激活适配器
        _adapter.activate();
    }

    //初始化 系统应用
    private void initApplication() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initIceLogger(String name,CommunicatorI ic) {
        Logger logger = ic.getInstance().initializationData().logger;
        if (!(logger instanceof IceLog4jLogger)){
            ic.getInstance().initializationData().logger = new IceLog4jLogger(name);
            initIceLogger(name, (CommunicatorI) communicator());
        }
    }

    private void relationID(String serverName,Communicator communicator,String groupName) {
        //创建servant
        Ice.Object object = specificServices(serverName);
        Identity identity = communicator.stringToIdentity(serverName);
        _adapter.add(object,identity);
        //配置rpc组信息
        if (groupName == null || groupName.length()==0) return ;
        identity = communicator.stringToIdentity(groupName);
        _adapter.add(object,identity);
        addRpcGroup(groupName);
        _communicator.getLogger().print("服务: "+serverName +" ,加入组: " + groupName);
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

}
