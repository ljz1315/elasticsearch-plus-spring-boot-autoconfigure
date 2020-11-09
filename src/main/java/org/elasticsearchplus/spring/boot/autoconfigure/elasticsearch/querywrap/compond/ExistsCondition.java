package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.QueryConditionBuilder;

/**
 * @Author: lijz
 * @Description 非空 查询conditon
 * @Date: 2020/7/24
 */
public class ExistsCondition implements Condition{
    private String field;

    @Override
    public QueryBuilder apply() {
        return QueryConditionBuilder.exists(field);
    }

    private ExistsCondition(){}

    public static ExistsCondition build(){
        return new ExistsCondition();
    }

    public ExistsCondition field(String field) {
        this.field = field;
        return this;
    }


    @Override
    public String toString() {
        return "ExistsCondition{" +
                "field='" + field + '\'' +
                '}';
    }
}
