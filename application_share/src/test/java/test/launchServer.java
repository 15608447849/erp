package test;

import Ice.InitializationData;
import Ice.Util;
import framework.iceabs.IceLog4jLogger;

import java.util.Arrays;

/**
 * @Author: leeping
 * @Date: 2019/8/23 16:57
 */
public class launchServer {


    public static void main(String[] args) {
//  "--Ice.Config=C:\\IDEAWORK\\erp\\Z_SERVERSTART\\node-global/./node-db/servers/globalServer-group-box-1/config/config",
        args = new String[]{
                "--Ice.MessageSizeMax=4096",
                "--Ice.Admin.ServerId=globalServer-box-1",
                "--Ice.Admin.Endpoints=tcp -h localhost",
                "--Ice.ProgramName=globalServer-box-1",
                "--IceBox.LoadOrder=globalService_1",
                "--Ice.PrintStackTraces=1",
                "--Ice.Trace.Retry=2",
                "--Ice.Trace.Network=2",
                "--Ice.Trace.ThreadPool=2",
                "--Ice.Trace.Locator=2",
                "--Ice.Default.Locator=DemoIceGrid/Locator:tcp -h 192.168.1.145 -p 4061:ws -h 192.168.1.145 -p 4062",
                "--IceBox.Service.globalService_1=framework.server.ServerIceBoxImp globalServer",
                "--IceBox.Service.globalService_1.Endpoints=tcp:ws",
                "--IceBox.Service.globalService_1.AdapterId=globalService_1",
                "--IceBox.Service.globalService_1.ReplicaGroupId=globalServer",
//                "--IceBox.Service.Ice.Default.Locator=DemoIceGrid/Locator:tcp -h 192.168.1.145 -p 4061:ws -h 192.168.1.145 -p 4062",
                "--Ice.Default.Locator=DemoIceGrid/Locator:tcp -h 192.168.1.145 -p 4061:ws -h 192.168.1.145 -p 4062",
//                "--Ice.Warn.UnknownProperties:0"
        };
        args = new String[]{

                "--Ice.Admin.ServerId=globalServer-box-1",
                "--Ice.Admin.Endpoints=tcp -h localhost",
                "--Ice.ProgramName=globalServer-box-1",
                "--IceBox.LoadOrder=globalService_1",
                "--Ice.PrintStackTraces=1",
                "--Ice.Trace.Retry=2",
                "--Ice.Trace.Network=2",
                "--Ice.Trace.ThreadPool=2",
                "--Ice.Trace.Locator=2",
                "--Ice.Default.Locator=DemoIceGrid/Locator:tcp -h 127.0.0.1 -p 4061:ws -h 127.0.0.1 -p 4062",

                "--IceBox.Service.globalService_1=framework.server.ServerIceBoxImp globalServer --Ice.Config='C:\\IDEAWORK\\erp\\Z_SERVERSTART\\node-global/./node-db/servers/globalServer-box-1/config/config_globalService_1'",
        };
//        args = new String[]{
//                "--Ice.Config=C:/IDEAWORK/erp/Z_SERVERSTART/node-global/node-db/servers/globalServer-box-1/config/config"
//        };
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
