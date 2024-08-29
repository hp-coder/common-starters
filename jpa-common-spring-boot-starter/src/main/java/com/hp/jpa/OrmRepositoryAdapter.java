package com.hp.jpa;

import com.hp.orm.api.OrmRepository;

import java.io.Serializable;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

/**
 * @author hp
 */
public class OrmRepositoryAdapter<AGGREGATE_ROOT, ID extends Serializable> implements OrmRepository<AGGREGATE_ROOT, ID> {

    protected final BaseRepository<AGGREGATE_ROOT, ID> baseRepository;

    public OrmRepositoryAdapter(BaseRepository<AGGREGATE_ROOT, ID> baseRepository) {
        this.baseRepository = Objects.requireNonNull(baseRepository);
    }

    @Override
    public void save(AGGREGATE_ROOT aggregation) {
        baseRepository.save(Objects.requireNonNull(aggregation));
    }

    @Override
    public void saveAll(Collection<AGGREGATE_ROOT> aggregateRoots) {
        baseRepository.saveAll(Objects.requireNonNull(aggregateRoots));
    }

    @Override
    public void updateById(AGGREGATE_ROOT aggregation) {
        baseRepository.save(Objects.requireNonNull(aggregation));
    }

    @Override
    public void updateAllById(Collection<AGGREGATE_ROOT> aggregateRoots) {
        baseRepository.saveAll(Objects.requireNonNull(aggregateRoots));
    }

    @Override
    public Optional<AGGREGATE_ROOT> findById(ID id) {
        return baseRepository.findById(Objects.requireNonNull(id));
    }

    @Override
    public Collection<AGGREGATE_ROOT> findAllById(Collection<ID> ids) {
        return baseRepository.findAllById(ids);
    }
}
