package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.compond;

import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.QueryConditionBuilder;

/**
 * @Author: lijz
 * @Description 地理位置 方圆xxx公里 查询conditon
 * @Date: 2020/8/24
 */
public class GeoDistanceCondition implements Condition {
    private String field;
    private double lat;
    private double lon;
    private double distance;
    private DistanceUnit unit = DistanceUnit.KILOMETERS;
    private GeoDistance geoDistanceType = GeoDistance.ARC;

    @Override
    public QueryBuilder apply() {
        return QueryConditionBuilder.geoDistance(field, lat, lon, distance, unit, geoDistanceType);
    }

    private GeoDistanceCondition() {
    }

    public static GeoDistanceCondition build() {
        return new GeoDistanceCondition();
    }

    public GeoDistanceCondition field(String field) {
        this.field = field;
        return this;
    }

    public GeoDistanceCondition lat(double lat) {
        this.lat = lat;
        return this;
    }

    public GeoDistanceCondition lon(double lon) {
        this.lon = lon;
        return this;
    }

    public GeoDistanceCondition distance(double distance) {
        this.distance = distance;
        return this;
    }

    public GeoDistanceCondition distanceUnit(DistanceUnit unit) {
        this.unit = unit;
        return this;
    }

    public GeoDistanceCondition geoDistanceType(GeoDistance geoDistanceType) {
        this.geoDistanceType = geoDistanceType;
        return this;
    }

    @Override
    public String toString() {
        return "GeoDistanceCondition{" +
                "field='" + field + '\'' +
                ", lat=" + lat +
                ", lon=" + lon +
                ", distance=" + distance +
                ", unit=" + unit +
                ", geoDistanceType=" + geoDistanceType +
                '}';
    }
}
