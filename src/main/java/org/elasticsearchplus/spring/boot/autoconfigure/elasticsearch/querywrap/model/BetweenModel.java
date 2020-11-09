package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: lijz
 * @Description BetweenModel
 * @Date: 2020/7/24
 */
public class BetweenModel extends BaseModel<BetweenModel>{
    /**
     * key=field value=from:to
     */
    private Map<String, Map<Object, Object>> fieldValueMap = new HashMap<>();

    public BetweenModel(){super();}

    public static BetweenModel builder(){
        return new BetweenModel();
    }

    public BetweenModel where(String field, Object from, Object to){
        Map<Object, Object> fromToValueMap = new HashMap<>();
        fromToValueMap.put(from,to);
        this.getFieldValueMap().put(field,fromToValueMap);
        return this;
    }


    public Map<String, Map<Object, Object>> getFieldValueMap() {
        return fieldValueMap;
    }

    public void setFieldValueMap(Map<String, Map<Object, Object>> fieldValueMap) {
        this.fieldValueMap = fieldValueMap;
    }

    @Override
    public String toString() {
        return "BetweenModel{" +
                "fieldValueMap=" + fieldValueMap +
                '}';
    }
}
