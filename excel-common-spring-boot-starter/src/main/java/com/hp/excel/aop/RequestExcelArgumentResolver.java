package com.hp.excel.aop;

import com.alibaba.excel.EasyExcel;
import com.hp.excel.annotation.RequestExcel;
import com.hp.excel.converter.LocalDateConverter;
import com.hp.excel.converter.LocalDateTimeConverter;
import com.hp.excel.listener.ExcelAnalysisEventListener;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.List;

/**
 * @author HP
 * @date 2022/11/7
 */
public class RequestExcelArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestExcel.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        try {
            final Class<?> parameterType = parameter.getParameterType();
            if (!parameterType.isAssignableFrom(List.class)) {
                throw new IllegalArgumentException(" @RequestExcel annotation only works with List type");
            }
            final RequestExcel requestExcel = parameter.getParameterAnnotation(RequestExcel.class);
            assert requestExcel != null;
            final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            InputStream inputStream;
            if (request instanceof MultipartRequest) {
                final MultipartFile file = ((MultipartRequest) request).getFile(requestExcel.filename());
                assert file != null;
                inputStream = file.getInputStream();
            } else {
                assert request != null;
                inputStream = request.getInputStream();
            }
            final Class<?> excelModelClass = ResolvableType.forMethodParameter(parameter).getGeneric(new int[]{0}).resolve();
            final Class<? extends ExcelAnalysisEventListener<?>> listenerClass = requestExcel.analysisEventListener();
            final ExcelAnalysisEventListener<?> listener = BeanUtils.instantiateClass(listenerClass);
            EasyExcel.read(inputStream, excelModelClass, listener)
                    .registerConverter(LocalDateTimeConverter.INSTANCE)
                    .registerConverter(LocalDateConverter.INSTANCE)
                    .ignoreEmptyRow(requestExcel.ignoreEmptyRow())
                    .sheet()
                    .doRead();
            final WebDataBinder binder = binderFactory.createBinder(webRequest, listener.getErrors(), "excel");
            ModelMap model = mavContainer.getModel();
            model.put(BindingResult.MODEL_KEY_PREFIX + "excel", binder.getBindingResult());
            return listener.getList();
        } catch (Exception e) {
            throw e;
        }
    }
}
