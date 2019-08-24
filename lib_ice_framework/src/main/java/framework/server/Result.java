package framework.server;

import java.util.HashMap;

import static jdk.nashorn.tools.Shell.SUCCESS;
import static util.StringUtils.printExceptInfo;

public class Result{

   private Result(){ }

   static Result factory(){
      //以后添加缓存等
      return new Result();
   }

    interface CODE{
      int INTERCEPT = -2;
      int FAIL = -1;
      int ERROR = 0;
      int SUCCESS = 200;
   }

   private int code = CODE.FAIL;

   private String message;

   private Object data;

   private String error;

   private HashMap<String,Object> map;

   // 分页信息
   private Integer pageNumber;
   private Integer pageSize;
   private Integer pageTotal;

   //是否成功
   public boolean isSuccess(){
      return code == CODE.SUCCESS;
   }

   //是否拦截
   public boolean isIntercept(){
      return code == CODE.INTERCEPT;
   }

   public Result success(String message,Object data){
      this.code = SUCCESS;
      this.message = message;
      this.data = data;
      return this;
   }

   public Result success(Object data){
      this.code = SUCCESS;
      this.data = data;
      return this;
   }

   public Result success(String message){
      return success(message,null);
   }

   public Result fail(String message){
      return fail(message,null);
   }

   public Result fail(String message,Object data){
      this.code = CODE.FAIL;
      this.message = message;
      this.data = data;
      return this;
   }

   //添加参数
   public Result addParam(String key,Object value){
      if (map == null) map = new HashMap<>();
      map.put(key,value);
      return this;
   }

   public Result setMessage(String message){
      this.message = message;
      return this;
   }

   public Result setData(Object data){
      this.data = data;
      return this;
   }

   //拦截
   public Result intercept(String cause){
      this.code = CODE.INTERCEPT;
      this.message = cause;
      return this;
   }

   //错误
   public Result error(String msg,Throwable e) {
      this.code = CODE.ERROR;
      this.message = msg;
      this.error = printExceptInfo(e);
      return this;
   }

   /* 设置查询后的分页信息 */
   public Result setPageInfo(int pageNumber,int pageSize,int pageTotal) {
      this.pageNumber = pageNumber;
      this.pageSize = pageSize;
      this.pageTotal = pageTotal;
      return this;
   }

}
