package com.hp.mybatisplus;

import com.baomidou.mybatisplus.annotation.TableField;
import com.google.common.collect.Lists;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author hp
 */
public class AbstractMbpAggregationRoot<AGGREGATE_ROOT extends AbstractMbpAggregationRoot<AGGREGATE_ROOT>> {

    @TableField(exist = false)
    private transient final List<Object> domainEvents = Lists.newArrayList();

    protected <T> T registerEvent(T event) {
        Assert.notNull(event, "Domain event must not be null");
        this.domainEvents.add(event);
        return event;
    }

    protected void clearDomainEvents() {
        this.domainEvents.clear();
    }

    protected Collection<Object> domainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }

    @SuppressWarnings("unchecked")
    protected final AGGREGATE_ROOT andEventsFrom(AGGREGATE_ROOT aggregate) {
        Assert.notNull(aggregate, "Aggregate must not be null");
        this.domainEvents.addAll(aggregate.domainEvents());
        return (AGGREGATE_ROOT) this;
    }

    @SuppressWarnings("unchecked")
    protected final AGGREGATE_ROOT andEvent(Object event) {
        registerEvent(event);
        return (AGGREGATE_ROOT) this;
    }
}
