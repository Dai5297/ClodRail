package com.rs.core.operations;

import lombok.AllArgsConstructor;
import org.elasticsearch.client.RestHighLevelClient;

@AllArgsConstructor
public class DefaultIndexOperations implements IndexOperations{

    private final RestHighLevelClient restHighLevelClient;


}
