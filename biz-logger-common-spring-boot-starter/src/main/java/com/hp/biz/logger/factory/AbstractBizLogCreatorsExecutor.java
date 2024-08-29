package com.hp.biz.logger.factory;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.expression.BeanResolver;

import java.util.List;

/**
 * @author hp
 */
@Getter(AccessLevel.PROTECTED)
public abstract class AbstractBizLogCreatorsExecutor implements IBizLogCreatorsExecutor {

    private final BeanResolver beanResolver;

    private final List<IBizLogCreator> creators;

    public AbstractBizLogCreatorsExecutor(BeanResolver beanResolver, List<IBizLogCreator> creators) {
        this.beanResolver = beanResolver;
        this.creators = creators;
    }
}
