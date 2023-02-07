package com.luban.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.luban.elasticsearch.config.EsProperties;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author HP
 * @date 2022/9/1
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(EsProperties.class)
public class ElasticSearchAutoConfiguration {

	@Bean
	@ConditionalOnProperty(prefix = "es", name = "enabled", havingValue = "true")
	public ElasticsearchClient elasticsearchClient(EsProperties esProperties) {
		final HttpHost httpHost = new HttpHost(esProperties.getHost(), esProperties.getPort());
		final RestClientBuilder builder = RestClient.builder(httpHost);
		if (StringUtils.hasText(esProperties.getUsername()) && StringUtils.hasText(esProperties.getPassword())) {
			final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY,
					new UsernamePasswordCredentials(esProperties.getUsername(), esProperties.getPassword()));
			builder.setHttpClientConfigCallback(b -> b.setDefaultCredentialsProvider(credentialsProvider));
			// b->b.disableAuthCaching //每次都不带认证请求， 如果401在重新待认证请求一次
		}
		final RestClient restClient = builder.setCompressionEnabled(true).build();
		final RestClientTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
		return new ElasticsearchClient(transport);
	}

}
