package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.QueryConditionBuilder;

/**
 * @Author: lijz
 * @Description 大于或等于值 查询conditon
 * @Date: 2020/7/24
 */
public class GteCondition implements Condition{
    private String field;
    private Object value;

    @Override
    public QueryBuilder apply() {
        return QueryConditionBuilder.gte(field,value);
    }

    private GteCondition(){}

    public static GteCondition build(){
        return new GteCondition();
    }

    public GteCondition field(String field) {
        this.field = field;
        return this;
    }
    public GteCondition value(Object value) {
        this.value =value;
        return this;
    }

    @Override
    public String toString() {
        return "GteCondition{" +
                "field='" + field + '\'' +
                ", value=" + value +
                '}';
    }
}
