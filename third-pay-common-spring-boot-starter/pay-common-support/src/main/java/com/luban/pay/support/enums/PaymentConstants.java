package com.luban.pay.support.enums;


public interface PaymentConstants {

	/**
	 * 证书序列号固定40字节的字符串
	 */
	int SERIAL_NUMBER_LENGTH = 40;

	/**
	 * 证书颁发者
	 */
	String ISSUER = "CN=Tenpay.com Root CA";

	/**
	 * 证书CN字段
	 */
	String CN = "CN=";

	/**
	 * 处理成功
	 */
	int CODE_200 = 200;

	/**
	 * 服务器已接受请求，但尚未处理
	 */
	int CODE_202 = 202;

	/**
	 * 处理成功，无返回Body
	 */
	int CODE_204 = 204;

	/**
	 * 协议或者参数非法
	 */
	int CODE_400 = 400;

	/**
	 * 签名验证失败
	 */
	int CODE_401 = 401;

	/**
	 * 权限异常
	 */
	int CODE_403 = 403;

	/**
	 * 请求的资源不存在
	 */
	int CODE_404 = 404;

	/**
	 * 请求超过频率限制
	 */
	int CODE_429 = 429;

	/**
	 * 系统错误
	 */
	int CODE_500 = 500;

	/**
	 * 服务下线，暂时不可用
	 */
	int CODE_502 = 502;

	/**
	 * 服务不可用，过载保护
	 */
	int CODE_503 = 503;

}
