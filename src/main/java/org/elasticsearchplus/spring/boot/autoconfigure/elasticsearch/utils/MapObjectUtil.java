package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * @Author: lijz
 * @Description
 * @Date: 2019/9/25
 */
public class MapObjectUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapObjectUtil.class);

    public static <T> T mapToObject(Map<String, Object> map, Class<T> beanClass) {
        if (map == null) {
            return null;
        }

        T obj = null;
        try {
            obj = beanClass.newInstance();
            BeanUtils.populate(obj, map);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("bean newInstance or populate failed.", e);
        }
        return obj;
    }

    public static <T> T mapToObjectOfString(Map<String, String> map, Class<T> beanClass) {
        if (map == null) {
            return null;
        }

        T obj = null;
        try {
            obj = beanClass.newInstance();
            BeanUtils.populate(obj, map);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            LOGGER.error("bean newInstance or populate failed.", e);
        }
        return obj;
    }

    public static Map<String, Object> objectToMap(Object obj, List<String> ignorePropertyNames) {
        Map<String, Object> beanPropertyMap = Maps.newHashMap();
        if (obj == null) {
            return beanPropertyMap;
        }
        List<String> ignorePropertyList = Lists.newArrayList();
        if (ignorePropertyNames != null) {
            ignorePropertyList.addAll(ignorePropertyNames);
        }
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(obj.getClass());
        } catch (IntrospectionException e) {
            LOGGER.error("Introspector.getBeanInfo failed.error:{}", e);
        }
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            String key = descriptor.getName();
            if ("class" .equalsIgnoreCase(key) || ignorePropertyList.contains(key)) {
                continue;
            }
            Method readMethod = descriptor.getReadMethod();
            Object value = null;
            try {
                value = (readMethod == null) ? null : readMethod.invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                LOGGER.error("Method.invoke failed.error:{}", e);
            }
            beanPropertyMap.put(key, value);
        }
        return beanPropertyMap;
    }
}
