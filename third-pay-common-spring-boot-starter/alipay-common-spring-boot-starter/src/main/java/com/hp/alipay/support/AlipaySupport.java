package com.hp.alipay.support;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.SecureUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayConstants;
import com.alipay.api.internal.util.AlipaySignature;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author HP 2022/11/14
 */
public class AlipaySupport {

    /**
     * 普通公钥模式
     */
    private final static String NOTIFY_URL = "/alipay/notify_url";
    private final static String RETURN_URL = "/alipay/return_url";

    /**
     * 证书模式
     */
    private final static String CERT_NOTIFY_URL = "/alipay/cert_notify_url";
    private final static String CERT_RETURN_URL = "/alipay/cert_return_url";

    /**
     * sendRedirect*
     */
    public static String getOauth2Url(String appId, String redirectUri) throws UnsupportedEncodingException {
        return "https://openauth.alipay.com/oauth2/appToAppAuth.htm?app_id=" + appId + "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8");
    }

    /**
     * 处理支付宝回调的数据*
     */
    public static Map<String, String> toMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; ++i) {
                valueStr = i == values.length - 1 ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }

    public static void batchTrans(Map<String, String> params, String privateKey, String signType, HttpServletResponse response) throws IOException {
        Assert.notNull(params, "参数不能为空");
        params.put("service", "batch_trans_notify");
        params.put("_input_charset", "UTF-8");
        params.put("pay_date", DateUtil.format(new Date(), "YYYYMMDD"));
        Map<String, String> param = buildRequestPara(params, privateKey, signType);
        assert param != null;
        response.sendRedirect("https://mapi.alipay.com/gateway.do?".concat(createLinkString(param)));
    }


    /**
     * 生成要请求给支付宝的参数数组
     *
     * @param params   请求前的参数数组
     * @param key      商户的私钥
     * @param signType 签名类型
     * @return 要请求的参数数组
     */
    public static Map<String, String> buildRequestPara(Map<String, String> params, String key, String signType) {
        // 除去数组中的空值和签名参数
        Map<String, String> tempMap = paraFilter(params);
        // 生成签名结果
        String mySign;
        try {
            mySign = buildRequestMySign(params, key, signType);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return null;
        }
        // 签名结果与签名方式加入请求提交参数组中
        tempMap.put("sign", mySign);
        tempMap.put("sign_type", signType);
        return tempMap;
    }

    /**
     * 除去数组中的空值和签名参数
     *
     * @param params 签名参数组
     * @return 去掉空值与签名参数后的新签名参数组
     */
    public static Map<String, String> paraFilter(Map<String, String> params) {
        if (params == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>(params.size());
        if (params.size() <= 0) {
            return result;
        }
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null || "".equals(value) || "sign".equalsIgnoreCase(key)
                    || "sign_type".equalsIgnoreCase(key)) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }

    /**
     * 生成签名结果
     *
     * @param params   要签名的数组
     * @param key      签名密钥
     * @param signType 签名类型
     * @return 签名结果字符串
     */
    public static String buildRequestMySign(Map<String, String> params, String key, String signType) throws AlipayApiException {
        // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
        String preStr = createLinkString(params);
        if (SignType.MD5.getType().equals(signType)) {
            return SecureUtil.md5(preStr.concat(key));
        } else if (SignType.RSA2.getType().equals(signType)) {
            return AlipaySignature.rsa256Sign(preStr, key, AlipayConstants.CHARSET_UTF8);
        } else if (SignType.RSA.getType().equals(signType)) {
            return AlipaySignature.rsaSign(preStr, key, AlipayConstants.CHARSET_UTF8);
        }
        return null;
    }

    /**
     * 把数组所有元素排序
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    public static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        StringBuilder content = new StringBuilder();
        for (String key : keys) {
            String value = params.get(key);
            // 拼接时，不包括最后一个&字符
            content.append(key).append("=").append(value).append("&");
        }
        if (content.lastIndexOf("&") == content.length() - 1) {
            content.deleteCharAt(content.length() - 1);
        }
        return content.toString();
    }

}
