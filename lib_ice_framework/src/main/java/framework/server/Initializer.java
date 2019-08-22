package framework.server;

/**
 * @Author: leeping
 * @Date: 2019/4/11 16:42
 */
public interface Initializer {
    void initialization(String serverName,String groupName);
    int priority();
}
