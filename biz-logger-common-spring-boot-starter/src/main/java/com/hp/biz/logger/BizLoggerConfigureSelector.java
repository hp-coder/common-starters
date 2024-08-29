package com.hp.biz.logger;

import com.hp.biz.logger.annotation.EnableBizLogger;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.context.annotation.AutoProxyRegistrar;

/**
 * @author hp
 */
public class BizLoggerConfigureSelector extends AdviceModeImportSelector<EnableBizLogger> {

    @Override
    protected String[] selectImports(AdviceMode adviceMode) {
        return switch (adviceMode) {
            case PROXY -> new String[]{AutoProxyRegistrar.class.getName(), BizLoggerAutoConfiguration.class.getName()};
            case ASPECTJ -> new String[]{BizLoggerAutoConfiguration.class.getName()};
        };
    }
}
