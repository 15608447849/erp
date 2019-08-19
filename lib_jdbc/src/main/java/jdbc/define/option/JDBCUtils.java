package jdbc.define.option;

import jdbc.define.exception.JDBCException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @Author: leeping
 * @Date: 2019/8/17 15:54
 */
public class JDBCUtils {

    private JDBCUtils(){}

    //关闭连接
    public static void closeSqlObject(AutoCloseable... closeables) {
        for (AutoCloseable obj : closeables) {
            if (obj != null) {
                try {
                    obj.close();
                } catch (Exception e) {
                    throw new JDBCException(e);
                }
            }
        }
    }

    public static PreparedStatement prepareStatement(Connection conn, String sql, boolean isCallable) throws SQLException {
        return !isCallable ? conn.prepareStatement(sql) : conn.prepareCall(sql);
    }

    public static void setParameters(PreparedStatement pst, Object... params) throws SQLException {
        if (params!=null) setInputParameters(pst, 1, params);
    }

    public static  void setInputParameters(PreparedStatement pst, int startPos, Object... params) throws SQLException {
        for(int i = 0; i < params.length; ++i) {
            Object o = params[i];
            if (o != null) {
                pst.setObject(i + startPos, o);
            } else {
                pst.setNull(i + startPos, 0);
            }
        }

    }

    public static final void registerOutputParameters(CallableStatement cst, int startPos, int[] types) throws SQLException {
        for(int i = 0; i < types.length; ++i) {
            int t = types[i];
            cst.registerOutParameter(i + startPos, t);
        }
    }
}
