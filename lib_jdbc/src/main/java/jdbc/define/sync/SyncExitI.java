package jdbc.define.sync;

/**
 * @Author: leeping
 * @Date: 2019/8/17 22:09
 */
public interface SyncExitI {
    //尝试获取一个可同步的任务
    SyncTask tryTakeTask();
    //执行
    void executeSync(SyncTask task);
    //尝试恢复错误数据
    void tryRecover();
}
