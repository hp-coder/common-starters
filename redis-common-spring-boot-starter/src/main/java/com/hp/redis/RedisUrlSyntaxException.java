package com.hp.redis;

import java.io.Serial;

public class RedisUrlSyntaxException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 5480413039453713749L;

	private final String url;

	public RedisUrlSyntaxException(String url, Exception cause) {
		super(buildMessage(url), cause);
		this.url = url;
	}

	public RedisUrlSyntaxException(String url) {
		super(buildMessage(url));
		this.url = url;
	}

	String getUrl() {
		return this.url;
	}

	private static String buildMessage(String url) {
		return "Invalid Redis URL '" + url + "'";
	}

}
