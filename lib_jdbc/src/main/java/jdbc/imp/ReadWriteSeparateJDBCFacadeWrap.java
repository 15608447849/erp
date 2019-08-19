package jdbc.imp;

import jdbc.define.log.JDBCLogger;
import jdbc.define.option.JDBCSessionFacade;
import jdbc.define.option.JDBCSessionFacadeWrap;
import jdbc.define.session.JDBCSessionManagerAbs;
import jdbc.define.sync.SyncTask;
import jdbc.define.tuples.Tuple2;

import java.util.List;

/**
 * @Author: leeping
 * @Date: 2019/8/18 1:15
 * 读写分离
 */
public class ReadWriteSeparateJDBCFacadeWrap extends MasterSlaveSyncJDBCFacadeWrap{
    private JDBCSessionFacade reader;
    public ReadWriteSeparateJDBCFacadeWrap(JDBCSessionFacade op) {
        super(op);
        reader = TomcatJDBC.getFacade(op.getManager().getDataBaseName(),false);
    }

    @Override
    public List<Object[]> query(String sql, Object[] params) {
        try {
            List<Object[]> list = reader.query(sql,params);
            if (list.size() == 0) throw new Exception();
            return list;
        } catch (Exception e) {
            return op.query(sql,params);
        }
    }


    @Override
    public <T> List<T> query(String sql, Object[] params, Class<T> beanClass) {
        try {
            List<T> list = reader.query(sql,params,beanClass);
            if (list.size() == 0) throw new Exception();
            return list;
        } catch (Exception e) {
            return op.query(sql,params,beanClass);
        }
    }
}
