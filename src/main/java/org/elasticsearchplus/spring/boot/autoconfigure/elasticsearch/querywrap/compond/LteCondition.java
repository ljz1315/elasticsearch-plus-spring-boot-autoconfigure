package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.QueryConditionBuilder;

/**
 * @Author: lijz
 * @Description 小于或等于值 查询conditon
 * @Date: 2020/7/24
 */
public class LteCondition implements Condition{
    private String field;
    private Object value;

    @Override
    public QueryBuilder apply() {
        return QueryConditionBuilder.lte(field,value);
    }

    private LteCondition(){}

    public static LteCondition build(){
        return new LteCondition();
    }

    public LteCondition field(String field) {
        this.field = field;
        return this;
    }
    public LteCondition value(Object value) {
        this.value =value;
        return this;
    }

    @Override
    public String toString() {
        return "LteCondition{" +
                "field='" + field + '\'' +
                ", value=" + value +
                '}';
    }
}
