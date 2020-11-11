package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.elasticsearch.action.search.*;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.handle.Handler;
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
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: lijz
 * @Description elasticsearch query util
 * @Date: 2020/8/6
 */
@Component
public class DocumentQuery {
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentQuery.class);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public DocumentQuery() {
    }

    public <T> List<T> queryEquals(@NonNull EqualsModel model, @NonNull Class<T> clazz) {
        validateNonNull(model, clazz);
        SearchResponse searchResponse = elasticsearchService.queryEquals(model);
        return wrapResponse((Class<T>) clazz, searchResponse);
    }

    /**
     * 返回结果集 每条记录为Map
     *
     * @param model
     * @return
     */
    public List<Map<String, Object>> queryEquals(@NonNull EqualsModel model) {
        validateNonNull(model);
        SearchResponse searchResponse = elasticsearchService.queryEquals(model);
        return wrapResponseMap(searchResponse);
    }

    public <T> List<T> queryIn(@NonNull InModel model, @NonNull Class<T> clazz) {
        validateNonNull(model, clazz);
        SearchResponse searchResponse = elasticsearchService.queryIn(model);
        return wrapResponse(clazz, searchResponse);

    }

    /**
     * 返回结果集 每条记录为Map
     *
     * @param model
     * @return
     */
    public List<Map<String, Object>> queryIn(@NonNull InModel model) {
        validateNonNull(model);
        SearchResponse searchResponse = elasticsearchService.queryIn(model);
        return wrapResponseMap(searchResponse);

    }

    public <T> List<T> queryNot(@NonNull EqualsModel model, @NonNull Class<T> clazz) {
        validateNonNull(model, clazz);
        SearchResponse searchResponse = elasticsearchService.queryNot(model);
        return wrapResponse(clazz, searchResponse);

    }

    /**
     * 返回结果集 每条记录为Map
     *
     * @param model
     * @return
     */
    public List<Map<String, Object>> queryNot(@NonNull EqualsModel model) {
        validateNonNull(model);
        SearchResponse searchResponse = elasticsearchService.queryNot(model);
        return wrapResponseMap(searchResponse);

    }

    public <T> List<T> queryBetween(@NonNull BetweenModel model, @NonNull Class<T> clazz) {
        validateNonNull(model, clazz);
        SearchResponse searchResponse = elasticsearchService.queryBetween(model);
        return wrapResponse(clazz, searchResponse);

    }

    /**
     * 返回结果集 每条记录为Map
     *
     * @param model
     * @return
     */
    public List<Map<String, Object>> queryBetween(@NonNull BetweenModel model) {
        validateNonNull(model);
        SearchResponse searchResponse = elasticsearchService.queryBetween(model);
        return wrapResponseMap(searchResponse);
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

    /**
     * 返回结果集 每条记录为Map
     * @param compondSearchRequestWrap
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> queryCompond(@NonNull CompondSearchRequestWrap compondSearchRequestWrap) {
        validateNonNull(compondSearchRequestWrap);
        SearchResponse searchResponse = elasticsearchService.queryCompond(compondSearchRequestWrap);
        return wrapResponseMap(searchResponse);
    }

    /**
     * scroll 处理
     * @param compondSearchRequestWrap
     * @param clazz
     * @param handler
     * @param <T>
     * @return
     */
    public <T> boolean queryCompondScroll(@NonNull CompondSearchRequestWrap compondSearchRequestWrap, @NonNull Class<T> clazz, Handler<T> handler) {
        SearchRequest searchRequest = compondSearchRequestWrap.size(1000).build();
        searchRequest.scroll(new TimeValue(60000));
        List<String> scrollIds = Lists.newArrayList();
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            String scrollId = searchResponse.getScrollId();
            SearchHit[] searchHits = searchResponse.getHits().getHits();

            do {
                List<SearchHit> hitList = Arrays.asList(searchHits);
                List<T> dataList = hitList.stream().map(hit -> {
                    try {
                        return this.objectMapper.readValue(hit.getSourceAsString(), clazz);
                    } catch (IOException var8) {
                        LOGGER.error("error:{}", var8);
                        return null;
                    }
                }).filter(data->data != null).collect(Collectors.toList());
                // 业务逻辑 自行实现
                handler.handle(dataList);

                // 滚动下一个窗口
                SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(new TimeValue(60000));
                searchResponse = restHighLevelClient.scroll(scrollRequest, RequestOptions.DEFAULT);
                scrollId = searchResponse.getScrollId();
                scrollIds.add(scrollId);
                searchHits = searchResponse.getHits().getHits();

            } while (searchHits != null && searchHits.length > 0);

            // clearScroll
            ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
            clearScrollRequest.scrollIds(scrollIds);
            ClearScrollResponse clearScrollResponse = restHighLevelClient.clearScroll(clearScrollRequest, RequestOptions.DEFAULT);
            boolean succeeded = clearScrollResponse.isSucceeded();
            return succeeded;
        } catch (Throwable e){
            LOGGER.error("DocumentQuery#queryCompondScroll error.", e);
            return false;
        }
    }

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

    private <T> void validateNonNull(BaseModel model) {
        if (model.getIndex() == null) {
            throw new NullPointerException("index");
        } else if (model.getType() == null) {
            throw new NullPointerException("type");
        } else {

        }
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
                    LOGGER.error("error:{}", var8);
                    return null;
                }
            }).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }

    private List<Map<String, Object>> wrapResponseMap(SearchResponse searchResponse) {
        if (searchResponse != null && !ArrayUtils.isEmpty(searchResponse.getHits().getHits())) {
            List<SearchHit> hitList = Arrays.asList(searchResponse.getHits().getHits());
            return hitList.stream().map(hit -> hit.getSourceAsMap()).collect(Collectors.toList());
        }
        return Lists.newArrayList();
    }
}
