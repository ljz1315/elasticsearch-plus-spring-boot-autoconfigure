package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.aggs;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.Constant.ES_SHARD_SIZE;

/**
 * @Author: lijz
 * @Description count aggregation
 * @Date: 2020/7/25
 */
public class CountAggsCondition implements AggsCondition{

    private List<String> groupbyFields = new ArrayList<>();
    private String distinctField;
    private long precisionThreshold = 500;

    @Override
    public AggregationBuilder apply() {
        if(CollectionUtils.isEmpty(this.groupbyFields)){
            throw new RuntimeException("aggregation field to group by is null");
        }

        if(!CollectionUtils.isEmpty(this.groupbyFields)){
            if(this.groupbyFields.size() == 1){
                String groupbyField = this.groupbyFields.get(0);
                TermsAggregationBuilder aggregationBuilder = AggregationBuilders.terms(groupName2(groupbyField)).field(groupbyField).size(ES_SHARD_SIZE).shardSize(ES_SHARD_SIZE).order(BucketOrder.count(false));
                if(StringUtils.isNoneBlank(distinctField)){
                    aggregationBuilder.subAggregation(AggregationBuilders.cardinality("count").field(distinctField).precisionThreshold(precisionThreshold));
                }
                return aggregationBuilder;
            }

            Stack<AggregationBuilder> stack = new Stack<>();
            this.groupbyFields.stream().forEach(field->stack.push(AggregationBuilders.terms(groupName2(field)).field(field).size(ES_SHARD_SIZE).shardSize(ES_SHARD_SIZE)));
            AggregationBuilder temp = null;
            boolean isFirstPop = true;
            do {
                AggregationBuilder aggbuilder = stack.pop();
                if(isFirstPop){
                    if(StringUtils.isNoneBlank(distinctField)){
                        aggbuilder.subAggregation(AggregationBuilders.cardinality("count").field(distinctField).precisionThreshold(100));
                    }
                    isFirstPop = false;
                    temp = aggbuilder;
                }else{
                    temp = aggbuilder.subAggregation(temp);
                }
            } while (!stack.empty());
            return temp;
        }

        return null;
    }

    public CountAggsCondition(){}
    public static CountAggsCondition builder(){
        return new CountAggsCondition();
    }

    public CountAggsCondition groupby(String field){
        this.groupbyFields.add(field);
        return this;
    }

    public CountAggsCondition distinctField(String field){
        this.distinctField = field;
        return this;
    }

    public CountAggsCondition precisionThreshold(long precisionThreshold){
        this.precisionThreshold = precisionThreshold;
        return this;
    }

    public String groupName(String field){
        return StringUtils.join(field,"_group");
    }

    public String groupName2(String field){
        return field;
    }
}
