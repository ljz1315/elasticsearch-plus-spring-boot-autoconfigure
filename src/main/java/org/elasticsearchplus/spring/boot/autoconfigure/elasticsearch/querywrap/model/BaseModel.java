package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.querywrap.model;

import org.elasticsearch.search.sort.SortOrder;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @Author: lijz
 * @Description elasticsearch base model
 * @Date: 2020/7/24
 */
public class BaseModel<T extends BaseModel> implements Serializable {
    private static final transient long serialVersionUID = -8468524719852068922L;

    private String index;
    private String type;
    private Integer size = 1000;
    private List<String> selectFields;
    private LinkedHashMap<String, SortOrder> orderMap = new LinkedHashMap<>();

    public T index(String index){
        this.setIndex(index);
        return (T)this;
    }
    public T type(String type){
        this.setType(type);
        return (T)this;
    }
    public T size(Integer size){
        this.setSize(size);
        return (T)this;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public T selectFields(String... selectFields){
        this.setSelectFields(Arrays.asList(selectFields));
        return (T)this;
    }
    public T orderby(String field, SortOrder sortOrder){
        this.getOrderMap().put(field,sortOrder);
        return (T)this;
    }



    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(List<String> selectFields) {
        this.selectFields = selectFields;
    }

    public LinkedHashMap<String, SortOrder> getOrderMap() {
        return orderMap;
    }

    public void setOrderMap(LinkedHashMap<String, SortOrder> orderMap) {
        this.orderMap = orderMap;
    }

    @Override
    public String toString() {
        return "BaseModel{" +
                "index='" + index + '\'' +
                ", type='" + type + '\'' +
                ", size=" + size +
                ", selectFields=" + selectFields +
                ", orderMap=" + orderMap +
                '}';
    }
}
