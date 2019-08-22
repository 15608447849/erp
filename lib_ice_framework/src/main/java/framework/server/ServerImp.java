package framework.server;

import Ice.Communicator;
import Ice.Current;
import Ice.Logger;
import framework.gen.inf.IRequest;
import objectref.ObjectRefUtil;
import util.GsonUtils;
import util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static util.StringUtils.printExceptInfo;

/**
 * ice bind type = ::inf::Interfaces
 * 接口实现
 */
public class ServerImp extends IMServerImps {

    //拦截器
    private ArrayList<Interceptor> interceptorList  = new ArrayList<>();

    //服务名
    String serverName;

    //日志
    private Logger logger;

    //服务对象的反射包路径
    private String pkgPath;

    ServerImp(Communicator communicator,String serverName) {
        super(communicator,serverName);
        this.serverName = serverName;
        this.logger = communicator.getLogger();
        this.pkgPath = IceProperties.pkgSrv+".api";

    }

    //会被回调- 全类查询所有拦截器对象
    @Override
    public void callback(String classPath)  {
        super.callback(classPath);
        try {
            //循环类
            Class<?> cls = Class.forName(classPath);
            if ( !cls.equals(Interceptor.class) && Interceptor.class.isAssignableFrom(cls)){
                //拦截器
                Interceptor iServerInterceptor = (Interceptor)ObjectRefUtil.createObject(classPath);
                interceptorList.add(iServerInterceptor);
                print(Thread.currentThread()+"添加拦截器:"+ iServerInterceptor.getClass());
                interceptorList.sort(Comparator.comparingInt(Interceptor::getPriority));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //打印参数
    private String printParam(IRequest request, Current __current,String ditail) {
            try {
                StringBuilder sb = new StringBuilder();
                if (__current != null) {
                    sb.append(__current.con.toString().split("\n")[1].replace("remote address =","客户端地址:"));
                }else{
                    sb.append("本地调用");
                }
                sb.append("\t接口路径: " + request.pkg +"." + request.cls +"."+request.method+"\t说明: "+ ditail);
                if(!StringUtils.isEmpty(request.param.token)){
                    sb.append( "\ntoken:\t"+ request.param.token);
                }
                if(!StringUtils.isEmpty(request.param.json)){
                    sb.append("\njson:\t" + request.param.json );
                }
                if(request.param.arrays!=null &&request.param.arrays.length>0){
                    sb.append("\narray:\t" + Arrays.toString(request.param.arrays));
                }
                if(request.param.pageIndex > 0 && request.param.pageNumber > 0){
                    sb.append("\npaging:\t"+ request.param.pageIndex +" , " +request.param.pageNumber);
                }
                return sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        return "调用信息异常";
    }

    //检测,查询配置的包路径 - 优先 客户端指定的全路径
    private void check(IRequest request) throws IllegalArgumentException {

        if (StringUtils.isEmpty(request.method)) throw new IllegalArgumentException("没有指定相关服务方法");

        if (StringUtils.isEmpty(request.cls)) throw new IllegalArgumentException("没有指定相关服务类路径");

        if (StringUtils.isEmpty(request.pkg)){
            if (StringUtils.isEmpty(pkgPath)) throw new IllegalArgumentException("没有指定相关服务包路径");
            request.pkg = pkgPath;
        }
    }

    //拦截
    private boolean interceptor(IceSessionContext context){
        for (Interceptor iServerInterceptor : interceptorList) {
            if ( iServerInterceptor.intercept(context)) return true;
        }
        return false;
    }

    //打印结果
    private String printResult(Object result) {
        String resultString;
        if (result instanceof String){
            resultString = String.valueOf(result);
        }else{
            resultString = GsonUtils.javaBeanToJson(result);
        }
        return resultString;
    }



    //客户端 - 接入服务
    @Override
    public String accessService(IRequest request, Current __current) {
        Object result;
        boolean inPrint = true;
        boolean timePrint = false;
        boolean outPrint = false;
        try {
            check(request);
            //产生context
            IceSessionContext context = new IceSessionContext(__current,request);
            if (context.debug != null){
                inPrint = context.debug.inPrint();
                outPrint = context.debug.outPrint();
                timePrint = context.debug.timePrint();
            }
            if (inPrint) logger.print(printParam(request,__current,context.api.detail()));

            boolean isInterceptor = interceptor(context);//拦截器
            if (isInterceptor) {
                Result r = context.getResult();
               if (!r.isIntercept()){
                   r = Result.factory().intercept("已拦截请求");
               }
                result = r;
            }else{
                //具体业务实现调用 返回值不限制
                long time = System.currentTimeMillis();
                result = context.call();
                if (timePrint) logger.print("调用耗时: " + (System.currentTimeMillis() - time) +" ms\n");
            }

        } catch (Exception e) {
            Throwable targetEx = e;
            if (e instanceof InvocationTargetException) {
                targetEx =((InvocationTargetException)e).getTargetException();
            }
            logger.error(printExceptInfo(targetEx));
            result = Result.factory().error("请求执行错误",targetEx);
        }
        String resultString =  printResult(result);
        if (outPrint) logger.print("返  回  信  息 :\t " +resultString );
        return resultString;
    }


}
