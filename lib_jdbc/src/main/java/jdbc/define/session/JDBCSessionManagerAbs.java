package jdbc.define.session;

import jdbc.define.exception.JDBCException;
import jdbc.define.log.JDBCLogger;
import jdbc.define.sync.SyncEnterI;
import jdbc.define.sync.SyncTask;
import jdbc.imp.TomcatJDBCPool;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @Author: leeping
 * @Date: 2019/8/16 9:30
 */
public abstract class JDBCSessionManagerAbs extends SessionManagerAbs<Connection> implements SyncEnterI {

    private TransactionIsolationLevel currentTransIsoLevel;

    protected String address;
    protected String dataBaseName;
    protected Integer seq = -1;

    public String getAddress() {
        return address;
    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public Integer getSeq() {
        return seq;
    }

    @Override
    public void loadDefaultTransactionIsolationLevel() {
        try {
            Connection session = getSession();
            int level = session.getTransactionIsolation();
            this.currentTransIsoLevel = TransactionIsolationLevel.fromInt(level);
        } catch (SQLException e) {
            throw new JDBCException(e);
        } finally {
            this.closeSession();
        }
    }

    @Override
    public TransactionIsolationLevel getCurrentTransactionIsolationLevel() {
        return this.currentTransIsoLevel;
    }

    @Override
    public void setSessionTransactionIsolationLevel(TransactionIsolationLevel transIsoLevel) {
        try {
            Connection session = getSession();
            session.setTransactionIsolation(transIsoLevel.toInt());
        } catch (SQLException e) {
            throw new JDBCException(e);
        }
    }

    @Override
    public void beginTransaction() {
        Connection session = getSession();
        try {
            session.setAutoCommit(false);
        } catch (SQLException e) {
            throw new JDBCException(e);
        }
    }

    @Override
    public void commit() {
        Connection session = getSession();
        try {
            session.commit();
        } catch (SQLException e) {
            throw new JDBCException(e);
        } finally {
            try {
                session.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void rollback() {
        Connection session = getSession();
        try {
            session.rollback();
        } catch (SQLException e) {
            throw new JDBCException(e);
        } finally {
            try {
                session.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Connection getSession() {
        Connection session = super.getSession();
        try {
            if (session == null || session.isClosed()) {
                session = getInternalConnection();
                if (session == null || session.isClosed() ) throw new SQLException("db connection non-existent or closed");
                setSession(session);
            }
            return session;
        } catch (SQLException e) {
            setSession(null);
            throw new JDBCException(e);
        }
    }

    abstract protected Connection getInternalConnection() throws SQLException;

    @Override
    public void closeSession() {
        Connection session = super.getSession();
        setSession(null);
        if (session != null) {
            try {
                session.close();
            } catch (SQLException e) {
                throw new JDBCException(e);
            }
        }
    }

    @Override
    public void unInitialize() {
        closeDestroy();
        closeSession();
        super.unInitialize();
    }

    protected SyncEnterI syncEnterI = null;

    @Override
    public void launchSync() {
        try {
            if (syncEnterI!=null) syncEnterI.launchSync();
        } catch (Exception e) {
            JDBCLogger.error(null,e);
        }
    }

    @Override
    public void addTask(SyncTask task) {

        try {
            if (syncEnterI!=null) syncEnterI.addTask(task);
        } catch (Exception e) {
            JDBCLogger.error(null,e);
        }
    }

    @Override
    public void sendTask(SyncTask task) {
        try{
            if (syncEnterI!=null) syncEnterI.sendTask(task);
        } catch (Exception e) {
            JDBCLogger.error(null,e);
        }
    }

    @Override
    public void cancel(SyncTask task) {
        try{
            if (syncEnterI!=null) syncEnterI.cancel(task);
        } catch (Exception e) {
            JDBCLogger.error(null,e);
        }
    }

    @Override
    public void closeDestroy() {
        try {
            if (syncEnterI!=null) syncEnterI.closeDestroy();
        } catch (Exception e) {
            JDBCLogger.error(null,e);
        }
    }
}
