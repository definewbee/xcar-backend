package io.doeasy.xcar.mapper;

import io.doeasy.xcar.bean.FuzzySearchItem;
import io.doeasy.xcar.bean.OrderByItem;
import lombok.NonNull;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author kris.wang
 */
public interface BaseMapper<T> {

    /**
     * 根据map查询一条
     * @param map
     * @return
     */
    T selectOneByMap(Map<String, Object> map);

    /**
     * 根据主键查询
     * @param id
     * @return
     */
    T selectByPrimaryKey(Long id);

    /**
     * 分页查询总数
     * @param entity 条件对象
     * @return count
     */
    long count(@Param("entity") T entity, @Param("fuzzySearchItemList") List<FuzzySearchItem> fuzzySearchItemList);


    /**
     * 分页查询
     * @param entity 分页条件实体
     * @param offset 开始下标
     * @param pageSize 查询数量
     * @param orderByItems 排序条件
     * @return 结果集
     */
    List<T> listPage(@Param("entity") T entity, @Param("offset") long offset, @Param("pageSize") long pageSize,
                     @Param("orderByItems") List<OrderByItem> orderByItems,
                     @Param("fuzzySearchItemList") List<FuzzySearchItem> fuzzySearchItemList);

    /**
     * 按对象条件查询数据集合
     * @param obj 对象条件
     * @return 符合条件的数据集合
     */
    List<T> listByObj(T obj);

    /**
     * 按对象条件查询数据集合
     * @param obj 对象条件
     * @param orderByItems 排序条件
     * @return 符合条件的数据集合
     */
    List<T> listByObjWithOrder(@Param("entity") T obj, @Param("orderByItems") List<OrderByItem> orderByItems);

    /**
     * in语句查询
     * @param colName 列名
     * @param dataList in集合
     * @return 符合条件的数据集合
     */
    List<T> listByIn(@Param("colName") String colName, @Param("list") List dataList);

    /**
     * in语句查询
     * @param colName 列名
     * @param dataList in集合
     * @param orderByItems 排序条件
     * @return 符合条件的数据集合
     */
    List<T> listByInWithOrder(@Param("colName") String colName, @Param("list") List dataList,
                              @Param("orderByItems") List<OrderByItem> orderByItems);

    /**
     * in语句删除（物理删除！）
     * @param inDataListColName in dataList集合的条件列名
     * @param dataList in 取值条件集合
     * @param entity 实体内条件（字段非空即视为过滤条件）
     */
    long deleteByInListAndEntity(@NonNull @Param("colName") String inDataListColName, @NonNull @Param("list") List dataList,
                                 @NonNull @Param("entity") T entity);

    /**
     * in语句删除（物理删除）
     * @param inDataListColName in dataList集合的条件列名
     * @param dataList in 取值条件集合
     */
    void deleteByInList(@NonNull @Param("colName") String inDataListColName, @NonNull @Param("list") List dataList);

    /**
     * 按条件删除（物理删除）
     * @param colNameAndEqValue 条件map
     */
    long deleteByCondition(@NonNull Map<String, Object> colNameAndEqValue);

    /**
     * 根据id删除对象（物理删除）
     * @param id 主键
     * @return 影响行数
     */
    long deleteByPrimaryKey(Long id);

    /**
     * 插入
     * @param t
     * @return
     */
    long insert(T t);

    /**
     * 批量插入
     * @param ts
     * @return
     */
    long batchInsert(List<T> ts);

    /**
     * 根据主键更新
     * @param t
     * @return
     */
    long updateByPrimaryKey(T t);

    /**
     * 根据map条件查询符合条件的最大版本好
     * @param map
     * @return
     */
    Integer selectMaxVersion(@NonNull Map<String, Object> map);
}
