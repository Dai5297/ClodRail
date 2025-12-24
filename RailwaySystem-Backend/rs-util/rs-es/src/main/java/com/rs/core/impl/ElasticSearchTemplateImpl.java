package com.rs.core.impl;

import com.rs.core.ElasticSearchTemplate;
import com.rs.core.operations.DefaultDocumentOperations;
import com.rs.core.operations.DefaultIndexOperations;
import com.rs.core.operations.DocumentOperations;
import com.rs.core.operations.IndexOperations;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticSearchTemplateImpl implements ElasticSearchTemplate {
    private final RestHighLevelClient restHighLevelClient;

    public ElasticSearchTemplateImpl(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public DocumentOperations opsForDoc() {
        return new DefaultDocumentOperations(restHighLevelClient);
    }

    @Override
    public IndexOperations opsForIndex() {
        return new DefaultIndexOperations(restHighLevelClient);
    }
}
