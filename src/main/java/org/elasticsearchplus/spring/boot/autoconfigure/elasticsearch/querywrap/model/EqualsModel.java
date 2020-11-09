package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.model;

import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: lijz
 * @Description EqualsModel
 * @Date: 2020/7/24
 */
public class EqualsModel extends BaseModel<EqualsModel>{
    /**
     * key:field value:查询关键字
     */
    private Map<String,Object> fieldValueMap;

    public EqualsModel(){super();}

    public static EqualsModel builder(){
        return new EqualsModel();
    }

    public EqualsModel where(String field, Object value){
        if(CollectionUtils.isEmpty(fieldValueMap)){
            fieldValueMap = new HashMap<>();
        }
        fieldValueMap.put(field,value);
        return this;
    }


    public Map<String, Object> getFieldValueMap() {
        return fieldValueMap;
    }

    public void setFieldValueMap(Map<String, Object> fieldValueMap) {
        this.fieldValueMap = fieldValueMap;
    }

    @Override
    public String toString() {
        return "EqualsModel{" +
                "fieldValueMap=" + fieldValueMap +
                '}';
    }
}
