package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap;

import com.google.common.collect.Lists;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.aggs.AggsCondition;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.aggs.SumAggsCondition;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond.*;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.Constant.ES_DEFAULT_SIZE;


/**
 * @Author: lijz
 * @Description 复合查询 请求封装
 * @Date: 2020/7/24
 */
public class CompondSearchRequestWrap {
    private String index;
    private String type;
    private Integer size;
    private final List<String> selectFields = new ArrayList<>();
    private final BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
    private final List<QueryBuilder> andList = new ArrayList<>();
    private final List<QueryBuilder> notList = new ArrayList<>();
    private final List<QueryBuilder> orList = new ArrayList<>();
    private final List<QueryBuilder> filters = new ArrayList<>();
    private LinkedHashMap<String, SortOrder> orderMap;
    private AggregationBuilder aggBuilder = null;
    /**
     * 分页
     */
    private PageInfo page;

    private CompondSearchRequestWrap() {
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static CompondSearchRequestWrap builder(String index, String type, List<String> selectFields) {
        CompondSearchRequestWrap wrap = new CompondSearchRequestWrap();
        wrap.index = index;
        wrap.type = type;
        wrap.size = ES_DEFAULT_SIZE;
        if(!CollectionUtils.isEmpty(selectFields)){
            wrap.selectFields.addAll(selectFields);
        }

        return wrap;
    }

    public CompondSearchRequestWrap size(Integer size) {
        this.size = size;
        return this;
    }

    /**
     * 字段非空的判断 exists加在must里
     *
     * @param condition
     * @return
     */
    public CompondSearchRequestWrap and(Condition condition) {
        andList.add(condition.apply());
        return this;
    }

    public CompondSearchRequestWrap or(Condition condition) {
        orList.add(condition.apply());
        return this;
    }

    /**
     * 字段为空的判断 exists加在mustnot里
     *
     * @param condition
     * @return
     */
    public CompondSearchRequestWrap not(Condition condition) {
        notList.add(condition.apply());
        return this;
    }

    /**
     * filter query
     * @param condition
     * @return
     */
    public CompondSearchRequestWrap filter(Condition condition) {
        if(NotCondition.class.isAssignableFrom(condition.getClass())){
            throw new RuntimeException("NotCondition can not be added filters.because mustnot is running by filter.");
        }
        filters.add(condition.apply());
        return this;
    }

    public CompondSearchRequestWrap aggs(AggsCondition condition) {
        this.aggBuilder = condition.apply();
        return this;
    }

    public CompondSearchRequestWrap orderby(LinkedHashMap<String, SortOrder> orderMap) {
        this.orderMap = orderMap;
        return this;
    }

    public CompondSearchRequestWrap page(int pageNum, int size) {
        PageInfo page = PageInfo.builder().isPage(true).pageNum(pageNum).size(size).build();
        this.page = page;
        return this;
    }

    public SearchRequest build() {
        List<QueryBuilder> andList_ = this.andList;
        List<QueryBuilder> orList_ = this.orList;
        List<QueryBuilder> notList_ = this.notList;
        List<QueryBuilder> filterList_ = this.filters;

        if (!CollectionUtils.isEmpty(andList_)) {
            andList_.stream().forEach(queryBuilder -> this.boolQueryBuilder.must(queryBuilder));
        }

        if (!CollectionUtils.isEmpty(orList_)) {
            orList_.stream().forEach(queryBuilder -> this.boolQueryBuilder.should(queryBuilder));
            this.boolQueryBuilder.minimumShouldMatch(1);
        }
        if (!CollectionUtils.isEmpty(notList_)) {
            notList_.stream().forEach(queryBuilder -> this.boolQueryBuilder.mustNot(queryBuilder));
        }
        if (!CollectionUtils.isEmpty(filterList_)) {
            filterList_.stream().forEach(queryBuilder -> this.boolQueryBuilder.filter(queryBuilder));
        }
        return QueryConditionBuilder.buildSearchRequestAggs(this.index, this.type, this.size, this.selectFields, this.boolQueryBuilder, this.orderMap, this.aggBuilder, this.page);
    }

    public static void main(String[] args) {
        // 示例
        SearchRequest searchRequest = CompondSearchRequestWrap
                .builder("index_1", "type_1", null)
                .and(EqualsCondition.build().field("city").value("010"))
                .and(InCondition.build().field("suplier").value(Lists.newArrayList("1", "2")))
                .or(EqualsCondition.build().field("amount").value("1000"))
                .aggs(SumAggsCondition.builder().groupby("city_code").groupby("hour_id").sum("amount").sum("pay_amount"))
                .build();


        System.out.println(searchRequest);
    }
}
