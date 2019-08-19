package com.drug;



import com.drug.dao.UserDaoBean;
import framework.server.IIceInitialize;

import jdbc.define.option.JDBCSessionFacade;
import jdbc.define.tuples.Tuple2;
import jdbc.imp.MasterSlaveSyncJDBCFacadeWrap;
import jdbc.imp.ReadWriteSeparateJDBCFacadeWrap;
import jdbc.imp.TomcatJDBC;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: leeping
 * @Date: 2019/8/5 15:01
 */
public class GlobalServerInit implements IIceInitialize {
    @Override
    public void startUp(String serverName) {
    }

    @Override
    public int priority() {
        return 1;
    }

    public static void main(String[] args) throws Exception {
        //通过接口, 对外部任何系统,提供分库分表业务规则的实现
        TomcatJDBC.setSliceFilter(new TomcatJDBC.SliceFilter() {
            @Override
            public String filterTableName(String tableName, int table_slice) {
                System.out.println("当前执行的表: "+ tableName+", 传递的表分片字段: "+ table_slice);
                if (table_slice> 0){
                    tableName += "_"+table_slice;
                }
                return tableName;  //td_push_msg_2019
            }

            @Override
            public String filterDataBaseName(List<String> dbList, int db_slice) {
                System.out.println("当前可匹配的数据库组: "+ dbList+", 传递的表分片字段: "+ db_slice+" ,默认匹配下标[0]的数据库");
                // order_00  oerder_01
                if (db_slice > 0){
                    int index = db_slice %  8192 % 8;
                    return dbList.get(index);
                }
                return null;
            }
        });
        TomcatJDBC.initialize("db",GlobalServerInit.class);
//        test1();

//        test4();
//        test5();

        test6();
    }




    //数据同步测试 - 非事务
    private static void test1(){
        String sql = "INSERT INTO tb_user ( userid, username,userpw )  VALUES ( ?,?,? )";
        JDBCSessionFacade facade = TomcatJDBC.getFacade("framework-user");
        MasterSlaveSyncJDBCFacadeWrap wrap = new ReadWriteSeparateJDBCFacadeWrap(facade);
        for (int i = 0; i < 100 ;i++){
            wrap.execute(sql,new Object[]{i,"用户名-"+i,"密码-"+i});
        }
    }

    //读写分离 - 非事务
    private static void test2(){
        String sql = "INSERT INTO tb_user ( userid, username,userpw,roleid )  VALUES ( ?,?,?,? )";
        JDBCSessionFacade facade = TomcatJDBC.getFacade("framework-user");
        ReadWriteSeparateJDBCFacadeWrap wrap = new ReadWriteSeparateJDBCFacadeWrap(facade);
        wrap.execute(sql,new Object[]{200,"读写分离-用户名-"+200,"读写分离-密码-"+200});
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sql = "SELECT * FROM tb_user";
        List<UserDaoBean> list = wrap.query(sql,null , UserDaoBean.class);
        System.out.println(list);
    }

    //数据同步事务-同库
    //1. 先停供灵活的接口 ,可自行处理业务上的分库分表
    //2. 设置默认规则 , 如,  分表类型  1.时间维度 , 2. 计算规则 - 通过配置文件,  在没有对外接口的情况下,执行默认的分库分表规则
    public static void test3(){
        JDBCSessionFacade op = TomcatJDBC.getFacade("framework-user");
        List<String> sqlList = new ArrayList<>();
        List<Object[]> paramList = new ArrayList<>();

        sqlList.add("INSERT INTO tb_user ( userid, username,userpw )  VALUES ( ?,?,? );");
        paramList.add(new Object[]{300,"本地事务-测试","123456789"});

        sqlList.add("UPDATE tb_user SET username=? WHERE userid=?");
        paramList.add(new Object[]{"事务修改的数据-",200});

        MasterSlaveSyncJDBCFacadeWrap wrap = new MasterSlaveSyncJDBCFacadeWrap(op);
        int i = wrap.executeTransaction(sqlList,paramList);
        System.out.println(i);
    }


    //分库分表测试
    public static void test4(){

        String sql = "INSERT INTO {{?td_push_msg}} ( unqid, identity, message, date,time, cstatus) VALUES ( ?,?,?,CURRENT_DATE, CURRENT_TIME,?);";
        int i = TomcatJDBC.DAO.update(sql,new Object[]{ 19078296637539328L, 536862721, "push:7##【满减活动（折扣）0520】将于2019-05-20 11:50:00开始进行",1 },536862721,2019);
        System.out.println("分库分表执行结果:" + i);

    }


    //分库分表测试
    public static void test5(){

        String sql1 = "INSERT INTO {{?td_push_msg}} ( unqid, identity, message, date,time, cstatus) VALUES ( ?,?,?,CURRENT_DATE, CURRENT_TIME,?);";
        Object[] param1 = new Object[]{ 19078296637539329L, 536862721, "11111",1 };

        String sql2 = "UPDATE {{?td_push_msg}} SET message=? WHERE unqid=?";
        Object[] param2 = new Object[]{"6666",19078296637539328L};

        List<String> sqlList = new ArrayList<>();
        sqlList.add(sql1);
        sqlList.add(sql2);

        List<Object[]> paramList = new ArrayList<>();
        paramList.add(param1);
        paramList.add(param2);

        int i = TomcatJDBC.DAO.update(sqlList,paramList,536862721,2019);
        System.out.println("分库分表执行结果:" + i);

    }

    private static void test6() {

        String sql = "SELECT * FROM {{?td_push_msg}} INNER JOIN {{?td_signin}}";
        List<Object[]> lines = TomcatJDBC.DAO.query(sql,null,536862721,2019);
        TomcatJDBC.DAO.printLines(lines);
    }





}
