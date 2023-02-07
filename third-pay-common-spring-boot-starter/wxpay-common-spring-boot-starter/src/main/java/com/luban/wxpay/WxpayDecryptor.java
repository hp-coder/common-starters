package com.luban.wxpay;

/**
 * @author HP 2022/11/23
 */
public interface WxpayDecryptor {

    String decrypt(String encryptContent, String encryptType, String charset);
}
