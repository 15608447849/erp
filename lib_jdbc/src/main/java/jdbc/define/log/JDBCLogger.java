package jdbc.define.log;

/**
 * @Author: leeping
 * @Date: 2019/8/16 11:04
 */
public class JDBCLogger {

    public interface JDBCSessionLogInterface {
        void info(String message);
        void error(String desc,Throwable e);
    }

    private static JDBCSessionLogInterface logger = new JDBCSessionLogInterface() {
        @Override
        public void info(String message) {
            System.out.println(message);
        }

        @Override
        public void error(String desc, Throwable e) {
            System.err.println(desc+"\n"+e);
//            e.printStackTrace();
        }
    };

    public static void setLogger(JDBCSessionLogInterface logger) {
        JDBCLogger.logger = logger;
    }

    public static void print(String message){
        if (logger == null) return;
        logger.info(message);
    }

    public static void error(String desc,Throwable e){
        if (logger == null) return;
        if (desc==null){
            logger.error("",e);
        }else{
            logger.error(desc,e);
        }

    }

}
