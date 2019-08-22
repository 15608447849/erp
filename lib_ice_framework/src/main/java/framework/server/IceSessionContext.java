package framework.server;

import Ice.Application;
import Ice.Current;
import Ice.Logger;

import framework.gen.inf.IParam;
import framework.gen.inf.IRequest;
import objectref.ObjectPoolManager;
import objectref.ObjectRefUtil;
import util.GsonUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;


/**
 * @Author: leeping
 * @Date: 2019/3/8 14:32
 */
public class IceSessionContext {
    //日志输出
    private final Logger logger;
    //当前连接对象
    private final Current current;
    //请求参数
    private final IParam param;
    //实际调用者
    private Class<?> callClass;
    private Method callMethod;
    IceDebug debug;
    Api api;
    //客户端返回值
    private Result result;

    public Logger getLogger() {
        return logger;
    }

    public Result getResult() {
        if (result == null) result = Result.factory();
        return result;
    }

    public Method getCallMethod() {
        return callMethod;
    }

    public Result FAIL(String message){
        return getResult().fail(message);
    }

    public Result SUCCESS(String message){
        return getResult().success(message);
    }

    public Result INTERCEPT(String cause){
        return getResult().intercept(cause);
    }

    public IceSessionContext print(Object message){
        if (message!=null){
            logger.print(message.toString());
        }
        return this;
    }


    private final HashMap<Class<?>,Object> additionalObjectMap = new HashMap<>();

    public String getServerName() {
        if (current!=null){
            return current.id.name;
        }
        return null;
    }

    public String[] getRemoteIpAndPoint() {
        if (current!=null){
            return current.con._toString().split("\\n")[1].split("=")[1].trim().split(":");
        }
        return null;
    }


    IceSessionContext(Current current, IRequest request) throws Exception  {
        if (request == null) throw new IllegalArgumentException("客户端请求不正确");
        this.current = current;
        this.logger = Application.communicator().getLogger();
        this.param = request.param;
        initialize(request);
    }

    private void initialize(IRequest request) throws Exception{
        String packagePath = request.pkg;
        String className = request.cls;
        String methodName = request.method;

        String classPath = packagePath + "."+ className;

        this.callClass = Class.forName(classPath);

        this.callMethod = callClass.getDeclaredMethod(methodName,this.getClass());
        this.debug = callMethod.getAnnotation(IceDebug.class);
        this.api = callMethod.getAnnotation(Api.class);
        if (api==null) throw new IllegalAccessException("未定义的API接口声明");
        Class<?> imp = api.imp();
        if (imp != void.class) this.callClass = imp;
//        this.callMethod = callClass.getMethod(refMed,this.getClass());
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
    Object call() throws Exception{
        Object caller = getCaller(callClass);
        Object value;
        try{
            value = ObjectRefUtil.callMethod(caller, callMethod,new Class[]{this.getClass()},this);
            if (value == null) value = getResult();
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
   @SuppressWarnings("unchecked")
   public <T> T  getObject(Class<? extends T> cls){
        Object o = additionalObjectMap.get(cls);
        if (o != null && o.getClass() == cls){
            return (T)o ;
        }
        return null;
   }

   //获取数组参数
   public String[] getArrayParam(){
        if (param!=null){
            if (param.arrays!=null && param.arrays.length>0) return param.arrays;
        }
        return null;
   }

    private String getJsonParam() {
        if (param!=null){
            if (param.json!=null && param.json.length()>0) return param.json;
        }
        return null;
    }

    public <T> T getJsonParamConvertObject(Class<T> cls) {
        if (param!=null){
            if (param.json!=null && param.json.length()>0) return GsonUtils.jsonToJavaBean(param.json,cls);
        }
        return null;
    }

    public <T> List<T> getJsonParamConvertList(Class<T> cls) {
        if (param!=null){
            if (param.json!=null && param.json.length()>0) return GsonUtils.json2List(param.json,cls);
        }
        return null;
    }

}
