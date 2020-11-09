package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.QueryConditionBuilder;

/**
 * @Author: lijz
 * @Description 小于值 查询conditon
 * @Date: 2020/7/24
 */
public class LtCondition implements Condition{
    private String field;
    private Object value;

    @Override
    public QueryBuilder apply() {
        return QueryConditionBuilder.lt(field,value);
    }

    private LtCondition(){}

    public static LtCondition build(){
        return new LtCondition();
    }

    public LtCondition field(String field) {
        this.field = field;
        return this;
    }
    public LtCondition value(Object value) {
        this.value =value;
        return this;
    }

    @Override
    public String toString() {
        return "LtCondition{" +
                "field='" + field + '\'' +
                ", value=" + value +
                '}';
    }
}
