package com.hp.wxpay;

/**
 * @author hp
 */
public interface WxpayDecryptor {

    String decrypt(String encryptContent, String encryptType, String charset);
}
