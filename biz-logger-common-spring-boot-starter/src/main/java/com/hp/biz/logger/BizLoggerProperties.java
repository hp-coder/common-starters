package com.hp.biz.logger;

import com.hp.common.base.annotation.FieldDesc;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hp
 */
@Data
@ConfigurationProperties(prefix = "biz.logger")
public class BizLoggerProperties {

    @FieldDesc("@BizLogger中可以使用的方法返回值对象, #_return")
    private String returnValueKey = "_return";

    @FieldDesc("@BizLogger中可以使用的方法抛出的异常对象, #_throwable")
    private String throwableKey = "_throwable";

    private DiffProperties diffConfig = new DiffProperties();

    @Data
    public static class DiffProperties {
        private Words words = new Words();
    }

    @Data
    public static class Words {
        @FieldDesc("存在父子层级时的连接词, 如: updater ‘的’ id")
        private String of = "的";
    }
}
