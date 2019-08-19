package framework.server;

import Ice.Application;
import Ice.Current;
import Ice.Logger;

import framework.gen.inf.IParam;
import framework.gen.inf.IRequest;
import objectref.ObjectPoolManager;
import objectref.ObjectRefUtil;

import java.lang.reflect.Method;
import java.util.HashMap;


/**
 * @Author: leeping
 * @Date: 2019/3/8 14:32
 */
public class IceSessionContext {

    public String remoteIp;
    public int remotePoint;
    public String serverName;
    public Logger logger;
    public Current current;
    public String refPkg;
    public String refCls;
    public String refMed;
    public IParam param;
    public Class callerCls;
    public Method method;
    public IceDebug debug;

    public IceApi api;


    private HashMap<Class<?>,Object> additionalObjectMap = new HashMap<>();

    public IceSessionContext(Current current, IRequest request) throws Exception  {
        if (current!=null){
            this.serverName = current.id.name;
            this.current = current;
            String[] arr = current.con._toString().split("\\n")[1].split("=")[1].trim().split(":");
            this.remoteIp = arr[0];
            this.remotePoint = Integer.parseInt(arr[1]);
        }
        this.logger = Application.communicator().getLogger();
        this.refPkg = request.pkg;
        this.refCls = request.cls;
        this.refMed = request.method;
        this.param = request.param;
        String classPath = this.refPkg + "."+this.refCls;
        this.callerCls = Class.forName(classPath);
        this.method = callerCls.getDeclaredMethod(refMed,this.getClass());
        debug = method.getAnnotation(IceDebug.class);
        api = method.getAnnotation(IceApi.class);
        if (api==null){
            throw new IllegalAccessException("未定义的API接口调用");
        }
        this.callerCls = api.imp();
        this.method = callerCls.getMethod(refMed,this.getClass());
    }

    private static Object getCaller(Class cls) throws Exception {
        Object obj = ObjectPoolManager.get().getObject(cls.getName()); //对象池中获取对象
        if (obj == null)  obj = ObjectRefUtil.createObject(cls,null);//创建
        return obj;
    }

    private static void putCaller(Object obj){
        ObjectPoolManager.get().putObject(obj.getClass().getName(),obj);//使用完毕之后再放入池中,缓存对象
    }



    /**调用具体方法*/
    public Object call() throws Exception{
        Object caller = getCaller(callerCls);
        Object value;
        try{
            value = ObjectRefUtil.callMethod(caller,method,new Class[]{this.getClass()},this);
        }catch (Exception e){
            throw e;
        }finally {
            putCaller(caller);
        }
        return value;
    }

   public void putObject(Class<?> cls,Object object){
        additionalObjectMap.put(cls,object);
   }

   //获取一个对象
   public <T> T  getObject(Class<? extends T> cls){
        return (T) additionalObjectMap.get(cls);
   }

}
