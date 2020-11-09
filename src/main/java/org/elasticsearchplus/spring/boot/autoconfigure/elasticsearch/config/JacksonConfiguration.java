package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lijz
 * @date 2020年8月6日
 */
@Configuration
public class JacksonConfiguration {

	private FilterProvider filterProvider(){
		SimpleFilterProvider filters = new SimpleFilterProvider();
		filters.addFilter("baseFilter",
				SimpleBeanPropertyFilter.serializeAllExcept("createUserId","createUserUsername","createTime",
						"updateUserId","updateUserUsername","updateTime","deleteFlag","sqlMap","page","languageCode",
						"company"));

//		filters.addFilter("dataSyncEsFilter", new DataSyncEsFilter());
		//可以继续添加过滤规则
		return filters;
	}

	@Bean
	public ObjectMapper javaTimeModule() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);

		objectMapper.setFilterProvider(filterProvider());
		return objectMapper.registerModule(new JavaTimeModule());
	}

}
