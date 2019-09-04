package jdbc.imp;

import javafx.concurrent.Task;
import jdbc.define.log.JDBCLogger;
import jdbc.define.option.JDBCSessionFacade;
import jdbc.define.sync.SyncEnterI;
import jdbc.define.sync.SyncExitI;
import jdbc.define.sync.SyncTask;
import jdbc.define.tuples.Tuple2;
import util.GsonUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author: leeping
 * @Date: 2019/8/17 21:47
 */
public class DBSyncThreadImp extends Thread implements SyncEnterI, SyncExitI {

    private final String TABLE_NAME ="async_db_task_queue";

    private final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS `"+TABLE_NAME+"` (" +
            "  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '任务唯一标识码'," +
            "  `recode_time` datetime NOT NULL COMMENT '记录时的时间'," +
            "  `sql_list` json NOT NULL COMMENT '需要执行sql语句列表'," +
            "  `param_list` json NOT NULL COMMENT '需要执行sql语句对应的参数列表'," +
            "  `method_flag` char(15) NOT NULL COMMENT '需要执行的dao层方法/标识'," +
            "  `state` smallint(5) unsigned NOT NULL DEFAULT '0' COMMENT '需要执行的sql当前状态,0-待执行确认,1-确认执行,10-未设置同步,20-执行失败'," +
            "  `success_db_list` json DEFAULT NULL COMMENT '已执行的db列表'," +
            "  `fail_cause` char(200) DEFAULT '' COMMENT '失败原因'," +
            "  PRIMARY KEY (`id`) USING BTREE" +
            ") ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC COMMENT='数据库主从同步保证最终一致性持久化消息队列表';";

    private final String INSET = "INSERT INTO "+TABLE_NAME+" ( sql_list, param_list, method_flag, recode_time )  VALUES (?,?,?,NOW());";

    private final String UPDATE = "UPDATE "+TABLE_NAME+" SET state=?,success_db_list=?,fail_cause=? WHERE id=?;";

    private final String DELETE = "DELETE FROM "+TABLE_NAME+" WHERE id=?;";

    private final String SELECT = "SELECT id, sql_list, param_list, method_flag, state, success_db_list FROM "+TABLE_NAME+" WHERE state=1 ORDER BY recode_time ASC LIMIT 0,1;";

    private final String SELECT_ERROR = "SELECT id, sql_list, param_list, method_flag, state, success_db_list FROM "+TABLE_NAME+" WHERE state=20 ORDER BY recode_time ASC LIMIT 0,1000;";

    private volatile boolean isRunning = false;

    private final TomcatJDBCPool pool;//当前关联得数据池对象

    DBSyncThreadImp(TomcatJDBCPool pool){
        this.pool = pool;
        setName("同步线程-"+pool.getDataBaseName()+ "-" +pool.getSeq());
    }

    /* 通知执行 */
    private void notifyExecute() {
        synchronized (TABLE_NAME){
            TABLE_NAME.notify();
        }
    }

    /* 堵塞执行 */
    private void blockExecute(){
        synchronized (TABLE_NAME){
            try {
                TABLE_NAME.wait() ;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void launchSync() {
        if (!isRunning){
            createTable();
            start();
        }
    }

    /* 创建表 */
    private void createTable() {
        JDBCSessionFacade facade = new JDBCSessionFacade(pool);
        int i = facade.execute(CREATE_TABLE,null);
        if (i == 0) isRunning = true;
    }

    @Override
    public void addTask(SyncTask task) {
        //插入一条需要同步的数据库操作信息
        String sqlListJson = GsonUtils.toJson(task.getSqlList());
        String paramListJson = GsonUtils.toJson(task.getParamList());
        String methodFlag = task.getMethodFlag();
        JDBCSessionFacade facade = new JDBCSessionFacade(pool);
        Tuple2<Integer, Object[]>  tuple = facade.insertAlsoGetGenerateKeys(INSET,new Object[]{sqlListJson,paramListJson, methodFlag},null);
        if ( tuple.getValue0() > 0) {
            task.setId(Integer.parseInt(tuple.getValue1()[0].toString()));
        }
    }

    @Override
    public void sendTask(SyncTask task) {
        //修改数据库同步操作状态->可执行状态
        int id = task.getId();
        String successDbList = GsonUtils.toJson(new String[]{ pool.getIdentity() });
        JDBCSessionFacade facade = new JDBCSessionFacade(pool);
        int i = facade.execute(UPDATE,new Object[]{1,successDbList,"",id});
        if (i>0){
            task.setState(1);
        }
       notifyExecute();
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
        notifyExecute();
    }

    @Override
    public SyncTask tryTakeTask() {
        //查询 状态 = 1 的所有任务
        JDBCSessionFacade facade = new JDBCSessionFacade(pool);

        if (facade.checkDBConnectionValid()) {
            List<Object[]> list = facade.query(SELECT,null);
            if (list.size()==1){
                return recoverTaskByDb(list.get(0));
            }
        }
        blockExecute();
        return isRunning ? tryTakeTask() : null;
    }
    /* 根据数据库数据还原任务 */
    private SyncTask recoverTaskByDb(Object[] o) {
        int id = Integer.parseInt(o[0].toString());
        List<String> sqlList = GsonUtils.toList(o[1].toString(),String.class);
        List<Object[]> paramList = GsonUtils.toList(o[2].toString(),Object[].class);
        String methodFlag = o[3].toString();
        int state = Integer.parseInt(o[4].toString());
        List<String> successDbList = GsonUtils.toList(o[5].toString(),String.class);

        SyncTask task = new SyncTask();
        task.setId(id);
        task.setSqlList(sqlList);
        task.setParamList(paramList);
        task.setMethodFlag(methodFlag);
        task.setState(state);
        task.setSuccessDbList(successDbList);
        return task;
    }

    private ReentrantLock lock = new ReentrantLock();

    @Override
    public void executeSync(SyncTask task) {
        try{
            lock.lock();

            if (task.getState() != 1) return;

            int id = task.getId();

            //当前库连接池
            JDBCSessionFacade facade = new JDBCSessionFacade(pool);

            if (!facade.checkDBConnectionValid()) return; //当前无效连接

            //获取关联的同步数据库
            List<TomcatJDBCPool> list = TomcatJDBC.getSpecDataBasePoolList(pool.getDataBaseName());
            if (list.size() == 1 && list.get(0).getIdentity().equals(pool.getIdentity())) {
                JDBCLogger.print("【警告】数据库: "+ pool.getDataBaseName()+" , 未设置备份库,同步任务将于3天后删除(暂未实现删除功能)");
                facade.execute(UPDATE,new Object[]{10,GsonUtils.toJson(task.getSuccessDbList()),"",id});
            }

            outer:
            for (TomcatJDBCPool p : list) {

                for (String identity : task.getSuccessDbList()) {
                    if (identity.equals(p.getIdentity())) continue outer;
                }

                Tuple2<Boolean,String> result = executeSql(task, new JDBCSessionFacade(p));

                boolean isSuccess = result.getValue0();

                if (isSuccess) {
                    task.setState(1);//执行中
                    //记录成功的db标识
                    task.getSuccessDbList().add(p.getIdentity());
                } else {
                    //设置同步失败状态
                    task.setState(20);
                }

                //记录到数据库
                facade.execute(UPDATE, new Object[]{task.getState(), GsonUtils.toJson(task.getSuccessDbList()),result.getValue1() ,id});

                JDBCLogger.print("【同步】目标数据库: " + p.getAddress()+" "+p.getDataBaseName() + " ,编号: " + p.getSeq() + ", "+task +" 执行结果: " + isSuccess);

                if (!isSuccess) break; //如果失败,结束同步
            }

            if (task.getState() == 1){
                //执行完成 且没有执行失败的记录 , 删除
                facade.execute(DELETE,new Object[]{id});
            }

        }finally {
            lock.unlock();
        }
    }

    /* 执行同步 */
    private Tuple2<Boolean,String> executeSql(SyncTask task, JDBCSessionFacade dao) {

        boolean result = false;
        String error = "";
        try {
            if (dao.checkDBConnectionValid()){
                List<String> sqlList = task.getSqlList();
                List<Object[]> paramList  = task.getParamList();
                if (sqlList.size() == 1){
                    Object[] params = paramList!=null && paramList.size() == 1 ? paramList.get(0) : null;
                    //非事务 执行
                    int i = dao.execute(sqlList.get(0),params);
                    if (i > 0 ) result = true;
                }else if (sqlList.size() > 1){
                    int i = dao.executeTransaction(sqlList,paramList);
                    if (i > 0 ) result = true;
                }
            }else{
                error = "数据库:"+dao.getManager().getAddress()+" "+dao.getManager().getDataBaseName()+" "+dao.getManager().getSeq()+" 连接失败!";
            }
        } catch (Exception e) {
            JDBCLogger.error("同步数据异常",e);
            error = e.toString();
        }
        return new Tuple2<>(result,error);
    }


    @Override
    public void tryRecover() {
        //打开定时器定时执行
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {

                JDBCSessionFacade facade = new JDBCSessionFacade(pool);
                if (facade.checkDBConnectionValid()){
                    try {
                        List<Object[]> list = facade.query(SELECT_ERROR,null);
                        for (Object[] o : list){
                            SyncTask t = recoverTaskByDb(o);
                            JDBCLogger.print("【恢复异常】 数据: "+ t);
                            t.setState(1); //设置其状态为执行中
                            executeSync(t);
                        }
                    } catch (Exception e) {

                    }
                }

            }
        }, 30 * 1000,60 * 1000 );
    }

    @Override
    public void run() {
        JDBCLogger.print("【通知】数据库: "+pool.getAddress()+" "+pool.getDataBaseName()+" , seq = "+pool.getSeq()+" , 启动数据同步: "+ isRunning +" , "+getName() );
        //尝试恢复
        tryRecover();
        while (isRunning){
            try {
                //间隔查询数据库-是否存在需要同步的任务 , 优先执行队列任务, 如果没有,则去数据库尝试获取持久化任务
                SyncTask task = tryTakeTask();
                if (task != null){
                    executeSync(task);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
