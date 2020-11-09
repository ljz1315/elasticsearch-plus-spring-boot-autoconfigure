package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.aggs;

import org.elasticsearch.search.aggregations.AggregationBuilder;

/**
 * @Author: lijz
 * @Description 聚合 conditon
 * @Date: 2020/7/24
 */
public interface AggsCondition{

    AggregationBuilder apply();

}
