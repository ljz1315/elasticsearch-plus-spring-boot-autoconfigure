package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond;

import com.google.common.collect.Lists;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.QueryConditionBuilder;

/**
 * @Author: lijz
 * @Description 不等值 查询conditon
 * @Date: 2020/7/24
 */
public class NotCondition implements Condition{
    private String field;
    private Object value;

    @Override
    public QueryBuilder apply() {
        return QueryConditionBuilder.getObject().not(Lists.newArrayList(QueryConditionBuilder.equals(field,value)));
    }

    private NotCondition(){}

    public static NotCondition build(){
        return new NotCondition();
    }

    public NotCondition field(String field) {
        this.field = field;
        return this;
    }
    public NotCondition value(Object value) {
        this.value =value;
        return this;
    }

    @Override
    public String toString() {
        return "NotCondition{" +
                "field='" + field + '\'' +
                ", value=" + value +
                '}';
    }

}
