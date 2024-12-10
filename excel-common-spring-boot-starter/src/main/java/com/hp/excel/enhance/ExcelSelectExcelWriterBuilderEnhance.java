package com.hp.excel.enhance;

import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.hp.excel.annotation.ResponseExcel;
import com.hp.excel.constant.ExcelConstants;
import com.hp.excel.context.ExcelContext;
import com.hp.excel.enhance.handler.SelectDataSheetWriteHandler;
import com.hp.excel.enhance.handler.SelectDataWorkbookWriteHandler;
import com.hp.excel.head.HeadGenerator;
import com.hp.excel.model.AbstractExcelSelectModel;
import com.hp.excel.util.ExcelSelectHelper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hp
 */
@Slf4j
public class ExcelSelectExcelWriterBuilderEnhance implements ExcelWriterBuilderEnhance {

    protected final AtomicInteger selectionColumnIndex = new AtomicInteger(0);
    protected Map<Class<?>, Map<Integer, ? extends AbstractExcelSelectModel<?>>> selectionMapMapping = Maps.newHashMap();


    @SuppressWarnings("unchecked")
    @Override
    public ExcelWriterBuilder enhanceExcel(
            ExcelWriterBuilder writerBuilder,
            ResponseExcel responseExcel,
            Collection<? extends Class<?>> dataClasses,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            Optional.ofNullable(request.getAttribute(ExcelConstants.DROPDOWN_QUERY_PARAMS_ATTRIBUTE_KEY)).map(p->{
                Preconditions.checkArgument(p instanceof Map<?, ?>);
                return (Map<String, Object>) p;
            }).ifPresent(ExcelContext::setParameters);
            dataClasses.forEach(dataClass -> selectionMapMapping.put(dataClass, ExcelSelectHelper.createSelectionMapping(dataClass, ExcelContext.getParameters().orElse(null))));
        } finally {
            ExcelContext.clearParameters();
        }
        return writerBuilder.registerWriteHandler(new SelectDataWorkbookWriteHandler());
    }

    @Override
    public ExcelWriterSheetBuilder enhanceSheet(
            ExcelWriterSheetBuilder writerSheetBuilder,
            Integer sheetNo,
            String sheetName,
            Class<?> dataClass,
            Class<? extends HeadGenerator> headEnhancerClass,
            String templatePath
    ) {
        if (selectionMapMapping.containsKey(dataClass)) {
            final Map<Integer, ? extends AbstractExcelSelectModel<?>> selectionMapping = selectionMapMapping.get(dataClass);
            writerSheetBuilder.registerWriteHandler(new SelectDataSheetWriteHandler(selectionColumnIndex, selectionMapping));
        }
        return writerSheetBuilder;
    }

}
