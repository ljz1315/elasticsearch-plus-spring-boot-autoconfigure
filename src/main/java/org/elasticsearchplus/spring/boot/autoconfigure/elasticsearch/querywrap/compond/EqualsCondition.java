package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.QueryConditionBuilder;

/**
 * @Author: lijz
 * @Description 等值 查询conditon
 * @Date: 2020/7/24
 */
public class EqualsCondition implements Condition{
    private String field;
    private Object value;

    @Override
    public QueryBuilder apply() {
        return QueryConditionBuilder.equals(field,value);
    }

    private EqualsCondition(){}

    public static EqualsCondition build(){
        return new EqualsCondition();
    }

    public EqualsCondition field(String field) {
        this.field = field;
        return this;
    }
    public EqualsCondition value(Object value) {
        this.value =value;
        return this;
    }

    @Override
    public String toString() {
        return "EqualsCondition{" +
                "field='" + field + '\'' +
                ", value=" + value +
                '}';
    }

}
