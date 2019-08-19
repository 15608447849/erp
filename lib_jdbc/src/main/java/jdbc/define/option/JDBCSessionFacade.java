package jdbc.define.option;
import jdbc.define.exception.JDBCException;
import jdbc.define.log.JDBCLogger;
import jdbc.define.session.JDBCSessionManagerAbs;
import jdbc.define.session.TransactionIsolationLevel;
import jdbc.define.sync.SyncEnterI;
import jdbc.define.sync.SyncTask;
import jdbc.define.tuples.Tuple2;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import static jdbc.define.option.JDBCUtils.*;


/**
 * @Author: leeping
 * @Date: 2019/8/16 11:30
 */
public class JDBCSessionFacade extends SessionOption<JDBCSessionManagerAbs, Connection> {

    public JDBCSessionFacade(JDBCSessionManagerAbs manager) {
        super(manager);
    }

    @Override
    public List<Object[]> query(String sql, Object[] params) {
//        JDBCLogger.print(getManager().getAddress() + " , " +getManager().getDataBaseName()+"\n\t" + sql+" , "+ Arrays.toString(params));
        JDBCLogger.print(getManager().getAddress() + " , " +getManager().getDataBaseName());
        List<Object[]> result = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement pst = null;
        try {
            Connection conn = this.getSession();
            pst = prepareStatement(conn, sql, false);
            setParameters(pst, params);
            rs = pst.executeQuery();
            int cols = rs.getMetaData().getColumnCount(); //行数
            while(rs.next()) {
                Object[] objs = new Object[cols];
                for(int i = 0; i < cols; ++i) {
                    objs[i] = rs.getObject(i + 1);
                }
                result.add(objs);
            }
        } catch (SQLException e) {
            throw new JDBCException(sql,e);
        } finally {
            closeSqlObject(pst, rs);
        }
        return result;
    }

    @Override
    public <T> List<T> query(String sql, Object[] params, Class<T> beanClass) {
//        JDBCLogger.print(getManager().getAddress() + " , " +getManager().getDataBaseName()+"\n\t" + sql+" , "+ Arrays.toString(params));
//        JDBCLogger.print(getManager().getAddress() + " , " +getManager().getDataBaseName());
        List<T> result = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement pst = null;
        try {
            Connection conn = this.getSession();
            pst = prepareStatement(conn, sql, false);
            setParameters(pst, params);
            rs = pst.executeQuery();
            while(rs.next()) {
                try {
                    T bean = beanClass.newInstance();
                    Field[] fields = bean.getClass().getDeclaredFields();
                    //遍历属性
                    for(Field field : fields){
                        try {
                            String name = field.getName();//获取属性的名字
                            RowName rowName = field.getAnnotation(RowName.class);
                            if (rowName!=null){
                                name = rowName.value();
                            }
                            if (name == null || name.length()==0) continue;
                            Type type = field.getGenericType();
                            String typeName = type.getTypeName();

                            if (typeName.lastIndexOf(".")>0){
                                typeName = typeName.substring(typeName.lastIndexOf(".")+1);
                            }
                            typeName = "get" + typeName.substring(0, 1).toUpperCase() + typeName.substring(1);
                            Method method = rs.getClass().getMethod(typeName,String.class);//得到方法对象
                            Object value = method.invoke(rs,name);
                            if (value == null) continue;
                            field.setAccessible(true);
                            field.set(bean, value);
                        } catch (Exception ignored) { }
                    }
                    result.add(bean);
                } catch (Exception ignored) { }
            }
        } catch (SQLException e) {
            throw new JDBCException(e);
        } finally {
            closeSqlObject(pst, rs);
        }
        return result;
    }

    @Override
    public int execute(String sql, Object[] params) {
//        JDBCLogger.print(getManager().getAddress() + " , " +getManager().getDataBaseName()+"\n\t" + sql+" , "+ Arrays.toString(params));
//        JDBCLogger.print(getManager().getAddress() + " , " +getManager().getDataBaseName() +" , "+ sql);
        int result = 0;
        PreparedStatement pst = null;
        try {
            Connection conn = getSession();
            pst = prepareStatement(conn, sql, false);
            setParameters(pst, params);
            result = pst.executeUpdate();
        } catch (SQLException e) {
            if (e instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException){
                JDBCLogger.print("插入数据失败,存在相同唯一键数据: "+ e);
            }else{
                throw new JDBCException(e);
            }
        } finally {
            closeSqlObject(pst);
        }
        return result;
    }

    @Override
    public int executeTransaction(List<String> sqlList, List<Object[]> paramList) {
        if (sqlList.size() != paramList.size()) throw new JDBCException("parameters do not match. If there is no value, use 'null' placeholder");
        JDBCSessionManagerAbs m = getManager();
        int res = 0;
        if (m.isTransactionInvoking()) throw new JDBCException("transaction in progress");

        m.setTransactionInvoking(true);
        try {
            m.beginTransaction();

            for (int i = 0;i<sqlList.size();i++){
                res = execute(sqlList.get(i),paramList.get(i));
                if (res == 0) throw new SQLException("unaffected rows , sql: "+ sqlList.get(0)+" , param: "+ Arrays.toString(paramList.get(i)));
            }

            m.commit();
        }catch (Exception e){
            try {
                m.rollback();
            } catch (Exception ignored) {
            }
            throw new JDBCException(e);
        }finally {
            m.setTransactionInvoking(false);
        }
        return res;
    }

    @Override
    public Tuple2<Integer, Object[]> insertAlsoGetGenerateKeys(String insetSql, Object[] params, int[] columnIndexes) {
//        JDBCLogger.print(getManager().getAddress() + " , " +getManager().getDataBaseName()+"\n\t" + insetSql+" , "+ Arrays.toString(params));
        int result;
        Object[] generateKeys;
        ResultSet rs = null;
        PreparedStatement pst = null;
        try {
            Connection conn = getSession();
            if (columnIndexes == null) {
                pst = conn.prepareStatement(insetSql, 1);
                generateKeys = new Object[1];
            } else {
                pst = conn.prepareStatement(insetSql, columnIndexes);
                generateKeys = new Object[columnIndexes.length];
            }
            setParameters(pst, params);
            result = pst.executeUpdate();
            if (result > 0) {
                rs = pst.getGeneratedKeys();
                if(rs.next()) {
                    for(int i = 0; i < generateKeys.length; ++i) {
                        generateKeys[i] = rs.getObject(i + 1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new JDBCException(e);
        } finally {
            closeSqlObject(pst, rs);
        }
        return new Tuple2<>(result,generateKeys);
    }


}
