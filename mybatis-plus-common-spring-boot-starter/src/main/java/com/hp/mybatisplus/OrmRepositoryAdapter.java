package com.hp.mybatisplus;

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
        this.baseRepository.insert(Objects.requireNonNull(aggregation));
    }

    @Override
    public void saveAll(Collection<AGGREGATE_ROOT> aggregateRoots) {
        Objects.requireNonNull(aggregateRoots)
                .forEach(this::save);
    }

    @Override
    public void updateById(AGGREGATE_ROOT aggregation) {
        this.baseRepository.updateById(Objects.requireNonNull(aggregation));
    }

    @Override
    public void updateAllById(Collection<AGGREGATE_ROOT> aggregateRoots) {
        Objects.requireNonNull(aggregateRoots)
                .forEach(this::updateById);
    }

    @Override
    public Optional<AGGREGATE_ROOT> findById(ID id) {
        return this.baseRepository.findById(Objects.requireNonNull(id));
    }

    @Override
    public Collection<AGGREGATE_ROOT> findAllById(Collection<ID> ids) {
        return this.baseRepository.findAllById(ids);
    }
}
