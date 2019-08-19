package framework.iceabs;

import Ice.Object;
import Ice.*;
import IceBox.Service;
import framework.server.IIceInitialize;
import framework.server.IceProperties;
import objectref.ObjectRefUtil;
import properties.abs.ApplicationPropertiesBase;

import java.lang.Exception;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static Ice.Application.communicator;

public abstract class IceBoxServerAbs implements Service {

    //服务名
    protected String _serverName;

    private static ObjectAdapter _adapter;

    protected Communicator _communicator;


    @Override
    public void start(String name, Communicator communicator, String[] args) {
        ApplicationPropertiesBase.initStaticFields(IceProperties.class);
        initIceLogger(name,(CommunicatorI) communicator);
        _communicator = communicator;
        _serverName = name;
        _adapter = _communicator.createObjectAdapter(_serverName);
        //创建servant
        Ice.Object object = specificServices();
        //关联servant
        relationID(object,communicator);
        //激活适配器
        _adapter.activate();
        _communicator.getLogger().print("启动服务:" + _serverName );
        initApplication(name);
    }

    //初始化 系统应用
    private void initApplication(String serverName) {
        //scan all class
        _communicator.getLogger().print(serverName + " 开始初始化系统");
        long time = System.currentTimeMillis();
        List<IIceInitialize> initList = new ArrayList<>();

        ObjectRefUtil.scanJarAllClass(classPath -> {
                   try {
                       if (classPath.startsWith(IceProperties.pkgSrv)){
                           Class<?> cls = Class.forName(classPath);
                           if ( !cls.equals(IIceInitialize.class) && IIceInitialize.class.isAssignableFrom(cls)){
                               initList.add(((IIceInitialize) ObjectRefUtil.createObject(cls,null)));
                               _communicator.getLogger().print("添加初始化类: "+ classPath);
                           }
                           findJarAllClass(classPath);
                       }

                   } catch (Exception ignored) { }

        });
        initList.sort(Comparator.comparingInt(IIceInitialize::priority));
        StringBuilder sb = new StringBuilder();
        for (IIceInitialize o : initList){
            try {
                o.startUp(serverName);
            } catch (Exception e) {
                _communicator.getLogger().error(o.getClass().getSimpleName()+" 初始化错误:"+ e);
            }
            sb.append(" ").append(o.getClass().getSimpleName()).append(" ").append(">");
        }
        sb.deleteCharAt(sb.length()-1);
        _communicator.getLogger().print("执行初始化:"+sb.toString());
        _communicator.getLogger().print("初始化完成,耗时:"+ (System.currentTimeMillis() - time)+"ms");
        IceServiceDispatchInterceptor.getInstance().startServer();
    }



    private void initIceLogger(String name,CommunicatorI ic) {
        Logger logger = ic.getInstance().initializationData().logger;
        if (!(logger instanceof IceLog4jLogger)){
            ic.getInstance().initializationData().logger = new IceLog4jLogger(name);
            initIceLogger(name, (CommunicatorI) communicator());
        }
    }

    private void relationID(Ice.Object object,Communicator communicator) {
        Identity identity = communicator.stringToIdentity(_serverName);
        _adapter.add(IceServiceDispatchInterceptor.getInstance().addIceObject(identity,object),identity);
        //查询是否存在组配置信息 (暂时只能 一个服务关联到一个 rpc组 ,理论上 一个服务可关联到多个组, 暂不实现)
        String name = IceProperties.getRepSrvMap().get(_serverName);
        if (name == null) return;
        identity = communicator.stringToIdentity(name);
        _adapter.add(IceServiceDispatchInterceptor.getInstance().addIceObject(identity,object),identity);
        addRpcGroup(name);
        _communicator.getLogger().print("服务: "+_serverName +" ,加入负载均衡组 " + name);
    }

    protected abstract Object specificServices();

    protected void addRpcGroup(String rpcName){
        //pass
    }

    protected void findJarAllClass(String classPath){

    }

    @Override
    public void stop() {
        _adapter.destroy();
        _communicator.getLogger().print("销毁服务:" + _serverName);
    }

}
