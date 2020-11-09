package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.service.impl;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.CompondSearchRequestWrap;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.SearchRequestWrap;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond.BetweenCondition;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.model.BetweenModel;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.model.EqualsModel;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.model.InModel;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.service.ElasticsearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Set;

/**
 * @Author: lijz
 * @Description ElasticsearchServiceImpl
 * @Date: 2020/7/24
 */
@Service
public class ElasticsearchServiceImpl implements ElasticsearchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchServiceImpl.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public SearchResponse queryEquals(EqualsModel model) {
        if(model == null || CollectionUtils.isEmpty(model.getFieldValueMap())){
            return new SearchResponse();
        }
        return responseExceptionHandle(SearchRequestWrap.equals(model.getIndex(),model.getType(),model.getSize(),model.getSelectFields(),model.getFieldValueMap(),model.getOrderMap()));
    }


    @Override
    public SearchResponse queryNot(EqualsModel model) {
        if(model == null || CollectionUtils.isEmpty(model.getFieldValueMap())){
            return new SearchResponse();
        }
        return responseExceptionHandle(SearchRequestWrap.not(model.getIndex(),model.getType(),model.getSize(),model.getSelectFields(),model.getFieldValueMap(),model.getOrderMap()));
    }

    @Override
    public SearchResponse queryIn(InModel model) {
        if(model == null || CollectionUtils.isEmpty(model.getFieldValueMap())){
            return new SearchResponse();
        }
        return responseExceptionHandle(SearchRequestWrap.in(model.getIndex(),model.getType(),model.getSize(),model.getSelectFields(),model.getFieldValueMap(),model.getOrderMap()));
    }

    @Override
    public SearchResponse queryBetween(BetweenModel model) {
        if(model == null || CollectionUtils.isEmpty(model.getFieldValueMap())){
            return new SearchResponse();
        }
        Map<String, Map<Object, Object>> fieldValueMap = model.getFieldValueMap();
        Set<Map.Entry<String, Map<Object, Object>>> entrySet = fieldValueMap.entrySet();
        if(fieldValueMap.size() == 1){
            Map.Entry<String, Map<Object, Object>> entry = entrySet.stream().findAny().get();
            Map.Entry<Object, Object> entryFromTo = entry.getValue().entrySet().stream().findAny().get();
            return responseExceptionHandle(SearchRequestWrap.between(model.getIndex(),model.getType(),model.getSize(),model.getSelectFields(),entry.getKey(),entryFromTo.getKey(),entryFromTo.getValue(),model.getOrderMap()));
        }else{
            CompondSearchRequestWrap requestWrap = CompondSearchRequestWrap.builder(model.getIndex(),model.getType(),model.getSelectFields());
            entrySet.stream().forEach(
                    entry -> {
                        Map.Entry<Object, Object> entryFromTo = entry.getValue().entrySet().stream().findAny().get();
                        requestWrap.and(BetweenCondition.build().field(entry.getKey()).from(entryFromTo.getKey()).to(entryFromTo.getValue()));
                    }
            );
            return responseExceptionHandle(requestWrap.build());
        }
    }

    @Override
    public SearchResponse queryCompond(CompondSearchRequestWrap compondSearchRequestWrap) {
        return responseExceptionHandle(compondSearchRequestWrap.build());
    }

    public SearchResponse responseExceptionHandle(SearchRequest searchRequest){
        LOGGER.info("SearchRequest:{}",searchRequest);
        try {
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        }catch (Exception e){
            LOGGER.error("查询Elasticesarch失败.error:{}",e);
            throw new RuntimeException("查询Elasticesarch失败.");
        }
    }
}
