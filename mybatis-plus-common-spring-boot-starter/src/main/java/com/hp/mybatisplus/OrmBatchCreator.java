package com.hp.mybatisplus;

import com.hp.orm.api.AbstractOrmBatchCreator;
import org.springframework.context.ApplicationEventPublisher;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author hp
 */
public class OrmBatchCreator<AGGREGATE_ROOT, ID extends Serializable> extends AbstractOrmBatchCreator<AGGREGATE_ROOT, OrmRepositoryAdapter<AGGREGATE_ROOT, ID>, ID> {

    protected final ApplicationEventPublisher eventPublisher;

    public OrmBatchCreator(BaseRepository<AGGREGATE_ROOT, ID> baseRepository, ApplicationEventPublisher eventPublisher) {
        super(new OrmRepositoryAdapter<>(baseRepository));
        this.eventPublisher = eventPublisher;
    }

    protected void publishingEvents(Collection<AGGREGATE_ROOT> aggregateRoots) {
        aggregateRoots.forEach(root -> {
            if (root instanceof AbstractMbpAggregationRoot<?> eventCapableAggRoot) {
                eventCapableAggRoot.domainEvents().forEach(eventPublisher::publishEvent);
            }
        });
    }

    @Override
    protected List<Consumer<Collection<AGGREGATE_ROOT>>> getOnSuccessConsumers() {
        this.onSuccessConsumers.addLast(this::publishingEvents);
        return this.onSuccessConsumers;
    }

    @Override
    protected void doFinally() {
        final Optional<AGGREGATE_ROOT> first = this.aggregateRoots.stream().findFirst();
        assert first.isPresent();
        if (first.get() instanceof AbstractMbpAggregationRoot<?>) {
            this.aggregateRoots.forEach(root-> ((AbstractMbpAggregationRoot<?>) root).clearDomainEvents());
        }
    }
}
