package com.hp.elasticsearch.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author HP
 * @date 2022/9/1
 */
@ConfigurationProperties(prefix = "es")
@Setter
@Getter
public class EsProperties {

	private boolean enabled = false;

	private String host = "127.0.0.1";

	private int port = 9200;

	private String username;

	private String password;

}
