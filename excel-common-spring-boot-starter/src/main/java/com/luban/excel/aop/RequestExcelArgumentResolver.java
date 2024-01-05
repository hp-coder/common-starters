package com.luban.excel.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.luban.excel.annotation.RequestExcel;
import com.luban.excel.converter.LocalDateConverter;
import com.luban.excel.converter.LocalDateTimeConverter;
import com.luban.excel.enhence.ExcelReaderBuilderEnhance;
import com.luban.excel.listener.ExcelAnalysisEventListener;
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

import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hp
 * @date 2022/11/7
 */
public class RequestExcelArgumentResolver implements HandlerMethodArgumentResolver {

    private final List<ExcelReaderBuilderEnhance> enhanceHolder = new ArrayList<>();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestExcel.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        try {
            final Class<?> parameterType = parameter.getParameterType();
            if (!parameterType.isAssignableFrom(List.class) && !parameterType.isAssignableFrom(Map.class)) {
                throw new IllegalArgumentException(" @RequestExcel annotation only works with List<T> or Map<K,List<T>> ");
            }
            Class<?> excelModelClass;
            if (parameterType.isAssignableFrom(List.class)) {
                excelModelClass = ResolvableType.forMethodParameter(parameter).getGeneric(0).resolve();
            } else if (parameterType.isAssignableFrom(Map.class)) {
                excelModelClass = ResolvableType.forMethodParameter(parameter).getGeneric(1).resolveGeneric(0);
            } else {
                throw new IllegalArgumentException(" @RequestExcel annotation only works with List<T> or Map<K,List<T>> ");
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
        } catch (Exception e) {
            throw e;
        }
    }

    private static ExcelReaderBuilder initReaderBuilder(RequestExcel requestExcel, InputStream inputStream, Class<?> excelModelClass, ExcelAnalysisEventListener<?, ?> listener) {
        return EasyExcel.read(inputStream, excelModelClass, listener)
                .registerConverter(LocalDateTimeConverter.INSTANCE)
                .registerConverter(LocalDateConverter.INSTANCE)
                .ignoreEmptyRow(requestExcel.ignoreEmptyRow());
    }

    private static ExcelReaderSheetBuilder initSheetReaderBuilder(ExcelReaderBuilder readerBuilder) {
        return readerBuilder.sheet();
    }

}
