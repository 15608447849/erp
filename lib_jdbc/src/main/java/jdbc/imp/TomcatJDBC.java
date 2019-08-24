package jdbc.imp;

import jdbc.define.exception.JDBCException;
import jdbc.define.log.JDBCLogger;
import jdbc.define.option.JDBCSessionFacade;

import jdbc.define.option.JDBCSessionFacadeWrap;
import jdbc.define.tuples.Tuple2;
import jdbc.define.tuples.Tuple3;
import jdbc.imp.TomcatJDBCPool;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: leeping
 * @Date: 2019/8/16 12:36
 */
public class TomcatJDBC {
    // 标识_库名
    private final static Map<String , List<TomcatJDBCPool>> poolGroupMap = new HashMap<>();

    //每个主库对应的所有表名
    private final static Map<String,List<String>> dbTableAllMap = new HashMap<>();

    //一个表名可能对应的多个库名
    private final static Map<String,List<String>> tableDbAllMap = new HashMap<>();

    /**使用一个目录中所有配置文件初始化连接池*/
    public static void initialize(String dirName,Class clazz) throws Exception{
      List<File> files = getDicFile(dirName,clazz);
      if (files.size() == 0) throw new IllegalArgumentException("dir name = '"+dirName+"' , No connection profile exists.");
    }

    /**使用多个配置文件名 在resource中查询配置并初始化连接池*/
    public static void initialize(String... configList) throws Exception{
      initialize(Arrays.asList(configList));
    }

    /**使用多个配置文件名 在resource中查询配置并初始化连接池*/
    public static void initialize(List<String> configList) throws Exception{
        for (String fileName : configList){
            try(InputStream is = getResourceConfig(fileName)){
                genPoolObjectAlsoAddGroup(is);
            }
        }
        genTableInfo();
    }

    /**多个配置文件初始化连接池*/
    public static void  initialize(File... files)throws Exception{
        for (File file : files){
            try(InputStream is = new FileInputStream(file)){
                genPoolObjectAlsoAddGroup(is);
            }
        }
        genTableInfo();
    }

    private static List<File> getDicFile(String dirName,Class clazz) throws Exception {
//        Class clazz = TomcatJDBC.class;
        //优先加载外部,外部不存在, 加载resource
        String dirPath = new File(clazz.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
        File dir = new File(dirPath+"/resources/"+dirName);
        if (!dir.exists()){
            dir.mkdirs();
            File resourcesOut = dir.getParentFile();
            URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
            JarFile localJarFile = new JarFile(new File(url.getPath()));
            Enumeration<JarEntry> entries = localJarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.isDirectory()){
                    continue;
                }
                String innerPath = jarEntry.getName();
                if(innerPath.startsWith(dirName)){
                    try(InputStream in =  clazz.getClassLoader().getResourceAsStream(innerPath)){
                        if (in==null) continue;
                        try(FileOutputStream out = new FileOutputStream(new File(resourcesOut,innerPath))){
                            byte[] bytes = new byte[1024];
                            int len;
                            while ( (len = in.read(bytes) )> 0 ){
                                out.write(bytes,0,len);
                            }
                        }
                    }
                }
            }
        }

        if (!dir.isDirectory()) throw new FileNotFoundException(dirName +" is not directory");
        return Arrays.asList(Objects.requireNonNull(dir.listFiles()));
    }

    private static InputStream getResourceConfig(String config) throws FileNotFoundException {
        //优先加载外部,外部不存在, 加载resource
        String dirPath = new File(TomcatJDBC.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
        File file = new File(dirPath+"/resources/"+config);
        if (file.exists() && file.isFile() && file.length() > 0) return new FileInputStream(file);
        return TomcatJDBC.class.getClassLoader().getResourceAsStream(config);
    }


    private static void genPoolObjectAlsoAddGroup(InputStream is) {
        if (is == null) return;
        TomcatJDBCPool pool = new TomcatJDBCPool();
        pool.initialize(is);
        String databaseName = pool.getDataBaseName();
        List<TomcatJDBCPool> list = poolGroupMap.get(pool.getDataBaseName());
        if (list == null){
            list = new ArrayList<>();
            poolGroupMap.put(databaseName,list);
        }
        list.add(pool);
        //排序 , 为 主从同步/读写分离 基础 , 定义, 第一个为 主数据库 , 其次都为从数据库
        list.sort(Comparator.comparing(TomcatJDBCPool::getSeq));
    }

    private static void genTableInfo() {
        //生成主数据库表信息-缺陷,程序运行中生成表时,此表将不可用,除非再次出发启动表信息加载
        Iterator<Map.Entry<String , List<TomcatJDBCPool>>> it = poolGroupMap.entrySet().iterator();
        Map.Entry<String , List<TomcatJDBCPool>> entry;
        List<TomcatJDBCPool> list;
        //每个组的主库
        TomcatJDBCPool pool;
        while (it.hasNext()){
            entry = it.next();
            list = entry.getValue();
            pool = list.get(0);
            genPoolTableAll(pool);
            //启动同步
            pool.launchSync();
        }
    }

    private static void genPoolTableAll(TomcatJDBCPool pool) {
        String databaseName = pool.getDataBaseName();
        List<String> tableList = new ArrayList<>();
        String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema=?";
        List<Object[]>  lines = new JDBCSessionFacade(pool).query(sql,new Object[]{databaseName});
        String tableName;
        for (Object[] row : lines ){
            tableName = String.valueOf(row[0]);
            List<String> dbList = tableDbAllMap.computeIfAbsent(tableName, k -> new ArrayList<>());

            if (!dbList.contains(databaseName)){
                dbList.add(databaseName);
            }
            Collections.sort(dbList);
            tableList.add(tableName);
        }
        dbTableAllMap.put(databaseName,tableList);
    }



    public static JDBCSessionFacade getFacade(String databaseName,boolean isMaster) {
        List<TomcatJDBCPool> list = poolGroupMap.get(databaseName);
        if (list!=null){
            int index = 0;
            if (!isMaster && list.size() > 1){
                index = new Random().nextInt(list.size()-1)+1; //排除主库的其他所有从库
            }
            return new JDBCSessionFacade(list.get(index));
        }
       return null;
    }

    public static JDBCSessionFacade getFacade(String databaseName) {

        return getFacade(databaseName,true);
    }

    public static JDBCSessionFacade getFacade(String databaseName,int seq) {
        List<TomcatJDBCPool> list = poolGroupMap.get(databaseName);
        if (list!=null){
            if (seq >= list.size() || seq<0) return null;
            return new JDBCSessionFacade(list.get(seq));
        }
        return null;
    }

    public static List<TomcatJDBCPool> getSpecDataBasePoolList(String databaseName){
        List list =  poolGroupMap.get(databaseName);
        if (list == null)   throw new JDBCException("a nonexistent database by '"+databaseName+"'");
        return list;
    }


    public interface SliceFilter{
        String filterTableName(String tableName, int table_slice);
        String filterDataBaseName(List<String> dbList, int db_slice);
    }

    private static SliceFilter filter;

    public static void setSliceFilter(SliceFilter filter) {
        TomcatJDBC.filter = filter;
    }

    public static class DAO{

        /**原生态SQL查询时，指定的查询表对象固定格式的前缀字符串。*/
        public static final String PREFIX_REGEX = "{{?";
        /**原生态SQL查询时，指定的查询表对象固定格式的后缀字符串。*/
        public static final String SUFFIX_REGEX = "}}";

        private static final String RGE = "\\{\\{\\?(.*?)\\}\\}";

        private static final Pattern pattern = Pattern.compile(RGE);// 匹配的字符串

        private static List<String> regSubStr(String sql){
            List<String> list = new ArrayList<>();
            Matcher m = pattern.matcher(sql);
            while (m.find()) {
                list.add(m.group(1));
            }
            return list;
        }


        //返回:  0-完整的sql, 1-库名
        private static Tuple2<String,List<String>> ergodicSqlFindAllTableNameReturnSQL(List<String> sqlList,int db_slice,int table_slice){
            if (sqlList==null || sqlList.size()==0) throw new JDBCException("sqlList is empty");

            List<String> nativeSqlList = new ArrayList<>();

            String _tableName = null;
            for (int i = 0;i<sqlList.size();i++){
                String sql = sqlList.get(i);
                List<String> tableList = regSubStr(sqlList.get(i));
                if(tableList.size() == 0) throw new JDBCException("sql: '"+sql+"' ,no matching table name, please use: {{?TABLE_NAME}}");

                for (int j = 0 ;j < tableList.size();j++){
                    String tableName  = tableList.get(j);
                    _tableName = tableName.trim();

                    if (filter!=null && table_slice > 0){
                        _tableName = filter.filterTableName(_tableName,table_slice);
                        if (_tableName == null || _tableName.length()==0) throw new JDBCException("'table_slice' is invalid");
                    }
                    sql = sql.replace(PREFIX_REGEX+tableName+SUFFIX_REGEX, _tableName); //还原sql语句
                }
                nativeSqlList.add(sql);
            }

            //通过一个表名查询库名列表
            List<String> dbList = tableDbAllMap.get(_tableName);
            if (dbList == null ) throw new JDBCException("undiscovered database table names: "+ _tableName);
            String dbName = dbList.get(0);//默认选中第一个
            if (filter!=null && db_slice>0){
                String _dbName = filter.filterDataBaseName(dbList,db_slice);
                if (_dbName == null || _dbName.length()==0) throw new JDBCException("'table_slice' is invalid");
                dbName = _dbName;
            }
           return new Tuple2<>(dbName,nativeSqlList);
        }

        private static Tuple2<JDBCSessionFacadeWrap,List<String>> getDaoOp(int type,List<String> sqlList,int db_slice,int table_slice){
            // 返回: 库名/ 获取原始sql列表
            Tuple2<String,List<String>> tuple = ergodicSqlFindAllTableNameReturnSQL(sqlList,db_slice,table_slice);
            //2. 获取连接池操作对象
            JDBCSessionFacade op = TomcatJDBC.getFacade(tuple.getValue0());
            JDBCSessionFacadeWrap wrap = type==0 ? new MasterSlaveSyncJDBCFacadeWrap(op) : new ReadWriteSeparateJDBCFacadeWrap(op);
            return new Tuple2<>(wrap,tuple.getValue1());
        }


        /** 普通修改 */
        public static int update(String sql, Object[] params,int db_slice,int table_slice) {
            List<String> sqlList = new ArrayList<>();
            sqlList.add(sql);
            Tuple2<JDBCSessionFacadeWrap,List<String>> tuple = getDaoOp(0, sqlList,db_slice,table_slice);
            return tuple.getValue0().execute(tuple.getValue1().get(0),params);
        }

        public static int update(String sql, Object[] params) {
           return update(sql,params,0,0);
        }

        /** 事务修改 */
        public static int update(List<String> sqlList,List<Object[]> paramList,int db_slice,int table_slice){
            Tuple2<JDBCSessionFacadeWrap,List<String>> tuple = getDaoOp(0, sqlList,db_slice,table_slice);
            return tuple.getValue0().executeTransaction(tuple.getValue1(),paramList);
        }

        /** 事务修改 */
        public static int update(List<String> sqlList,List<Object[]> paramList){
            return update(sqlList,paramList,0,0);
        }

        /** 查询 */
        public static List<Object[]> query(String sql, Object[] params,int db_slice,int table_slice){
            List<String> sqlList = new ArrayList<>();
            sqlList.add(sql);
            Tuple2<JDBCSessionFacadeWrap,List<String>> tuple = getDaoOp(1, sqlList,db_slice,table_slice);
            return tuple.getValue0().query(tuple.getValue1().get(0),params);
        }

        public static List<Object[]> query(String sql, Object[] params){
            return query(sql,params,0,0);
        }

        /** 查询,根据对象 */
        public static <T> List<T> query(String sql,Object[] params,Class<T> beanClass,int db_slice,int table_slice){
            List<String> sqlList = new ArrayList<>();
            sqlList.add(sql);
            Tuple2<JDBCSessionFacadeWrap,List<String>> tuple = getDaoOp(1, sqlList,db_slice,table_slice);
            return tuple.getValue0().query(tuple.getValue1().get(0),params,beanClass);
        }

        public static <T> List<T> query(String sql,Object[] params,Class<T> beanClass){
            return query(sql,params,beanClass,0,0);
        }

        public static void printLines(List<Object[]> lines){
            for (int i = 0 ;i<lines.size();i++){
                JDBCLogger.print("当前第"+i+"行\t"+Arrays.toString(lines.get(i)));
            }
        }

    }


}
