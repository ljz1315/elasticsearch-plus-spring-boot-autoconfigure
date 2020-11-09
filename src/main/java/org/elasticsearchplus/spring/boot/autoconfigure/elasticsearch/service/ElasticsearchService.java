package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.service;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.CompondSearchRequestWrap;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.model.BetweenModel;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.model.EqualsModel;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.model.InModel;

/**
 * ElasticsearchService 封装
 */
public interface ElasticsearchService {

    /**
     * 等值 查询
     * @param model
     * @return
     */
    SearchResponse queryEquals(EqualsModel model);

    /**
     * 不等值 查询
     * @param model
     * @return
     */
    SearchResponse queryNot(EqualsModel model);

    /**
     * IN 查询
     * @param model
     * @return
     */
    SearchResponse queryIn(InModel model);

    /**
     * BETWEEN AND 查询
     * @param model
     * @return
     */
    SearchResponse queryBetween(BetweenModel model);

    /**
     * 复合查询 含聚集
     * @param compondSearchRequestWrap
     * @return
     */
    SearchResponse queryCompond(CompondSearchRequestWrap compondSearchRequestWrap);
}
