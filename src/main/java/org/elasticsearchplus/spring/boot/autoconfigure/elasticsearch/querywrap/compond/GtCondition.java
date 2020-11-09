package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.QueryConditionBuilder;

/**
 * @Author: lijz
 * @Description 大于值 查询conditon
 * @Date: 2020/7/24
 */
public class GtCondition implements Condition{
    private String field;
    private Object value;

    @Override
    public QueryBuilder apply() {
        return QueryConditionBuilder.gt(field,value);
    }

    private GtCondition(){}

    public static GtCondition build(){
        return new GtCondition();
    }

    public GtCondition field(String field) {
        this.field = field;
        return this;
    }
    public GtCondition value(Object value) {
        this.value =value;
        return this;
    }

    @Override
    public String toString() {
        return "GtCondition{" +
                "field='" + field + '\'' +
                ", value=" + value +
                '}';
    }
}
