package jdbc.define.sync;

/**
 * @Author: leeping
 * @Date: 2019/8/17 22:09
 */
public interface SyncExitI {
    //尝试获取一个可同步的任务
    SyncTask tryTakeTask();

    void executeSync(SyncTask task);
}
