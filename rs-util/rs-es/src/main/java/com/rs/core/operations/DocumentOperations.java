package com.rs.core.operations;

import com.rs.model.PageResult;
import com.rs.model.dto.PageQueryDTO;

import java.util.List;

public interface DocumentOperations {

    /**
     * 新增文档
     *
     * @param index    文档所属索引
     * @param document 文档
     * @param <T>      document类型
     * @return 文档创建结果
     */
    <T> Boolean insert(String index, T document);

    <T> Boolean batchInsert(String index, List<T> documents);

    <T> Boolean batchUpsert(String index, List<T> documents);

    <T> Boolean updateById(String index, T document);

    <ID> Boolean deleteById(String index, ID id);

    /**
     * 批量删除
     * @param index
     * @param ids
     * @return
     * @param <ID>
     */
    <ID> Boolean batchDelete(String index, List<ID> ids);

    <T, ID> T findById(String index, ID id, Class<T> clazz);

    <T, ID> List<T> findByIds(String index, List<ID> ids, Class<T> clazz);

    <T, ID> List<T> findByIds(String index, List<ID> ids, List<String> includes, Class<T> clazz);

    /**
     * 分页查询
     * @param index
     * @param pageQueryDTO
     * @param targetClass
     * @return
     * @param <T>
     */
    <T> PageResult<T> findForPage(String index, PageQueryDTO pageQueryDTO, Class<T> targetClass);

    <T> PageResult<T> searchForPage(String index, String query, PageQueryDTO pageQueryDTO, Class<T> clazz);
}
