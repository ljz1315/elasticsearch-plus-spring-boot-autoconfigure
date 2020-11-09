package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.QueryConditionBuilder;

/**
 * @Author: lijz
 * @Description BETWEEN 查询conditon
 * @Date: 2020/7/24
 */
public class BetweenCondition implements Condition{
    private String field;
    private Object from;
    private Object to;

    @Override
    public QueryBuilder apply() {
        return QueryConditionBuilder.between(field,from,to);
    }

    private BetweenCondition(){}

    public static BetweenCondition build(){
        return new BetweenCondition();
    }

    public BetweenCondition field(String field) {
        this.field = field;
        return this;
    }
    public BetweenCondition from(Object from) {
        this.from =from;
        return this;
    }

    public BetweenCondition to(Object to) {
        this.to =to;
        return this;
    }

    @Override
    public String toString() {
        return "BetweenCondition{" +
                "field='" + field + '\'' +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
