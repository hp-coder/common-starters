package com.hp.pay.support.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SignType {

	HMAC_SHA256("HMAC-SHA256"),
	MD5("MD5"),
	RSA("RSA"),
	;
	private final String type;
}
