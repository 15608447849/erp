package framework.iceabs;

import Ice.Object;
import Ice.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static Ice.Application.communicator;

/**
 * @Author: leeping
 * @Date: 2019/3/7 12:01
 */
public class IceDispatchInterceptor extends DispatchInterceptor {

    /**服务质量监控单例模式对象*/
    private static final IceDispatchInterceptor instance = new IceDispatchInterceptor();

    /**用来存放我们需要拦截的Ice服务对象，Key为服务ID，value为对应的Servant*/
    private Map<Identity, Object> map ;

    private IceDispatchInterceptor() {
        map = new ConcurrentHashMap<>();
    }

    public static IceDispatchInterceptor getInstance(){
        return instance;
    }

    /**
     * 添加服务
     */
    public DispatchInterceptor addIceObject(Ice.Identity id, Ice.Object iceObj){
//        map.put(id, iceObj);
//        communicator().getLogger().print("监听服务:" + id.name);
        return this;
    }

    public void removeIceObject(Identity id){
        map.remove(id);
    }

    private boolean systemRunning = false;

    public void startServer(){
        systemRunning = true;
    }



    /**
     * 移除服务
     */

    @Override
    public DispatchStatus dispatch(Request request) {
        if (!systemRunning)  return DispatchStatus.DispatchUserException;
//            long time = System.currentTimeMillis();
//            Current current = request.getCurrent();
            Identity identity = request.getCurrent().id;
            Object object = map.get(identity);
//        communicator().getLogger()
//                .print("\n\t\t- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - \t\t");
            DispatchStatus status = object.ice_dispatch(request);
//            if (current.operation .equals("accessService")) communicator().getLogger()
//                    .print("调用状态: "+ statusString(status) + " , 调用耗时: " + (System.currentTimeMillis() - time) +" ms\n");
            return status;
    }

    private String statusString(DispatchStatus status){
        if (status == DispatchStatus.DispatchOK) return "成功";
        if (status == DispatchStatus.DispatchAsync) return "异步派发";
         if (status == DispatchStatus.DispatchUserException) return "错误";
         return "未知";
    }
}
