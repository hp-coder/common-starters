package com.hp.excel.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.google.common.collect.Lists;
import com.hp.excel.listener.ExcelAnalysisEventListener;
import com.hp.excel.annotation.RequestExcel;
import com.hp.excel.converter.LocalDateConverter;
import com.hp.excel.converter.LocalDateTimeConverter;
import com.hp.excel.enhance.ExcelReaderBuilderEnhance;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.NonNull;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author hp
 * @date 2022/11/7
 */
public class RequestExcelArgumentResolver implements HandlerMethodArgumentResolver {

    private final List<ExcelReaderBuilderEnhance> enhanceHolder = Lists.newArrayList();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestExcel.class);
    }

    @Override
    public Object resolveArgument(
            @NonNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        final Class<?> parameterType = parameter.getParameterType();
        if (!parameterType.isAssignableFrom(List.class) && !parameterType.isAssignableFrom(Map.class)) {
            throw new IllegalArgumentException("@RequestExcel annotation only works with List<T> or Map<K,List<T>>");
        }
        Class<?> excelModelClass;
        if (parameterType.isAssignableFrom(List.class)) {
            excelModelClass = ResolvableType.forMethodParameter(parameter).getGeneric(0).resolve();
        } else if (parameterType.isAssignableFrom(Map.class)) {
            excelModelClass = ResolvableType.forMethodParameter(parameter).getGeneric(1).resolveGeneric(0);
        } else {
            throw new IllegalArgumentException("@RequestExcel annotation only works with List<T> or Map<K,List<T>>");
        }
        final RequestExcel requestExcel = parameter.getParameterAnnotation(RequestExcel.class);
        assert requestExcel != null;
        final HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        InputStream inputStream;
        if (request instanceof MultipartRequest multipartRequest) {
            final MultipartFile file = multipartRequest.getFile(requestExcel.filename());
            inputStream = Optional.ofNullable(file).orElseThrow(() -> new IllegalArgumentException("Excel file=%s not found in the request.".formatted(requestExcel.filename()))).getInputStream();
        } else {
            assert request != null;
            inputStream = request.getInputStream();
        }
        final Class<? extends ExcelAnalysisEventListener<?, ?>> listenerClass = requestExcel.listener();
        final ExcelAnalysisEventListener<?, ?> listener = BeanUtils.instantiateClass(listenerClass);

        ExcelReaderBuilder excelReaderBuilder = initReaderBuilder(requestExcel, inputStream, excelModelClass, listener);
        final Class<? extends ExcelReaderBuilderEnhance>[] enhancements = requestExcel.enhancement();
        if (ArrayUtil.isNotEmpty(enhancements)) {
            for (Class<? extends ExcelReaderBuilderEnhance> enhancement : enhancements) {
                final ExcelReaderBuilderEnhance enhance = BeanUtils.instantiateClass(enhancement);
                enhanceHolder.add(enhance);
                excelReaderBuilder = enhance.enhanceExcel(excelReaderBuilder, request, requestExcel, excelModelClass);
            }
        }
        ExcelReaderSheetBuilder excelReaderSheetBuilder = initSheetReaderBuilder(excelReaderBuilder);
        if (CollUtil.isNotEmpty(enhanceHolder)) {
            for (ExcelReaderBuilderEnhance enhance : enhanceHolder) {
                excelReaderSheetBuilder = enhance.enhanceSheet(excelReaderSheetBuilder, request, requestExcel, excelModelClass);
            }
        }
        excelReaderSheetBuilder.doRead();

        final WebDataBinder binder = binderFactory.createBinder(webRequest, listener.getErrors(), "excel");
        ModelMap model = mavContainer.getModel();
        model.put(BindingResult.MODEL_KEY_PREFIX + "excel", binder.getBindingResult());

        return listener.getData();
    }

    private static ExcelReaderBuilder initReaderBuilder(
            RequestExcel requestExcel,
            InputStream inputStream,
            Class<?> excelModelClass,
            ExcelAnalysisEventListener<?, ?> listener
    ) {
        return EasyExcel.read(inputStream, excelModelClass, listener)
                .registerConverter(LocalDateTimeConverter.INSTANCE)
                .registerConverter(LocalDateConverter.INSTANCE)
                .ignoreEmptyRow(requestExcel.ignoreEmptyRow());
    }

    private static ExcelReaderSheetBuilder initSheetReaderBuilder(ExcelReaderBuilder readerBuilder) {
        return readerBuilder.sheet();
    }

}
