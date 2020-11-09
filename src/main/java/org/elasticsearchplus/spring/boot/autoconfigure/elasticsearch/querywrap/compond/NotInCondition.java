package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond;

import com.google.common.collect.Lists;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.QueryConditionBuilder;

import java.util.List;

/**
 * @Author: lijz
 * @Description IN 查询conditon
 * @Date: 2020/7/24
 */
public class NotInCondition implements Condition{
    private String field;
    private List<Object> value;

    @Override
    public QueryBuilder apply() {
        return QueryConditionBuilder.getObject().not(Lists.newArrayList(QueryConditionBuilder.in(field,value)));
    }

    private NotInCondition(){}

    public static NotInCondition build(){
        return new NotInCondition();
    }

    public NotInCondition field(String field) {
        this.field = field;
        return this;
    }
    public NotInCondition value(List<Object> value) {
        this.value =value;
        return this;
    }

    @Override
    public String toString() {
        return "NotInCondition{" +
                "field='" + field + '\'' +
                ", value=" + value +
                '}';
    }
}
