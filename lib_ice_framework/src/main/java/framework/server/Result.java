package framework.server;




import java.util.HashMap;

import static jdk.nashorn.tools.Shell.SUCCESS;
import static util.StringUtils.printExceptInfo;

public class Result{

    interface CODE{
      int INTERCEPT = -2;
      int FAIL = -1;
      int ERROR = 0;
      int SUCCESS = 200;
   }

   private int code = CODE.FAIL;

   private String message = null;

   private Object data;

   private HashMap<String,Object> map;

   // 分页信息
   private Integer pageNumber;
   private Integer pageSize;
   private Integer pageTotal;

   public boolean isSuccess(){
      return code == CODE.SUCCESS;
   }

   public boolean isIntercept(){
      return code == CODE.INTERCEPT;
   }

   public Result success(String message,Object data){
      this.code = SUCCESS;
      this.message = message;
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


   public Result intercept(String cause){
      this.code = CODE.INTERCEPT;
      this.message = cause;
      return this;
   }


   public Result error(String msg,Throwable e) {
      this.code = CODE.ERROR;
      this.message = msg;
      this.data = printExceptInfo(e);
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
