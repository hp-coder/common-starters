package com.hp.mybatisplus;

import com.hp.orm.api.AbstractOrmCreator;
import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author hp
 */
public class OrmCreator<AGGREGATE_ROOT, ID extends Serializable> extends AbstractOrmCreator<AGGREGATE_ROOT, OrmRepositoryAdapter<AGGREGATE_ROOT, ID>, ID> {

    protected final ApplicationEventPublisher eventPublisher;

    public OrmCreator(BaseRepository<AGGREGATE_ROOT, ID> baseRepository, ApplicationEventPublisher eventPublisher) {
        super(new OrmRepositoryAdapter<>(baseRepository));
        this.eventPublisher = eventPublisher;
    }

    protected void publishingEvents(AGGREGATE_ROOT aggregateRoot) {
        if (aggregateRoot instanceof AbstractMbpAggregationRoot<?> eventCapableAggRoot) {
            eventCapableAggRoot.domainEvents().forEach(eventPublisher::publishEvent);
        }
    }

    @Override
    protected List<Consumer<AGGREGATE_ROOT>> getOnSuccessConsumers() {
        this.onSuccessConsumers.addLast(this::publishingEvents);
        return this.onSuccessConsumers;
    }

    @Override
    protected void doFinally() {
        if (this.aggregateRoot instanceof AbstractMbpAggregationRoot<?> eventCapableAggregateRoot) {
            eventCapableAggregateRoot.clearDomainEvents();
        }
    }
}
