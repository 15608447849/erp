package drug.erp.beans;

/**
 * @Author: leeping
 * @Date: 2019/9/6 17:34
 * 用户资源对象
 */
public class UserRes {
    public int code;//资源码
    public String name;//资源名
    int type;//资源类型:0-不可见 1-不可操作
    public int[] roleCodeArr;//角色复合码数组
}
