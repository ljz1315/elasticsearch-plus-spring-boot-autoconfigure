package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: lijz
 * @Description SearchRequestWrap
 * @Date: 2020/7/24
 */
public class SearchRequestWrap {

    public static SearchRequest equals(String index, String type, Integer size, List<String> selectField, Map<String, Object> fieldValueMap, LinkedHashMap<String, SortOrder> orderMap) {
        SearchRequest searchRequest = null;
        if (CollectionUtils.isEmpty(fieldValueMap)) {
            return null;
        } else if (fieldValueMap.size() == 1) {
            Map.Entry<String, Object> entry = fieldValueMap.entrySet().stream().findAny().get();
            searchRequest = QueryConditionBuilder.buildSearchRequest(index, type, size, selectField, QueryConditionBuilder.equals(entry.getKey(), entry.getValue()),orderMap);
        } else {
            List<QueryBuilder> queryBuilders = fieldValueMap.entrySet().stream().map(entry -> QueryConditionBuilder.equals(entry.getKey(), entry.getValue())).collect(Collectors.toList());
            searchRequest = QueryConditionBuilder.buildSearchRequest(index, type, size, selectField, QueryConditionBuilder.getObject().and(queryBuilders),orderMap);
        }
        return searchRequest;
    }

    public static SearchRequest not(String index, String type, Integer size, List<String> selectField, Map<String, Object> fieldValueMap, LinkedHashMap<String, SortOrder> orderMap) {
        SearchRequest searchRequest = null;
        if (CollectionUtils.isEmpty(fieldValueMap)) {
            return null;
        } else {
            List<QueryBuilder> queryBuilders = fieldValueMap.entrySet().stream().map(entry -> QueryConditionBuilder.equals(entry.getKey(), entry.getValue())).collect(Collectors.toList());
            searchRequest = QueryConditionBuilder.buildSearchRequest(index, type, size, selectField, QueryConditionBuilder.getObject().not(queryBuilders),orderMap);
        }
        return searchRequest;
    }

    public static SearchRequest in(String index, String type, Integer size, List<String> selectField, Map<String, List<Object>> fieldValueMap, LinkedHashMap<String, SortOrder> orderMap) {
        SearchRequest searchRequest = null;
        if (CollectionUtils.isEmpty(fieldValueMap)) {
            return null;
        } else if (fieldValueMap.size() == 1) {
            Map.Entry<String, List<Object>> entry = fieldValueMap.entrySet().stream().findAny().get();
            searchRequest = QueryConditionBuilder.buildSearchRequest(index, type, size, selectField, QueryConditionBuilder.in(entry.getKey(), entry.getValue()),orderMap);
        }  else {
            List<QueryBuilder> queryBuilders = fieldValueMap.entrySet().stream().map(entry -> QueryConditionBuilder.in(entry.getKey(), entry.getValue())).collect(Collectors.toList());
            searchRequest = QueryConditionBuilder.buildSearchRequest(index, type, size, selectField, QueryConditionBuilder.getObject().and(queryBuilders),orderMap);
        }
        return searchRequest;
    }

    public static SearchRequest between(String index, String type, Integer size, List<String> selectField, String field, Object from, Object to, LinkedHashMap<String, SortOrder> orderMap) {
        return QueryConditionBuilder.buildSearchRequest(index, type, size, selectField, QueryConditionBuilder.between(field,from,to),orderMap);
    }
}
