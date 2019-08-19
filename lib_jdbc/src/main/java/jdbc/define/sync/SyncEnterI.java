package jdbc.define.sync;

import java.io.Closeable;

/**
 * @Author: leeping
 * @Date: 2019/8/17 20:38
 * 数据库同步接口
 */
public interface SyncEnterI {
    void launchSync();
    void addTask(SyncTask task);
    void sendTask(SyncTask task);
    void cancel(SyncTask task);
    void closeDestroy();
}
