package jdbc.imp;

import jdbc.define.option.JDBCSessionFacade;
import jdbc.define.option.SessionOption;
import jdbc.define.option.JDBCSessionFacadeWrap;
import jdbc.define.session.JDBCSessionManagerAbs;
import jdbc.define.sync.SyncTask;
import jdbc.define.tuples.Tuple2;

import java.sql.Connection;
import java.util.List;

/**
 * @Author: leeping
 * @Date: 2019/8/18 1:15
 * 复制主从同步
 */
public class MasterSlaveSyncJDBCFacadeWrap extends JDBCSessionFacadeWrap{

    public MasterSlaveSyncJDBCFacadeWrap(JDBCSessionFacade op) {
        super(op);
    }

    @Override
    public int execute(String sql, Object[] params) {
        SyncTask task = new SyncTask(sql,params,"execute");
        op.getManager().addTask(task);
        int res = op.execute(sql,params);
        if (res>0) {
            op.getManager().sendTask(task);
        } else {
            op.getManager().cancel(task);
        }
        return res;
    }

    @Override
    public int executeTransaction(List<String> sqlList, List<Object[]> paramList) {
        SyncTask task = new SyncTask(sqlList,paramList,"transaction");
        op.getManager().addTask(task);
        int res = op.executeTransaction(sqlList,paramList);
        if (res>0) {
            op.getManager().sendTask(task);
        } else {
            op.getManager().cancel(task);
        }
        return res;
    }

    @Override
    public Tuple2<Integer, Object[]> insertAlsoGetGenerateKeys(String insetSql, Object[] params, int[] columnIndexes) {
        SyncTask task = new SyncTask(insetSql,params,"execute");
        op.getManager().addTask(task);
        Tuple2<Integer, Object[]> tuple2 = op.insertAlsoGetGenerateKeys(insetSql,params,columnIndexes);
        if (tuple2.getValue0() >0 ) op.getManager().sendTask(task);
        return tuple2;
    }
}
