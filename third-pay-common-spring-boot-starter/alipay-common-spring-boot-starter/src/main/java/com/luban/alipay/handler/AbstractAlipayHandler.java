package com.luban.alipay.handler;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayObject;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.luban.alipay.context.AlipayContext;
import com.luban.alipay.request.AlipayRequestBuilder;
import com.luban.alipay.request.AlipayRequestUpdater;
import com.luban.alipay.response.AlipayResponseBuilder;
import com.hp.pay.support.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.util.function.Supplier;

/**
 * @author HP 2022/11/11
 */
@Slf4j
public abstract class AbstractAlipayHandler<T extends AlipayObject, E extends AlipayRequest<R>, R extends AlipayResponse>
        implements
        AlipayRequestBuilder<T, E, R>,
        AlipayRequestUpdater<T, E, R>,
        AlipayHandler<E, R>,
        AlipayResponseBuilder<R> {

    public final AlipayContext<T, E, R> alipayContext;

    public AbstractAlipayHandler(Supplier<AlipayContext<T, E, R>> supplier) {
        this.alipayContext = supplier.get();
        this.initContext();
    }

    protected void initContext() {
        final ParameterizedType parameterizedType = TypeUtil.toParameterizedType(this.getClass().getGenericSuperclass());
        this.alipayContext.request = ReflectUtil.newInstance(parameterizedType.getActualTypeArguments()[1].getTypeName());
    }

    @Override
    public R doExecute() {
        try {
            R response;
            if (this.alipayContext.certificatedRequest) {
                response = this.alipayContext.alipayClient.certificateExecute(this.alipayContext.request, this.alipayContext.accessToken, this.alipayContext.appAuthToken, this.alipayContext.targetAppId);
            } else {
                response = this.alipayContext.alipayClient.execute(this.alipayContext.request, this.alipayContext.accessToken, this.alipayContext.appAuthToken, this.alipayContext.targetAppId);
            }
            log.info("Alipay Response: {}", response.toString());
            if (response.isSuccess()) {
                return response;
            }
            throw new AlipayApiException(response.getSubCode(), response.getSubMsg());
        } catch (AlipayApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AlipayRequestUpdater<T, E, R> request(T model) {
        this.alipayContext.request.setBizModel(model);
        this.alipayContext.model = model;
        return this;
    }

    @Override
    public AlipayRequestUpdater<T, E, R> notifyUrl(String notifyUrl) {
        this.alipayContext.request.setNotifyUrl(notifyUrl);
        return this;
    }

    @Override
    public AlipayHandler<E, R> handler() {
        return this;
    }

    @Override
    public AlipayRequestUpdater<T, E, R> returnUrl(String returnUrl) {
        this.alipayContext.request.setReturnUrl(returnUrl);
        return this;
    }

    @Override
    public AlipayRequestUpdater<T, E, R> certificatedRequest(boolean isCertificatedRequest) {
        this.alipayContext.certificatedRequest = isCertificatedRequest;
        return this;
    }

    @Override
    public AlipayRequestUpdater<T, E, R> accessToken(String accessToken) {
        this.alipayContext.accessToken = accessToken;
        return this;
    }

    @Override
    public AlipayRequestUpdater<T, E, R> appAuthToken(String appAuthToken) {
        this.alipayContext.appAuthToken = appAuthToken;
        return this;
    }

    @Override
    public AlipayRequestUpdater<T, E, R> targetAppId(String targetAppId) {
        this.alipayContext.targetAppId = targetAppId;
        return this;
    }

    @Override
    public void writeBodyAsStream(R r) {
        String charset = "UTF-8";
        HttpServletResponse httpServletResponse = this.alipayContext.httpServletResponse;
        if (httpServletResponse == null) {
            httpServletResponse = WebUtils.getResponse();
        }
        Assert.notNull(httpServletResponse, "HttpServletResponse实例不能为空");
        httpServletResponse.setContentType("text/html;charset=" + charset);
        try (PrintWriter out = httpServletResponse.getWriter()) {
            out.write(r.getBody());
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
