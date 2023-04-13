package com.luban.elasticsearch.config;


import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author HP
 * @date 2022/9/1
 */
@ConfigurationProperties(prefix = "es")
public class EsProperties {

	private boolean enabled = false;

	private String host = "127.0.0.1";

	private int port = 9200;

	private String username;

	private String password;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
