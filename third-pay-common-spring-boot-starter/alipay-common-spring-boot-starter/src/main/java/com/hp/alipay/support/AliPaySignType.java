package com.hp.alipay.support;

public enum AliPaySignType {
	/**
	 * MD5 加密
	 */
	MD5("MD5"),
	/**
	 * RSA2
	 */
	RSA2("RSA2"),
	/**
	 * RSA
	 */
	RSA("RSA");

	AliPaySignType(String type) {
		this.type = type;
	}

	private final String type;

	public String getType() {
		return type;
	}
}
