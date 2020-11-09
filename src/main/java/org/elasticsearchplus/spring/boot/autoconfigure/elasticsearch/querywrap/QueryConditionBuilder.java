package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond.PageInfo;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author: lijz
 * @Description QueryCondition
 * @Date: 2020/7/23
 */
public final class QueryConditionBuilder implements Serializable {
    private static final transient long serialVersionUID = -8468524719852068922L;

    private BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
    private List<String> selectFields = new ArrayList<>();
    private AggregationBuilder aggregation;
    private LinkedHashMap<String, SortOrder> orderMap = new LinkedHashMap<>();

    public static QueryConditionBuilder getObject() {
        QueryConditionBuilder condition = new QueryConditionBuilder();
        return condition;
    }

    public QueryConditionBuilder selectFields(List<String> selectFields) {
        this.selectFields = selectFields;
        return this;
    }

    public QueryConditionBuilder aggregation(AggregationBuilder aggregation) {
        this.aggregation = aggregation;
        return this;
    }

    public QueryConditionBuilder orderby(LinkedHashMap<String, SortOrder> orderMap) {
        this.orderMap = orderMap;
        return this;
    }

    public static SearchRequest buildSearchRequest(String index, String type, Integer size, List<String> selectFields, QueryBuilder queryBuilder, LinkedHashMap<String, SortOrder> orderMap) {
        return buildSearchRequestAggs(index, type, size, selectFields, queryBuilder, orderMap, null, null);
    }

    public static SearchRequest buildSearchRequestAggs(String index, String type, Integer customSize, List<String> selectFields, QueryBuilder queryBuilder, LinkedHashMap<String, SortOrder> orderMap, AggregationBuilder aggregation, PageInfo page) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        boolean isBoolQuery = BoolQueryBuilder.class.isAssignableFrom(queryBuilder.getClass());
        sourceBuilder.query(isBoolQuery ? (BoolQueryBuilder) queryBuilder : queryBuilder);
        // 线上 es集群配置 这里不能设置的太大ES_DEFAULT_SIZE 返回会超时
        sourceBuilder.size(customSize);
//        sourceBuilder.size(Integer.MAX_VALUE);// 获取全量数据 es默认返回size=10
        if (page != null && page.isPage()) {
            int size = page.getSize();
            int pageNum = page.getPageNum();
            int from = (pageNum - 1) * size;
            sourceBuilder.from(from);
            sourceBuilder.size(size);
        }
        if (aggregation != null) {
            // 聚集时，不关注返回记录
            sourceBuilder.size(0);
            sourceBuilder.aggregation(aggregation);
        }
        if (!CollectionUtils.isEmpty(selectFields)) {
            String[] includes = selectFields.toArray(new String[selectFields.size()]);
            sourceBuilder.fetchSource(includes, null);
        }
        if (!CollectionUtils.isEmpty(orderMap)) {
            orderMap.forEach((field, sortOrder) -> sourceBuilder.sort(new FieldSortBuilder(field).order(sortOrder)));
            sourceBuilder.sort(new ScoreSortBuilder().order(SortOrder.DESC));
        }

        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.types(type);
        searchRequest.source(sourceBuilder);
        return searchRequest;
    }

    public BoolQueryBuilder and(List<QueryBuilder> queryBuilders) {
        queryBuilders.stream().forEach(queryBuilder -> boolQueryBuilder.must(queryBuilder));
        return boolQueryBuilder;
    }

    public BoolQueryBuilder or(List<QueryBuilder> queryBuilders) {
        queryBuilders.stream().forEach(queryBuilder -> boolQueryBuilder.should(queryBuilder));
        return boolQueryBuilder.minimumShouldMatch(1);
    }

    public QueryBuilder not(List<QueryBuilder> queryBuilders) {
        queryBuilders.stream().forEach(queryBuilder -> boolQueryBuilder.mustNot(queryBuilder));
        return boolQueryBuilder;
    }

    /**
     * 字段非空 exists 必须在must/mustnot中
     *
     * @param queryBuilders
     * @return
     */
    public QueryBuilder exists(List<QueryBuilder> queryBuilders) {
        queryBuilders.stream().forEach(queryBuilder -> boolQueryBuilder.must(queryBuilder));
        return boolQueryBuilder;
    }

    /**
     * 字段为空
     *
     * @param queryBuilders
     * @return
     */
    public QueryBuilder notExists(List<QueryBuilder> queryBuilders) {
        queryBuilders.stream().forEach(queryBuilder -> boolQueryBuilder.mustNot(queryBuilder));
        return boolQueryBuilder;
    }


    public static void main(String[] args) {
        QueryBuilder term = QueryConditionBuilder.equals("cityCode", "010");
        QueryBuilder term2 = QueryConditionBuilder.in("supplier", Lists.newArrayList("1", "2"));
        BoolQueryBuilder boolQueryBuilder = QueryConditionBuilder.getObject().and(Lists.newArrayList(term, term2));

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(boolQueryBuilder);

        SearchRequest searchRequest = new SearchRequest("index");
        searchRequest.types("type");
        searchRequest.source(sourceBuilder);

        System.out.println(searchRequest.toString());
    }

    public static QueryBuilder exists(String field) {
        return QueryBuilders.existsQuery(field);
    }

    public static QueryBuilder equals(String field, Object value) {
        if (Collection.class.isAssignableFrom(value.getClass()) || Array.class.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("val can not be Collection or Array");
        }
        return QueryBuilders.termQuery(field, value);
    }

    public static QueryBuilder in(String field, List<Object> values) {
        return QueryBuilders.termsQuery(field, values);
    }

    public static QueryBuilder gt(String field, Object value) {
        if (Collection.class.isAssignableFrom(value.getClass()) || Array.class.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("val can not be not Collection or Array");
        }
        return QueryBuilders.rangeQuery(field).gt(value);
    }

    public static QueryBuilder gte(String field, Object value) {
        if (Collection.class.isAssignableFrom(value.getClass()) || Array.class.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("val can not be Collection or Array");
        }
        return QueryBuilders.rangeQuery(field).gte(value);
    }

    public static QueryBuilder lt(String field, Object value) {
        if (Collection.class.isAssignableFrom(value.getClass()) || Array.class.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("val can not be Collection or Array");
        }
        return QueryBuilders.rangeQuery(field).lt(value);
    }

    public static QueryBuilder lte(String field, Object value) {
        if (Collection.class.isAssignableFrom(value.getClass()) || Array.class.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("val can not be Collection or Array");
        }
        return QueryBuilders.rangeQuery(field).lte(value);
    }

    public static QueryBuilder between(String field, Object from, Object to) {
        if (Collection.class.isAssignableFrom(from.getClass()) || Array.class.isAssignableFrom(from.getClass())) {
            throw new IllegalArgumentException("val can not be Collection or Array");
        }
        if (Collection.class.isAssignableFrom(to.getClass()) || Array.class.isAssignableFrom(to.getClass())) {
            throw new IllegalArgumentException("val can not be Collection or Array");
        }
        return QueryBuilders.rangeQuery(field).gte(from).lte(to);
    }

    /**
     * geoDistance
     * 坐标点方圆XXX公里 查询
     *
     * @param locationField
     * @param lat
     * @param lon
     * @param distance
     * @param unit
     * @param geoDistanceType
     * @return
     */
    public static QueryBuilder geoDistance(String locationField, double lat, double lon, double distance, DistanceUnit unit, GeoDistance geoDistanceType) {
        DistanceUnit unit_ = unit == null ? DistanceUnit.KILOMETERS : unit;
        GeoDistance geoDistanceType_ = geoDistanceType == null ? GeoDistance.ARC : geoDistanceType;
        return QueryBuilders.geoDistanceQuery(locationField).point(lat, lon).distance(distance, unit_).geoDistance(geoDistanceType_);
    }

    /**
     * 多边形
     * A query allowing to include hits that only fall within a polygon of points
     *
     * @param locationField
     * @param points
     * @return
     */
    public static QueryBuilder geoPolygon(String locationField, List<GeoPoint> points) {
        if (StringUtils.isBlank(locationField) || CollectionUtils.isEmpty(points)) {
            throw new NullPointerException("geo field or shape points is null");
        }
        return QueryBuilders.geoPolygonQuery(locationField, points);
    }

    /**
     * BoundingBox
     * 左上-右下边界
     *
     * @param locationField
     * @param topLeft
     * @param bottomRight
     * @return
     */
    public static QueryBuilder geoBoundingBox(String locationField, GeoPoint topLeft, GeoPoint bottomRight) {
        if (StringUtils.isBlank(locationField) || topLeft == null || bottomRight == null) {
            throw new NullPointerException("geo field or boundingbox points is null");
        }
        return QueryBuilders.geoBoundingBoxQuery(locationField).setCorners(topLeft,bottomRight);
    }

}
