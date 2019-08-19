package jdbc.define.option;

import jdbc.define.tuples.Tuple2;

import java.util.List;

/**
 * @Author: leeping
 * @Date: 2019/8/17 15:56
 * 数据库操作API
 */
public interface DaoApi {
    /** 查询数据 */
    List<Object[]> query(String sql,Object[] params);
    /** 查询数据 */
    <T> List<T> query(String sql, Object[] params,Class<T> beanClass);
    /** 新增,修改,删除 */
    int execute(String sql, Object[] params);
    /** 新增,修改,删除 事务执行 */
    int executeTransaction(List<String> sqlList,List<Object[]> paramList);
    /** 新增数据并且获取自增主键值 */
    Tuple2<Integer,Object[]> insertAlsoGetGenerateKeys(String insetSql, Object[] params, int[] columnIndexes);

}
