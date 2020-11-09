package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.model;

import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: lijz
 * @Description InModel
 * @Date: 2020/7/24
 */
public class InModel extends BaseModel<InModel>{
    /**
     * key:field value:查询关键字 list
     */
    private Map<String, List<Object>> fieldValueMap;

    public InModel(){super();}

    public static InModel builder(){
        return new InModel();
    }

    public InModel where(String field, List<Object> value){
        if(CollectionUtils.isEmpty(fieldValueMap)){
            fieldValueMap = new HashMap<>();
        }
        fieldValueMap.put(field,value);
        return this;
    }


    public Map<String, List<Object>> getFieldValueMap() {
        return fieldValueMap;
    }

    public void setFieldValueMap(Map<String, List<Object>> fieldValueMap) {
        this.fieldValueMap = fieldValueMap;
    }

    @Override
    public String toString() {
        return "InModel{" +
                "fieldValueMap=" + fieldValueMap +
                '}';
    }
}
