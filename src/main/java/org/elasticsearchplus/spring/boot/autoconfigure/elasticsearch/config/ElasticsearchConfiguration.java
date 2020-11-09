package org.elasticsearchplus.spring.boot.autoconfigure.elasticsearch.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.ElasticsearchNodesSniffer;
import org.elasticsearch.client.sniff.NodesSniffer;
import org.elasticsearch.client.sniff.SniffOnFailureListener;
import org.elasticsearch.client.sniff.Sniffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: lijz
 * @Description elasticsearch config
 * @Date: 2020/7/23
 */
@Configuration
@EnableConfigurationProperties(ElasticsearchProperties.class)
public class ElasticsearchConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ElasticsearchConfiguration.class);

    private final ElasticsearchProperties elasticsearchProperties;

    public ElasticsearchConfiguration(ElasticsearchProperties elasticsearchProperties) {
        this.elasticsearchProperties = elasticsearchProperties;
    }

    @Bean
    @Lazy
    public RestClient restLowClient() {
        HttpHost[] httpHostsArr = parseHttpHosts();
        RestClient restClient = RestClient.builder(httpHostsArr).build();
        return restClient;
    }

    @Bean
    @Lazy
    public RestHighLevelClient restHighLevelClient(){
        HttpHost[] httpHostsArr = parseHttpHosts();
//        普通RestHighLevelClient，不支持sniffer
//        RestHighLevelClient client = new RestHighLevelClient(
//                RestClient.builder(httpHostsArr));

        /*********************支持sniffer节点嗅探********************/
        // create RestClientBuilder with httpHosts
        RestClientBuilder restClientBuilder = RestClient.builder(httpHostsArr);

        // set a SniffOnFailureListener to restClientBuilder
        SniffOnFailureListener sniffOnFailureListener = new SniffOnFailureListener();
        restClientBuilder.setFailureListener(sniffOnFailureListener);

        // create RestHighLevelClient
        RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);

        // configure the node sniffer(optional)
        NodesSniffer nodesSniffer = new ElasticsearchNodesSniffer(
                client.getLowLevelClient(),
                // 自定义sniff节点超时时间，超过的就不等了，默认1秒，这里设为5秒
                TimeUnit.SECONDS.toSeconds(5),
                // 协议，默认HTTP
                ElasticsearchNodesSniffer.Scheme.HTTP);

        // build the sniffer
        // RestHighLevelClient 对LowLevelClient进行了封装，底层调用的还是LowLevelClient，LowLevelClient支持sniffer
        Sniffer sniffer = Sniffer
                .builder(client.getLowLevelClient())
                .setNodesSniffer(nodesSniffer)
                // 默认每5分钟获取一次集群中的data node，这里改为1分钟
                .setSniffIntervalMillis(60000)
                // sniff failure后额外触发sniff行为的延迟时间，默认1分钟，这里改为30秒
                .setSniffAfterFailureDelayMillis(30000)
                .build();

        // connect the sniffer and the listener
        sniffOnFailureListener.setSniffer(sniffer);
        return client;
    }

    private HttpHost[] parseHttpHosts(){
        if(elasticsearchProperties == null || CollectionUtils.isEmpty(elasticsearchProperties.getHostnames())){
            throw new NullPointerException("elasticsearch config is null");
        }
        List<HttpHost> httpHosts = elasticsearchProperties.getHostnames().stream().map(hostname-> new HttpHost(hostname, 9200, "http")).collect(Collectors.toList());
        HttpHost[] httpHostsArr = httpHosts.toArray(new HttpHost[httpHosts.size()]);
        System.out.println(httpHosts);
        return httpHostsArr;
    }

}
