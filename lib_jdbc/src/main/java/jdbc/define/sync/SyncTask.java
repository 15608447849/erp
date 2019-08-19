package jdbc.define.sync;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: leeping
 * @Date: 2019/8/17 20:47
 */
public class SyncTask {
    //标识-唯一
    int id;
    //执行的sql集合
    List<String> sqlList;
    List<Object[]> paramList;
    String methodName;
    int state;


    public SyncTask() { }

    public SyncTask(String sql, Object[] params, String methodName) {
        sqlList = new ArrayList<>();
        sqlList.add(sql);
        paramList = new ArrayList<>();
        paramList.add(params);
        this.methodName = methodName;
    }

    public SyncTask(List<String> sqlList, List<Object[]> paramList, String methodName) {
        this.sqlList = sqlList;
        this.paramList = paramList;
        this.methodName = methodName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<String> getSqlList() {
        return sqlList;
    }

    public void setSqlList(List<String> sqlList) {
        this.sqlList = sqlList;
    }

    public List<Object[]> getParamList() {
        return paramList;
    }

    public void setParamList(List<Object[]> paramList) {
        this.paramList = paramList;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
