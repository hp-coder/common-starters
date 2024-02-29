package com.hp.excel.aop;

import com.hp.excel.annotation.ResponseExcel;
import com.hp.excel.handler.ExcelSheetWriteHandler;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

/**
 * @author hp
 * @date 2022/11/7
 */
@RequiredArgsConstructor
public class ResponseExcelReturnValueHandler implements HandlerMethodReturnValueHandler {

    private final List<ExcelSheetWriteHandler> excelSheetWriteHandlers;

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(ResponseExcel.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        final HttpServletResponse nativeResponse = webRequest.getNativeResponse(HttpServletResponse.class);
        Assert.notNull(nativeResponse, "No httpServletResponse was found");
        final ResponseExcel responseExcel = returnType.getMethodAnnotation(ResponseExcel.class);
        Assert.notNull(responseExcel, "No @ResponseExcel annotation was found");
        mavContainer.setRequestHandled(true);
        this.excelSheetWriteHandlers.stream()
                .filter(handler -> handler.support(returnValue))
                .findFirst()
                .ifPresent(handler ->
                {
                    try {
                        handler.export(returnValue, nativeResponse, responseExcel);
                    } catch (Exception e) {
                       throw new RuntimeException(e);
                    }
                });
    }
}
