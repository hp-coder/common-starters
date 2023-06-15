package com.luban.alipay.controller;

import cn.hutool.json.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.luban.alipay.config.AlipayClientConfig;
import com.luban.alipay.processor.AlipayPostProcessor;
import com.luban.alipay.support.AlipaySupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * @author HP 2022/11/14
 */
@Slf4j
@ConditionalOnProperty(prefix = "alipay", name = "default-controller-enabled", havingValue = "true")
@RequiredArgsConstructor
@RequestMapping("/alipay")
@RestController
public class AlipayController {

    private final AlipayClientConfig alipayClientConfig;

    @RequestMapping(value = "/notify_url")
    @ResponseBody
    public String notifyUrl(HttpServletRequest request) {
        try {
            // 获取支付宝POST过来反馈信息
            Map<String, String> params = AlipaySupport.toMap(request);
            log.info("Alipay post payment notify: params: {}", new JSONObject(params));
            boolean verifyResult = AlipaySignature.rsaCheckV1(params, alipayClientConfig.getAlipayPublicKey(), alipayClientConfig.getCharset(), alipayClientConfig.getSignType());
            log.info("Alipay post payment notify: verifyResult: {}", verifyResult);
            if (!verifyResult) {
                throw new AlipayApiException("Alipay post payment notify: ERRORS OCCURRED, please check logs for more info");
            }
            //do something
            final Optional<AlipayPostProcessor> processor = AlipayPostProcessor.PROCESSORS
                    .stream()
                    .filter(_1 -> _1.predication().test(params))
                    .findFirst();
            if (processor.isPresent()) {
            }
            return "success";
        } catch (Exception e) {
            log.error("lipay post payment notify: ERRORS OCCURRED, please check logs for more info");
            log.error(Arrays.toString(e.getStackTrace()));
            return "failure";
        }
    }


    @RequestMapping(value = "/return_url")
    @ResponseBody
    public String returnUrl(HttpServletRequest request) {
        try {
            Map<String, String> params = AlipaySupport.toMap(request);
            boolean verifyResult = AlipaySignature.rsaCheckV1(params, alipayClientConfig.getAlipayPublicKey(), alipayClientConfig.getCharset(), alipayClientConfig.getSignType());
            if (!verifyResult) {
                throw new AlipayApiException(" Alipay post payment notify: ERRORS OCCURRED, please check logs for more info");
            }
            //do something
            return "success";
        } catch (AlipayApiException e) {
            log.error("lipay post payment notify: ERRORS OCCURRED, please check logs for more info");
            log.error(Arrays.toString(e.getStackTrace()));
            return "failure";
        }
    }

}
