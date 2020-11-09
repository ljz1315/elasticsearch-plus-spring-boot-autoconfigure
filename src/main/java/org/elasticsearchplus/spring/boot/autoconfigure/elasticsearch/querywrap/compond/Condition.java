package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond;

import org.elasticsearch.index.query.QueryBuilder;

/**
 * elasticsearch search request condition
 */
public interface Condition {

    QueryBuilder apply();
}
