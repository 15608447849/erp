package jdbc.define.option;

import jdbc.define.exception.JDBCException;
import jdbc.define.session.SessionManagerI;
import jdbc.define.tuples.Tuple2;

import java.util.List;

/**
 * @Author: leeping
 * @Date: 2019/8/18 10:56
 */
public class JDBCSessionFacadeWrap implements DaoApi{
    protected JDBCSessionFacade op;

    public JDBCSessionFacadeWrap(JDBCSessionFacade op) {
        if (op == null) throw new JDBCException("invalid db connection pool object.");
        this.op = op;
    }

    @Override
    public List<Object[]> query(String sql, Object[] params) {
        return op.query(sql,params);
    }

    @Override
    public <T> List<T> query(String sql, Object[] params, Class<T> beanClass) {
        return op.query(sql,params,beanClass);
    }

    @Override
    public int execute(String sql, Object[] params) {
        return op.execute(sql,params);
    }

    @Override
    public int executeTransaction(List<String> sqlList, List<Object[]> paramList) {
        return op.executeTransaction(sqlList,paramList);
    }

    @Override
    public Tuple2<Integer, Object[]> insertAlsoGetGenerateKeys(String insetSql, Object[] params, int[] columnIndexes) {
        return op.insertAlsoGetGenerateKeys(insetSql,params,columnIndexes);
    }
}
