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
    static {
        /************************服务关联的数据库连接池配置信息常量化******************************/
        addServerDB("globalServer", "db-global-0.properties","db-global-1.properties");
    }


    public static class Table{
        private final String name;
        private final int index;
        private Table(String name, int index) {
            this.name = name;
            this.index = index;
        }
        public String getName() {
            return "{{?"+name+"}}";
        }
        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return getName();
        }
    }

    public interface Tables{
        /**************************可能要用到的表常量化******************************************/
        Table TB_SYS_USER = new Table("tb_sys_user",0);
        Table TB_SYS_RESOURCE = new Table("tb_sys_resource",1);
        Table TB_SYS_ROLE = new Table("tb_sys_role",2);
    }

    private DBConst(){ }

    private static final Map<String, List<String>> map = new HashMap<>();

    public static List<String> serverDBConfigFiles(String serverGroupName){
        return map.get(serverGroupName);
    }

    private static void addServerDB(String serverName, String configName){
        List<String> list = map.computeIfAbsent(serverName, k -> new ArrayList<>());
        list.add(configName);
    }

    private static void addServerDB(String serverName, String... configNames){
        for (String configName : configNames) addServerDB(configName);
    }

    /** 生成平台无关主键ID */
    public static long genTableOID(Table table){
        return  table.getIndex() + System.currentTimeMillis();
    }

}
