package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond;

import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.QueryConditionBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lijz
 * @Description 地理位置 多边形 查询conditon
 * @Date: 2020/8/24
 */
public class GeoPolygonCondition implements Condition {
    private String field;
    private double lat;
    private double lon;
    private List<GeoPoint> points = new ArrayList<>();

    @Override
    public QueryBuilder apply() {
        return QueryConditionBuilder.geoPolygon(field, points);
    }

    private GeoPolygonCondition() {
    }

    public static GeoPolygonCondition build() {
        return new GeoPolygonCondition();
    }

    public GeoPolygonCondition field(String field) {
        this.field = field;
        return this;
    }

    public GeoPolygonCondition lacation(double lat, double lon) {
        GeoPoint point = new GeoPoint(lat, lon);
        this.points.add(point);
        return this;
    }

    public GeoPolygonCondition lon(double lon) {
        this.lon = lon;
        return this;
    }

}
