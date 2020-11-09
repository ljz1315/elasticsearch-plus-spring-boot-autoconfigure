package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.QueryConditionBuilder;

import java.util.List;

/**
 * @Author: lijz
 * @Description IN 查询conditon
 * @Date: 2020/7/24
 */
public class InCondition implements Condition{
    private String field;
    private List<Object> value;

    @Override
    public QueryBuilder apply() {
        return QueryConditionBuilder.in(field,value);
    }

    private InCondition(){}

    public static InCondition build(){
        return new InCondition();
    }

    public InCondition field(String field) {
        this.field = field;
        return this;
    }
    public InCondition value(List<Object> value) {
        this.value =value;
        return this;
    }

    @Override
    public String toString() {
        return "InCondition{" +
                "field='" + field + '\'' +
                ", value=" + value +
                '}';
    }
}
