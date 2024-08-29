package com.hp.jpa;

import com.hp.orm.api.*;

import java.io.Serializable;

/**
 * @author hp
 */
public interface OrmOperations {

    static <AGGREGATE_ROOT, ID extends Serializable> Loader<AGGREGATE_ROOT> create(BaseRepository<AGGREGATE_ROOT, ID> baseRepository) {

        return new AbstractOrmCreator<>(new OrmRepositoryAdapter<>(baseRepository)) {
        };
    }

    static <AGGREGATE_ROOT, ID extends Serializable> UpdaterLoader<AGGREGATE_ROOT, ID> update(BaseRepository<AGGREGATE_ROOT, ID> baseRepository) {
        return new AbstractOrmUpdater<>(new OrmRepositoryAdapter<>(baseRepository)) {
        };
    }

    static <AGGREGATE_ROOT, ID extends Serializable> BatchLoader<AGGREGATE_ROOT> createBatch(BaseRepository<AGGREGATE_ROOT, ID> baseRepository) {
        return new AbstractOrmBatchCreator<>(new OrmRepositoryAdapter<>(baseRepository)) {
        };
    }

    static <AGGREGATE_ROOT, ID extends Serializable> BatchUpdaterLoader<AGGREGATE_ROOT, ID> updateBatch(BaseRepository<AGGREGATE_ROOT, ID> baseRepository) {
        return new AbstractOrmBatchUpdater<>(new OrmRepositoryAdapter<>(baseRepository)) {
        };
    }

}
