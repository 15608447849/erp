package drug.erp.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: leeping
 * @Date: 2019/8/24 1:05
 */
public class DBConst {
    private DBConst(){ }

    private static final Map<String, List<String>> map = new HashMap<>();

    public static List<String> serverDBConfigFiles(String serverGroupName){
        return map.get(serverGroupName);
    }

    private static void put(String serverName,String configName){
        List<String> list = map.computeIfAbsent(serverName, k -> new ArrayList<>());
        list.add(configName);
    }

    static {
        /************************服务关联的数据库连接池配置信息常量化******************************/
        put("globalServer", "db-global-0.properties");
    }

    public interface Table{
        /**************************可能需要用到的表常量化******************************************/
        String TB_SYS_USER = "{{?tb_sys_user}}"; //用户表
        String TB_SYS_RESOURCE = "{{?tb_sys_resource}}";//资源表
        String TB_SYS_ROLE = "{{?tb_sys_role}}";//角色表
    }
}
