package jdbc.imp;

import com.google.gson.Gson;
import javafx.concurrent.Task;
import jdbc.define.exception.JDBCException;
import jdbc.define.log.JDBCLogger;
import jdbc.define.option.JDBCSessionFacade;
import jdbc.define.sync.SyncEnterI;
import jdbc.define.sync.SyncExitI;
import jdbc.define.sync.SyncTask;
import jdbc.define.tuples.Tuple2;
import util.GsonUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author: leeping
 * @Date: 2019/8/17 21:47
 */
public class DBSyncThreadImp extends Thread implements SyncEnterI, SyncExitI {

    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `async_db_task_queue` (" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '任务唯一标识码'," +
            "  `sql_list` json NOT NULL COMMENT '需要执行sql语句列表'," +
            "  `param_list` json NOT NULL COMMENT '需要执行sql语句对应的参数列表'," +
            "  `method_name` char(15) NOT NULL COMMENT '需要执行的dao层方法名'," +
            "  `state` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '需要执行的sql当前状态,0-待执行确认,1-确认执行,2-执行失败,下次重试'," +
            "  PRIMARY KEY (`id`) USING BTREE" +
            ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='数据库主从同步保证最终一致性持久化消息队列表';";

    private final String INSET = "INSERT INTO async_db_task_queue ( sql_list, param_list,method_name )  VALUES (?,?,?);";

    private final String UPDATE = "UPDATE async_db_task_queue SET state=? WHERE id=?;";

    private final String DELETE = "DELETE FROM async_db_task_queue WHERE id=?;";

    private final String SELECT = "SELECT * FROM async_db_task_queue WHERE state>=?;";

    private volatile boolean isRunning = false;

    private final TomcatJDBCPool pool;

    private boolean isOne = true;

    private final ConcurrentLinkedQueue<SyncTask> queue = new ConcurrentLinkedQueue<>();


    DBSyncThreadImp(TomcatJDBCPool pool){
        this.pool = pool;
        setName("db-sync-"+pool.getDataBaseName() + "-" + getId());
    }

    @Override
    public void launchSync() {
        if (!isRunning){
            createTable();
            start();
        }
    }

    private void createTable() {
        JDBCSessionFacade facade = new JDBCSessionFacade(pool);
        int i = facade.execute(CREATE_TABLE,null);
        if (i == 0) isRunning = true;
    }



    @Override
    public void addTask(SyncTask task) {

        //插入一条需要同步的数据库操作信息
        String sqlListJson = GsonUtils.javaBeanToJson(task.getSqlList());
        String paramListJson = GsonUtils.javaBeanToJson(task.getParamList());
        String methodName = task.getMethodName();
        JDBCSessionFacade facade = new JDBCSessionFacade(pool);
        Tuple2<Integer, Object[]>  tuple = facade.insertAlsoGetGenerateKeys(INSET,new Object[]{sqlListJson,paramListJson,methodName},null);
        if (tuple.getValue0() > 0) {
            task.setId(Integer.parseInt(tuple.getValue1()[0].toString()));
        }
    }

    @Override
    public void sendTask(SyncTask task) {
        //修改数据库同步操作状态->可执行状态
        int id = task.getId();
        JDBCSessionFacade facade = new JDBCSessionFacade(pool);
        int i = facade.execute(UPDATE,new Object[]{1,id});
        if (i>0){
            task.setState(1);
        }
        queue.offer(task);
        synchronized (queue){
            queue.notify();
        }
    }

    @Override
    public void cancel(SyncTask task) {
        //删除一条数据
        int id = task.getId();
        JDBCSessionFacade facade = new JDBCSessionFacade(pool);
        facade.execute(DELETE,new Object[]{id});
    }

    @Override
    public void closeDestroy() {
        isRunning = false;
        synchronized (queue){
            queue.notify();
        }
    }

    @Override
    public SyncTask tryTakeTask() {
        //查询 状态 >= 1 的所有任务

        JDBCSessionFacade facade = new JDBCSessionFacade(pool);

        List<Object[]> list = facade.query(SELECT,new Object[]{1});

        SyncTask task;
        for (Object[] o : list){
            task = new SyncTask();
            int id = Integer.parseInt(o[0].toString());
            List<String> sqlList = GsonUtils.json2List(o[1].toString(),String.class);
            List<Object[]> paramList = GsonUtils.json2List(o[2].toString(),Object[].class);
            String methodName = o[3].toString();
            int state = Integer.parseInt(o[4].toString());

            task.setId(id);
            task.setSqlList(sqlList);
            task.setParamList(paramList);
            task.setMethodName(methodName);
            task.setState(state);
            queue.offer(task);
        }
        return queue.poll();
    }

    @Override
    public void executeSync(SyncTask task) {
        if (task.getState() == 0) return;

        //获取除了自己之外的其他从数据库
        boolean isSuccess = true;
        List<TomcatJDBCPool> list = TomcatJDBC.getSpecDataBasePoolList(pool.getDataBaseName());
        if (isOne ) {
            if (list.size() == 1) JDBCLogger.print("【警告】数据库: "+ pool.getDataBaseName()+" , 未设置备份库");
            isOne = false;
        }
        for (TomcatJDBCPool p : list){
            if (p.getSeq().equals(pool.getSeq())) continue;
            JDBCLogger.print("【同步】数据库组: "+ p.getDataBaseName()+" ,当前同步数据库编号: "+p.getSeq());
            isSuccess = executeSql(task,new JDBCSessionFacade(p));
        }

        int id = task.getId();
        JDBCSessionFacade facade = new JDBCSessionFacade(pool);
        if (isSuccess){
            //设置同步成功-删除,同步操作任务
            facade.execute(DELETE,new Object[]{id});
        }else{
            //设置同步失败
            facade.execute(UPDATE,new Object[]{2,id});
        }

//        JDBCLogger.print("当前同步任务: "+ task.getId() +" 结果: "+ isSuccess);
    }

    private boolean executeSql(SyncTask task, JDBCSessionFacade dao) {
        try {
            List<String> sqlList = task.getSqlList();
            List<Object[]> paramList  = task.getParamList();
            if (sqlList.size() == 1){
                Object[] params = paramList!=null && paramList.size() == 1 ? paramList.get(0) : null;
                //非事务 执行
                int i = dao.execute(sqlList.get(0),params);
                if (i > 0 ) return true;
            }else if (sqlList.size() > 1){
                int i = dao.executeTransaction(sqlList,paramList);
                if (i > 0 ) return true;
            }
        } catch (Exception e) {
            JDBCLogger.error("同步数据异常",e);
        }
        return false;
    }


    @Override
    public void run() {
        JDBCLogger.print("【通知】数据库组: "+pool.getDataBaseName()+" , seq = "+pool.getSeq()+" , 启动数据同步: "+ isRunning );
        while (isRunning){
            //间隔查询数据库-是否存在需要同步的任务 , 优先执行队列任务, 如果没有,则去数据库尝试获取持久化任务
            SyncTask task = queue.poll();
            if (task == null){
                task = tryTakeTask();
            }
            if (task == null){
                synchronized (queue){
                    synchronized (queue){
                        try {
                            queue.wait(3 * 60 * 1000L ) ; //最大休眠3分钟
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else{
                executeSync(task);
            }

        }
    }

}
