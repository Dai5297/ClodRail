package com.rs.core.operations;

import cn.hutool.json.JSONUtil;
import com.rs.model.PageResult;
import com.rs.model.dto.PageQueryDTO;
import com.rs.util.ReflectUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DefaultDocumentOperations implements DocumentOperations {

    private final RestHighLevelClient restHighLevelClient;

    public DefaultDocumentOperations(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public <T> Boolean insert(String index, T document) {
        try {
            IndexRequest request = new IndexRequest(index);
            request.source(JSONUtil.toJsonStr(document), XContentType.JSON);
            request.id(getId(document));
            IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);
            return response.status().getStatus() == 201;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> Boolean batchInsert(String index, List<T> documents) {
        BulkRequest bulkRequest = new BulkRequest();
        documents.forEach(document -> {
            IndexRequest request = new IndexRequest(index);
            request.id(getId(document));
            request.source(JSONUtil.toJsonStr(document), XContentType.JSON);
            bulkRequest.add(request);
        });
        try {
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            return response.status().getStatus() == 200;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> Boolean batchUpsert(String index, List<T> documents) {
        BulkRequest bulkRequest = new BulkRequest();
        for (T doc : documents) {
            String id = getId(doc);
            if (id == null) {
                continue;
            }
            IndexRequest request = new IndexRequest(index)
                    .id(id)
                    .source(JSONUtil.toJsonStr(doc), XContentType.JSON);
            bulkRequest.add(request);
        }

        try {
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            // 注意：bulk 成功 ≠ 每个 item 成功！
            return !response.hasFailures();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> Boolean updateById(String index, T document) {
        String id = getId(document);
        UpdateRequest request = new UpdateRequest(index, id);
        request.doc(JSONUtil.toJsonStr(document), XContentType.JSON);
        try {
            UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);
            return response.status().getStatus() == 200;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <ID> Boolean deleteById(String index, ID id) {
        DeleteRequest request = new DeleteRequest(index, id.toString());
        try {
            DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);
            return response.status().getStatus() == 200;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <ID> Boolean batchDelete(String index, List<ID> ids) {
        BulkRequest bulkRequest = new BulkRequest();
        ids.forEach(id -> {
            DeleteRequest request = new DeleteRequest(index, id.toString());
            bulkRequest.add(request);
        });
        try {
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            return response.status().getStatus() == 200;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T, ID> T findById(String index, ID id, Class<T> clazz) {
        GetRequest request = new GetRequest(index, id.toString());
        try {
            GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            if (response.isExists()) {
                return JSONUtil.toBean(response.getSourceAsString(), clazz);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public <T, ID> List<T> findByIds(String index, List<ID> ids, Class<T> clazz) {
        MultiGetRequest request = new MultiGetRequest();
        List<T> result = new ArrayList<>();
        ids.forEach(id -> request.add(new MultiGetRequest.Item(index, id.toString())));
        try {
            MultiGetResponse response = restHighLevelClient.mget(request, RequestOptions.DEFAULT);
            response.forEach(res -> {
                if (res.isFailed()) {
                    throw new RuntimeException(res.getFailure().getMessage());
                }
                result.add(JSONUtil.toBean(res.getResponse().getSourceAsString(), clazz));
            });
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T, ID> List<T> findByIds(String index, List<ID> ids, List<String> includes, Class<T> clazz) {
        // 1. 准备 FetchSourceContext
        FetchSourceContext fetchSourceContext = null;

        // 检查 includes 列表是否有效
        if (includes != null && !includes.isEmpty()) {
            // 创建 FetchSourceContext，设置为 true 表示包含 (_source=true)，
            // 第二个参数是包含的字段列表 (includes)，第三个参数是排除的字段列表 (excludes)
            fetchSourceContext = new FetchSourceContext(
                    true,
                    includes.toArray(new String[0]), // 转换为 String 数组
                    null // 不需要排除任何字段
            );
        }

        MultiGetRequest request = new MultiGetRequest();
        List<T> result = new ArrayList<>();
        for (ID id : ids) {
            MultiGetRequest.Item item = new MultiGetRequest.Item(index, id.toString());
            if (fetchSourceContext != null) {
                item.fetchSourceContext(fetchSourceContext);
            }
            request.add(item);
        }
        try {
            MultiGetResponse response = restHighLevelClient.mget(request, RequestOptions.DEFAULT);
            for (MultiGetItemResponse multiGetItemResponse : response) {
                if (multiGetItemResponse.isFailed()) {
                    throw new RuntimeException(multiGetItemResponse.getFailure().getMessage());
                }
                result.add(JSONUtil.toBean(multiGetItemResponse.getResponse().getSourceAsString(), clazz));
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> PageResult<T> findForPage(String index, PageQueryDTO pageQueryDTO, Class<T> targetClass) {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder source = request.source();
        // 设置分页属性
        source.from(pageQueryDTO.getPageNum());
        source.size(pageQueryDTO.getPageSize());
        source.sort(pageQueryDTO.getOrderBy1(), pageQueryDTO.getIsAsc1() ? SortOrder.ASC : SortOrder.DESC);
        source.sort(pageQueryDTO.getOrderBy2(), pageQueryDTO.getIsAsc2() ? SortOrder.ASC : SortOrder.DESC);
        source.query(QueryBuilders.matchAllQuery());
        request.source(source);
        try {
            PageResult<T> result = new PageResult<>();
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            response.getHits().forEach(hit -> {
                result.getRecords().add(JSONUtil.toBean(hit.getSourceAsString(), targetClass));
            });
            result.setTotal((long) result.getRecords().size());
            result.setCurrent(Long.valueOf(pageQueryDTO.getPageNum()));
            result.setSize(Long.valueOf(pageQueryDTO.getPageSize()));
            result.setPages(result.getTotal() % result.getSize() == 0 ? result.getTotal() / result.getSize() : result.getTotal() / result.getSize() + 1);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> PageResult<T> searchForPage(String index, String query, PageQueryDTO pageQueryDTO, Class<T> clazz) {
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder source = request.source();
        source.query(QueryBuilders.multiMatchQuery(query, "search_all"));
        request.source(source);
        try {
            PageResult<T> result = new PageResult<>();
            result.setCurrent(Long.valueOf(pageQueryDTO.getPageNum()));
            result.setSize(Long.valueOf(pageQueryDTO.getPageSize()));
            result.setRecords(new ArrayList<>());
            if (pageQueryDTO.getOrderBy1() != null) {
                source.sort(pageQueryDTO.getOrderBy1(), pageQueryDTO.getIsAsc1() == null ? SortOrder.DESC : pageQueryDTO.getIsAsc1() ? SortOrder.ASC : SortOrder.DESC);
            }
            if (pageQueryDTO.getOrderBy2() != null) {
                source.sort(pageQueryDTO.getOrderBy2(), pageQueryDTO.getIsAsc1() == null ? SortOrder.DESC : pageQueryDTO.getIsAsc1() ? SortOrder.ASC : SortOrder.DESC);
            }
            source.from((int) pageQueryDTO.calFrom());
            source.size(pageQueryDTO.getPageSize());
            System.out.println(source);
            SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
            if (Objects.requireNonNull(response.getHits().getTotalHits()).value > 0) {
                response.getHits().forEach(hit -> {
                    result.getRecords().add(JSONUtil.toBean(hit.getSourceAsString(), clazz));
                });
            }
            result.setTotal(response.getHits().getTotalHits().value);
            result.setPages(result.getTotal() % result.getSize() == 0 ? result.getTotal() / result.getSize() : result.getTotal() / result.getSize() + 1);
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文档id， 如果文档中设置了id，使用文档的id，如果未设置，使用雪花算法生成
     *
     * @param document
     * @param <T>
     * @return
     */
    private static final String ID_FIELD = "id";

    private <T> String getId(T document) {
        if (document == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }

        // 情况1: document 是 Map
        if (document instanceof Map) {
            Object idValue = ((Map<?, ?>) document).get(ID_FIELD);
            if (idValue != null) {
                return idValue.toString();
            }
        }
        // 情况2: document 是 Java Bean（POJO）
        else {
            Object idValue = ReflectUtils.getFieldValue(document, ID_FIELD);
            if (idValue != null) {
                return idValue.toString();
            }
        }

        // 如果都拿不到 id，不能 fallback 到随机 ID！
        throw new IllegalArgumentException("Document missing required field 'id': " + document);
    }
}
