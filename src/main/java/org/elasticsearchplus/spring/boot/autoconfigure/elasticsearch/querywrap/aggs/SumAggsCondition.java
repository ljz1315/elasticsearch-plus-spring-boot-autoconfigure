package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.aggs;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.Constant.ES_SHARD_SIZE;


/**
 * @Author: lijz
 * @Description sum aggregation
 * @Date: 2020/7/25
 */
public class SumAggsCondition implements AggsCondition {

    private List<String> groupbyFields = new ArrayList<>();
    private List<String> sumFields = new ArrayList<>();
    private String distinctField;

    @Override
    public AggregationBuilder apply() {
        if (CollectionUtils.isEmpty(this.sumFields) || CollectionUtils.isEmpty(this.groupbyFields)) {
            throw new RuntimeException("aggregation field to sum or group by is null");
        }

        if (!CollectionUtils.isEmpty(this.groupbyFields)) {
            if (this.groupbyFields.size() == 1) {
                String groupbyField = this.groupbyFields.get(0);
                TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(groupName2(groupbyField)).field(groupbyField).size(ES_SHARD_SIZE).shardSize(ES_SHARD_SIZE);
                if(StringUtils.isNoneBlank(distinctField)){
                    aggregationBuilder.subAggregation(AggregationBuilders.cardinality("count").field(distinctField));
                }
                sumFields.stream().forEach(sumField -> aggregationBuilder.subAggregation(AggregationBuilders.sum(sumName2(sumField)).field(sumField)));
                return aggregationBuilder;
            }

            Stack<AggregationBuilder> stack = new Stack<>();
            this.groupbyFields.stream().forEach(field -> stack.push(AggregationBuilders.terms(groupName2(field)).field(field).size(ES_SHARD_SIZE).shardSize(ES_SHARD_SIZE)));
            AggregationBuilder temp = null;
            boolean isFirstPop = true;
            do {
                AggregationBuilder aggbuilder = stack.pop();
                if (isFirstPop) {
                    if(StringUtils.isNoneBlank(distinctField)){
                        aggbuilder.subAggregation(AggregationBuilders.cardinality("count").field(distinctField));
                    }
                    sumFields.stream().forEach(sumField -> aggbuilder.subAggregation(AggregationBuilders.sum(sumName2(sumField)).field(sumField)));
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

    public SumAggsCondition() {
    }

    public static SumAggsCondition builder() {
        return new SumAggsCondition();
    }

    public SumAggsCondition groupby(String field) {
        this.groupbyFields.add(field);
        return this;
    }

    public SumAggsCondition sum(String field) {
        this.sumFields.add(field);
        return this;
    }

    public SumAggsCondition distinctField(String field){
        this.distinctField = field;
        return this;
    }

    public String groupName(String field) {
        return StringUtils.join(field, "_group");
    }

    public String groupName2(String field) {
        return field;
    }

    public String sumName(String field) {
        return StringUtils.join(field, "_sum");
    }

    public String sumName2(String field) {
        return field;
    }
}
