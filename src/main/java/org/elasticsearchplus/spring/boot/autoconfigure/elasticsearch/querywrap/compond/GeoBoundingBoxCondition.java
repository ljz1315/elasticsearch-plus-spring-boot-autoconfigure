package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond;

import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.QueryConditionBuilder;

/**
 * @Author: lijz
 * @Description 地理位置 左上-右下边界 查询conditon
 * @Date: 2020/8/24
 */
public class GeoBoundingBoxCondition implements Condition {
    private String field;
    private GeoPoint topLeft;
    private GeoPoint bottomRight;

    @Override
    public QueryBuilder apply() {
        return QueryConditionBuilder.geoBoundingBox(field, topLeft, bottomRight);
    }

    private GeoBoundingBoxCondition() {
    }

    public static GeoBoundingBoxCondition build() {
        return new GeoBoundingBoxCondition();
    }

    public GeoBoundingBoxCondition field(String field) {
        this.field = field;
        return this;
    }

    public GeoBoundingBoxCondition topLeft(double lat, double lon) {
        this.topLeft = new GeoPoint(lat, lon);
        return this;
    }

    public GeoBoundingBoxCondition bottomRight(double lat, double lon) {
        this.bottomRight = new GeoPoint(lat, lon);
        return this;
    }

    @Override
    public String toString() {
        return "GeoBoundingBoxCondition{" +
                "field='" + field + '\'' +
                ", topLeft=" + topLeft +
                ", bottomRight=" + bottomRight +
                '}';
    }
}
