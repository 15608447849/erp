package jdbc.imp;

import jdbc.define.exception.JDBCException;
import jdbc.define.session.JDBCSessionManagerAbs;
import jdbc.define.log.JDBCLogger;
import jdbc.define.sync.SyncEnterI;
import jdbc.define.sync.SyncTask;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.io.*;
import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @Author: leeping
 * @Date: 2019/8/16 10:04
 */
public class TomcatJDBCPool extends JDBCSessionManagerAbs{

    private DataSource dataSource;

    @Override
    public void initialize(Object... args) {
        try {
            if (args.length >= 1) {
                if (args[0] instanceof InputStream) {
                    loadProperties((InputStream) args[0]);
                    return;
                }
            }
            throw new InvalidParameterException("tomcat-jdbc connection pool initialize fail");
        } catch (Exception e) {
            throw new JDBCException(e);
        }
    }

    private void loadProperties(InputStream is) throws IOException {
        Properties props = new Properties();
        props.load(is);
        String seqStr = props.getProperty("node.seq");
        seq = Integer.parseInt(seqStr);
        String url = props.getProperty("url");
        int bIndex = url.indexOf("//");
        int eIndex = url.indexOf("?") > 0 ? url.indexOf("?") : url.length();
        String tmpDBSInfo = url.substring(bIndex + 2, eIndex);
        int mIndex = tmpDBSInfo.indexOf("/");
        address = tmpDBSInfo.substring(0, mIndex);
        dataBaseName = tmpDBSInfo.substring(mIndex + 1);

        PoolProperties poolProperties = new PoolProperties();
        setPoolPropertiesValue(poolProperties,props);
        dataSource = new DataSource();
        dataSource.setPoolProperties(poolProperties);
        loadDefaultTransactionIsolationLevel();
        syncEnterI = new DBSyncThreadImp(this);
        JDBCLogger.print("success load data source , address = " + address + ",database = " + dataBaseName+",seq = "+seq);
    }

    private void setPoolPropertiesValue(PoolProperties poolProperties, Properties props) {
        Field[] fields = poolProperties.getClass().getDeclaredFields();
        for (Field field : fields){
            try {
                String name = field.getName();//获取属性的名字
                String value = props.getProperty(name);
                if (value == null) continue;
                field.setAccessible(true);
                field.set(poolProperties, value);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    protected Connection getInternalConnection() throws SQLException {
        return this.dataSource.getConnection();
    }


}
