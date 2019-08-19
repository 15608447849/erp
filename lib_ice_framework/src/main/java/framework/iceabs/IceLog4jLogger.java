package framework.iceabs;

import Ice.Logger;
import util.Log4j;


/**
 * @Author: leeping
 * @Date: 2019/4/3 10:57
 */
public class IceLog4jLogger implements Ice.Logger {

    private static final String MESSAGE_FORMAT = "【%s】\t%s";

    private final org.apache.logging.log4j.Logger logger;

    private final String prefix;

    public IceLog4jLogger(String prefix){
        this.logger = Log4j.logger;
        this.prefix = prefix;
    }

    @Override
    public Logger cloneWithPrefix(String prefix) {
        return new IceLog4jLogger(prefix);
    }

    @Override
    public void error(String message) {
        logger.error( String.format(MESSAGE_FORMAT,prefix,message) );
    }

    @Override
    public String getPrefix() {
        return prefix;
    }

    @Override
    public void print(String message) {
        logger.debug( String.format(MESSAGE_FORMAT,prefix,message));
    }

    @Override
    public void trace(String category, String message) {
        logger.trace(category, String.format(MESSAGE_FORMAT,prefix,message));
    }

    @Override
    public void warning(String message) {
        logger.warn( String.format(MESSAGE_FORMAT,prefix,message));
    }
}
