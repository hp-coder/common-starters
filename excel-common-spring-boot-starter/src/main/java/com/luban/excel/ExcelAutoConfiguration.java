package com.luban.excel;

import com.luban.excel.aop.RequestExcelArgumentResolver;
import com.luban.excel.aop.ResponseExcelReturnValueHandler;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hp
 * @date 2022/11/7
 */
@RequiredArgsConstructor
@Configuration
@Import({ExcelHandlerConfiguration.class})
public class ExcelAutoConfiguration {

    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    private final ResponseExcelReturnValueHandler responseExcelReturnValueHandler;

    @PostConstruct
    public void setReturnValueHandlers() {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = this.requestMappingHandlerAdapter.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> newHandlers = new ArrayList<>();
        newHandlers.add(this.responseExcelReturnValueHandler);
        assert returnValueHandlers != null;
        newHandlers.addAll(returnValueHandlers);
        this.requestMappingHandlerAdapter.setReturnValueHandlers(newHandlers);
    }

    @PostConstruct
    public void setRequestExcelArgumentResolver() {
        List<HandlerMethodArgumentResolver> argumentResolvers = this.requestMappingHandlerAdapter.getArgumentResolvers();
        List<HandlerMethodArgumentResolver> resolverList = new ArrayList<>();
        resolverList.add(new RequestExcelArgumentResolver());
        resolverList.addAll(argumentResolvers);
        this.requestMappingHandlerAdapter.setArgumentResolvers(resolverList);
    }
}
