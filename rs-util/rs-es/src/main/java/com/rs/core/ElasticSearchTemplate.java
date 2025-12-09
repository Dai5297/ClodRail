package com.rs.core;

import com.rs.core.operations.DocumentOperations;
import com.rs.core.operations.IndexOperations;

public interface ElasticSearchTemplate {
    DocumentOperations opsForDoc();
    IndexOperations opsForIndex();
}
