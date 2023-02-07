package com.hp.excel.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.hp.excel.annotation.ResponseExcel;
import com.hp.excel.annotation.Sheet;
import com.hp.excel.converter.LocalDateConverter;
import com.hp.excel.converter.LocalDateTimeConverter;
import com.hp.excel.enhence.ExcelWriterBuilderEnhance;
import com.hp.excel.head.HeadGenerator;
import com.hp.excel.head.HeadMetaData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaTypeFactory;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author HP
 * @date 2022/11/7
 */
@RequiredArgsConstructor
public abstract class AbstractExcelSheetWriteHandler implements ExcelSheetWriteHandler, ApplicationContextAware {
    private final ObjectProvider<List<Converter<?>>> converterProvider;
    private ApplicationContext applicationContext;

    private final List<ExcelWriterBuilderEnhance> enhanceHolder = new ArrayList<>();

    @Override
    public void check(ResponseExcel responseExcel) {
        if (responseExcel.sheets().length == 0) {
            throw new IllegalArgumentException("Sheet attribute of @ResponseExcel annotation has to be set");
        }
    }

    @Override
    public void export(Object o, HttpServletResponse response, ResponseExcel responseExcel) throws Exception {
        this.check(responseExcel);
        final RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String name = (String) ((RequestAttributes) Objects.requireNonNull(requestAttributes)).getAttribute("__EXCEL_NAME_KEY__", 0);
        if (StrUtil.isEmpty(name)) {
            name = responseExcel.name();
            if (StrUtil.isEmpty(name)) {
                name = UUID.randomUUID().toString();
            }
        }
        String fileName = String.format("%s%s", URLEncoder.encode(name, "UTF-8"), responseExcel.suffix().getValue());
        String contentType = (String) MediaTypeFactory.getMediaType(fileName).map(MimeType::toString).orElse("application/vnd.ms-excel");
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", -1);
        response.setCharacterEncoding("UTF-8");
        this.write(o, response, responseExcel);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ExcelWriter getExcelWriter(HttpServletResponse response, ResponseExcel responseExcel, Class<?> dataClass) throws IOException {
        ExcelWriterBuilder excelWriterBuilder = EasyExcel.write(response.getOutputStream())
                .registerConverter(LocalDateConverter.INSTANCE)
                .registerConverter(LocalDateTimeConverter.INSTANCE)
                .autoCloseStream(true)
                .excelType(responseExcel.suffix())
                .inMemory(responseExcel.inMemory());
        if (StrUtil.isNotEmpty(responseExcel.password())) {
            excelWriterBuilder.password(responseExcel.password());
        }
        if (responseExcel.include().length != 0) {
            excelWriterBuilder.includeColumnFieldNames(Arrays.asList(responseExcel.include()));
        }
        if (responseExcel.exclude().length != 0) {
            excelWriterBuilder.excludeColumnFieldNames(Arrays.asList(responseExcel.exclude()));
        }
        final Class<? extends WriteHandler>[] handlers = responseExcel.writeHandler();
        if (handlers.length != 0) {
            ExcelWriterBuilder finalExcelWriterBuilder = excelWriterBuilder;
            Arrays.stream(handlers).forEach(handler -> finalExcelWriterBuilder.registerWriteHandler(BeanUtils.instantiateClass(handler)));
        }
        this.registerCustomConverter(excelWriterBuilder);
        this.registerCommonConverter(excelWriterBuilder, responseExcel);

        String templatePath = responseExcel.template();
        if (StringUtils.hasText(templatePath)) {
            ClassPathResource classPathResource = new ClassPathResource(templatePath + File.separator + templatePath);
            InputStream inputStream = classPathResource.getInputStream();
            excelWriterBuilder.withTemplate(inputStream);
        }
        final Class<? extends ExcelWriterBuilderEnhance>[] enhancements = responseExcel.enhancement();
        if (ArrayUtil.isNotEmpty(enhancements)) {
            for (Class<? extends ExcelWriterBuilderEnhance> enhancement : enhancements) {
                final ExcelWriterBuilderEnhance enhance = BeanUtils.instantiateClass(enhancement);
                enhanceHolder.add(enhance);
                excelWriterBuilder = enhance.enhanceExcel(excelWriterBuilder, response, responseExcel, dataClass, templatePath);
            }
        }
        return excelWriterBuilder.build();
    }

    public void registerCommonConverter(ExcelWriterBuilder excelWriterBuilder, ResponseExcel responseExcel) {
        final Class<? extends Converter>[] converters = responseExcel.converter();
        if (converters.length != 0) {
            Arrays.stream(converters).forEach(converter -> excelWriterBuilder.registerConverter(BeanUtils.instantiateClass(converter)));
        }
    }

    public void registerCustomConverter(ExcelWriterBuilder excelWriterBuilder) {
        this.converterProvider.ifAvailable(converters -> {
            Objects.requireNonNull(excelWriterBuilder);
            converters.forEach(excelWriterBuilder::registerConverter);
        });
    }

    public WriteSheet sheet(Sheet sheet, Class<?> dataClass, String template, Class<? extends HeadGenerator> headGenerator) {
        final Integer sheetNo = sheet.sheetNo() >= 0 ? sheet.sheetNo() : null;
        final String sheetName = sheet.sheetName();
        ExcelWriterSheetBuilder sheetBuilder = StrUtil.isEmpty(sheetName) ? EasyExcel.writerSheet(sheetNo) : EasyExcel.writerSheet(sheetNo, sheetName);
        Class<? extends HeadGenerator> headGenerateClass = null;
        if (this.isNotInterface(sheet.headGenerateClass())) {
            headGenerateClass = sheet.headGenerateClass();
        } else if (this.isNotInterface(headGenerator)) {
            headGenerateClass = headGenerator;
        }

        if (headGenerateClass != null) {
            this.fillCustomHead(dataClass, headGenerateClass, sheetBuilder);
        } else if (dataClass != null) {
            sheetBuilder.head(dataClass);
            if (sheet.excludes().length > 0) {
                sheetBuilder.excludeColumnFiledNames(Arrays.asList(sheet.excludes()));
            }
            if (sheet.includes().length > 0) {
                sheetBuilder.includeColumnFiledNames(Arrays.asList(sheet.includes()));
            }
        }
        if (CollUtil.isNotEmpty(enhanceHolder)) {
            for (ExcelWriterBuilderEnhance enhance : enhanceHolder) {
                sheetBuilder = enhance.enhanceSheet(sheetBuilder, sheetNo, sheetName, dataClass, template, headGenerateClass);
            }
        }
        return sheetBuilder.build();
    }

    protected void fillCustomHead(Class<?> dataClass, Class<? extends HeadGenerator> headGenerateClass, ExcelWriterSheetBuilder sheetBuilder) {
        final HeadGenerator headGenerator = this.applicationContext.getBean(headGenerateClass);
        Assert.notNull(headGenerator, "The header generator could not be found in SpringContext");
        final HeadMetaData head = headGenerator.head(dataClass);
        sheetBuilder.head(head.getHead());
        sheetBuilder.excludeColumnFieldNames(head.getIgnoreHeadFields());
    }

    protected boolean isNotInterface(Class<? extends HeadGenerator> headGeneratorClass) {
        return !Modifier.isInterface(headGeneratorClass.getModifiers());

    }
}
