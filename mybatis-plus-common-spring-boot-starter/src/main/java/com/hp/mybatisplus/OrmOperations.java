package com.hp.mybatisplus;

import cn.hutool.extra.spring.SpringUtil;
import com.hp.orm.api.BatchLoader;
import com.hp.orm.api.BatchUpdaterLoader;
import com.hp.orm.api.Loader;
import com.hp.orm.api.UpdaterLoader;

import java.io.Serializable;

/**
 * @author hp
 */
public interface OrmOperations {

    static <AGGREGATE_ROOT, ID extends Serializable> Loader<AGGREGATE_ROOT> create(BaseRepository<AGGREGATE_ROOT, ID> baseRepository) {
        return new OrmCreator<>(baseRepository, SpringUtil.getApplicationContext());
    }

    static <AGGREGATE_ROOT, ID extends Serializable> UpdaterLoader<AGGREGATE_ROOT, ID> update(BaseRepository<AGGREGATE_ROOT, ID> baseRepository) {
        return new OrmUpdater<>(baseRepository, SpringUtil.getApplicationContext());
    }

    static <AGGREGATE_ROOT, ID extends Serializable> BatchLoader<AGGREGATE_ROOT> createBatch(BaseRepository<AGGREGATE_ROOT, ID> baseRepository) {
        return new OrmBatchCreator<>(baseRepository, SpringUtil.getApplicationContext());
    }

    static <AGGREGATE_ROOT, ID extends Serializable> BatchUpdaterLoader<AGGREGATE_ROOT, ID> updateBatch(BaseRepository<AGGREGATE_ROOT, ID> baseRepository) {
        return new OrmBatchUpdater<>(baseRepository, SpringUtil.getApplicationContext());
    }
}
