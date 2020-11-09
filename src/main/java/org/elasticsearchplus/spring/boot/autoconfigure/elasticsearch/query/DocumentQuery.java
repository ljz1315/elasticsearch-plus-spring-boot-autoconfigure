package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.CompondSearchRequestWrap;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.model.BaseModel;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.model.BetweenModel;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.model.EqualsModel;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.model.InModel;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.service.ElasticsearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: lijz
 * @Description elasticsearch query util
 * @Date: 2020/8/6
 */
@Component
public class DocumentQuery {
    private static final Logger log = LoggerFactory.getLogger(DocumentQuery.class);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ElasticsearchService elasticsearchService;

    public DocumentQuery() {
    }

    public <T> List<T> queryEquals(@NonNull EqualsModel model, @NonNull Class<T> clazz) {
        validateNonNull(model, clazz);
        SearchResponse searchResponse = elasticsearchService.queryEquals(model);
        return wrapResponse((Class<T>) clazz, searchResponse);
    }


    public <T> List<T> queryIn(@NonNull InModel model, @NonNull Class<T> clazz) {
        validateNonNull(model, clazz);
        SearchResponse searchResponse = elasticsearchService.queryIn(model);
        return wrapResponse(clazz, searchResponse);

    }

    public <T> List<T> queryNot(@NonNull EqualsModel model, @NonNull Class<T> clazz) {
        validateNonNull(model, clazz);
        SearchResponse searchResponse = elasticsearchService.queryNot(model);
        return wrapResponse(clazz, searchResponse);

    }

    public <T> List<T> queryBetween(@NonNull BetweenModel model, @NonNull Class<T> clazz) {
        validateNonNull(model, clazz);
        SearchResponse searchResponse = elasticsearchService.queryBetween(model);
        return wrapResponse(clazz, searchResponse);

    }

    /**
     * 非聚集的复合查询
     *
     * @param compondSearchRequestWrap
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> queryCompond(@NonNull CompondSearchRequestWrap compondSearchRequestWrap, @NonNull Class<T> clazz) {
        validateNonNull(compondSearchRequestWrap, clazz);
        SearchResponse searchResponse = elasticsearchService.queryCompond(compondSearchRequestWrap);
        return wrapResponse(clazz, searchResponse);
    }

//    public <T> List<T> queryCompondScroll(@NonNull CompondSearchRequestWrap compondSearchRequestWrap, @NonNull Class<T> clazz) {
//        validateNonNull(compondSearchRequestWrap, clazz);
//        SearchResponse searchResponse = elasticsearchService.queryCompond(compondSearchRequestWrap);
//        return wrapResponse(clazz, searchResponse);
//    }

    /**
     * 聚集的复合查询
     * 结果自行解析
     * @param compondSearchRequestWrap
     * @return
     */
    public SearchResponse queryAggs(@NonNull CompondSearchRequestWrap compondSearchRequestWrap) {
        validateNonNull(compondSearchRequestWrap);
        return elasticsearchService.queryCompond(compondSearchRequestWrap);
    }

    private <T> void validateNonNull(BaseModel model, Class<T> clazz) {
        if (model.getIndex() == null) {
            throw new NullPointerException("index");
        } else if (model.getType() == null) {
            throw new NullPointerException("type");
        } else if (clazz == null) {
            throw new NullPointerException("clazz");
        } else {

        }
    }
    private <T> void validateNonNull(CompondSearchRequestWrap compondSearchRequestWrap) {
        if (compondSearchRequestWrap.getIndex() == null) {
            throw new NullPointerException("index");
        } else if (compondSearchRequestWrap.getType() == null) {
            throw new NullPointerException("type");
        } else {

        }
    }
    private <T> void validateNonNull(CompondSearchRequestWrap compondSearchRequestWrap, Class<T> clazz) {
        if (compondSearchRequestWrap.getIndex() == null) {
            throw new NullPointerException("index");
        } else if (compondSearchRequestWrap.getType() == null) {
            throw new NullPointerException("type");
        } else if (clazz == null) {
            throw new NullPointerException("clazz");
        } else {

        }
    }

    private <T> List<T> wrapResponse(@NonNull Class<T> clazz, SearchResponse searchResponse) {
        if (searchResponse != null && !ArrayUtils.isEmpty(searchResponse.getHits().getHits())) {
            List<SearchHit> hitList = Arrays.asList(searchResponse.getHits().getHits());
            return hitList.stream().map(hit -> {
                try {
                    return this.objectMapper.readValue(hit.getSourceAsString(), clazz);
                } catch (IOException var8) {
                    log.error("error:{}", var8);
                    return null;
                }
            }).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }
}
