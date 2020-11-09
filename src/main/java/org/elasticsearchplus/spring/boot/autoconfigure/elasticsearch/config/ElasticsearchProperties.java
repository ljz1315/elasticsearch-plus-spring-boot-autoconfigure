package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: lijz
 * @Description ElasticsearchProperties
 * @Date: 2020/7/23
 */
@ConfigurationProperties(prefix = "elasticsearch")
public class ElasticsearchProperties {
    private List<String> hostnames = new ArrayList<>();

    public List<String> getHostnames() {
        return hostnames;
    }

    public void setHostnames(List<String> hostnames) {
        this.hostnames = hostnames;
    }
}
