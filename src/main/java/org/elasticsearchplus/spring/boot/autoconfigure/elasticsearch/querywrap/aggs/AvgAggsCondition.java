package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.aggs;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

import static org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.Constant.ES_SHARD_SIZE;

/**
 * @Author: lijz
 * @Description avg aggregation
 * @Date: 2020/9/9
 */
public class AvgAggsCondition implements AggsCondition {

    private List<String> groupbyFields = new ArrayList<>();
    private List<String> avgFields = new ArrayList<>();
    private LinkedHashMap<String, Boolean> orderMap = new LinkedHashMap<>();


    @Override
    public AggregationBuilder apply() {
        if(CollectionUtils.isEmpty(this.groupbyFields)){
            throw new RuntimeException("aggregation field to group by is null");
        }
        if (CollectionUtils.isEmpty(this.avgFields)) {
            throw new RuntimeException("aggregation field to avg is null");
        }
        BucketOrder bucketOrder = BucketOrder.compound(BucketOrder.count(false));
        if(!CollectionUtils.isEmpty(orderMap)){
            List<BucketOrder> bucketOrderList = orderMap.entrySet().stream().map(entry->BucketOrder.aggregation(entry.getKey(), entry.getValue())).collect(Collectors.toList());
            bucketOrderList.add(BucketOrder.count(false));
            bucketOrder = BucketOrder.compound(bucketOrderList);
        }
        if (!CollectionUtils.isEmpty(this.groupbyFields)) {
            if (this.groupbyFields.size() == 1) {
                String groupbyField = this.groupbyFields.get(0);
                TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(groupName2(groupbyField)).field(groupbyField).size(ES_SHARD_SIZE).shardSize(ES_SHARD_SIZE).order(bucketOrder);
                avgFields.stream().forEach(
                        avgField -> aggregationBuilder.subAggregation(AggregationBuilders.avg(avgField).field(avgField))
                );

                return aggregationBuilder;
            }

            Stack<AggregationBuilder> stack = new Stack<>();
            this.groupbyFields.stream().forEach(field -> stack.push(AggregationBuilders.terms(groupName2(field)).field(field).size(ES_SHARD_SIZE).shardSize(ES_SHARD_SIZE)));
            AggregationBuilder temp = null;
            boolean isFirstPop = true;
            do {
                AggregationBuilder aggbuilder = stack.pop();
                if (isFirstPop) {
                    avgFields.stream().forEach(
                            avgField -> aggbuilder.subAggregation(AggregationBuilders.avg(avgField).field(avgField))
                    );
                    isFirstPop = false;
                    temp = aggbuilder;
                } else {
                    temp = aggbuilder.subAggregation(temp);
                }
            } while (!stack.empty());
            return temp;
        }

        return null;
    }

    public AvgAggsCondition() {
    }

    public static AvgAggsCondition builder() {
        return new AvgAggsCondition();
    }

    public AvgAggsCondition groupby(String field) {
        this.groupbyFields.add(field);
        return this;
    }

    public AvgAggsCondition avgField(String field) {
        this.avgFields.add(field);
        return this;
    }

    public AvgAggsCondition orderby(String field, boolean asc){
        this.orderMap.put(field,asc);
        return this;
    }
    public String groupName(String field) {
        return StringUtils.join(field, "_group");
    }

    public String groupName2(String field) {
        return field;
    }
}
