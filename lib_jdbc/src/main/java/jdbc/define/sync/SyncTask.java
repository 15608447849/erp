package jdbc.define.sync;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: leeping
 * @Date: 2019/8/17 20:47
 */
public class SyncTask {

    //标识-唯一
    private int id;
    //执行的sql集合
    private List<String> sqlList;
    private List<Object[]> paramList;
    private String methodFlag;
    private int state;
    private List<String> successDbList;

    public SyncTask() { }

    public SyncTask(String sql, Object[] params, String methodFlag) {
        sqlList = new ArrayList<>();
        sqlList.add(sql);
        paramList = new ArrayList<>();
        paramList.add(params);
        this.methodFlag = methodFlag;
    }

    public SyncTask(List<String> sqlList, List<Object[]> paramList, String methodFlag) {
        this.sqlList = sqlList;
        this.paramList = paramList;
        this.methodFlag = methodFlag;
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

    public String getMethodFlag() {
        return methodFlag;
    }

    public void setMethodFlag(String methodFlag) {
        this.methodFlag = methodFlag;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<String> getSuccessDbList() {
        return successDbList;
    }

    public void setSuccessDbList(List<String> successDbList) {
        this.successDbList = successDbList;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("id").append("= ").append(id).append(" , ");
        s.append("SQL").append("= ").append(sqlList).append(" , ");
        if (paramList!=null){
            s.append("参数集").append("= ");
            for (Object[] o : paramList){
                s.append(Arrays.toString(o));
            }
            s.append(" , ");
        }
        s.append("methodFlag").append("= ").append(methodFlag).append(" , ");
        s.append("state").append("= ").append(state).append(" , ");
        s.append("successDbList").append("= ").append(successDbList);

        return s.toString();
    }
}
